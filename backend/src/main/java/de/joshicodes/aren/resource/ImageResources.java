package de.joshicodes.aren.resource;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.service.UploadService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;
import org.flywaydb.core.internal.util.Pair;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Path("/api/v1/avatar/")
public class ImageResources {

    private static final Logger LOG = LoggerFactory.getLogger(ImageResources.class);

    public static final int DEFAULT_SIZE = 512;

    @Inject
    SecurityIdentity identity;

    @Inject
    UploadService uploadService;

    @GET
    @Path("/user/{id}")
    @Produces({"image/png", "image/jpeg", "image/*"})
    public Response getUserAvatar(@PathParam("id") String id, @QueryParam("size") Integer size) {

        size = toValidSize(size);

        try {
            final Pair<byte[], String> response = uploadService.getFile(
                    UploadService.UploadType.USER_AVATAR,
                    id,
                    size
            );

            if(response == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(response.getLeft())
                    .type(response.getRight())
                    .cacheControl(RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class).fromString("max-age=3600, public"))
                    .build();
        } catch (IOException e) {
            LOG.error("Failure while fetching avatar! ", e);
            throw new RuntimeException("Failed to get avatar");
        }
    }

    @GET
    @Path("/project/{id}")
    @Produces({"image/png", "image/jpeg", "image/*"})
    public Response getProjectAvatar(@PathParam("id") String id, @QueryParam("size") Integer size) {
        size = toValidSize(size);
        try {
            final Pair<byte[], String> response = uploadService.getFile(
                    UploadService.UploadType.PROJECT_AVATAR,
                    id,
                    size
            );

            if(response == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(response.getLeft())
                    .type(response.getRight())
                    .cacheControl(RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class).fromString("max-age=3600, public"))
                    .build();
        } catch (IOException e) {
            LOG.error("Failure while fetching avatar! ", e);
            throw new RuntimeException("Failed to get avatar");
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

    private int toValidSize(final Integer size) {
        if(size == null) return DEFAULT_SIZE;
        return switch(size) {
            case 64, 128, 256, 512, 1024 -> size;
            default -> DEFAULT_SIZE;
        };
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
            System.err.println(e.getMessage());
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Upload failed."))
                    .build();
        }
    }

    @RegisterForReflection
    public static record ImageResponseDTO(UploadService.UploadType type, String avatarId) {

    }

}
