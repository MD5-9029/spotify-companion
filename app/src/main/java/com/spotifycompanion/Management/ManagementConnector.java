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
     * @param pActivity context from MainActivity
     */
    public ManagementConnector(@Nullable Activity pActivity) {
        gActivity = pActivity;
        gDatabaseHandler = new DatabaseHandler(pActivity);
        gRemote = new RemoteHandler(pActivity);
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

    public void resumePlayback() {
        gRemote.resume();
    }

    private void addToSkipped(String pUri) {
        gDatabaseHandler.addSkipped(pUri);
    }

    private int getSkipped(String pUri) {
        return gDatabaseHandler.getSkipped(pUri);
    }

}