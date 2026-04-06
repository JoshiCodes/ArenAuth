package de.joshicodes.aren;

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

    }

}
