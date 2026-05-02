package agriconnect.farming.product;

import agriconnect.farming.auth.AuthService;
import agriconnect.farming.auth.JsonSupport;
import agriconnect.farming.auth.Role;
import agriconnect.farming.auth.SessionKeys;
import agriconnect.farming.auth.User;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "farmerProductServlet", urlPatterns = "/api/farmer/products/*")
public class FarmerProductServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final ProductRepository productRepository = ProductRepository.getInstance();
    private final AuthService authService = AuthService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireFarmer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if ("/".equals(pathInfo)) {
            listMyProducts(resp, user);
            return;
        }

        String[] parts = pathInfo.substring(1).split("/");
        if (parts.length == 1) {
            getProduct(resp, user, parts[0]);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireFarmer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if (!"/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ProductRequest productRequest;
        try {
            productRequest = MAPPER.readValue(req.getInputStream(), ProductRequest.class);
        } catch (Exception ex) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        String name = trimToNull(productRequest.getName());
        String category = trimToNull(productRequest.getCategory());
        String description = productRequest.getDescription() != null ? productRequest.getDescription().trim() : "";

        if (name == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product name is required");
            return;
        }
        if (category == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category is required");
            return;
        }
        if (productRequest.getPrice() <= 0) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Price must be greater than 0");
            return;
        }
        if (productRequest.getStock() < 0) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Stock cannot be negative");
            return;
        }

        Product product = new Product(
                user.getId(),
                name,
                category,
                description,
                productRequest.getPrice(),
                productRequest.getStock()
        );

        Product saved = productRepository.save(product);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        writeJson(resp, saved);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireFarmer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        if ("/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = pathInfo.substring(1).split("/");
        if (parts.length == 2 && "stock".equalsIgnoreCase(parts[1])) {
            StockUpdateRequest stockUpdateRequest;
            try {
                stockUpdateRequest = MAPPER.readValue(req.getInputStream(), StockUpdateRequest.class);
            } catch (Exception ex) {
                jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
                return;
            }
            updateStock(resp, user, parts[0], stockUpdateRequest);
            return;
        }

        if (parts.length != 1) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ProductRequest productRequest;
        try {
            productRequest = MAPPER.readValue(req.getInputStream(), ProductRequest.class);
        } catch (Exception ex) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        updateProduct(resp, user, parts[0], productRequest);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = requireFarmer(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = normalizePathInfo(req);
        String[] parts = pathInfo.substring(1).split("/");
        if (parts.length != 1 || parts[0].isBlank()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        deleteProduct(resp, user, parts[0]);
    }

    private void listMyProducts(HttpServletResponse resp, User user) throws IOException {
        List<Product> products = productRepository.findByFarmerId(user.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("count", products.size());
        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, response);
    }

    private void getProduct(HttpServletResponse resp, User user, String productId) throws IOException {
        Long id = parseProductId(productId);
        if (id == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
            return;
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            jsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }

        if (!product.get().getFarmerId().equals(user.getId())) {
            jsonError(resp, HttpServletResponse.SC_FORBIDDEN, "You do not own this product");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, product.get());
    }

    private void updateProduct(HttpServletResponse resp, User user, String productId, ProductRequest productRequest) throws IOException {
        Long id = parseProductId(productId);
        if (id == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
            return;
        }

        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            jsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }

        Product product = existingProduct.get();
        if (!product.getFarmerId().equals(user.getId())) {
            jsonError(resp, HttpServletResponse.SC_FORBIDDEN, "You do not own this product");
            return;
        }

        String name = trimToNull(productRequest.getName());
        String category = trimToNull(productRequest.getCategory());
        String description = productRequest.getDescription() != null ? productRequest.getDescription().trim() : "";

        if (name == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product name is required");
            return;
        }
        if (category == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category is required");
            return;
        }
        if (productRequest.getPrice() <= 0) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Price must be greater than 0");
            return;
        }
        if (productRequest.getStock() < 0) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Stock cannot be negative");
            return;
        }

        Product updated = product.withDetails(
                name,
                category,
                description,
                productRequest.getPrice()
        );

        if (productRequest.getStock() != product.getStock()) {
            updated = updated.withStock(productRequest.getStock());
        }

        productRepository.save(updated);
        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, updated);
    }

    private void deleteProduct(HttpServletResponse resp, User user, String productId) throws IOException {
        Long id = parseProductId(productId);
        if (id == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
            return;
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            jsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }

        if (!product.get().getFarmerId().equals(user.getId())) {
            jsonError(resp, HttpServletResponse.SC_FORBIDDEN, "You do not own this product");
            return;
        }

        productRepository.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, response);
    }

    private void updateStock(HttpServletResponse resp, User user, String productId, StockUpdateRequest stockUpdateRequest) throws IOException {
        if (stockUpdateRequest.getStock() < 0) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Stock cannot be negative");
            return;
        }

        Long id = parseProductId(productId);
        if (id == null) {
            jsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
            return;
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            jsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }

        if (!product.get().getFarmerId().equals(user.getId())) {
            jsonError(resp, HttpServletResponse.SC_FORBIDDEN, "You do not own this product");
            return;
        }

        Product updated = product.get().withStock(stockUpdateRequest.getStock());
        productRepository.save(updated);
        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, updated);
    }

    private Long parseProductId(String productId) {
        if (productId == null) {
            return null;
        }
        String trimmed = productId.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            long id = Long.parseLong(trimmed);
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
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

    public static class ProductRequest {
        public String name;
        public String category;
        public String description;
        public double price;
        public int stock;

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }
    }

    public static class StockUpdateRequest {
        public int stock;

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }
    }
}
