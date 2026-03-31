package de.joshicodes.aren.service;

import de.joshicodes.aren.entities.KeyEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class KeyService {

    public KeyEntity getActiveKey() {
        return KeyEntity.find("active", true).firstResult();
    }

    public List<KeyEntity> getAllKeys() {
        return KeyEntity.findAll().list();
    }

    public void deleteOldKeys() {
        KeyEntity.delete("active = false AND disabledAt < ?1", Instant.now());
    }

    public Map<String, Object> toJwkMap(final KeyEntity keyEntity) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPublicKey publicKey = (RSAPublicKey) keyEntity.getPublicKey();
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

        byte[] modulusBytes = publicKey.getModulus().toByteArray();

        if (modulusBytes.length == 257 && modulusBytes[0] == 0) {
            byte[] strippedModulus = new byte[256];
            System.arraycopy(modulusBytes, 1, strippedModulus, 0, 256);
            modulusBytes = strippedModulus;
        }

        return Map.of(
                "kty", "RSA",
                "use", "sig",
                "kid", keyEntity.kid,
                "alg", "RS256",
                "n", encoder.encodeToString(modulusBytes),
                "e", encoder.encodeToString(publicKey.getPublicExponent().toByteArray())
        );
    }

    // Die Rotations-Logik
    @Transactional
    public void rotateKeys() throws NoSuchAlgorithmException {
        KeyPair newKeyPair = createNewKeyPair();
        String kid = "key-" + System.currentTimeMillis();

        // Disable old keys
        List<KeyEntity> keys = getAllKeys();
        for (KeyEntity key : keys) {
            if(key.active) {
                key.disable();
                key.persist();
            }
        }

        final KeyEntity newKey = KeyEntity.create(kid, newKeyPair);
        newKey.persist();

        deleteOldKeys();
    }

    private KeyPair createNewKeyPair() throws NoSuchAlgorithmException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

}
