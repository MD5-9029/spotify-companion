package com.spotifycompanion.management;


import android.util.Log;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.protocol.types.Track;
import com.spotifycompanion.activities.MainActivity;
import com.spotifycompanion.models.Playlist;
import com.spotifycompanion.models.PlaylistTrack;

import java.util.Arrays;
import java.util.List;

/**
 * remote handler manages interaction (requests) with the main app.
 * relays requests to spotify
 */
public class RemoteHandler {
    private static final String gClientID = "4234dd4558284817abdb7c7ecc4d7df7";
    private static final String gRedirectURI = "spotifyCompanion://authCall";


    private static final int SKIPPED_LIMIT = 3;
    //time tolerance for noticing a skip event
    private static final long TOLERANCE = 3000;

    private MainActivity gActivity;
    private SpotifyAppRemote gSpotifyAppRemote;

    private PlayerState gPlayer;
    private DatabaseHandler gDatabase;
    private RESTHandler gRestHandler;

    private long gTime;
    private String gPreviousTrackUri, gPlaylistUri;
    private List<Playlist> gPlaylists;
    boolean gAvoidSkip = false;


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

                if (!gAvoidSkip && !gPlayer.track.uri.equals(gPreviousTrackUri) && gTime > System.currentTimeMillis()) {
                    remove();
                } else if (!gPlayer.track.uri.equals(gPreviousTrackUri) && gTime < System.currentTimeMillis()) {
                    add();
                }

                updateImage();
                setTime();
            });
            gSpotifyAppRemote.getPlayerApi().subscribeToPlayerContext().setEventCallback(playerContext -> {
                gPlaylistUri = playerContext.uri;
            });
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    private void setTime() {
        //Todo: ive caught a nullpointer exception when the user closes the sidepanel immediately after opening it
        try {
            gTime = System.currentTimeMillis() + gPlayer.track.duration - gPlayer.playbackPosition - TOLERANCE;
            gPreviousTrackUri = gPlayer.track.uri;
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public String getPlaylistUri() {
        return gPlaylistUri;
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
        setTime();
    }

    /***
     * set avoidSkip flag to true
     */
    private void lock(){
        gAvoidSkip = true;
    }

    /**
     * set avoidSkip flag to false
     */
    private void unlock(){
        gAvoidSkip = false;
    }

    public void setPlaylist(String pUri) {
        try {
            lock();
            gSpotifyAppRemote.getPlayerApi().play(pUri);

        } catch (Exception e) {

            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
        unlock();
        setTime();
    }

    public void skipForward() {
        try {
            gSpotifyAppRemote.getPlayerApi().skipNext();
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void remove() {
        try {
            String lUri = gPreviousTrackUri;
            gDatabase.addSkipped(lUri);
            if (gDatabase.getSkipped(lUri) >= SKIPPED_LIMIT) {
                if (gActivity.deleteFromLiked()) {
                    removeCurrentFromLibrary();
                }
                if (gActivity.deleteFromList()) {
                    removeCurrentFromOriginList();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void add() {
        try {
            String lUri = gPreviousTrackUri;
            gDatabase.removeAllSkipped(lUri);

            if (gActivity.deleteFromLiked()) {
                addCurrentToLibrary();
            }
            if (gActivity.deleteFromList()) {
                addCurrentToDestinationPlaylist();
            }
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void skipBackward() {
        try {
            lock();
            gSpotifyAppRemote.getPlayerApi().skipPrevious();
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
        unlock();
        setTime();
    }

    public void addCurrentToLibrary() {
        try {
            gSpotifyAppRemote.getUserApi().addToLibrary(gPreviousTrackUri);
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void removeCurrentFromLibrary() {
        try {
            Track lTrack = gPlayer.track;
            gSpotifyAppRemote.getUserApi().removeFromLibrary(lTrack.uri);
        } catch (Exception e) {
            Toast.makeText(this.gActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void addCurrentToDestinationPlaylist() {
        String[] lAdd = new String[1];
        lAdd[0] = gPreviousTrackUri;
        if (!isInList(lAdd[0], gActivity.getDestinationList())) {
            gRestHandler.addToPlaylist(gActivity.getDestinationList().id, lAdd);
        }
    }

    private boolean isInList(String pAddURi, Playlist pList) {
        try {
            for (PlaylistTrack current : gRestHandler.getPlaylist(pList.id).tracks) {
                if (current.track.uri.equals(pAddURi)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().toString(), e.toString());
        }
        return false;
    }

    private String getPlaylistID() throws Exception {
        String lPlaylistUri = getPlaylistUri();
        int i = 0;
        for (Playlist list : gPlaylists) {
            if (list.uri.equals(lPlaylistUri)) {
                return list.id;
            }
            i++;
        }
        throw new Exception();
    }

    public void removeCurrentFromOriginList() {
        String[] lRemove = new String[1];
        lRemove[0] = gPlayer.track.uri;
        gRestHandler.removeFromPlaylist(gActivity.getOriginList().id, lRemove);
    }

    public List<Playlist> getPlaylists() {
        gPlaylists = Arrays.asList(gRestHandler.getUserPlaylists().items);
        return gPlaylists;
    }
}


