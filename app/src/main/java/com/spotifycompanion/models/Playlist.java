package com.spotifycompanion.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of the <strong>public</strong> representation of an playlist object response from the API.
 * https://developer.spotify.com/documentation/web-api/reference/playlists/get-playlist/
 * https://developer.spotify.com/documentation/general/guides/working-with-playlists/
 */
public class Playlist {
    /**
     * Shared playlist.
     * true if the owner allows other users to modify the playlist.
     */
    public Boolean collaborative;
    /**
     * The playlist description. Only returned for modified, verified playlists, otherwise null .
     */
    public String description;
    /**
     * 	A link to the Web API endpoint providing full details of the playlist.
     */
    public String href;
    /**
     * The Spotify ID for the playlist.
     */
    public String id;
    /**
     * Images for the playlist.
     * The array may be empty or contain up to three images.
     * The images are returned by size in descending order.
     */
    public Image[] images;
    /**
     * The name of the playlist.
     */
    public String name;
    /**
     * The user who owns the playlist
     */
    public User owner;
    /**
     * 	The playlist’s public/private status: true the playlist is public,
     * 	false the playlist is private, null the playlist status is not relevant.
     */
    public Boolean is_public;
    /**
     * The version identifier for the current playlist. Can be supplied in other requests to target a specific playlist version
     */
    public String snapshot_id;
    /**
     * Information about the tracks of the playlist. Note, a track object may be null.
     * This can happen if a track is no longer available.
     */
    public PlaylistTrack[] tracks;
    /**
     * The object type: “playlist”
     */
    public final String type = "playlist";
    /**
     * 	The Spotify URI for the playlist.
     */
    public String uri;


    public Playlist(boolean collaborative, String description, String href, String id, Image[] images, String name, User owner, Boolean is_public, PlaylistTrack[] tracks, String uri) {
        //snapshots skipped for now
        this.collaborative = collaborative;
        this.description = description;
        this.href = href;
        this.id = id;
        this.images = images;
        this.name = name;
        this.owner = owner;
        this.is_public = is_public;
        this.tracks = tracks;
        this.uri = uri;
    }

    public Playlist(JSONObject data){
        this.images = null;
        try {
            this.href = ModelUtility.getString(data, "href");
            this.id = ModelUtility.getString(data, "id");
            this.name = ModelUtility.getString(data, "name");
            this.is_public = ModelUtility.getBoolean(data, "public");
            this.uri = ModelUtility.getString(data, "uri");

            //further processing
            this.owner = ModelUtility.exists(data,"owner") ? new User(data.getJSONObject("owner")):null;

            JSONArray dataArray = ModelUtility.getJSONArray(data, "images"); //data.getJSONArray("images");
            if (dataArray != null){
                this.images = new Image[dataArray.length()];
                for(int i = 0; i < dataArray.length(); i++){
                    JSONObject imageData = dataArray.getJSONObject(i);
                    images[i] = new Image(imageData);
                }
            }

            // get all user lists and get specific list uses same playlist class
            // but has a different "tracks" attribute (see PlaylistTracksSummary class), therefore gets ignored
            // -> {"href":String,"total":Integer}
            JSONObject objTracks = ModelUtility.getJSONObject(data, "tracks");
            if (objTracks != null && objTracks.has("items")) {
                dataArray = objTracks.getJSONArray("items");
                this.tracks = new PlaylistTrack[dataArray.length()];
                for(int i = 0; i < dataArray.length(); i++){
                    JSONObject trackData = dataArray.getJSONObject(i);
                    tracks[i] = new PlaylistTrack(trackData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Playlist(String name, PlaylistTrack[] pTracks) {
        this.collaborative = false;
        this.description = "";
        this.href = "";
        this.id = "";
        this.images = null;
        this.name = name;
        this.owner = null;
        this.is_public = false;
        this.tracks = pTracks;
        this.uri = "";
    }

    public String toString(){
        return name;
    }
}
