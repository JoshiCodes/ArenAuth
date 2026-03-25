package de.joshicodes.aren.security;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record Session(String id, String username, UUID userId, Set<String> roles, Instant expiresAt) {
}
