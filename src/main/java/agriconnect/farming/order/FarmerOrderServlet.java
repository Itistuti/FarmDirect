package agriconnect.farming.order;

import agriconnect.farming.auth.AuthService;
import agriconnect.farming.auth.JsonSupport;
import agriconnect.farming.auth.Role;
import agriconnect.farming.auth.SessionKeys;
import agriconnect.farming.auth.User;
import agriconnect.farming.product.Product;
import agriconnect.farming.product.ProductRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet(name = "farmerOrderServlet", urlPatterns = "/api/farmer/orders/*")
public class FarmerOrderServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final OrderRepository orderRepository = OrderRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireFarmer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if (!"/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<OrderView> orderViews = orderRepository.findByFarmerId(user.getId()).stream()
                .map(this::toView)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderViews);
        response.put("count", orderViews.size());

        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, response);
    }

    private OrderView toView(Order order) {
        Optional<Product> product = productRepository.findById(order.getProductId());
        String productName = product.map(Product::getName).orElse("(unknown product)");

        String status = order.getStatus();
        if ("ACTIVE".equals(status) || status == null) {
            long minutes = java.time.Duration.between(order.getCreatedAt(), Instant.now()).toMinutes();
            if (minutes < 3) status = "PLACED";
            else if (minutes < 8) status = "PACKING";
            else if (minutes < 20) status = "IN_TRANSIT";
            else status = "DELIVERED";
        }

        return new OrderView(
                order.getId(),
                order.getProductId(),
                productName,
                order.getCustomerName(),
                order.getQuantity(),
                order.getLocation(),
                order.getPhoneNumber(),
                order.getCreatedAt(),
                status
        );
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireFarmer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if (pathInfo == null || pathInfo.equals("/") || !pathInfo.endsWith("/status")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length != 3) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path format");
            return;
        }

        String orderIdStr = parts[1];
        UUID orderId;
        try {
            orderId = UUID.fromString(orderIdStr);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
            return;
        }

        UpdateStatusRequest updateReq;
        try {
            updateReq = MAPPER.readValue(req.getInputStream(), UpdateStatusRequest.class);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        if (updateReq.status == null || updateReq.status.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Status is required");
            return;
        }

        Optional<Order> orderOpt = orderRepository.findByFarmerId(user.getId()).stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst();

        if (orderOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        Order order = orderOpt.get();
        if ("CANCELLED".equals(order.getStatus())) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot change status of a cancelled order");
            return;
        }

        orderRepository.save(order.withStatus(updateReq.status));

        resp.setStatus(HttpServletResponse.SC_OK);
        Map<String, String> result = new HashMap<>();
        result.put("message", "Order status updated successfully");
        writeJson(resp, result);
    }

    public static class UpdateStatusRequest {
        public String status;
    }

    private User requireFarmer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = currentUser(req);
        if (user == null) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonSupport.errorJson("Not authenticated"));
            return null;
        }
        if (user.getRole() != Role.FARMER) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(JsonSupport.errorJson("You do not have permission to access this resource"));
            return null;
        }
        return user;
    }

    private User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object email = session.getAttribute(SessionKeys.USER_EMAIL);
        if (!(email instanceof String)) {
            return null;
        }
        return authService.findByEmail((String) email);
    }

    private String normalizePathInfo(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank()) {
            return "/";
        }
        if (!pathInfo.startsWith("/")) {
            return "/" + pathInfo;
        }
        return pathInfo;
    }

    private void writeJson(HttpServletResponse resp, Object payload) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        MAPPER.writeValue(resp.getOutputStream(), payload);
    }

    public static final class OrderView {
        public UUID id;
        public long productId;
        public String productName;
        public String customerName;
        public int quantity;
        public String location;
        public String phoneNumber;
        public Instant createdAt;
        public String status;

        public OrderView(UUID id,
                         long productId,
                         String productName,
                         String customerName,
                         int quantity,
                         String location,
                         String phoneNumber,
                         Instant createdAt,
                         String status) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.customerName = customerName;
            this.quantity = quantity;
            this.location = location;
            this.phoneNumber = phoneNumber;
            this.createdAt = createdAt;
            this.status = status;
        }

        public UUID getId() {
            return id;
        }

        public long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getLocation() {
            return location;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public String getStatus() {
            return status;
        }
    }
}
