package de.joshicodes.aren.resource;

import de.joshicodes.aren.entities.KeyEntity;
import de.joshicodes.aren.oauth.Scopes;
import de.joshicodes.aren.service.KeyService;
import io.vertx.ext.auth.impl.jose.JWK;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.logging.Logger;

@Path("/.well-known")
public class WellKnownResources {

    @ConfigProperty(name = "aren.jwt.issuer")
    String issuer;

    @Inject
    KeyService keyService;

    @GET
    @Path("/jwks.json")
    @Transactional
    public Response getJwks() {
        List<KeyEntity> keys = keyService.getAllKeys();
        List<Map<String, Object>> jwks = new ArrayList<>();
        for (KeyEntity key : keys) {
            try {
                jwks.add(keyService.toJwkMap(key));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                Logger.getLogger("WellKnownResources").severe("Failed to convert key to JWK: " + e.getMessage());
            }
        }
        return Response.ok(Map.of("keys", jwks)).build();
    }

    @GET
    @Path("/openid-configuration")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOpenidConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("issuer", issuer);
        config.put("authorization_endpoint", issuer + "/oauth2/authorize");
        config.put("token_endpoint", issuer + "/oauth2/token");
        config.put("jwks_uri", issuer + "/.well-known/jwks.json");
        config.put("userinfo_endpoint", issuer + "/api/user/me");
        config.put("response_types_supported", List.of("code", "token", "id_token"));
        config.put("subject_types_supported", List.of("public"));
        config.put("id_token_signing_alg_values_supported", List.of("RS256"));
        config.put("scopes_supported", List.of(Arrays.stream(Scopes.values()).map(Scopes::getId).toArray()));
        return Response.ok().entity(config).build();
    }

}
