package agriconnect.farming.order;

import agriconnect.farming.auth.AuthService;
import agriconnect.farming.auth.JsonSupport;
import agriconnect.farming.auth.Role;
import agriconnect.farming.auth.SessionKeys;
import agriconnect.farming.auth.User;
import agriconnect.farming.auth.UserRepository;
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
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@WebServlet(name = "customerOrderServlet", urlPatterns = "/api/customer/orders/*")
public class CustomerOrderServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final OrderRepository orderRepository = OrderRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();
    private final UserRepository userRepository = new UserRepository();
    private final AuthService authService = AuthService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireCustomer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if (!"/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<OrderView> orders = new ArrayList<>();
        for (Order order : orderRepository.findByCustomerId(user.getId())) {
            orders.add(toView(order));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("count", orders.size());
        
        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireCustomer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if (!"/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        CreateOrderRequest createOrderRequest;
        try {
            createOrderRequest = MAPPER.readValue(req.getInputStream(), CreateOrderRequest.class);
        } catch (Exception ex) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        Long productId = parseProductId(createOrderRequest.getProductId());
        if (productId == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid productId");
            return;
        }

        int quantity = createOrderRequest.getQuantity();
        if (quantity <= 0) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Quantity must be greater than 0");
            return;
        }

        String location = trimToNull(createOrderRequest.getLocation());
        if (location == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Location is required");
            return;
        }

        String phoneNumber = trimToNull(createOrderRequest.getPhoneNumber());
        if (phoneNumber == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Phone number is required");
            return;
        }

        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            jsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }

        if (product.get().getStock() < quantity) {
            int stockLeft = product.get().getStock();
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Out of stock. Only " + stockLeft + " item" + (stockLeft == 1 ? "" : "s") + " left");
            return;
        }

        Order order = new Order(
                productId,
                product.get().getFarmerId(),
                user.getId(),
                user.getFullName(),
                quantity,
                location,
                phoneNumber
        );

        orderRepository.save(order);

        // Reduce stock to reflect the order
        productRepository.save(product.get().withStock(product.get().getStock() - quantity));

        resp.setStatus(HttpServletResponse.SC_CREATED);
        writeJson(resp, order);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireCustomer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if ("/".equals(pathInfo) || pathInfo.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID required");
            return;
        }

        String orderIdStr = pathInfo.substring(1);
        UUID orderId;
        try {
            orderId = UUID.fromString(orderIdStr);
        } catch (IllegalArgumentException e) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid Order ID");
            return;
        }

        Optional<Order> orderOpt = orderRepository.findByCustomerId(user.getId()).stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst();

        if (orderOpt.isEmpty()) {
            jsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        Order order = orderOpt.get();
        if ("CANCELLED".equals(order.getStatus()) || "DELIVERED".equals(deriveStatus(order.getCreatedAt(), order.getStatus()))) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Order cannot be cancelled at this stage");
            return;
        }

        orderRepository.save(order.withStatus("CANCELLED"));
        
        // Restore stock
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            productRepository.save(product.withStock(product.getStock() + order.getQuantity()));
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        Map<String, String> result = new HashMap<>();
        result.put("message", "Order cancelled successfully");
        writeJson(resp, result);
    }

    private OrderView toView(Order order) {
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        String productName = productOpt.map(Product::getName).orElse("Unknown Product");
        double unitPrice = productOpt.map(Product::getPrice).orElse(0.0d);

        Optional<User> farmerOpt = userRepository.findById(order.getFarmerId());
        String farmerName = farmerOpt.map(User::getFullName).orElse("Unknown Farmer");
        String farmerLocation = farmerOpt.map(User::getLocation).orElse("Unknown");
        String farmerPhone = farmerOpt.map(User::getPhone).orElse("Unknown");

        String status = deriveStatus(order.getCreatedAt(), order.getStatus());
        return new OrderView(
                order.getId().toString(),
                order.getProductId(),
                productName,
                farmerName,
                farmerLocation,
                farmerPhone,
                order.getQuantity(),
                unitPrice,
                unitPrice * order.getQuantity(),
                order.getLocation(),
                order.getPhoneNumber(),
                status,
                order.getCreatedAt().toEpochMilli()
        );
    }

    private String deriveStatus(Instant createdAt, String orderStatus) {
        if ("CANCELLED".equals(orderStatus)) {
            return "CANCELLED";
        }
        if (orderStatus != null && !"ACTIVE".equals(orderStatus)) {
            return orderStatus;
        }
        long minutes = Duration.between(createdAt, Instant.now()).toMinutes();
        if (minutes < 3) {
            return "PLACED";
        }
        if (minutes < 8) {
            return "PACKING";
        }
        if (minutes < 20) {
            return "IN_TRANSIT";
        }
        return "DELIVERED";
    }

    private Long parseProductId(String raw) {
        String value = trimToNull(raw);
        if (value == null) {
            return null;
        }
        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private User requireCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = currentUser(req);
        if (user == null) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonSupport.errorJson("Not authenticated"));
            return null;
        }
        if (user.getRole() != Role.CUSTOMER) {
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

    private void jsonError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        MAPPER.writeValue(resp.getOutputStream(), error);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static class CreateOrderRequest {
        public String productId;
        public int quantity;
        public String location;
        public String phoneNumber;

        public String getProductId() {
            return productId;
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

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public static class OrderView {
        public String id;
        public long productId;
        public String productName;
        public String farmerName;
        public String farmerLocation;
        public String farmerPhone;
        public int quantity;
        public double unitPrice;
        public double totalPrice;
        public String deliveryLocation;
        public String deliveryPhone;
        public String status;
        public long createdAtMs;

        public OrderView(String id,
                         long productId,
                         String productName,
                         String farmerName,
                         String farmerLocation,
                         String farmerPhone,
                         int quantity,
                         double unitPrice,
                         double totalPrice,
                         String deliveryLocation,
                         String deliveryPhone,
                         String status,
                         long createdAtMs) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.farmerName = farmerName;
            this.farmerLocation = farmerLocation;
            this.farmerPhone = farmerPhone;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.deliveryLocation = deliveryLocation;
            this.deliveryPhone = deliveryPhone;
            this.status = status;
            this.createdAtMs = createdAtMs;
        }
    }
}
