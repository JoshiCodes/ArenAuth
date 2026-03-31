package de.joshicodes.aren.resource.user;

import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.entities.dto.UserDTO;
import de.joshicodes.aren.entities.oauth.OAuthToken;
import de.joshicodes.aren.oauth.Scopes;
import de.joshicodes.aren.security.oauth.OAuthAuthenticated;
import de.joshicodes.aren.security.oauth.OAuthSecurityContext;
import de.joshicodes.aren.security.oauth.OAuthUser;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;

@Path("/api/user")
@OAuthAuthenticated
public class UserResource {

    @Inject
    OAuthSecurityContext securityContext;

    @GET
    @Path("/me")
    public Response me() {
        final HashMap<String, Object> response = new HashMap<>();

        final User user = securityContext.getCurrentUser();

        response.put("sub", user.id.toString());
        response.put("name", user.username);

        response.put("user", UserDTO.from(user));
        if(securityContext.hasScope(Scopes.EMAIL.getId())) {
            response.put("email", user.email);
        }
        if(securityContext.hasScope(Scopes.APPLICATIONS_READ.getId())) {
            response.put("projects", user.ownedProjects.stream().map(p -> p.id).toArray());
        }

        return Response.ok(response).build();
    }

}
