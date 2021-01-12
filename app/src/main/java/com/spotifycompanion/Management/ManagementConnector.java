package com.spotifycompanion.Management;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

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
    private boolean authorized = false;

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

    public void updateImage(ImageView pIV){
        gRemote.updateImage(pIV);
    }

    public void likeCurrentTrack(View view) {
        gRemote.like();
    }

    public void unlikeCurrentTrack() {
        gRemote.unlike();
    }

    public void resumePlayback() {
        gRemote.resume();
    }

    public void clearSkipped(){
        gDatabaseHandler.removeAllSkipped();
    }

    public boolean isAuthorized() {
        return authorized;
    }
}