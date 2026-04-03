package de.joshicodes.aren.resource.internal;

import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.security.Session;
import de.joshicodes.aren.security.SessionService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@Path("/api/v1/internal/auth")
public class AuthResource {

    @ConfigProperty(name = "aren.jwt.issuer")
    String issuer;

    @Inject
    SessionService sessionService;

    public record LoginRequest(String username, String password) { }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest req) {

        if(req == null || req.username == null || req.password == null) {
            return Response.status(400).entity(Map.of("error", "username and password must be set.")).build();
        }

        User user = User.find("username", req.username).firstResult();

        if(user == null || !BcryptUtil.matches(req.password, user.password)) {
            return Response.status(401).entity(Map.of("error", "Invalid Username or Password.")).build();
        }

        final Session session = sessionService.createSession(user);

        final NewCookie authCookie = new NewCookie.Builder("auth_session")
                .value(session.id())
                .path("/")
                .httpOnly(true)
                .secure(false) // TODO: Read from environment variable for production! In prod -> true
                .maxAge((int) SessionService.TTL.getSeconds())
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        return Response.ok().entity(
                Map.of(
                        "success", true
                )
        )
                .cookie(authCookie)
                .build();

    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpHeaders headers) {

        Cookie c = headers.getCookies().get("auth_session");
        if (c != null) {
            sessionService.invalidate(c.getValue());
        }

        final NewCookie authCookie = new NewCookie.Builder("auth_session")
                .value("")
                .path("/")
                .httpOnly(true)
                .secure(false) // TODO: Read from environment variable for production! In prod -> true
                .maxAge(0)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
        return Response.ok().cookie(authCookie).build();
    }

}
