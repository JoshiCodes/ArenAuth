package de.joshicodes.aren.entities.dto;

import de.joshicodes.aren.entities.Project;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ProjectDTO(UUID id, String name, String description, String avatarId, UUID ownerId,
                         Set<String> redirectUris, Instant createdAt) {

    public static ProjectDTO from(Project project) {
        return new ProjectDTO(
                project.id,
                project.name,
                project.description,
                project.avatarId,
                project.owner != null ? project.owner.id : null,
                project.redirectUris != null ? Set.copyOf(project.redirectUris) : Set.of(),
                project.createdAt
        );
    }

}
