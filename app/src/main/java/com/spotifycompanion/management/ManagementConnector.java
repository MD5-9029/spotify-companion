package com.spotifycompanion.management;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotifycompanion.R;
import com.spotifycompanion.activities.MainActivity;
import com.spotifycompanion.models.Playlist;

import java.util.Comparator;
import java.util.List;

/**
 * class containing and managing all components below view
 */
public class ManagementConnector extends Service {
    private MainActivity gActivity;
    private DatabaseHandler gDatabaseHandler;
    private RemoteHandler gRemote;
    public RESTHandler gRESTHandler;
    private boolean gAuthorized = false;
    private List<Playlist> gPlayLists;

    /**
     * constructor for management
     *
     * @param pActivity context from MainActivity
     */
    public ManagementConnector(MainActivity pActivity) {
        gActivity = pActivity;
        gRESTHandler = new RESTHandler(pActivity);
        gDatabaseHandler = new DatabaseHandler(pActivity);
        gRemote = new RemoteHandler(gActivity, gDatabaseHandler, gRESTHandler);
    }

    public void connectRemote() {
        gRemote.connect();
    }

    public void disconnectRemote() {
        gRemote.disconnect();
    }

    public void togglePlayback() {
        gRemote.togglePlayback();
    }

    public void skipForward() {
        gRemote.skipForward();
    }

    public void setPlaylist(String pUri) {
        gRemote.setPlaylist(pUri);
    }

    /***
     * @return position the current playlist has in spinner or 0 if not found
     */
    public int getPlaylistPosition() {
        String lPlaylistUri = gRemote.getPlaylistUri();
        int i = 0;
        for (Playlist list : gPlayLists) {
            if (list.uri.equals(lPlaylistUri)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    /**
     * Attempts to authorize application access
     */
    public void authorizeAccess() {
        this.gRESTHandler.requestToken(gActivity);
    }

    /**
     * Clears Cookies and forces re-login
     */
    public void disallowAccess() {
        gRESTHandler.cancelCall();
        AuthorizationClient.clearCookies(gActivity);
        gAuthorized = false;
    }

    /**
     * Callback from the attempt to authorize application access
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true if access was authorized
     */
    public boolean authorizeCallback(int requestCode, int resultCode, Intent data) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (gRESTHandler.authConfig.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            gRESTHandler.mAccessToken = response.getAccessToken();
            gRESTHandler.mExpiresIn = response.getExpiresIn();
            return gAuthorized = true;
        } else if (gRESTHandler.authConfig.AUTH_CODE_REQUEST_CODE == requestCode) {
            gRESTHandler.mAccessCode = response.getCode();
        }
        return false;
    }

    /**
     * Callback from the attempt to receive an auth code from the API
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true if code was received
     */
    public boolean authCodeCallback(int requestCode, int resultCode, Intent data) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (gRESTHandler.authConfig.AUTH_CODE_REQUEST_CODE == requestCode) {
            gRESTHandler.mAccessCode = response.getCode();
            return true;
        }
        return false;
    }

    public boolean isgAuthorized() {
        return gAuthorized;
    }

    public void skipBackward() {
        gRemote.skipBackward();
    }

    public void clearSkipped() {
        gDatabaseHandler.removeAllSkipped();
    }

    /**
     * fill spinners with names of selectable playlists
     *
     * @param pOrigin      spinner the list of playlists should be displayed in
     * @param pDestination spinner the list of playlists should be displayed in
     */
    public void fillPlaylistsSelection(Spinner pOrigin, Spinner pDestination) {
        //can NOT add library/saved tracks du to no uri being provided (neither api can access this data)
        gPlayLists = gRemote.getPlaylists();
        gPlayLists.sort(new Comparator<Playlist>() {
            @Override
            public int compare(Playlist o1, Playlist o2) {
                return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
            }
        });

        ArrayAdapter<Playlist> lAdapter = new ArrayAdapter(gActivity, android.R.layout.simple_spinner_item, gPlayLists);
        lAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        pOrigin.setAdapter(lAdapter);
        pDestination.setAdapter(lAdapter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        Intent lIntent = new Intent(this, MainActivity.class);
        PendingIntent lPendingIntent = PendingIntent.getActivity(this, 0, lIntent, 0);

        Notification lNotification = new NotificationCompat.Builder(this, getString(R.string.notification_channelID)).
                setContentTitle(getString(R.string.notification_title)).
                setContentText(Integer.toString(gRemote.getCurrentSkipps())).
                setSmallIcon(R.mipmap.ic_launcher).
                setContentIntent(lPendingIntent).
                build();

        startForeground(1, lNotification);
        return START_REDELIVER_INTENT;
    }
}