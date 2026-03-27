package de.joshicodes.aren.oauth;

public enum Scopes {

    /**
     * OpenID Scope
     */
    OPEN_ID("openid", "See who you are (via OpenID)"),
    /**
     * Username / Avatar
     */
    IDENTIFY("profile", "View your Username and Avatar"),
    /**
     * View all Applications the user has access to
     */
    APPLICATIONS_READ("applications.read", "View your applications"),
    EMAIL("email", "View your email address"),;

    // TODO:
    //  - Config Scope to read/write application-config
    //  - Global config scope to access all user configs
    //  - Connections (Google, Github, etc.),
    //    as soon as other providers are configured
    //  - Teams
    //    as soon as teams are implemented
    //  - Webhooks
    //    as soon as webhooks are implemented

    private final String id;
    private final String fallbackDescription;

    Scopes(final String id, final String fallbackDescription) {
        this.id = id;
        this.fallbackDescription = fallbackDescription;
    }

    public String getId() {
        return id;
    }

    public String getFallbackDescription() {
        return fallbackDescription;
    }

}
