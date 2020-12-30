package com.spotifycompanion.Management;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

/**
 * remote handler manages interaction (requests) with the main app.
 * relays requests to spotify
 */
public class RemoteHandler {
    private static final String zClientID = "4234dd4558284817abdb7c7ecc4d7df7";
    private static final String zRedirectURI = "spotifyCompanion://authCall";

    private Context zContext;
    private SpotifyAppRemote zSpotifyAppRemote;
    private PlayerState zPlayer;

    public RemoteHandler(Context pContext) {
        zContext = pContext;
    }

    /**
     * connect; links the companion to the official app via IPC
     */
    public void connect() {
        SpotifyAppRemote.connect(this,
                new ConnectionParams.Builder(zClientID).setRedirectUri(zRedirectURI).showAuthView(true).build(),
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote pSpotifyAppRemote) {
                        zSpotifyAppRemote = pSpotifyAppRemote;
                        subscribeToStates();
                        //zSpotifyAppRemote.getPlayerApi().resume();
                        //resume();
                    }

                    public void onFailure(Throwable throwable) {
                        //Toast.makeText(zContext, "MSG", Toast.LENGTH_LONG).show();
                        Log.e("RemoteHandler", throwable.getMessage(), throwable);
                    }
                });
    }

    public void disconnect() {
        SpotifyAppRemote.disconnect(zSpotifyAppRemote);
    }

    public void subscribeToStates() {
        zSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            zPlayer = playerState;
        });

    }

    public void resume() {
        try {
            zSpotifyAppRemote.getPlayerApi().resume();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            Toast.makeText(this.zContext, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

    public void like() {
        Track lTrack = zPlayer.track;
        zSpotifyAppRemote.getUserApi().addToLibrary(lTrack.uri);
    }

    public void unlike() {
        Track lTrack = zPlayer.track;
        zSpotifyAppRemote.getUserApi().removeFromLibrary(lTrack.uri);
    }
}


