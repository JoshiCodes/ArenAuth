package de.joshicodes.aren.resource.oauth;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.dto.OAuthRequestDTO;
import de.joshicodes.aren.entities.oauth.OAuthRequest;
import de.joshicodes.aren.oauth.Scopes;
import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/oauth2")
public class PublicOAuthResources {

    @ConfigProperty(name = "aren.frontend.url")
    String frontendUrl;

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

        if(!req.isValid()) {
            req.delete();
            return Response.status(404).build();
        }

        final OAuthRequestDTO dto = OAuthRequestDTO.from(req);
        req.delete();

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
    public Response authorize(@QueryParam("client_id") String clientId, @QueryParam("redirect_uri") String redirectUri, @QueryParam("response_type") String responseType, @QueryParam("scope") String scope, @QueryParam("state") String state) {

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

}
