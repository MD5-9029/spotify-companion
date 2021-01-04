package com.spotifycompanion.Management;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static String gDatabase = "companion.db";

    public DatabaseHandler(@Nullable Context pContext) {
        super(pContext, gDatabase, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase pDB) {
        String lCreateTableString = "CREATE TABLE skipped (id int PRIMARY KEY AUTOINCREMENT)";
        pDB.execSQL(lCreateTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
