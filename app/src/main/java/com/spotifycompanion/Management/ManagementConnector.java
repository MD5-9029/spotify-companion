package com.spotifycompanion.Management;

import android.content.Context;

import androidx.annotation.Nullable;

/**
 * class containing and managing all components below view
 */
public class ManagementConnector {
    private Context zContext;
    private DatabaseHandler zDatabaseHandler;
    private RemoteHandler zRemote;
    ;
    private DataParser zDataParser = new DataParser();
    private RESTHandler zRESTHandler = new RESTHandler();

    /**
     * constructor for management
     *
     * @param pContext context from MainActivity
     */
    public ManagementConnector(@Nullable Context pContext) {
        zContext = pContext;
        zDatabaseHandler = new DatabaseHandler(pContext);
        zRemote = new RemoteHandler(pContext);
    }

    public void connectRemote() {
        zRemote.connect();
    }

    public void disconnectRemote() {
        zRemote.disconnect();
    }

    public RemoteHandler getRemote() {
        return zRemote;
    }

}