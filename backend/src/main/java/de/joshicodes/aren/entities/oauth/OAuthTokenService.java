package de.joshicodes.aren.entities.oauth;

import de.joshicodes.aren.util.OAuthUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class OAuthTokenService {

    @Inject
    OAuthUtil oAuthUtil;

    @Transactional
    public OAuthToken create(final OAuthAuthorizationCode code) {
        OAuthToken token = new OAuthToken();
        token.project = code.project;
        token.user = code.user;
        token.createdAt = Instant.now();
        token.scope = code.scope;
        token.accessTokenExpiresAt = Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(1)); // 1 hour
        token.refreshTokenExpiresAt = Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30)); // 30 days
        token.accessToken = oAuthUtil.generateAccessToken(token);
        token.refreshToken = UUID.randomUUID().toString();
        token.persist();
        return token;
    }

    @Transactional
    public OAuthToken create(final OAuthToken oldToken) {
        OAuthToken token = new OAuthToken();
        token.project = oldToken.project;
        token.user = oldToken.user;
        token.createdAt = Instant.now();
        token.scope = oldToken.scope;
        token.accessTokenExpiresAt = Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(1)); // 1 hour
        token.refreshTokenExpiresAt = Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30)); // 30 days
        token.accessToken = oAuthUtil.generateAccessToken(token);
        token.refreshToken = UUID.randomUUID().toString();
        token.persist();
        return token;
    }

}
