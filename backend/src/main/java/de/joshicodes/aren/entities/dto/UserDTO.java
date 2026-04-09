package de.joshicodes.aren.entities.dto;

import de.joshicodes.aren.entities.User;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RegisterForReflection
public record UserDTO(UUID id, String name, String email, String avatarId, Set<String> roles, List<String> projects) {

    public static UserDTO from(User user, boolean includeEmail) {
        return new UserDTO(
                user.id,
                user.username,
                includeEmail ? user.email : null,
                user.avatarId,
                user.roles,
                user.ownedProjects.stream().map(p -> p.id.toString()).toList()
        );
    }

    public static UserDTO from(User user) {
        return from(user, false);
    }

}
