package com.spotifycompanion.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotifycompanion.Management.gManagementConnector;
import com.spotifycompanion.R;

public class MainActivity extends AppCompatActivity {
    private final gManagementConnector zManagementConnector = new gManagementConnector(MainActivity.this);
    Button btnBottomLeft, btnBottomRight, btnAuth, btnBottomMiddle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllByID();
        registerListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        zManagementConnector.initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        zManagementConnector.close();
    }

    private void getAllByID() {
        btnBottomLeft = findViewById(R.id.btn_bottonLeft);
        btnBottomRight = findViewById(R.id.btn_bottomRight);
        btnAuth = findViewById(R.id.btn_authActivity);
        btnBottomMiddle = findViewById(R.id.btn_bottomMiddle);
    }


    private void registerListeners() {

        btnBottomLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zManagementConnector.getRemote().like();
            }
        });

        btnBottomRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zManagementConnector.getRemote().unlike();
            }
        });


        btnBottomMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zManagementConnector.getRemote().resume();
            }
        });

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zManagementConnector.auth();
            }
        });
    }

}
