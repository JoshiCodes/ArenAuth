package de.joshicodes.aren;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class ExampleResource {

    @Inject
    SecurityIdentity identity;

    @GET
    @Authenticated
    @Path("/me")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String me() {
        return "Hallo " + identity.getPrincipal().getName() +
                ", deine Rollen: " + identity.getRoles();
    }

}
