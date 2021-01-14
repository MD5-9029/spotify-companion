package com.spotifycompanion.management;


import android.util.Log;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.protocol.types.Track;
import com.spotifycompanion.R;
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
    private static final int SKIPPED_LIMIT = 3;
    //time tolerance for noticing a skip event
    private static final long TOLERANCE = 3000;

    private MainActivity gActivity;
    private SpotifyAppRemote gSpotifyAppRemote;

    private PlayerState gPlayer;
    private DatabaseHandler gDatabase;
    private RESTHandler gRestHandler;
    private AuthorizationConfig gAuth = new AuthorizationConfig();

    private long gTime = System.nanoTime() + TOLERANCE;
    private String gPreviousTrackUri, gPlaylistUri;
    private List<Playlist> gPlaylists;
    boolean gAvoidSkip = false;

    /**
     * @param pActivity context to display toast and such
     * @param pDatabase database holding skipped statistics
     * @param pREST     interface for requesting WEB-API content
     */
    public RemoteHandler(MainActivity pActivity, DatabaseHandler pDatabase, RESTHandler pREST) {
        gActivity = pActivity;
        gDatabase = pDatabase;
        gRestHandler = pREST;
    }

    /**
     * connect; links the companion to the official app via IPC
     */
    public void connect() {
        if (gSpotifyAppRemote.isSpotifyInstalled(gActivity)) {
            SpotifyAppRemote.connect(gActivity,
                    new ConnectionParams.Builder(gAuth.CLIENT_ID).setRedirectUri(gAuth.REDIRECT_URI).showAuthView(true).build(),
                    new Connector.ConnectionListener() {
                        public void onConnected(SpotifyAppRemote pSpotifyAppRemote) {
                            gSpotifyAppRemote = pSpotifyAppRemote;
                            subscribeToStates();

                            //setup playback for convenient use
                            gSpotifyAppRemote.getPlayerApi().setRepeat(Repeat.ALL);
                            gSpotifyAppRemote.getPlayerApi().setShuffle(true);
                        }

                        public void onFailure(Throwable throwable) {
                            Log.e("RemoteHandler", throwable.getMessage(), throwable);
                        }
                    });
        } else {
            Toast.makeText(gActivity, gActivity.getString(R.string.toast_rhInstalled), Toast.LENGTH_LONG);
        }
    }

    /**
     * disconnect remote controlling the spotify application
     */
    public void disconnect() {
        SpotifyAppRemote.disconnect(gSpotifyAppRemote);
    }

    /**
     * set callbacks for state-changes
     */
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
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhStated), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * set time the track is supposed to end at
     * used for recognising skips
     */
    private void setTime() {
        try {
            gTime = System.currentTimeMillis() + gPlayer.track.duration - gPlayer.playbackPosition - TOLERANCE;
            gPreviousTrackUri = gPlayer.track.uri;
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhTrackData), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @return uri to current playlist
     */
    public String getPlaylistUri() {
        return gPlaylistUri;
    }

    /**
     * set image view to cover-image of current track
     */
    private void updateImage() {
        try {
            gSpotifyAppRemote.getImagesApi().getImage(gPlayer.track.imageUri).setResultCallback(bitmap -> {
                gActivity.getCoverView().setImageBitmap(bitmap);
            });
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhImage), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * play and pause toggle
     */
    public void togglePlayback() {
        try {
            if (gPlayer.isPaused) {
                gSpotifyAppRemote.getPlayerApi().resume();
            } else {
                gSpotifyAppRemote.getPlayerApi().pause();
            }
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhPlayback), Toast.LENGTH_LONG).show();
        }
        setTime();
    }

    /***
     * set avoidSkip flag to true
     */
    private void lock() {
        gAvoidSkip = true;
    }

    /**
     * set avoidSkip flag to false
     */
    private void unlock() {
        gAvoidSkip = false;
    }

    /**
     * set playlist
     *
     * @param pUri playlist to play from
     */
    public void setPlaylist(String pUri) {
        try {
            lock();
            gSpotifyAppRemote.getPlayerApi().play(pUri);
            skipForward();
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhPlaylist), Toast.LENGTH_LONG).show();
        }
        unlock();
        setTime();
    }

    /**
     * skip to net title in queue or list
     */
    public void skipForward() {
        try {
            gSpotifyAppRemote.getPlayerApi().skipNext();
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhSkip), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * called when a song was skipped
     * checks if and what criteria for removal are met
     */
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
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhRemove), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * called when a song was not skipped
     * will add song according to settings
     */
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
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhAdd), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * retunr to prior track
     */
    public void skipBackward() {
        try {
            lock();
            gSpotifyAppRemote.getPlayerApi().skipPrevious();
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhSkip), Toast.LENGTH_LONG).show();
        }
        unlock();
        setTime();
    }

    /**
     * method for likeing/adding a track to the users library
     */
    public void addCurrentToLibrary() {
        try {
            gSpotifyAppRemote.getUserApi().addToLibrary(gPreviousTrackUri);
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhAdd), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * method for disliking/unliking/removing a track from  the users library
     */
    public void removeCurrentFromLibrary() {
        try {
            Track lTrack = gPlayer.track;
            gSpotifyAppRemote.getUserApi().removeFromLibrary(lTrack.uri);
        } catch (Exception e) {
            Toast.makeText(this.gActivity, gActivity.getString(R.string.toast_rhRemove), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * adds the not skipped track to the designated playlist
     */
    public void addCurrentToDestinationPlaylist() {
        String[] lAdd = new String[1];
        lAdd[0] = gPreviousTrackUri;
        if (!isInList(lAdd[0], gActivity.getDestinationList())) {
            gRestHandler.addToPlaylist(gActivity.getDestinationList().id, lAdd);
        }
    }

    /**
     * @param pAddURi uri to check for in provided list
     * @param pList   list to check in
     * @return true if track is contained in given list otherwise false
     */
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

    /**
     * remove skipped track from current playlist
     */
    public void removeCurrentFromOriginList() {
        String[] lRemove = new String[1];
        lRemove[0] = gPlayer.track.uri;
        gRestHandler.removeFromPlaylist(gActivity.getOriginList().id, lRemove);
    }

    /**
     * @return list of playlist the user has access to
     */
    public List<Playlist> getPlaylists() {
        gPlaylists = Arrays.asList(gRestHandler.getUserPlaylists().items);
        return gPlaylists;
    }
}