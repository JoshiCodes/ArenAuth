package de.joshicodes.aren.resource.internal;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.entities.dto.ProjectDTO;
import de.joshicodes.aren.security.UserExtractor;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.*;
import java.util.stream.Collectors;

@Path("/api/internal/projects")
@Authenticated
public class ProjectsResources {

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(String id) {

        final User user = UserExtractor.getUser(identity);
        if(user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final UUID projectId;
        try {
            projectId = UUID.fromString(id);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    Map.of("error", "Invalid project ID")
            ).build();
        }

        final Project project = Project.findById(projectId);
        if(project == null || project.owner == null || !project.owner.id.equals(user.id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(ProjectDTO.from(project)).build();

    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProject(String id) {

        final User user = UserExtractor.getUser(identity);
        if(user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final UUID projectId;
        try {
            projectId = UUID.fromString(id);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    Map.of("error", "Invalid project ID")
            ).build();
        }

        final Project project = Project.findById(projectId);
        if(project == null || project.owner == null || !project.owner.id.equals(user.id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean success = Project.deleteById(projectId);

        if(!success) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(Map.of("success", true)).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf() {

        final User user = UserExtractor.getUser(identity);
        if(user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final List<Project> projects = Project.list("owner.id", user.id);
        final List<ProjectDTO> dtos = projects.stream().map(ProjectDTO::from).toList();
        return Response
                .ok(dtos)
                .build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CreateProjectDTO dto) {

        final User user = UserExtractor.getUser(identity);
        if(user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if(dto.name == null || dto.name.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    Map.of("error", "Name cannot be empty")
            ).build();
        }
        if(dto.name.stripLeading().stripTrailing().length() < Project.MIN_NAME_LENGTH) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    Map.of("error", "Name must be at least " + Project.MIN_NAME_LENGTH + " characters long")
            ).build();
        }
        if(identity == null || identity.getPrincipal() == null || identity.getAttributes() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if(
                Project.list("owner.id", user.id)
                        .stream()
                        .map(p -> (Project) p)
                        .anyMatch(
                                p -> p.name.equalsIgnoreCase(dto.name))
        ) {
            return Response.status(Response.Status.CONFLICT).entity(
                    Map.of("error", "You already have a project with this name.")
            ).build();
        }

        Project project = new Project();
        project.name = dto.name;
        project.description = dto.description;
        project.imageBlob = null; // TODO
        project.owner = user;
        project.persist();
        return Response.ok(ProjectDTO.from(project)).build();
    }

    public static class CreateProjectDTO {
        @RestForm("name")
        public @Nullable String name;
        @RestForm("description")
        public @Nullable String description;
        @RestForm("image")
        public @Nullable FileUpload image;
    }

}
