package com.spotifycompanion.Management;


import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotifycompanion.Activities.MainActivity;

/**
 * remote handler manages interaction (requests) with the main app.
 * relays requests to spotify
 */
public class RemoteHandler {
    private static final String gClientID = "4234dd4558284817abdb7c7ecc4d7df7";
    private static final String gRedirectURI = "spotifyCompanion://authCall";

    private MainActivity gActivity;
    private SpotifyAppRemote gSpotifyAppRemote;
    private PlayerState gPlayer;

    public RemoteHandler(MainActivity pActivity) {
        gActivity = pActivity;
    }

    /**
     * connect; links the companion to the official app via IPC
     */
    public void connect() {
        SpotifyAppRemote.connect(gActivity,
                new ConnectionParams.Builder(gClientID).setRedirectUri(gRedirectURI).showAuthView(true).build(),
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote pSpotifyAppRemote) {
                        gSpotifyAppRemote = pSpotifyAppRemote;
                        subscribeToStates();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("RemoteHandler", throwable.getMessage(), throwable);
                    }
                });
    }

    public void disconnect() {
        SpotifyAppRemote.disconnect(gSpotifyAppRemote);
    }

    private void subscribeToStates() {
        gSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            gPlayer = playerState;
            updateImage();
        });
    }

    private void updateImage() {
        gSpotifyAppRemote.getImagesApi().getImage(gPlayer.track.imageUri).setResultCallback(bitmap -> {
            gActivity.getCoverView().setImageBitmap(bitmap);
        });
    }

    public void resume() {
        try {
            gSpotifyAppRemote.getPlayerApi().resume();
        } catch (java.lang.Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void like() {
        Track lTrack = gPlayer.track;
        gSpotifyAppRemote.getUserApi().addToLibrary(lTrack.uri);
    }

    public void unlike() {
        Track lTrack = gPlayer.track;
        gSpotifyAppRemote.getUserApi().removeFromLibrary(lTrack.uri);
    }
}


