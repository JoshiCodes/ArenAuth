package de.joshicodes.aren.entities.oauth;

import de.joshicodes.aren.entities.Project;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

/**
 * Used to store a requested oauth flow
 * Generated when /authorize is called and redirect to frontend
 * Frontend gets client with this object.
 * Get removed once frontend accepts.
 */
@Entity
@Table(name = "oauth_requests")
public class OAuthRequest extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "client_id", nullable = false)
    public String clientId;

    @Column(name = "redirect_uri", nullable = false)
    public String redirectUri;

    @Column(name = "scope", nullable = false)
    public String scope;

    @Column(nullable = true)
    public String state;
    @Column(nullable = true)
    public String nonce;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    @Column(nullable = false)
    public Instant createdAt;
    @Column(nullable = false)
    public Instant expiresAt;

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
