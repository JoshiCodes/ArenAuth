package de.joshicodes.aren.security;

import de.joshicodes.aren.entities.User;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionService {

    /*
     * TODO:
     *      - move from in-memory to DB. (maybe)
     */

    private static SecureRandom RANDOM;
    public static final Duration TTL = Duration.ofHours(8);

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        RANDOM = new SecureRandom();
    }

    public Session createSession(final User user) {
        final String id = newId();
        final Session s = new Session(id, user.username, user.id, user.roles, Instant.now().plus(TTL));
        sessions.put(id, s);
        return s;
    }

    public Optional<Session> getSession(final String id) {
        if(id == null || id.isBlank()) return Optional.empty();
        final Session s = sessions.get(id);
        if(s == null) return Optional.empty();
        if(Instant.now().isAfter(s.expiresAt())) {
            // Invalid
            sessions.remove(id);
            return Optional.empty();
        }
        return Optional.of(s);
    }

    public void invalidate(final String id) {
        if(id == null || id.isBlank()) return;
        sessions.remove(id);
    }

    private static String newId() {
        byte[] b = new byte[32];
        RANDOM.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

}
