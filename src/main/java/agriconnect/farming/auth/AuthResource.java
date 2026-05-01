package agriconnect.farming.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/auth")
public class AuthResource {
    private final AuthService authService = AuthService.getInstance();

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response signup(@FormParam("fullName") String fullName,
                           @FormParam("email") String email,
                           @FormParam("password") String password,
                           @FormParam("role") String role,
                           @Context HttpServletRequest request) {
        try {
            Role userRole = Role.from(role);
            User user = authService.register(fullName, email, password, userRole);
            establishSession(request, user);
            return redirect(request, "/dashboard.html");
        } catch (IllegalArgumentException ex) {
            return redirect(request, "/signup.html?error=" + encode(ex.getMessage()));
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("email") String email,
                          @FormParam("password") String password,
                          @Context HttpServletRequest request) {
        try {
            User user = authService.authenticate(email, password);
            establishSession(request, user);
            return redirect(request, "/dashboard.html");
        } catch (IllegalArgumentException ex) {
            return redirect(request, "/login.html?error=" + encode(ex.getMessage()));
        }
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return redirect(request, "/login.html");
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response me(@Context HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(JsonSupport.errorJson("Not authenticated"))
                    .build();
        }
        return Response.ok(JsonSupport.authJson(user), MediaType.APPLICATION_JSON).build();
    }

    private void establishSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SessionKeys.USER_ID, user.getId().toString());
        session.setAttribute(SessionKeys.USER_NAME, user.getFullName());
        session.setAttribute(SessionKeys.USER_EMAIL, user.getEmail());
        session.setAttribute(SessionKeys.USER_ROLE, user.getRole().name());
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

    private Response redirect(HttpServletRequest request, String location) {
        return Response.seeOther(URI.create(request.getContextPath() + location)).build();
    }

    private String encode(String message) {
        return URLEncoder.encode(message == null ? "Something went wrong" : message, StandardCharsets.UTF_8);
    }
}


