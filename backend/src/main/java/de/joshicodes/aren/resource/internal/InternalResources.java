package de.joshicodes.aren.resource.internal;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/api/v1/internal/")
public class InternalResources {

    @Inject
    SecurityIdentity identity;

    @GET
    @Authenticated
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response me() {
        return Response.ok(Map.of(
                "username", identity.getPrincipal().getName(),
                "userId", identity.getAttribute("userId").toString(),
                "roles", identity.getRoles()
        )).build();
    }

}
