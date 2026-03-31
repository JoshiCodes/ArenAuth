package de.joshicodes.aren.oauth.openid;

import de.joshicodes.aren.entities.KeyEntity;
import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.entities.oauth.OAuthToken;
import de.joshicodes.aren.service.KeyService;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

@ApplicationScoped
public class OpenIdManager {

    @ConfigProperty(name = "aren.jwt.issuer")
    String issuer;

    @Inject
    KeyService keyService;

    public String buildOpenIdToken(final OAuthToken token, final String nonce) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final User user = token.user;
        JwtClaimsBuilder builder = Jwt.issuer(issuer)
                .claim("email", user.email)
                .claim("name", user.username)
                .issuedAt(token.createdAt)
                .expiresAt(token.accessTokenExpiresAt)
                .audience(token.project.id.toString());
        if(nonce != null)
                builder = builder.claim("nonce", nonce);

        final KeyEntity activeKey = keyService.getActiveKey();

        return builder.jws()
                .keyId(activeKey.kid)
                .header("type", "JWT")
                .sign(activeKey.getPrivateKey());
    }

}
