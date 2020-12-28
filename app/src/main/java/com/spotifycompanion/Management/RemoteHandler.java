package com.spotifycompanion.Management;


import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

/**
 * remote handler manages interaction (requests) with the main app
 */
public class RemoteHandler {
    private static final String zClientID = "4234dd4558284817abdb7c7ecc4d7df7";
    private static final String zRedirectURI = "spotifyCompanion://authCall";
    private SpotifyAppRemote zSpotifyAppRemote;

    private Track zTrack;

    /**
     * connect app to local spotify instance
     *
     * @param pActivity context from MainActivity
     */
    public void connect(Context pActivity) {
        ConnectionParams lConnectionParams = new ConnectionParams.Builder(zClientID).setRedirectUri(zRedirectURI).showAuthView(true).build();

        SpotifyAppRemote.connect(pActivity, lConnectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote pSpotifyAppRemote) {
                        zSpotifyAppRemote = pSpotifyAppRemote;

                        //keep the zTrack attribute up2date
                        subscribeToTrack();

                        //for test, play a list
                        playCurrent();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    /**
     * disconnect app from spotify instance
     */
    public void disconnect() {
        SpotifyAppRemote.disconnect(zSpotifyAppRemote);
    }

    public void subscribeToTrack() {
        zSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            zTrack = playerState.track;
        });
    }

    /**
     * perform test action to verify code integrity
     */
    public void playCurrent() {
        zSpotifyAppRemote.getPlayerApi().resume();

        /*
        // Play a playlist
        zSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        zSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);

                    }
                });

         */
    }

    public void addCurrentToLibrary() {
        zSpotifyAppRemote.getUserApi().addToLibrary(zTrack.uri);
    }

    public void removeCurrentFromLibrary() {
        zSpotifyAppRemote.getUserApi().removeFromLibrary(zTrack.uri);
    }
}


