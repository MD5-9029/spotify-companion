package com.spotifycompanion.Management;

import com.spotifycompanion.Activities.MainActivity;

public class ManagementConnector {
    private RemoteHandler zRemote = new RemoteHandler();
    private DatabaseHandler zDatabaseHandler = new DatabaseHandler();
    private DataParser zDataParser = new DataParser();

    public void connectRemote(MainActivity pActivity) {
        zRemote.connect(pActivity);
    }

    public void disconnectRemote() {
        zRemote.disconnect();
    }

}