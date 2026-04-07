package de.joshicodes.aren;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

public class Startup {

    private static final Logger LOG = Logger.getLogger("Startup");

    @ConfigProperty(name = "aren.signup.disabled", defaultValue = "false")
    boolean signUpDisabled;

    @Transactional
    public void startupInfos(@Observes StartupEvent evt) {
        if(signUpDisabled) {
            LOG.warning("Signup is disabled. Users won't be able to create new accounts. If you don't have an admin account already, it is not possible to create one while DISABLE_SIGNUP is true.");
        }
    }

}
