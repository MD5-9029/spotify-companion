package com.spotifycompanion.Management;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RESTHandler {

    public final AuthorizationConfig authConfig = new AuthorizationConfig();
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private Call mCall;

    private Boolean isAuthorized = false;
    private String accessToken = "";
    private String refreshToken = "";

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

    public void getUserProfile() {
        if (mAccessToken == null) {
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //setResponse("Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.e("callback", jsonObject.toString());
                } catch (JSONException e) {
                    //setResponse("Failed to parse data: " + e);
                }
            }
        });
    }


    public Boolean authorizeUser(){
        String request_url = "https://accounts.spotify.com/authorize?client_id=5fe01282e44241328a84e7c5cc169165&response_type=code&redirect_uri=https%3A%2F%2Fexample.com%2Fcallback&scope=user-read-private%20user-read-email&state=34fFs29kd09";

        //Todo: send request and check response

        this.isAuthorized = true;
        return true;
    }

    public Boolean getRefreshAndAccessToken() {
        if (!this.isAuthorized) return false;

        this.accessToken = "";
        this.refreshToken = "";
        return true;
    }

    public Boolean getNewAccessToken() {
        return true;
    }
}
