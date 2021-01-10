package com.spotifycompanion.Management;

public class RESTHandler {

    /**
     * Die ID der Application von Spotify gegeben
     */
    private String client_id;

    /**
     * Die Art der Response der API
     */
    private String response_type = "code";
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
    private String redirect_url;
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


    private Boolean isAuthorized = false;
    private String accessToken = "";
    private String refreshToken = "";


    public RESTHandler(String client_id, String redirect_url) {
        this.client_id = client_id;
        this.redirect_url = redirect_url;
        this.scope = "playlist-modify-private user-read-recently-played user-read-playback-state user-read-currently-playing";
        this.show_dialog = false;
    }

    public RESTHandler(String client_id, String redirect_url, String state, String scope, Boolean show_dialog) {
        this.client_id = client_id;
        this.redirect_url = redirect_url;
        this.state = state;
        this.scope = scope;
        this.show_dialog = show_dialog;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
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

    public Boolean getShow_dialog() {
        return show_dialog;
    }

    public void setShow_dialog(Boolean show_dialog) {
        this.show_dialog = show_dialog;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Boolean authorizeUser(){
        String request_url = "https://accounts.spotify.com/authorize?client_id=5fe01282e44241328a84e7c5cc169165&response_type=code&redirect_uri=https%3A%2F%2Fexample.com%2Fcallback&scope=user-read-private%20user-read-email&state=34fFs29kd09";

        //Todo: send request and check response

        this.isAuthorized = true;
        return true;
    }

    public Boolean getRefreshAndAccessToken() {
        if (!this.isAuthorized) return false;

        this.accessToken = "";
        this.refreshToken = "";
        return true;
    }

    public Boolean getNewAccessToken() {
        return true;
    }
}
