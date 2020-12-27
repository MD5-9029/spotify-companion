package com.spotifycompanion.Management;

import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotifycompanion.Activities.MainActivity;

public class RemoteHandler {
    private static final String zClientID = "4234dd4558284817abdb7c7ecc4d7df7";
    private static final String zRedirectURI = "spotifyCompanion://authCall";
    private SpotifyAppRemote zSpotifyAppRemote;

    public void connect(MainActivity pActivity) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(zClientID).setRedirectUri(zRedirectURI).showAuthView(true).build();

        SpotifyAppRemote.connect(pActivity, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        zSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public void disconnect() {
        SpotifyAppRemote.disconnect(zSpotifyAppRemote);
    }

    private void connected() {
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
    }
}


