package com.spotifycompanion.Management;


import android.util.Log;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.protocol.types.Track;
import com.spotifycompanion.Activities.MainActivity;

/**
 * remote handler manages interaction (requests) with the main app.
 * relays requests to spotify
 */
public class RemoteHandler {
    private static final String gClientID = "4234dd4558284817abdb7c7ecc4d7df7";
    private static final String gRedirectURI = "spotifyCompanion://authCall";

    private static final int gSkippedLimit = 3;

    private MainActivity gActivity;
    private SpotifyAppRemote gSpotifyAppRemote;
    private PlayerState gPlayer;
    private DatabaseHandler gDatabase;
    private RESTHandler gRestHandler;



    public RemoteHandler(MainActivity pActivity, DatabaseHandler pDatabase, RESTHandler pREST) {
        gActivity = pActivity;
        gDatabase = pDatabase;
        gRestHandler = pREST;
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

                        //setup playback for convenient use
                        gSpotifyAppRemote.getPlayerApi().setRepeat(Repeat.ALL);
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
        try {
            gSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
                gPlayer = playerState;
                updateImage();

            });
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateImage() {
        try {
            gSpotifyAppRemote.getImagesApi().getImage(gPlayer.track.imageUri).setResultCallback(bitmap -> {
                gActivity.getCoverView().setImageBitmap(bitmap);
            });
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void togglePlayback() {
        try {
            if (gPlayer.isPaused) {
                gSpotifyAppRemote.getPlayerApi().resume();
            } else {
                gSpotifyAppRemote.getPlayerApi().pause();
            }
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void setPlaylist(String pUri){
        try {
            gSpotifyAppRemote.getPlayerApi().play(pUri);
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void skipForward() {
        try {
            String lUri = gPlayer.track.uri;
            gDatabase.addSkipped(lUri);
            if (gDatabase.getSkipped(lUri) >= gSkippedLimit) {
                if (gActivity.deleteFromLiked()) {
                    unlike();
                }
                if (gActivity.deleteFromList()) {
                    removeCurrentFromOriginList();
                }
            }
            gSpotifyAppRemote.getPlayerApi().skipNext();
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void skipBackward() {
        try {
            gSpotifyAppRemote.getPlayerApi().skipPrevious();
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void like() {
        try {
            Track lTrack = gPlayer.track;
            gSpotifyAppRemote.getUserApi().addToLibrary(lTrack.uri);
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void unlike() {
        try {
            Track lTrack = gPlayer.track;
            gSpotifyAppRemote.getUserApi().removeFromLibrary(lTrack.uri);
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void removeCurrentFromOriginList() {
        String[] lRemove = new String[1];
        lRemove[0] = gPlayer.track.uri;
        gRestHandler.removeFromPlaylist(gActivity.getOriginList().id, lRemove);
    }
}


