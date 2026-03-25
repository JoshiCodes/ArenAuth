package de.joshicodes.aren.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project extends PanacheEntityBase {

    public static final int MIN_NAME_LENGTH = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column
    public String name;

    @Column
    public String description;

    @Column
    public String imageBlob;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "project_redirect_uris",
            joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "redirect_uri", nullable = false)
    public Set<String> redirectUris = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    public User owner;

}
