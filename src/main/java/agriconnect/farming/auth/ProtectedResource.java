package agriconnect.farming.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/protected")
@Produces(MediaType.APPLICATION_JSON)
public class ProtectedResource {
    private final AuthService authService = AuthService.getInstance();

    @GET
    @Path("/farmer")
    @SecureRole({Role.FARMER})
    public Response farmerArea(@Context HttpServletRequest request) {
        return roleResponse(request, "Welcome to the farmer workspace");
    }

    @GET
    @Path("/customer")
    @SecureRole({Role.CUSTOMER})
    public Response customerArea(@Context HttpServletRequest request) {
        return roleResponse(request, "Welcome to the customer workspace");
    }

    @GET
    @Path("/any")
    public Response anyAuthenticatedUser(@Context HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(JsonSupport.errorJson("Not authenticated"))
                    .build();
        }
        return Response.ok(JsonSupport.roleMessageJson(user, "Welcome back, " + user.getFullName())).build();
    }

    private Response roleResponse(HttpServletRequest request, String message) {
        User user = currentUser(request);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(JsonSupport.errorJson("Not authenticated"))
                    .build();
        }
        return Response.ok(JsonSupport.roleMessageJson(user, message)).build();
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
}

