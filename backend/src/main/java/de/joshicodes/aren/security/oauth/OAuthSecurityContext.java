package de.joshicodes.aren.security.oauth;

import de.joshicodes.aren.entities.User;
import de.joshicodes.aren.entities.oauth.OAuthToken;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class OAuthSecurityContext {

    private OAuthToken currentToken;

    public void setCurrentToken(OAuthToken token) {
        currentToken = token;
    }

    public OAuthToken getCurrentToken() {
        return currentToken;
    }

    public User getCurrentUser() {
        final OAuthToken token = getCurrentToken();
        if (token == null) return null;
        return token.user;
    }

    public String getCurrentScope() {
        final OAuthToken token = getCurrentToken();
        if (token == null) return null;
        return token.scope;
    }

    public boolean hasScope(final String scope) {
        final String currentScope = getCurrentScope();
        if (currentScope == null) return false;
        return currentScope.contains(scope);
    }

    public void clearCurrentScope() {
        currentToken  = null;
    }

}
