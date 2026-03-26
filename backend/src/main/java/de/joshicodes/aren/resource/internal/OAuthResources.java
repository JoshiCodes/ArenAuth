package de.joshicodes.aren.resource.internal;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.URISyntaxException;

@Path("/api/internal/projects")
@Authenticated
public class OAuthResources {

    @ConfigProperty(name = "aren.frontend.url")
    String frontendUrl;

    /**
     * The default endpoint redirected to by the client.
     * Checks for GET/QUERY params:
     *  -
     * Creates a data object,
     * redirects to frontend with /consent?req=[data obj. id]
     * @return a redirect response
     */
    @GET
    public Response authorize() {



        try {
            return Response.seeOther(
                    new URI(frontendUrl + "/consent?req=")
            ).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

}
