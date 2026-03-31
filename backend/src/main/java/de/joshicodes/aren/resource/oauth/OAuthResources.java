package de.joshicodes.aren.resource.oauth;

import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.entities.oauth.OAuthAuthorizationCode;
import de.joshicodes.aren.entities.oauth.OAuthRequest;
import de.joshicodes.aren.security.UserExtractor;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;
import java.util.UUID;

@Path("/oauth2")
@Authenticated
public class OAuthResources {

    @ConfigProperty(name = "aren.frontend.url")
    String frontendUrl;

    @Inject
    SecurityIdentity securityIdentity;

    @POST
    @Transactional
    @Path("/authorize")
    public Response authorize(OAuthAuthorizeDTO dto) {
        final User user = UserExtractor.getUser(securityIdentity);
        if(user == null) {
            return Response.status(401).build();
        }

        final boolean approved = dto.approved();

        if(dto.req() == null) {
            return Response.status(404).entity(Map.of("error", "invalid_req_param")).build();
        }

        final UUID reqId;
        try {
            reqId = UUID.fromString(dto.req());
        } catch (IllegalArgumentException e) {
            return Response.status(404).entity(Map.of("error", "invalid_req_param")).build();
        }

        OAuthRequest req = OAuthRequest.findById(reqId);

        if(req == null || !req.isValid()) {
            if(req != null) req.delete();
            return Response.status(404).entity(Map.of("error", "invalid_req")).build();
        }

        OAuthAuthorizationCode code = OAuthAuthorizationCode.create(
                req.project,
                user,
                req.scope,
                req.redirectUri,
                req.state,
                req.nonce
        );

        final String state = req.state;
        final String redirectUri = req.redirectUri;

        req.delete();

        String finalUri;
        if(approved) {
            finalUri = redirectUri + "?code=" + code.code + (state != null ? "&state=" + state : "");
        } else {
            finalUri = redirectUri + "?error=access_denied" + (state != null ? "&state=" + state : "");
        }

        return Response.ok().entity(Map.of(
                "redirectUri", finalUri
        )).build();

    }

    public static record OAuthAuthorizeDTO( String req, boolean approved ) {}

}
