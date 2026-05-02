package agriconnect.farming.product;

import agriconnect.farming.auth.AuthService;
import agriconnect.farming.auth.JsonSupport;
import agriconnect.farming.auth.Role;
import agriconnect.farming.auth.SessionKeys;
import agriconnect.farming.auth.User;
import agriconnect.farming.auth.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "customerProductServlet", urlPatterns = "/api/customer/products/*")
public class CustomerProductServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
        if ("/".equals(pathInfo)) {
            listProducts(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String category = trimToNull(req.getParameter("category"));
        String query = trimToNull(req.getParameter("q"));
        String normalizedQuery = query == null ? null : query.toLowerCase();

        List<Product> products = new ArrayList<>(productRepository.findAll());
        if (category != null) {
            products = products.stream()
                    .filter(product -> category.equalsIgnoreCase(product.getCategory()))
                    .collect(Collectors.toList());
        }
        if (normalizedQuery != null) {
            products = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(normalizedQuery)
                            || product.getDescription().toLowerCase().contains(normalizedQuery))
                    .collect(Collectors.toList());
        }

        List<ProductDTO> dtos = products.stream().map(this::toDTO).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", dtos);
        response.put("count", dtos.size());
        
        resp.setStatus(HttpServletResponse.SC_OK);
        writeJson(resp, response);
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.id = product.getId();
        dto.farmerId = product.getFarmerId().toString();
        dto.name = product.getName();
        dto.category = product.getCategory();
        dto.description = product.getDescription();
        dto.price = product.getPrice();
        dto.stock = product.getStock();

        Optional<User> farmerOpt = userRepository.findById(product.getFarmerId());
        if (farmerOpt.isPresent()) {
            User farmer = farmerOpt.get();
            dto.farmerName = farmer.getFullName();
            dto.farmerLocation = farmer.getLocation();
            dto.farmerPhone = farmer.getPhone();
        } else {
            dto.farmerName = "Unknown Farmer";
            dto.farmerLocation = "Unknown";
            dto.farmerPhone = "Unknown";
        }
        return dto;
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static class ProductDTO {
        public long id;
        public String farmerId;
        public String name;
        public String category;
        public String description;
        public double price;
        public int stock;
        public String farmerName;
        public String farmerLocation;
        public String farmerPhone;
    }
}
