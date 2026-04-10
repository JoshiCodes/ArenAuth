package de.joshicodes.aren.resource.internal;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.entities.dto.ProjectDTO;
import de.joshicodes.aren.entities.dto.UserDTO;
import de.joshicodes.aren.entities.oauth.OAuthToken;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v1/internal/admin")
@RolesAllowed("admin")
public class AdminResources {

    @GET
    @Path("/stats")
    public Response getStats() {
        final Map<String, Object> stats = new HashMap<>();

        stats.put("users", User.count());
        stats.put("projects", Project.count());
        stats.put("tokens", OAuthToken.count("revoked = false AND accessTokenExpiresAt < ?1", Instant.now()));

        return Response.ok(stats).build();
    }

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
    @Path("/projects")
    public Response getAllProjects(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {

        if(page == null) page = 0;
        if(size == null) size = 10;

        List<Project> pageOfProjects = Project.findAll(Sort.by("id")).page(page, size).list();

        List<ProjectDTO> projects = pageOfProjects.stream()
                .map(ProjectDTO::from)
                .toList();

        final long count = Project.count();

        return Response.ok(Map.of(
                "projects", projects,
                "page", page,
                "size", size,
                "total_pages", (int) Math.ceil(count / (double) size),
                "total_count", count
        )).build();

    }

}
