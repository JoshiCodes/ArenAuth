package de.joshicodes.aren.security;

import de.joshicodes.aren.entities.User;
import io.quarkus.security.identity.SecurityIdentity;

import java.util.UUID;

public class UserExtractor {

    public static User getUser(final SecurityIdentity identity) {
        if(identity == null || identity.getPrincipal() == null || identity.getAttributes() == null) {
            return null;
        }
        final UUID userId;
        try {
            userId = identity.getAttribute("userId");
            if(userId == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return User.findById(userId);
    }

}
