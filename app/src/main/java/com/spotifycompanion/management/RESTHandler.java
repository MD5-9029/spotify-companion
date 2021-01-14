package com.spotifycompanion.management;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotifycompanion.models.Playlist;
import com.spotifycompanion.models.Playlists;
import com.spotifycompanion.models.SavedTracks;
import com.spotifycompanion.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RESTHandler {

    private final String APP_TOKEN = "spotify-companion-token";
    public final AuthorizationConfig authConfig = new AuthorizationConfig();
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private final Activity contextActivity;
    final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
    public String mAccessToken;
    public String mTokenType;       // SDK does not supply String type
    public String mScope;           // SDK does not supply this
    public int mExpiresIn;
    public String mRefreshToken;    // SDK does not supply this
    public String mAccessCode;
    private Call mCall;


    private Boolean isAuthorized = false;


    public RESTHandler(Activity pActivity) {
        this.contextActivity = pActivity;
    }

    public void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(this.authConfig.CLIENT_ID, type, this.authConfig.getRedirectUriString())
                .setShowDialog(false)
                .setScopes(this.authConfig.getScopesArray())
                .setCampaign(this.APP_TOKEN)
                .build();
    }

    public void requestToken(Activity contextActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(contextActivity, this.authConfig.AUTH_TOKEN_REQUEST_CODE, request);
    }

    public void requestCode(Activity contextActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(contextActivity, this.authConfig.AUTH_CODE_REQUEST_CODE, request);
    }

    /**
     * Not functional due to missing refresh token
     * @return false
     */
    public Boolean requestRefreshToken() {
        final String path = "https://accounts.spotify.com/api/token";
        JSONObject postData = new JSONObject();
        try {
            postData.put("grant_type", "refresh_token");
            postData.put("refresh_token", mRefreshToken);
            JSONObject result = postData(path, postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearCredentials(Activity contextActivity) {
        AuthorizationClient.clearCookies(contextActivity);
    }

    /**
     * Utility function to send a GET request to the API
     * @param route the url for the request
     * @return JSON data response
     */
    public JSONObject requestData(String route) {
        JSONObject data = null;
        if (mAccessToken == null) {
            this.requestToken(contextActivity); //better would be a general token_refresh beforehand
            return null;
        }
        final Request request = new Request.Builder()
                .url(route)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        try {
            Response response = mCall.execute();
            try {
                data = new JSONObject(Objects.requireNonNull(response.body()).string());
            } catch (JSONException | IOException e) {
                Log.e("response", "Cannot convert reply to JSON");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data.has("error")){
            this.requestToken(contextActivity);
            return null;
        }
        return data;
    }

    /**
     * Utility function to send a POST request to the API
     * @param route the url for the request
     * @param jsonString the post data in JSON type
     * @return JSON data response
     */
    public JSONObject postData(String route, String jsonString) {
        JSONObject data = null;
        if (mAccessToken == null) {
            this.requestToken(contextActivity); //better would be a general token_refresh beforehand
            return null;
        }
        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json; charset=utf-8"));
        String test = body.toString();
        final Request request = new Request.Builder()
                .url(route)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .post(body)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        try {
            Response response = mCall.execute();
            try {
                data = new JSONObject(Objects.requireNonNull(response.body()).string());
            } catch (JSONException | IOException e) {
                Log.e("response", "Cannot convert reply to JSON");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data.has("error")){
            this.requestToken(contextActivity);
            return null;
        }
        return data;
    }

    /**D
     * Utility function to send a DELETE request to the API
     * @param route the url for the request
     * @param jsonString the post data in JSON type
     * @return JSON data response
     */
    public JSONObject deleteData(String route, String jsonString) {
        JSONObject data = null;
        if (mAccessToken == null) {
            this.requestToken(contextActivity); //better would be a general token_refresh beforehand
            return null;
        }
        RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json; charset=utf-8"));
        String test = body.toString();
        final Request request = new Request.Builder()
                .url(route)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .delete(body)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        try {
            Response response = mCall.execute();
            try {
                data = new JSONObject(Objects.requireNonNull(response.body()).string());
            } catch (JSONException | IOException e) {
                Log.e("response", "Cannot convert reply to JSON");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data.has("error")){
            this.requestToken(contextActivity);
            return null;
        }
        return data;
    }

    /**
     * Returns profile of logged-in user
     * @return User Profile Object | null
     */
    public User getUserProfile() {
        final String route = "https://api.spotify.com/v1/me";
        User user = null;
        JSONObject data = requestData(route);
        if(data != null) {
            user = new User(data);
        }
        return user;
    }

    /**
     * Returns max. 50 playlist of logged-in user
     * @return Playlists Object | null
     */
    public Playlists getUserPlaylists(){
        final String route = "https://api.spotify.com/v1/me/playlists?limit=50";
        Playlists playlists = null;
        JSONObject data = requestData(route);
        if(data != null) {
            playlists = new Playlists(data);
        }
        return playlists;
    }

    /**
     * Requests specific playlist via ID
     * @param playlist_id Unique string id
     * @return Playlist Object | null
     */
    public Playlist getPlaylist(String playlist_id) {
        final String route = String.format("https://api.spotify.com/v1/playlists/%s", playlist_id);
        Playlist playlist = null;
        JSONObject data = requestData(route);
        if(data != null) {
            playlist = new Playlist(data);
        }
        return playlist;
    }

    /**
     * Adds to a given playlist_id a list of tracks given in form of a comma-seperated uri-string
     * @param playlist_id 	The Spotify ID for the playlist.
     * @param track_uri_list Optional. A comma-separated list of Spotify URIs to add, can be track or episode URIs. For example:
     * uris=spotify:track:4iV5W9uYEdYUVa79Axb7Rh, spotify:track:1301WleyT98MSxVHPZCA6M,spotify:episode:512ojhOuo1ktJprKbVcKyQ
     * @return true if successful
     */
    public boolean addToPlaylist(String playlist_id, String[] track_uri_list){
        final String route = String.format("https://api.spotify.com/v1/playlists/%s/tracks", playlist_id);
        JSONObject postData = new JSONObject();
        try {
            JSONArray uri_list = new JSONArray(track_uri_list);
            postData.put("uris", uri_list);
            JSONObject response = this.postData(route, postData.toString());
            return response != null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Adds to a given playlist_id a list of tracks given in form of a comma-seperated uri-string
     * @param playlist_id 	The Spotify ID for the playlist.
     * @param track_uri_list Optional. A comma-separated list of Spotify URIs to add, can be track or episode URIs. For example:
     * uris=spotify:track:4iV5W9uYEdYUVa79Axb7Rh, spotify:track:1301WleyT98MSxVHPZCA6M,spotify:episode:512ojhOuo1ktJprKbVcKyQ
     * @return true if successful
     */
    public boolean removeFromPlaylist(String playlist_id, String[] track_uri_list){
        final String route = String.format("https://api.spotify.com/v1/playlists/%s/tracks", playlist_id);
        JSONObject postData = new JSONObject();
        try {
            JSONArray uri_list_json = new JSONArray();
            for (String s : track_uri_list) {
                JSONObject entry = new JSONObject();
                entry.put("uri", s);
                uri_list_json.put(entry);
            }
            postData.put("tracks", uri_list_json);
            JSONObject response = this.deleteData(route, postData.toString());
            return response != null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @deprecated No longer used Todo: purge code
     * Utility function to extract an integer at the end of a string
     * @param input string containing some integer at the end
     * @return the integer value
     */
    private int getOffsetNumber(String input){
        Matcher matcher = lastIntPattern.matcher(input);
        if (matcher.find()) {
            String numberStr = matcher.group(1);
            return Integer.parseInt(numberStr);
        }
        return 0;
    }

    /**
     * Return an SavedTracks object of all favorite tracks
     * @return SavedTracks object | null
     */
    public SavedTracks getSavedTracks(){
        final String routeBase = "https://api.spotify.com/v1/me/tracks?limit=50&offset=0";
        SavedTracks savedTracks = null;
        String nextRoute = routeBase;
        while (nextRoute != null && !nextRoute.equals("null")){
            JSONObject data = requestData(nextRoute);
            if(data != null && savedTracks == null) {
                savedTracks = new SavedTracks(data);
            }
            try {
                JSONArray trackItems = data.getJSONArray("items");
                Uri uri = Uri.parse(nextRoute);
                int offset = Integer.parseInt(uri.getQueryParameter("offset"));
                savedTracks.addTracks(trackItems, offset);
                nextRoute = data.getString("next");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return savedTracks;
    }
    public Playlist getSavedTracksAsPlaylist(){
        Playlist playlist = null;
        SavedTracks savedTracks = getSavedTracks();
        playlist = new Playlist("favorites", savedTracks.getPlaylistTracks());
        return playlist;
    }
}
