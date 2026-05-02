package agriconnect.farming.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "protectedServlet", urlPatterns = "/api/protected/*")
public class ProtectedServlet extends HttpServlet {
    private final AuthService authService = AuthService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = safePath(req);
        resp.setContentType("application/json;charset=UTF-8");

        User user = currentUser(req);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonSupport.errorJson("Not authenticated"));
            return;
        }

        switch (path) {
            case "/any" -> {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(JsonSupport.roleMessageJson(user, "Welcome back, " + user.getFullName()));
            }
            case "/farmer" -> requireRole(resp, user, Role.FARMER, "Welcome to the farmer workspace");
            case "/customer" -> requireRole(resp, user, Role.CUSTOMER, "Welcome to the customer workspace");
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void requireRole(HttpServletResponse resp, User user, Role requiredRole, String message) throws IOException {
        if (user.getRole() != requiredRole) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(JsonSupport.errorJson("You do not have permission to access this resource"));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonSupport.roleMessageJson(user, message));
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

    private String safePath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.isBlank()) {
            return "/";
        }
        return path;
    }
}
