package de.joshicodes.aren;

import de.joshicodes.aren.entities.User;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

public class Startup {

    private static final Logger LOG = Logger.getLogger("Startup");

    @Transactional
    public void loadUsers(@Observes StartupEvent evt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Prüfen ob Admin schon da ist (verhindert Duplicate Key Exception beim Neustart)
        if (User.find("username", "admin").firstResult() == null) {
            User.add("admin", "admin123", "admin", "admin@aren.local");
            LOG.info("Admin user created (Password: admin123)");
        }
    }

}
