package com.spotifycompanion.Management;

import android.app.Activity;

import androidx.annotation.Nullable;

/**
 * class containing and managing all components below view
 */
public class gManagementConnector {
    private Activity gActivity;
    private DatabaseHandler gDatabaseHandler;
    private RemoteHandler gRemote;
    private DataParser gDataParser;
    private RESTHandler gRESTHandler;

    /**
     * constructor for management
     *
     * @param pActivity context from MainActivity
     */
    public gManagementConnector(@Nullable Activity pActivity) {
        gActivity = pActivity;
        gDatabaseHandler = new DatabaseHandler(pActivity);
        gRemote = new RemoteHandler(pActivity);
    }

    public void initialize() {
        this.connectRemote();
        this.auth();
    }

    public void close() {
        this.disconnectRemote();
        this.deAuth();
    }

    public void connectRemote() {
        gRemote.connect();
    }

    public void disconnectRemote() {
        gRemote.disconnect();
    }

    public RemoteHandler getRemote() {
        return gRemote;
    }

    public void auth() {
        gRESTHandler = new RESTHandler(gActivity);
        gRESTHandler.getUserProfile();
    }

    public void deAuth() {
        gRESTHandler.cancelCall();
    }

}