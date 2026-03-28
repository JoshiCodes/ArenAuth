package de.joshicodes.aren.util;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class JwtUtil {

    @ConfigProperty(name = "aren.jwt.issuer")
    String issuer;

    public JwtClaimsBuilder createBuilder() {
        return Jwt.issuer(issuer);
    }

}
