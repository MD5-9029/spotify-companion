package com.spotifycompanion.Management;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotifycompanion.models.Playlist;
import com.spotifycompanion.models.Playlists;
import com.spotifycompanion.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RESTHandler {

    public final AuthorizationConfig authConfig = new AuthorizationConfig();
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    public String mAccessToken;
    public String mAccessCode;
    private Call mCall;

    private Boolean isAuthorized = false;
    public String refreshToken = "";

    private final String APP_TOKEN = "spotify-companion-token";

    public RESTHandler() {
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

    public void clearCredentials(Activity contextActivity) {
        AuthorizationClient.clearCookies(contextActivity);
    }

    protected void onActivityResultCallback(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (this.authConfig.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();

        } else if (this.authConfig.AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();

        }
    }

    public JSONObject requestData(String route) {
        JSONObject data = null;
        if (mAccessToken == null) {
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
        return data;
    }

    public User getUserProfile() {
        String route = "https://api.spotify.com/v1/me";
        User user = null;
        JSONObject data = requestData(route);
        if(data != null) {
            user = new User(data);
        }
        return user;
    }
    public Playlists getUserPlaylists(){
        String route = "https://api.spotify.com/v1/me/playlists?limit=50";
        Playlists playlists = null;
        JSONObject data = requestData(route);
        if(data != null) {
            playlists = new Playlists(data);
        }
        return playlists;
    }

    public Playlist getPlaylist(String playlist_id) {
        String route = String.format("https://api.spotify.com/v1/playlists/%s", playlist_id);
        Playlist playlist = null;
        JSONObject data = requestData(route);
        if(data != null) {
            playlist = new Playlist(data);
        }
        return playlist;
    }

    public Boolean authorizeUser(Activity contextActivity){
        this.requestToken(contextActivity);

        this.isAuthorized = true;
        return true;
    }

    public Boolean getRefreshAndAccessToken() {
        if (!this.isAuthorized) return false;

        //this.accessToken = "";
        //this.refreshToken = "";
        return true;
    }

    public Boolean getNewAccessToken() {
        return true;
    }
}
