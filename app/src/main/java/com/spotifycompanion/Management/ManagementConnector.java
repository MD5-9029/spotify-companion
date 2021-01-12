package com.spotifycompanion.Management;

import android.app.Activity;

import com.spotifycompanion.Activities.MainActivity;

/**
 * class containing and managing all components below view
 */
public class ManagementConnector {
    private Activity gActivity;
    private DatabaseHandler gDatabaseHandler;
    private RemoteHandler gRemote;
    private DataParser gDataParser;
    private RESTHandler gRESTHandler;
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
    }

    //not optimal, method authorized before returning auth-state
    public boolean isAuthorized() {
        return gRESTHandler.authorizeUser();
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

    public void skipBackward() {
        gRemote.skipBackward();
    }


    public void clearSkipped() {
        gDatabaseHandler.removeAllSkipped();
    }

}