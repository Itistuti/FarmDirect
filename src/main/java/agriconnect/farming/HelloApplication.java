package agriconnect.farming;

import agriconnect.farming.auth.AuthResource;
import agriconnect.farming.auth.ProtectedResource;
import agriconnect.farming.auth.RoleFilter;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class HelloApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(HelloResource.class);
		classes.add(AuthResource.class);
		classes.add(ProtectedResource.class);
		classes.add(RoleFilter.class);
		return classes;
	}

}