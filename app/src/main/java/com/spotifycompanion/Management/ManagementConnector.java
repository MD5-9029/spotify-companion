package com.spotifycompanion.Management;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.spotifycompanion.Activities.MainActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotifycompanion.R;

import okhttp3.Response;

/**
 * class containing and managing all components below view
 */
public class ManagementConnector {
    public final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public final int AUTH_CODE_REQUEST_CODE = 0x11;

    private Activity gActivity;
    private DatabaseHandler gDatabaseHandler;
    private RemoteHandler gRemote;
    private DataParser gDataParser;
    public RESTHandler gRESTHandler;
    private boolean authorized = false;

    /**
     * constructor for management
     *
     * @param pActivity context from MainActivity
     */
    public ManagementConnector(MainActivity pActivity) {
        gActivity = pActivity;
        gDatabaseHandler = new DatabaseHandler(pActivity);
        gRemote = new RemoteHandler(pActivity, gDatabaseHandler);
        gRESTHandler = new RESTHandler();
    }

    public void initialize() {
        this.connectRemote();
    }

    public void close() {
        this.disconnectRemote();
    }

    public void connectRemote() {
        gRemote.connect();
    }

    public void disconnectRemote() {
        gRemote.disconnect();
    }

    public void likeCurrentTrack() {
        gRemote.like();
    }

    public void unlikeCurrentTrack() {
        gRemote.unlike();
    }

    public void togglePlayback() {
        gRemote.togglePlayback();
    }

    public void skipForward() {
        gRemote.skipForward();
    }

    /**
     * Attempts to authorize application access
     * @param contextActivity
     */
    public void authorizeAccess(Activity contextActivity) {
        this.gRESTHandler.requestToken(contextActivity);
    }

    /**
     * Clears Cookies and forces re-login
     * @param contextActivity
     */
    public void disallowAccess(Activity contextActivity) {
        gRESTHandler.cancelCall();
        AuthorizationClient.clearCookies(contextActivity);
        authorized = false;
    }

    /**
     * Callback from the attempt to authorize application access
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true if access was authorized
     */
    public boolean authorizeCallback(int requestCode, int resultCode, Intent data) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            gRESTHandler.mAccessToken = response.getAccessToken();
            return authorized = true;
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            gRESTHandler.mAccessCode = response.getCode();
        }
        return false;
    }

    /**
     * Callback from the attempt to receive an auth code from the API
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true if code was received
     */
    public boolean authCodeCallback(int requestCode, int resultCode, Intent data) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (AUTH_CODE_REQUEST_CODE == requestCode) {
            gRESTHandler.mAccessCode = response.getCode();
            return true;
        }
        return false;
    }
    public boolean isAuthorized() {
        return authorized;
    }


    public void skipBackward() {
        gRemote.skipBackward();
    }


    public void clearSkipped() {
        gDatabaseHandler.removeAllSkipped();
    }

}