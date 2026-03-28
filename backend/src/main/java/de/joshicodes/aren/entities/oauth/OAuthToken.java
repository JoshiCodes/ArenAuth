package de.joshicodes.aren.entities.oauth;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.util.OAuthUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "oauth_tokens")
public class OAuthToken extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JoinColumn(name = "project_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public Project project;

    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public User user;

    @Column(name = "scope")
    public String scope;

    @Column(name = "created_at")
    public Instant createdAt;

    @Column(name = "access_token", length = 2048)
    public String accessToken;

    @Column(name = "refresh_token", length = 2048)
    public String refreshToken;

    @Column(name = "access_token_expires_at")
    public Instant accessTokenExpiresAt;
    @Column(name = "refresh_token_expires_at")
    public Instant refreshTokenExpiresAt;

    @Column(name = "is_revoked")
    public boolean revoked;

    public Set<String> getScopes() {
        return Set.of(scope.split(" "));
    }

    public boolean isAccessTokenValid() {
        return !revoked && accessTokenExpiresAt.isBefore(Instant.now());
    }

    public boolean isRefreshTokenValid() {
        return !revoked && refreshTokenExpiresAt.isBefore(Instant.now());
    }

}
