package agriconnect.farming.auth;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Arrays;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        SecureRole secureRole = getSecureRole();
        if (secureRole == null) {
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(JsonSupport.errorJson("Login required"))
                    .build());
        }

        String sessionRole = (String) session.getAttribute(SessionKeys.USER_ROLE);
        if (sessionRole == null || Arrays.stream(secureRole.value()).noneMatch(role -> role.name().equalsIgnoreCase(sessionRole))) {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(JsonSupport.errorJson("You do not have permission to access this resource"))
                    .build());
        }
    }

    private SecureRole getSecureRole() {
        if (resourceInfo == null || resourceInfo.getResourceMethod() == null) {
            return null;
        }

        SecureRole methodAnnotation = resourceInfo.getResourceMethod().getAnnotation(SecureRole.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return resourceInfo.getResourceClass().getAnnotation(SecureRole.class);
    }
}


