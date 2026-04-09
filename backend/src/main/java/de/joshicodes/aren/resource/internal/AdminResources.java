package de.joshicodes.aren.resource.internal;

import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.entities.dto.UserDTO;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/api/v1/internal/admin")
@RolesAllowed("admin")
public class AdminResources {

    @GET
    @Path("/users")
    public Response getAllUsers(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {

        if(page == null) page = 0;
        if(size == null) size = 10;

        List<User> pageOfUsers = User.findAll(Sort.by("id")).page(page, size).list();

        List<UserDTO> users = pageOfUsers.stream()
                .map(u -> UserDTO.from(u, true))
                .toList();

        final long count = User.count();

        return Response.ok(Map.of(
                "users", users,
                "page", page,
                "size", size,
                "total_pages", (int) Math.ceil(count / (double) size),
                "total_count", count
        )).build();

    }

    @GET
    @Path("/users/count")
    public Response getUserCount() {
        return Response.ok(User.count()).build();
    }

}
