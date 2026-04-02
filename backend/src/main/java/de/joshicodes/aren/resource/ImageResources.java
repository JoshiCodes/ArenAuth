package de.joshicodes.aren.resource;

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
            final byte[] bytes = uploadService.getFile(
                    UploadService.UploadType.USER_AVATAR,
                    id
            );

            return Response.ok(bytes)
                    .type("image/png")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/project/{id}")
    @Produces({"image/png", "image/jpeg", "image/*"})
    public Response getProjectAvatar(@PathParam("id") String id) {
        return Response.ok().build();
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
                        .entity("{\"error\": \"Keine Datei hochgeladen\"}")
                        .build();
            }

            try (InputStream inputStream = Files.newInputStream(file.filePath())) {
                String mimeType = file.contentType();

                UUID userId = identity.getAttribute("userId");
                final User user = User.findById(userId);

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
                user.avatarId = fileName;
                user.persist();

                return Response.ok()
                        .entity(Map.of("avatarId", fileName))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Upload fehlgeschlagen\"}")
                    .build();
        }
    }

    @POST
    @Path("/upload/project/{id}")
    @Authenticated
    public Response uploadUserAvatar(@FormParam("id") String projectId) {
        return Response.ok().build();
    }

}
