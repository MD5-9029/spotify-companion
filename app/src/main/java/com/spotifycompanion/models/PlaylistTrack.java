package com.spotifycompanion.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of the representation of an playlistTrack object response from the API.
 * It is a wrapper containing information about added_at and added_by besides the track itself.
 */
public class PlaylistTrack {
    /**
     * The date and time the track or episode was added.
     * Note that some very old playlists may return null in this field.
     */
    public String added_at;
    /**
     * The Spotify user who added the track or episode.
     * Note that some very old playlists may return null in this field.
     */
    public User added_by;
    /**
     * 	Whether this track or episode is a local file or not.
     */
    public Boolean is_local;
    /**
     * 	Information about the track or episode.
     */
    public Track track;

    public PlaylistTrack(String added_at, User added_by, Track track) {
        //is_local skipped for now
        this.added_at = added_at;
        this.added_by = added_by;
        this.track = track;
    }

    public PlaylistTrack(JSONObject data) {
        try {
            this.added_at = data.has("added_at") ? data.getString("added_at") : null;
            this.added_by = data.has("added_by") ? new User(data.getJSONObject("added_by")) : null;
            this.track = data.has("track") ? new Track(data.getJSONObject("track")) : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
