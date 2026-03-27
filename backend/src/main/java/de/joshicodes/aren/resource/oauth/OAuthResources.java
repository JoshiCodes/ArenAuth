package de.joshicodes.aren.resource.oauth;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Path("/oauth2")
@Authenticated
public class OAuthResources {

    @ConfigProperty(name = "aren.frontend.url")
    String frontendUrl;


    @POST
    @Path("/authorize")
    public Response authorize() {
        return Response.status(418).build();
    }

}
