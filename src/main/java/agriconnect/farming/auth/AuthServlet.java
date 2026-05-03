package agriconnect.farming.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "authServlet", urlPatterns = "/api/auth/*")
public class AuthServlet extends HttpServlet {
    private final AuthService authService = AuthService.getInstance();

    private static final String FLASH_MESSAGE_KEY = "FLASH_MESSAGE";
    private static final String FLASH_TYPE_KEY = "FLASH_TYPE";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = safePath(req);
        if ("/me".equals(path)) {
            handleMe(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = safePath(req);
        switch (path) {
            case "/signup" -> handleSignup(req, resp);
            case "/login" -> handleLogin(req, resp);
            case "/logout" -> handleLogout(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleSignup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String location = req.getParameter("location");
        String phone = req.getParameter("phone");

        try {
            Role userRole = Role.from(role);
            authService.register(fullName, email, password, userRole, location, phone);
            setFlash(req, "success", "Account created");
            redirect(req, resp, "/login.jsp");
        } catch (IllegalArgumentException ex) {
            setFlash(req, "error", ex.getMessage());
            redirect(req, resp, "/signup.jsp");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = authService.authenticate(email, password);
            establishSession(req, user);
            redirect(req, resp, postAuthRedirect(user));
        } catch (IllegalArgumentException ex) {
            setFlash(req, "error", ex.getMessage());
            redirect(req, resp, "/login.jsp");
        }
    }

    private String postAuthRedirect(User user) {
        if (user != null && user.getRole() == Role.FARMER) {
            return "/farmer-dashboard.jsp";
        } else if (user != null && user.getRole() == Role.CUSTOMER) {
            return "/customer-dashboard.jsp";
        }
        return "/index.jsp";
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        setFlash(req, "success", "You have been logged out");
        redirect(req, resp, "/login.jsp");
    }

    private void setFlash(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(FLASH_TYPE_KEY, type);
        session.setAttribute(FLASH_MESSAGE_KEY, message);
    }

    private void handleMe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = currentUser(req);
        resp.setContentType("application/json;charset=UTF-8");

        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonSupport.errorJson("Not authenticated"));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonSupport.authJson(user));
    }

    private void establishSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SessionKeys.USER_ID, user.getId().toString());
        session.setAttribute(SessionKeys.USER_NAME, user.getFullName());
        session.setAttribute(SessionKeys.USER_EMAIL, user.getEmail());
        session.setAttribute(SessionKeys.USER_ROLE, user.getRole().name());
        session.setAttribute(SessionKeys.USER_LOCATION, user.getLocation());
        session.setAttribute(SessionKeys.USER_PHONE, user.getPhone());
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

    private void redirect(HttpServletRequest request, HttpServletResponse response, String location) throws IOException {
        response.sendRedirect(request.getContextPath() + location);
    }

    private String safePath(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.isBlank()) {
            return "/";
        }
        return path;
    }
}
