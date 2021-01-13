package com.spotifycompanion.models;

import org.json.JSONObject;

/**
 * Helper class for the Playlist (simple) response (not used)
 */
public class PlaylistTracksSummary {
    public String href;
    public int total;

    public PlaylistTracksSummary(String href, int total) {
        this.href = href;
        this.total = total;
    }

    public PlaylistTracksSummary(JSONObject summary) {

    }
}
