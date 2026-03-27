package de.joshicodes.aren.entities.dto;

import de.joshicodes.aren.entities.Project;

import java.util.Set;
import java.util.UUID;

public record ProjectDTO(UUID id, String name, String description, String imageBlob, UUID ownerId,
                         Set<String> redirectUris) {

    public static ProjectDTO from(Project project) {
        return new ProjectDTO(
                project.id,
                project.name,
                project.description,
                project.imageBlob,
                project.owner != null ? project.owner.id : null,
                project.redirectUris != null ? Set.copyOf(project.redirectUris) : Set.of()
        );
    }

}
