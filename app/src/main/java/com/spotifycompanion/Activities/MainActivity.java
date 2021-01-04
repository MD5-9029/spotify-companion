package com.spotifycompanion.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotifycompanion.Management.ManagementConnector;
import com.spotifycompanion.R;

public class MainActivity extends AppCompatActivity {
    private final ManagementConnector zManagementConnector = new ManagementConnector(MainActivity.this);
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

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAuthActivity();
            }
        });

        btnBottomMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zManagementConnector.getRemote().resume();
            }
        });
    }

    private void openAuthActivity() {
        Intent intent = new Intent(MainActivity.this, TestActivity.class);
        startActivity(intent);
    }
}
