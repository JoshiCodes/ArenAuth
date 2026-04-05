package de.joshicodes.aren.security;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.Principal;

@ApplicationScoped
@Priority(10)
public class SessionCookieAuthMechanism implements HttpAuthenticationMechanism {

    public static final String COOKIE_NAME = "auth_session";

    @Inject SessionService sessionService;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        final Cookie cookie = context.request().getCookie(COOKIE_NAME);
        System.out.println("Auth Cookie: " + cookie);
        if(cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            return Uni.createFrom().nullItem();
        }
        System.out.println(cookie.getName() + " = " + cookie.getValue());

        return Uni.createFrom().optional(() -> sessionService.getSession(cookie.getValue()))
                .onItem().ifNotNull().transform(session -> {
                    return QuarkusSecurityIdentity.builder()
                            .setPrincipal((Principal) session::username)
                            .addAttribute("userId", session.userId())
                            .addAttribute("sessionId", session.id())
                            .addRoles(session.roles())
                            .build();
                });
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().item(new ChallengeData(401, null, null));
    }

}
