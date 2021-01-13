package com.spotifycompanion.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

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
    Toolbar gToolbarTop;
    DrawerLayout gDrawerLayout;
    ImageView gImageView;
    Switch gDeleteFromList, gDeleteFromLiked;
    SharedPreferences gPreferences;
    SharedPreferences.Editor gEditor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllByID();

        gPreferences = getSharedPreferences("spotifyCompanion", MODE_PRIVATE);
        gEditor = getSharedPreferences("spotifyCompanion", MODE_PRIVATE).edit();

        gDrawerLayout.openDrawer(Gravity.LEFT);
        gDrawerLayout.closeDrawer(Gravity.LEFT);
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
        gToolbarTop = findViewById(R.id.tb_top);

        setSupportActionBar(gToolbarTop);
        gDrawerLayout = findViewById(R.id.main_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, gDrawerLayout, gToolbarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                gDeleteFromList = findViewById(R.id.sw_rmList);
                gDeleteFromLiked = findViewById(R.id.sw_rmLiked);

                gDeleteFromLiked.setChecked(gPreferences.getBoolean("liked", false));
                gDeleteFromList.setChecked(gPreferences.getBoolean("list", true));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                gDeleteFromList = findViewById(R.id.sw_rmList);
                gDeleteFromLiked = findViewById(R.id.sw_rmLiked);

                gEditor.putBoolean("list", gDeleteFromList.isChecked());
                gEditor.putBoolean("liked", gDeleteFromLiked.isChecked());
                gEditor.apply();

            }
        };
        gDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        gImageView = findViewById(R.id.iv_mainCover);

    }


    @Override
    public void onBackPressed() {
        if (gDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            gDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    public void togglePlayback(View v) {
        gManagementConnector.togglePlayback();
    }

    public ImageView getCoverView() {
        return gImageView;
    }

    public boolean deleteFromLiked() {
        return gPreferences.getBoolean("liked", false);
    }

    public boolean deleteFromList() {
        return gPreferences.getBoolean("list", true);
    }

    public void skipForward(View view) {
        gManagementConnector.skipForward();
    }

    public void skipBackward(View view) {
        gManagementConnector.skipBackward();
    }

    public void clearSkipped(View view) {
        gManagementConnector.clearSkipped();
    }


    public void rest(View view) {
        try{
            Button lBt = findViewById(R.id.bt_logInOut);
            Log.e("UI", "Button clicked");
            //Playlists lists = gManagementConnector.gRESTHandler.getUserPlaylists();
            //Playlist list = gManagementConnector.gRESTHandler.getPlaylist(lists.items[5].id);

            if(gManagementConnector.isAuthorized()) {
                //logout
                gManagementConnector.disallowAccess(this);
                lBt.setText(R.string.drawer_logIn);
            }else{
                //login
                gManagementConnector.authorizeAccess(this);
                //lBt.setText(R.string.drawer_logOut); //button text change on successful login (onActivityResult)
            }
        } catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
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
