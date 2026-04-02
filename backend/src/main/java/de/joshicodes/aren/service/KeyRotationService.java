package de.joshicodes.aren.service;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@ApplicationScoped
public class KeyRotationService {

    private static final Logger LOG = Logger.getLogger("KeyRotationService");

    @Inject
    KeyService keyService;

    @Transactional
    public void generateKey(@Observes StartupEvent evt) {
        if(keyService.getActiveKey() == null) {
            LOG.info("Generating initial key.");
            try {
                keyService.rotateKeys();
            } catch (NoSuchAlgorithmException e) {
                LOG.severe("Key Rotation failed:" + e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 3 ? * SUN")
    @Transactional
    public void cleanUpExpired() {
        LOG.info("Starting key rotation...");
        try {
            keyService.rotateKeys();
        } catch (NoSuchAlgorithmException e) {
            LOG.severe("Key Rotation failed:" + e.getMessage());
        }
    }

}
