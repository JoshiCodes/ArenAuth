package de.joshicodes.aren.oauth;

public enum Scopes {

    /**
     * OpenID Scope
     */
    OPEN_ID("openid"),
    /**
     * Username / Avatar
     */
    IDENTIFY("identify"),
    /**
     * Update Username / Avatar
     */
    DATA_WRITE("identity.write", true),
    /**
     * View all Applications the user has access to
     */
    APPLICATIONS_READ("applications.read"),
    EMAIL("email");

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
    private final boolean warn;

    Scopes(String id) {
        this(id, false);
    }

    Scopes(final String id, boolean warn) {
        this.id = id;
        this.warn = warn;
    }

    public String getId() {
        return id;
    }

    /**
     * If the user should be warned to accept this scope
     * @return true if the user should be warned.
     */
    public boolean doWarn() {
        return warn;
    }

}
