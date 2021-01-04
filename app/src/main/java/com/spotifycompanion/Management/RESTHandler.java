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

    public static final String CLIENT_ID = "089d841ccc194c10a77afad9e1c11d54";
    public static final String REDIRECT_URI = "spotify-sdk://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private Call mCall;
    private Activity gActivity;


    public RESTHandler(Activity pContext) {
        gActivity = pContext;
        requestToken();
        requestCode();

    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    public void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    public void requestToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(gActivity, AUTH_TOKEN_REQUEST_CODE, request);
    }

    public void requestCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(gActivity, AUTH_CODE_REQUEST_CODE, request);
    }


    public void onClearCredentialsClicked() {
        AuthorizationClient.clearCookies(gActivity);
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
                } catch (JSONException e) {
                    //setResponse("Failed to parse data: " + e);
                }
            }
        });
        Log.e("callback", "works!!!!!!!!!!!!!");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();

        }
    }
}
