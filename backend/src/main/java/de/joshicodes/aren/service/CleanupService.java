package de.joshicodes.aren.service;

import de.joshicodes.aren.entities.oauth.OAuthAuthorizationCode;
import de.joshicodes.aren.entities.oauth.OAuthRequest;
import de.joshicodes.aren.entities.oauth.OAuthToken;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.logging.Logger;

@ApplicationScoped
public class CleanupService {

    private static final Logger LOG = Logger.getLogger("CleanupService");

    @Scheduled(every = "1h")
    @Transactional
    public void cleanUpExpired() {
        cleanUpExpiredRequests();
        cleanUpAuthCodes();
        cleanUpTokens();
    }

    private void cleanUpExpiredRequests() {
        try {
            final long count = OAuthRequest.delete("expiresAt < ?1", Instant.now());
            LOG.info(String.format("Deleted %d expired requests", count));
        } catch (Exception e) {
            LOG.severe("Cleanup failed: " + e.getMessage());
        }
    }

    private void cleanUpAuthCodes() {
        try {
            final long count = OAuthAuthorizationCode.delete("expiresAt < ?1", Instant.now());
            LOG.info(String.format("Deleted %d expired auth codes", count));
        } catch (Exception e) {
            LOG.severe("Cleanup failed: " + e.getMessage());
        }
    }

    private void cleanUpTokens() {
        try {
            final long count = OAuthToken.delete("(accessTokenExpiresAt < ?1 AND refreshTokenExpiresAt < ?1) OR revoked IS TRUE", Instant.now());
            LOG.info(String.format("Deleted %d expired auth tokens", count));
        } catch (Exception e) {
            LOG.severe("Cleanup failed: " + e.getMessage());
        }
    }

}
