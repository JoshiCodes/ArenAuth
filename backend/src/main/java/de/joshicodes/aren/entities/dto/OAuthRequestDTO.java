package de.joshicodes.aren.entities.dto;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.oauth.OAuthRequest;

import java.time.Instant;
import java.util.UUID;

public record OAuthRequestDTO(UUID id, String clientId, String redirectUri, String scope, String state, ProjectDTO project, Instant expires) {

    public static OAuthRequestDTO from(OAuthRequest req) {
        return new OAuthRequestDTO(
                req.id,
                req.clientId,
                req.redirectUri,
                req.scope,
                req.state,
                ProjectDTO.from(req.project),
                req.expiresAt
        );
    }

}
