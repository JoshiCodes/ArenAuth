package de.joshicodes.aren.resource;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.security.oauth.OAuthAuthenticated;
import de.joshicodes.aren.service.UploadService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.flywaydb.core.internal.util.Pair;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/api/avatar/")
public class ImageResources {

    @Inject
    SecurityIdentity identity;

    @Inject
    UploadService uploadService;

    @GET
    @Path("/user/{id}")
    @Produces({"image/png", "image/jpeg", "image/*"})
    public Response getUserAvatar(@PathParam("id") String id) {
        try {
            final Pair<byte[], String> response = uploadService.getFile(
                    UploadService.UploadType.USER_AVATAR,
                    id
            );

            if(response == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(response.getLeft())
                    .type(response.getRight())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/project/{id}")
    @Produces({"image/png", "image/jpeg", "image/*"})
    public Response getProjectAvatar(@PathParam("id") String id) {
        try {
            final Pair<byte[], String> response = uploadService.getFile(
                    UploadService.UploadType.PROJECT_AVATAR,
                    id
            );

            if(response == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(response.getLeft())
                    .type(response.getRight())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/upload/user")
    @Authenticated
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadUserAvatar(@FormParam("file") FileUpload file) {
        try {
            if (file == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "No Image provided."))
                        .build();
            }

            try (InputStream inputStream = Files.newInputStream(file.filePath())) {
                String mimeType = file.contentType();

                UUID userId = identity.getAttribute("userId");
                final User user = User.findById(userId);

                if(user == null) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity(Map.of("error", "User not found"))
                            .build();
                }

                if(user.avatarId != null) {
                    final String prevAvatar = user.avatarId;
                    uploadService.deleteFile(UploadService.UploadType.USER_AVATAR, prevAvatar);
                }

                String fileName = uploadService.uploadFile(
                        UploadService.UploadType.USER_AVATAR,
                        inputStream,
                        mimeType,
                        file.fileName()
                );

                if(fileName == null) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(Map.of("error", "Failed to upload file."))
                            .build();
                }

                user.avatarId = fileName;
                user.avatarMimeType = mimeType;
                user.persist();

                return Response.ok()
                        .entity(new ImageResponseDTO(UploadService.UploadType.USER_AVATAR, fileName))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Upload failed."))
                    .build();
        }
    }

    @POST
    @Path("/upload/project/{id}")
    @Transactional
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProjectAvatar(@PathParam("id") String projectId, @FormParam("file") FileUpload file) {
        try {

            UUID userId = identity.getAttribute("userId");
            final User user = User.findById(userId);

            if(user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("error", "User not found"))
                        .build();
            }

            Project project;
            try {
                System.out.println(projectId);
                if(projectId == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(Map.of("error", "Invalid project ID."))
                            .build();
                }
                final UUID projectUUID = UUID.fromString(projectId);
                project = Project.findById(projectUUID);
            } catch (NumberFormatException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Invalid project ID."))
                        .build();
            }

            if(project == null || !project.owner.id.equals(userId)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            if (file == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "No Image provided."))
                        .build();
            }

            try (InputStream inputStream = Files.newInputStream(file.filePath())) {
                String mimeType = file.contentType();

                if(project.avatarId != null) {
                    final String prevAvatar = project.avatarId;
                    uploadService.deleteFile(UploadService.UploadType.PROJECT_AVATAR, prevAvatar);
                }

                String fileName = uploadService.uploadFile(
                        UploadService.UploadType.PROJECT_AVATAR,
                        inputStream,
                        mimeType,
                        file.fileName()
                );

                if(fileName == null) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(Map.of("error", "Failed to upload file."))
                            .build();
                }

                project.avatarId = fileName;
                project.avatarMimeType = mimeType;
                project.persist();

                return Response.ok()
                        .entity(new ImageResponseDTO(UploadService.UploadType.PROJECT_AVATAR, fileName))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Upload failed."))
                    .build();
        }
    }

    public static record ImageResponseDTO(UploadService.UploadType type, String avatarId) {

    }

}
