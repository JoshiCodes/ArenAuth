package de.joshicodes.aren.entities.oauth;

import de.joshicodes.aren.entities.Project;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "oauth_requests")
public class OAuthRequest extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "client_id", nullable = false)
    String clientId;

    @Column(name = "redirect_uri", nullable = false)
    String redirectUri;

    @Column(name = "scope", nullable = false)
    String scope;

    @Column(nullable = true)
    String state;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @Column(nullable = false)
    Instant createdAt;
    @Column(nullable = false)
    Instant expiresAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (expiresAt == null) {
            expiresAt = Instant.now().plusSeconds(600); // 10 minutes TTL
        }
    }

    public String[] getScopes() {
        return scope.split(" ");
    }

    public boolean hasScope(final String scope) {
        return Arrays.asList(getScopes()).contains(scope);
    }

    public boolean isValid() {
        return expiresAt != null && Instant.now().isBefore(expiresAt);
    }

}
