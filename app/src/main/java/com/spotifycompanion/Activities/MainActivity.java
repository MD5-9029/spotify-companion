package com.spotifycompanion.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotifycompanion.Management.ManagementConnector;
import com.spotifycompanion.R;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllByID();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestToken();
        gManagementConnector.initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gManagementConnector.close();
    }

    private void getAllByID() {
        gToolbarTop = findViewById(R.id.toolbar);

        setSupportActionBar(gToolbarTop);
        gDrawerLayout = findViewById(R.id.main_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, gDrawerLayout, gToolbarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

    public void resume(View v) {
        gManagementConnector.resumePlayback();

        //relocate to some listener callback for automization
        gManagementConnector.updateImage(gImageView);
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
        if(gManagementConnector.isAuthorized()) {
            cancelCall();
            onClearCredentialsClicked();

            lBt.setText(R.string.drawer_logIn);
        }else{
            getUserProfile();

            lBt.setText(R.string.drawer_logOut);
        }
    }


    public static final String CLIENT_ID = "4234dd4558284817abdb7c7ecc4d7df7";
    public static final String REDIRECT_URI = "spotifyCompanion://authCall";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private Call mCall;


    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    public void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    public void requestToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    public void requestCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request);
    }

    public void onClearCredentialsClicked() {
        AuthorizationClient.clearCookies(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();

        }
    }


    public void getUserProfile() {
        if (mAccessToken == null) {
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //setResponse("Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.e("callback", jsonObject.toString());
                } catch (JSONException e) {
                    //setResponse("Failed to parse data: " + e);
                }
            }
        });
    }

}
