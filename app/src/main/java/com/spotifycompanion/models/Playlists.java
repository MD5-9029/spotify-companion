package com.spotifycompanion.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of the representation of an playlists object response from the API.
 */
public class Playlists {
    /**
     * A link to the Web API endpoint returning the full result of the request.
     */
    public String href;
    /**
     * The requested data.
     */
    public Playlist[] items;

    public Playlists(JSONObject data) {
        items = null;
        try {
            this.href = data.getString("href");
            JSONArray dataArray = data.getJSONArray("items");
            items = new Playlist[dataArray.length()];
            for(int i = 0; i < dataArray.length(); i++){
                JSONObject playlistData = dataArray.getJSONObject(i);
                items[i] = new Playlist(playlistData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
