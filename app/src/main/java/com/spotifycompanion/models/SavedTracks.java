package com.spotifycompanion.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SavedTracks {
    public String href;
    public Track[] items;
    //limit
    public String next;
    //offset
    //previous
    public int total;

    /**
     * Creates a SavedTracks Object with no Tracks loaded
     */
    public SavedTracks(JSONObject data) {
        try {
            this.href = data.has("href") && !data.isNull("href") ? data.getString("href") : null;
            this.total = data.has("total") && !data.isNull("total") ? data.getInt("total") : null;
            this.next = data.has("next") && !data.isNull("next") ? data.getString("next") : null;
            this.items = new Track[total];
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * With more than 50 saved tracks a iterative readout is needed to get all tracks
     * @param trackDataList the chunk of trackListData (usually 50)
     * @param offset the offset in the saved tracks list
     */
    public void addTracks(JSONArray trackDataList, int offset) {
        try {
            for (int i = 0; i < trackDataList.length(); i++){
                JSONObject trackData = trackDataList.getJSONObject(i).getJSONObject("track");
                this.items[i+offset] = new Track(trackData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
