package de.joshicodes.aren;

import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.service.KeyService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Logger;

public class Startup {

    private static final Logger LOG = Logger.getLogger("Startup");

    @Inject
    KeyService keyService;

    @Transactional
    public void loadUsers(@Observes StartupEvent evt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Prüfen ob Admin schon da ist (verhindert Duplicate Key Exception beim Neustart)
        if (User.find("username", "admin").firstResult() == null) {
            User.add("admin", "admin123", "admin", "admin@aren.local");
            LOG.info("Admin user created (Password: admin123)");
        }
        PublicKey publicKey = keyService.getActiveKey().getPublicKey();

        // 2. Standard Base64 mit Zeilenumbrüchen (MIME)
        Base64.Encoder encoder = Base64.getMimeEncoder(64, new byte[]{'\n'});
        String rawBase64 = encoder.encodeToString(publicKey.getEncoded());

        System.out.println("-----BEGIN PUBLIC KEY-----");
        System.out.println(rawBase64);
        System.out.println("-----END PUBLIC KEY-----");
    }

}
