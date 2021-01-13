package com.spotifycompanion.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of the representation of an image object response from the API.
 */
public class Image {
    /**
     * Ressource url
     */
    public String url;
    /**
     * Dimension height. Nullable.
     */
    public Integer height;
    /**
     * Dimension width. Nullable.
     */
    public Integer width;

    public Image(String url, int height, int width) {
        this.url = url;
        this.height = height;
        this.width = width;
    }

    public Image(JSONObject data) {
        try {
            this.url = data.has("url") ? data.getString("url") : null;
            this.height = data.has("height")&& !data.getString("height").equals("null") ? data.getInt("height") : null;
            this.width = data.has("width")&& !data.getString("width").equals("null") ? data.getInt("width") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
