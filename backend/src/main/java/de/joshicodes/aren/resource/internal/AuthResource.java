package de.joshicodes.aren.resource.internal;

import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.oauth.Role;
import de.joshicodes.aren.security.Session;
import de.joshicodes.aren.security.SessionService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Locale;
import java.util.Map;

import static de.joshicodes.aren.security.SessionCookieAuthMechanism.COOKIE_NAME;

@Path("/api/v1/internal/auth")
public class AuthResource {

    @ConfigProperty(name = "aren.jwt.issuer")
    String issuer;

    @ConfigProperty(name = "aren.use_https", defaultValue = "false")
    boolean isSecure;

    @ConfigProperty(name = "aren.cookie-domain")
    String cookieDomain;

    @Inject
    SessionService sessionService;

    public record LoginRequest(String username, String password) { }
    public record RegisterRequest(String username, String email, String password, String passwordConfirm) {}

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest req) {

        if(req == null || req.username == null || req.password == null) {
            return Response.status(400).entity(Map.of("error", "username and password must be set.")).build();
        }

        User user = User.find("username", req.username).firstResult();

        return login(user, req.password);

    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpHeaders headers) {

        Cookie c = headers.getCookies().get(COOKIE_NAME);
        if (c != null) {
            sessionService.invalidate(c.getValue());
        }

        final NewCookie authCookie = new NewCookie.Builder(COOKIE_NAME)
                .value("")
                .path("/")
                .domain(cookieDomain)
                .httpOnly(true)
                .secure(isSecure)
                .maxAge(0)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
        return Response.ok().cookie(authCookie).build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(RegisterRequest dto) {

        final String username = dto.username();
        final String email = dto.email();
        final String password = dto.password();
        final String passwordConfirm = dto.passwordConfirm();

        if(username == null || email == null || password == null || passwordConfirm == null ||
        username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            return Response.status(400).entity(Map.of("error", "All fields must be set.")).build();
        }

        if(!password.equals(passwordConfirm)) {
            return Response.status(400).entity(Map.of("error", "Passwords do not match.")).build();
        }

        if(User.find("username", username).stream().findAny().isPresent()) {
            return Response.status(400).entity(Map.of("error", "Username is already taken.")).build();
        }

        if(User.find("email", email).stream().findAny().isPresent()) {
            return Response.status(400).entity(Map.of("error", "E-Mail is already in use.")).build();
        }

        Role role = Role.USER;
        if(User.count() == 0) role = Role.ADMIN;

        User user;
        try {
            user = User.add(
                    username,
                    password,
                    role.name().toLowerCase(Locale.ROOT),
                    email
            );
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return Response.status(500).entity(Map.of("error", "An error occurred while creating the user.")).build();
        }

        return login(user, password);

    }

    private Response login(User user, final String password) {

        if(user == null || !BcryptUtil.matches(password, user.password)) {
            return Response.status(401).entity(Map.of("error", "Invalid Username or Password.")).build();
        }

        final Session session = sessionService.createSession(user);

        final NewCookie authCookie = new NewCookie.Builder(COOKIE_NAME)
                .value(session.id())
                .path("/")
                .domain(cookieDomain)
                .httpOnly(true)
                .secure(isSecure)
                .maxAge((int) SessionService.TTL.getSeconds())
                .sameSite(isSecure ? NewCookie.SameSite.NONE : NewCookie.SameSite.LAX)
                .build();

        return Response.ok().entity(
                        Map.of(
                                "success", true
                        )
                )
                .cookie(authCookie)
                .build();
    }


}
