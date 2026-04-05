package de.joshicodes.aren.security;

import de.joshicodes.aren.entities.User;
import io.quarkus.security.identity.SecurityIdentity;

import java.util.UUID;

public class UserExtractor {

    public static User getUser(final SecurityIdentity identity) {
        if (identity == null || identity.getPrincipal() == null || identity.getAttributes() == null) {
            return null;
        }

        Object raw = identity.getAttribute("userId");
        System.out.println("Raw userId: " + raw);
        if (raw == null) return null;

        UUID userId;
        try {
            if (raw instanceof UUID u) {
                userId = u;
            } else {
                userId = UUID.fromString(String.valueOf(raw));
            }
        } catch (Exception e) {
            System.err.println("Failed to parse userId from SecurityIdentity: " + e.getMessage());
            return null;
        }

        return User.findById(userId);
    }

}
