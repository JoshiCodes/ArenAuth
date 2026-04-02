package de.joshicodes.aren.resource.oauth;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.dto.OAuthRequestDTO;
import de.joshicodes.aren.entities.oauth.OAuthAuthorizationCode;
import de.joshicodes.aren.entities.oauth.OAuthRequest;
import de.joshicodes.aren.entities.oauth.OAuthToken;
import de.joshicodes.aren.entities.oauth.OAuthTokenService;
import de.joshicodes.aren.oauth.Scopes;
import de.joshicodes.aren.oauth.openid.OpenIdManager;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestHeader;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/oauth2")
public class PublicOAuthResources {

    @ConfigProperty(name = "aren.frontend.url")
    String frontendUrl;

    @Inject
    OAuthTokenService tokenService;

    @Inject
    OpenIdManager openIdManager;

    @GET
    @Path("/scopes")
    public Response getScopes() {
        return Response.ok(
                Arrays.stream(Scopes.values())
                        .map(scope -> Map.of("name", scope.getId(), "description", scope.getFallbackDescription()))
                        .collect(Collectors.toList())
        ).build();
    }

    @GET
    @Transactional
    @Path("/consent")
    public Response getConsentRequest(@QueryParam("req") String request) {

        if(request == null || request.isEmpty()) {
            return Response.status(404).entity(Map.of("error", "missing_req_param")).build();
        }

        final UUID reqId;
        try {
            reqId = UUID.fromString(request);
        } catch (IllegalArgumentException e) {
            return Response.status(404).entity(Map.of("error", "invalid_req_param")).build();
        }

        OAuthRequest req = OAuthRequest.findById(reqId);

        if(req == null || !req.isValid()) {
            if(req != null) req.delete();
            return Response.status(404).build();
        }

        final OAuthRequestDTO dto = OAuthRequestDTO.from(req);

        return Response.ok(dto).build();

    }

    /**
     * The default endpoint redirected to by the client.
     * Checks for GET/QUERY params:
     *  -
     * Creates a data object,
     * redirects to frontend with /consent?req=[data obj. id]
     * @return a redirect response
     */
    @GET
    @Transactional
    @Path("/authorize")
    public Response authorize(
            @QueryParam("client_id") String clientId,
            @QueryParam("redirect_uri") String redirectUri,
            @QueryParam("response_type") String responseType,
            @QueryParam("scope") String scope,
            @QueryParam("state") String state,
            @QueryParam("nonce") String nonce
    ) {

        if(clientId == null || clientId.isEmpty() || redirectUri == null || redirectUri.isEmpty() || responseType == null || responseType.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "missing_arguments"
            )).build();
        }

        // Response Type: NUR code erlauben
        if (!responseType.equals("code")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "invalid_response_type"
            )).build();
        }

        final UUID projectId;
        try {
            projectId = UUID.fromString(clientId);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "invalid_client_id"
            )).build();
        }
        final Project project = Project.findById(projectId);
        if (project == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "invalid_client_id"
            )).build();
        }

        if(!project.redirectUris.contains(redirectUri)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of(
                    "error", "invalid_redirect_uri"
            )).build();
        }

        OAuthRequest request = new OAuthRequest();
        request.clientId = clientId;
        request.redirectUri = redirectUri;
        request.scope = scope;
        request.project = project;

        request.state = state;
        request.nonce = nonce;

        request.persist();

        try {
            return Response.seeOther(
                    new URI(frontendUrl + "/authorize?req=" + request.id)
            ).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(
            @FormParam("grant_type") String grantType,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri,
            @FormParam("refresh_token") String refreshToken,
            @RestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {

        if(authHeader == null || !authHeader.startsWith("Basic ")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "missing_or_invalid_auth_header")).build();
        }

        final String[] credentials = new String(Base64.getDecoder().decode(authHeader.substring(6))).split(":", 2);
        final String clientId = URLDecoder.decode(credentials[0], StandardCharsets.UTF_8);
        final String clientSecret = URLDecoder.decode(credentials[1], StandardCharsets.UTF_8);

        if(clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "missing_client_credentials")).build();
        }

        final UUID projectId;
        try {
            projectId = UUID.fromString(clientId);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "invalid_client_id")).build();
        }

        final Project project = Project.findById(projectId);

        System.out.println(clientSecret);
        if(project == null || !project.verifySecret(clientSecret)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "invalid_client_secret")).build();
        }

        if(grantType == null || grantType.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "invalid_grant_type")).build();
        }

        if(grantType.equals("authorization_code")) {
            /*
             * Auth Code needs:
             * - code
             * - redirect_uri
             * - client_secret OR code_verifier (PKCS)
             */
            return handleAuthCodeGrant(code, redirectUri);
        } else if(grantType.equals("refresh_token")) {
            /**
             * Refresh Token needs:
             * - grant_type : refresh_token
             * - refresh_token
             */
            return handeRefreshGrant(refreshToken, project);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "unsupported_grant_type")).build();
        }

    }

    @Transactional
    public Response handeRefreshGrant(String refreshToken, Project project) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "missing_refresh_token")).build();
        }

        final OAuthToken token = OAuthToken.find("refreshToken", refreshToken).firstResult();

        if(token == null || !token.isRefreshTokenValid() || !token.project.id.equals(project.id)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "invalid_refresh_token")).build();
        }

        final OAuthToken newToken = tokenService.create(token);
        token.delete();

        return Response.ok(Map.of(
                "access_token", newToken.accessToken,
                "refresh_token", newToken.refreshToken,
                "token_type", "bearer",
                "expires_in", newToken.accessTokenExpiresAt.getEpochSecond() - System.currentTimeMillis() / 1000,
                "scope", newToken.scope
        )).build();
    }

    @Transactional
    public Response handleAuthCodeGrant(final String code, final String redirectUri) {

        if(code == null || code.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "invalid_code")).build();
        }

        final OAuthAuthorizationCode authCode = OAuthAuthorizationCode.find("code", code).firstResult();

        if(authCode == null || !authCode.isValid() || !authCode.redirectUri.equals(redirectUri)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "invalid_code")).build();
        }

        final String nonce = authCode.nonce;

        final OAuthToken token = tokenService.create(authCode);
        authCode.delete();

        HashMap<String, Object> map = new HashMap<>(Map.of(
                "access_token", token.accessToken,
                "refresh_token", token.refreshToken,
                "token_type", "bearer",
                "expires_in", token.accessTokenExpiresAt.getEpochSecond() - System.currentTimeMillis() / 1000,
                "scope", token.scope
        ));

        if(token.hasScope(Scopes.OPEN_ID)) {
            final String openIdToken;
            try {
                openIdToken = openIdManager.buildOpenIdToken(token, nonce);
                map.put("id_token", openIdToken);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                Logger.getLogger("PublicOAuthResources").severe("Failed to build OpenID token: " + e.getMessage());
            }
        }

        return Response.ok(map).build();

    }

}
