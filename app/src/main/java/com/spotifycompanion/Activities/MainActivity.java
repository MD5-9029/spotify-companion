package com.spotifycompanion.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotifycompanion.Management.ManagementConnector;
import com.spotifycompanion.R;

public class MainActivity extends AppCompatActivity {
    private final ManagementConnector zManagementConnector = new ManagementConnector(MainActivity.this);

    Button btnBottomLeft, getBtnBottomRight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllByID();
        registerListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        zManagementConnector.connectRemote();
    }

    @Override
    protected void onStop() {
        super.onStop();
        zManagementConnector.disconnectRemote();
    }


    private void getAllByID() {
        btnBottomLeft = findViewById(R.id.btn_bottonLeft);
        getBtnBottomRight = findViewById(R.id.btn_bottomRight);
    }

    private void registerListeners() {

        btnBottomLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zManagementConnector.getRemote().addCurrentToLibrary();
            }
        });

        getBtnBottomRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zManagementConnector.getRemote().removeCurrentFromLibrary();
            }
        });
    }
}
