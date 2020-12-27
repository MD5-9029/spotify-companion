package com.spotifycompanion.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.spotifycompanion.Management.ManagementConnector;
import com.spotifycompanion.R;

public class MainActivity extends AppCompatActivity {

    private final ManagementConnector zManagementConnector = new ManagementConnector();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        zManagementConnector.connectRemote(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        zManagementConnector.disconnectRemote();
    }

}
