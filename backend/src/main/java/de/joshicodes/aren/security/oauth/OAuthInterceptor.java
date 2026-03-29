package de.joshicodes.aren.security.oauth;

import de.joshicodes.aren.entities.oauth.OAuthToken;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@OAuthAuthenticated
@Interceptor
public class OAuthInterceptor {

    @Context
    HttpHeaders httpHeaders;

    @Inject
    OAuthSecurityContext securityContext;

    @AroundInvoke
    public Object authenticate(InvocationContext context) throws Exception {
        try {
            String authHeader = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);

            if(authHeader == null || authHeader.isEmpty()) {
                return unauthorized("invalid_request", "Missing Authorization header");
            }

            final String accessToken  = authHeader.substring(authHeader.indexOf(' ') + 1);

            final OAuthToken token = OAuthToken.find("accessToken", accessToken).firstResult();

            if(token == null) {
                return unauthorized("invalid_request", "Invalid access token");
            }

            if(!token.isAccessTokenValid()) {
                return unauthorized("invalid_request", "Invalid access token");
            }

            OAuthAuthenticated annotation = context.getMethod()
                    .getAnnotation(OAuthAuthenticated.class);

            if(annotation == null) {
                // get from class instead
                annotation = context.getMethod().getDeclaringClass()
                        .getAnnotation(OAuthAuthenticated.class);
            }

            if(annotation != null && annotation.scopes().length > 0) {
                for(String required : annotation.scopes()) {
                    if(!token.scope.contains(required)) {
                        return forbidden("insufficient_scope", "Required scope: " + required);
                    }
                }
            }

            securityContext.setCurrentToken(token);

            try {
                return context.proceed();
            } finally {
                securityContext.clearCurrentScope();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    private Response unauthorized(String error, String description) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of(
                        "error", error,
                        "message", description
                ))
                .build();
    }

    private Response forbidden(String error, String description) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(Map.of(
                        "error", error,
                        "error_description", description
                ))
                .build();
    }

}
