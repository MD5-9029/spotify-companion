package com.spotifycompanion.Management;

import android.app.Activity;

import androidx.annotation.Nullable;

/**
 * class containing and managing all components below view
 */
public class ManagementConnector {
    private Activity gActivity;
    private DatabaseHandler gDatabaseHandler;
    private RemoteHandler gRemote;
    private DataParser gDataParser;
    private RESTHandler gRESTHandler;

    /**
     * constructor for management
     *
     * @param pContext context from MainActivity
     */
    public ManagementConnector(@Nullable Activity pContext) {
        gActivity = pContext;
        gDatabaseHandler = new DatabaseHandler(pContext);
        gRemote = new RemoteHandler(pContext);
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

    }

    public void deAuth() {
        gRESTHandler.cancelCall();
    }

}