package de.joshicodes.aren.entities.dto;

import de.joshicodes.aren.entities.User;

import java.util.Set;
import java.util.UUID;

public record UserDTO(UUID id, String name, String avatarId, Set<String> roles) {

    public static UserDTO from(User user) {
        return new UserDTO(
                user.id,
                user.username,
                user.avatarId,
                user.roles
        );
    }

}
