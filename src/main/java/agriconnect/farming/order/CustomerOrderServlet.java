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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@WebServlet(name = "customerOrderServlet", urlPatterns = "/api/customer/orders/*")
public class CustomerOrderServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final OrderRepository orderRepository = OrderRepository.getInstance();
    private final ProductRepository productRepository = ProductRepository.getInstance();
    private final AuthService authService = AuthService.getInstance();

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
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Not enough stock available");
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
}
