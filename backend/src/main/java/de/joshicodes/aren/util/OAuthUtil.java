package de.joshicodes.aren.util;

import de.joshicodes.aren.entities.oauth.OAuthToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class OAuthUtil {

    @Inject
    JwtUtil jwtUtil;

    public String generateAccessToken(final OAuthToken token) {
        return jwtUtil.createBuilder()
                .subject(token.user.id.toString())
                .audience(token.project.id.toString())
                .issuedAt(Instant.now())
                .expiresAt(token.accessTokenExpiresAt)
                .claim("jti", UUID.randomUUID().toString())

                .claim("scope", token.scope)
                .claim("user_id", token.user.id.toString())

                .claim("username", token.user.username)

                .sign();
    }

}
