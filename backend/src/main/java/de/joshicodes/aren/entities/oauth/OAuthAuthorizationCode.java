package de.joshicodes.aren.entities.oauth;

import de.joshicodes.aren.entities.Project;
import de.joshicodes.aren.entities.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Used to store the one-time use of an auth code, generated once the frontend accepts
 * This code gets sent to the oauth client and is used to get an access/refresh/oicd token
 */
@Entity
@Table(name = "oauth_authorization_codes")
public class OAuthAuthorizationCode extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @JoinColumn(name = "project_id", nullable = false)
    @ManyToOne (optional = false, fetch = FetchType.LAZY)
    public Project project;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne (optional = false, fetch = FetchType.LAZY)
    public User user;

    @Column(nullable = false)
    public String code;
    @Column(nullable = false)
    public String scope;
    @Column(nullable = false)
    public String redirectUri;
    @Column(nullable = true)
    public String state;

    @Column(nullable = false, name = "created_at")
    public Instant createdAt;
    @Column(nullable = false, name = "expires_at")
    public Instant expiresAt;

    public static OAuthAuthorizationCode create(final Project project, final User user, final String scope, final String redirectUri, final String state) {
        final OAuthAuthorizationCode code = new OAuthAuthorizationCode();
        code.project = project;
        code.user = user;
        code.code = UUID.randomUUID().toString().replaceAll("-", "");
        code.scope = scope;
        code.redirectUri = redirectUri;
        code.state = state;
        code.createdAt = Instant.now();
        code.expiresAt = code.createdAt.plusSeconds(300); // expires in 5 minutes
        code.persist();
        return code;
    }

    public boolean isValid() {
        return !expiresAt.isBefore(Instant.now());
    }

}
