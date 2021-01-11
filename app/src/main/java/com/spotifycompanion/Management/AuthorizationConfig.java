package com.spotifycompanion.Management;

import android.net.Uri;

public class AuthorizationConfig {
    /**
     * Die ID der Application von Spotify gegeben
     */
    public final String CLIENT_ID = "4234dd4558284817abdb7c7ecc4d7df7";

    /**
     * Der URI, zu dem umgeleitet werden soll, nachdem der Benutzer die Genehmigung erteilt oder verweigert hat.
     * <p>
     * Dieser URI muss in der Whitelist "Redirect URI" eingetragen worden sein,
     * die Sie bei der Registrierung Ihrer Anwendung angegeben haben.
     * Der Wert von redirect_uri muss hier genau mit einem der Werte übereinstimmen,
     * die Sie bei der Registrierung Ihrer Anwendung eingegeben haben,
     * einschließlich Groß- oder Kleinschreibung, abschließende Schrägstriche usw.
     * </p>
     */
    public final String REDIRECT_URI = "spotifyCompanion://authCall";
    /**
     * Die Art der Response der API
     */
    private String response_type = "code";
    /**
     * Optional, but strongly recommended.
     * <p>This provides protection against attacks such as cross-site request forgery. See RFC-6749.</p>
     */
    private String state;
    /**
     * Optional.
     * <p>
     * A space-separated list of scopes.If no scopes are specified,
     * authorization will be granted only to access publicly available information: that is,
     * only information normally visible in the Spotify desktop, web, and mobile players.
     * </p>
     * See https://developer.spotify.com/documentation/general/guides/scopes/
     */
    private String scope;
    /**
     * Optional.
     * <p>
     * Whether or not to force the user to approve the app again if they’ve already done so.
     * If false (default), a user who has already approved the application may be automatically redirected to the URI specified by redirect_uri.
     * If true, the user will not be automatically redirected and will have to approve the app again.
     * </p>
     */
    private Boolean show_dialog = false;

    public final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public final int AUTH_CODE_REQUEST_CODE = 0x11;

    public AuthorizationConfig() {
        this.scope = "playlist-modify-private user-read-recently-played user-read-playback-state user-read-currently-playing user-read-email";
        this.show_dialog = false;
    }

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public String getRedirectUriString() {
        return this.REDIRECT_URI;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String[] getScopesArray(){
        return this.scope.split(" ");
    }

    public Boolean getShow_dialog() {
        return show_dialog;
    }

    public void setShow_dialog(Boolean show_dialog) {
        this.show_dialog = show_dialog;
    }
}