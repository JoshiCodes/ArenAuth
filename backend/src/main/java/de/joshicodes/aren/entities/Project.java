package de.joshicodes.aren.entities;

import io.netty.handler.codec.base64.Base64Encoder;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
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

    @Column(nullable = true, name = "avatar_id")
    public String avatarId;
    @Column(nullable = true, name = "avatar_mime_type")
    public String avatarMimeType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "project_redirect_uris",
            joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "redirect_uri", nullable = false)
    public Set<String> redirectUris = new HashSet<>();

    @Column(name = "client_secret", nullable = false)
    public String clientSecret;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    public User owner;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String generateSecret() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        final String secret = UUID.randomUUID().toString().split("-")[0] + "_" + Base64.getUrlEncoder().encodeToString(randomBytes);
        this.clientSecret = BcryptUtil.bcryptHash(secret);
        return secret;
    }

    public boolean verifySecret(String clientSecret) {
        return BcryptUtil.matches(clientSecret, this.clientSecret);
    }

}
