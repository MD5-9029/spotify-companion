package com.spotifycompanion.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of the representation of an track object response from the API.
 * Several unimportant attributes are getting ignored. Maybe getting added in future updates.
 */
public class Track {
    //artists
    //available markets
    //disc_number
    public int duration_ms;
    //explicit
    //external_urls
    /**
     * 	A link to the Web API endpoint providing full details of the track.
     */
    public String href;
    /**
     * 	The Spotify ID for the track.
     */
    public String id;
    /**
     * 	Part of the response when Track Relinking is applied. If true , the track is playable in the given market. Otherwise false.
     */
    public Boolean is_playable;
    //linked_from
    //restrictions
    /**
     * The name of the track.
     */
    public String name;
    //preview_url
    //track_number
    /**
     * 	The object type: “track”.
     */
    public final String type = "track";
    /**
     * 	The Spotify URI for the track.
     */
    public String uri;
    //is_local

    public Track(int duration_ms, String href, String id, Boolean is_playable, String name, String uri) {
        this.duration_ms = duration_ms;
        this.href = href;
        this.id = id;
        this.is_playable = is_playable;
        this.name = name;
        this.uri = uri;
    }

    public Track(JSONObject track) {
        try {
            this.duration_ms = track.has("duration_ms") ? track.getInt("duration_ms") : 0;
            this.href = track.has("href") ? track.getString("href") : null;
            this.id = track.has("id") ? track.getString("id") : null;
            this.is_playable = track.has("is_playable") ? track.getBoolean("is_playable") : null;
            this.name = track.has("name") ? track.getString("name") : null;
            this.uri = track.has("uri") ? track.getString("uri") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
