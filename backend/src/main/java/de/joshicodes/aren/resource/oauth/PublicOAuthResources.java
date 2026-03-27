package de.joshicodes.aren.resource.oauth;

import de.joshicodes.aren.oauth.Scopes;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/oauth2")
public class PublicOAuthResources {

    @GET
    @Path("/scopes")
    public Response getScopes() {
        return Response.ok(
                Arrays.stream(Scopes.values())
                        .map(scope -> Map.of("name", scope.getId(), "description", scope.getFallbackDescription()))
                        .collect(Collectors.toList())
        ).build();
    }

}
