package de.joshicodes.aren.entities.oauth;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oauth_consent_records")
public class OAuthConsentRecord extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if(createdAt != null) return;
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(14);
        }
    }

    @Transient
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }

}
