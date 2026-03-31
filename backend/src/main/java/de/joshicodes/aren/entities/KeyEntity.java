package de.joshicodes.aren.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

@Entity
@Table(name = "oidc_keys")
public class KeyEntity extends PanacheEntityBase {

    @Id
    @Column(length = 50)
    public String kid;

    // Public Key in X.509 Format (Base64)
    @Lob
    @Column(nullable = false)
    public String publicKeyPem;

    // Private key in PKCS8 Format (Base64)
    @Lob
    @Column(nullable = false)
    public String privateKeyPem;

    @Column(nullable = false)
    public Instant createdAt;

    // Is active to use for new generation?
    @Column(nullable = false)
    public boolean active;
    @Column(nullable = true)
    public Instant disabledAt;

    public KeyEntity() {}

    public static KeyEntity create(final String kid, final String privateKeyPem, final String publicKeyPem) {
        final KeyEntity keyEntity = new KeyEntity();
        keyEntity.kid = kid;
        keyEntity.publicKeyPem = publicKeyPem;
        keyEntity.privateKeyPem = privateKeyPem;
        keyEntity.createdAt = Instant.now();
        keyEntity.active = true;
        return keyEntity;
    }

    public void disable() {
        this.active = false;
        this.disabledAt = Instant.now();
    }

    public static KeyEntity create(final String kid, final KeyPair keyPair) {
        return create(
                kid,
                Base64.getUrlEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded())
        );
    }

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decoded = Base64.getUrlDecoder().decode(cleanPem(privateKeyPem));
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decoded = Base64.getUrlDecoder().decode(cleanPem(publicKeyPem));
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
    }

    private String cleanPem(String pem) {
        return pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
    }

}
