package de.joshicodes.aren.util;

import de.joshicodes.aren.entities.KeyEntity;
import de.joshicodes.aren.entities.oauth.OAuthToken;
import de.joshicodes.aren.service.KeyService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class OAuthUtil {

    @Inject
    JwtUtil jwtUtil;

    @Inject
    KeyService keyService;

    public String generateAccessToken(final OAuthToken token) throws NoSuchAlgorithmException, InvalidKeySpecException {

        final KeyEntity activeKey = keyService.getActiveKey();

        return jwtUtil.createBuilder()
                .subject(token.user.id.toString())
                .audience(token.project.id.toString())
                .issuedAt(Instant.now())
                .expiresAt(token.accessTokenExpiresAt)
                .claim("jti", UUID.randomUUID().toString())

                .claim("scope", token.scope)
                .claim("user_id", token.user.id.toString())

                .claim("username", token.user.username)

                .jws()
                .keyId(activeKey.kid)
                .header("type", "JWT")
                .sign(activeKey.getPrivateKey());
    }

}
