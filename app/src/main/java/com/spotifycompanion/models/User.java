package com.spotifycompanion.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of the <strong>private or public</strong> representation of an user object response from the API.
 * If only the public data are available email- country- and other private information will be missing.
 */
public class User {
    /**
     * The name displayed on the user’s profile. null if not available.
     * e.g. "Spotify"
     */
    public String display_name;
    /**
     * A link to the Web API endpoint for this user.
     * e.g. "https://api.spotify.com/v1/users/spotify"
     */
    public String href;
    /**
     * The Spotify user ID for the user
     * e.g. "spotify"
     */
    public String id;
    /**
     * The object type: “user”
     */
    public final String type = "user";
    /**
     * The Spotify URI for the user.
     * e.g. "spotify:user:spotify"
     */
    public String uri;

    /*PRIVATE Attributes*/
    public String email;

    /**
     * Public User Data
     * @param display_name Username
     * @param href API Request URL
     * @param id User ID
     * @param uri Spotify User URI
     */
    public User(String display_name, String href, String id, String uri) {
        this.display_name = display_name;
        this.href = href;
        this.id = id;
        this.uri = uri;
    }

    /**
     * Private User Data
     * @param display_name Username
     * @param href API Request URL
     * @param id User ID
     * @param uri Spotify User URI
     * @param email User Email
     */
    public User(String display_name, String href, String id, String uri, String email) {
        this.display_name = display_name;
        this.href = href;
        this.id = id;
        this.uri = uri;
        this.email = email;
    }

    /**
     * Generates a user object from an API json response. Private or Public.
     * @param userObj The user data retrieved from the "https://api.spotify.com/v1/me" route
     */
    public User(JSONObject userObj) {
        try {
            this.display_name = userObj.has("display_name") ? userObj.getString("display_name") : null;
            this.href = userObj.has("href") ? userObj.getString("href") : null;
            this.id = userObj.has("id") ? userObj.getString("id") : null;
            this.uri = userObj.has("uri") ? userObj.getString("uri") : null;
            if (userObj.has("email")) {
                this.email = userObj.getString("email");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
