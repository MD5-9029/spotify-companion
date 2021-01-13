package com.spotifycompanion.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotifycompanion.Management.ManagementConnector;
import com.spotifycompanion.R;
import com.spotifycompanion.models.Playlist;
import com.spotifycompanion.models.Playlists;
import com.spotifycompanion.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private final ManagementConnector gManagementConnector = new ManagementConnector(MainActivity.this);
    Button btBottomLeft, btBottomRight, btLogInOut, btBottomMiddle;
    Toolbar tbTop;
    DrawerLayout dlLeft;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllByID();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //allow synchronous web calls in main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        gManagementConnector.initialize();
        gManagementConnector.authorizeAccess(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gManagementConnector.close();
    }

    private void getAllByID() {
        tbTop = findViewById(R.id.toolbar);

        setSupportActionBar(tbTop);
        dlLeft = findViewById(R.id.main_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlLeft, tbTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlLeft.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public void onBackPressed() {
        if (dlLeft.isDrawerOpen(Gravity.LEFT)) {
            dlLeft.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    public void resume(View v) {
        gManagementConnector.resumePlayback();
    }

    public void like(View view) {
        gManagementConnector.likeCurrentTrack(view);
    }

    public void unLike(View view) {
        gManagementConnector.unlikeCurrentTrack();
    }

    public void clearSkipped(View view) {
        gManagementConnector.clearSkipped();
    }


    public void rest(View view) {
        Button lBt = findViewById(R.id.bt_logInOut);
        Log.e("UI", "Button clicked");
        //Playlists lists = gManagementConnector.gRESTHandler.getUserPlaylists();
        //Playlist list = gManagementConnector.gRESTHandler.getPlaylist(lists.items[3].id);

        if(gManagementConnector.isAuthorized()) {
            //logout
            gManagementConnector.disallowAccess(this);
            lBt.setText(R.string.drawer_logIn);
        }else{
            //login
            gManagementConnector.authorizeAccess(this);
            //lBt.setText(R.string.drawer_logOut); //button text change on successful login (onActivityResult)
        }
    }

    public void onClearCredentialsClicked() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Button lBt = findViewById(R.id.bt_logInOut);
        if (requestCode == gManagementConnector.AUTH_TOKEN_REQUEST_CODE) {
            boolean hasAccess = gManagementConnector.authorizeCallback(requestCode, resultCode, data);
            if (hasAccess)lBt.setText(R.string.drawer_logOut);
        }
    }
}
