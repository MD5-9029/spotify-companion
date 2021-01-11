package com.spotifycompanion.Management;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String gDatabase = "companion.db";
    private static final String gTable = "skipped";

    public DatabaseHandler(@Nullable Context pContext) {
        super(pContext, gDatabase, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase pDB) {
        String lCreateTableString = "CREATE TABLE skipped (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "uri TEXT NOT NULL)";
        pDB.execSQL(lCreateTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addSkipped(String pUri) {
        SQLiteDatabase lDb = this.getWritableDatabase();
        lDb.execSQL("INSERT INTO " + gTable + "(uri) VALUES (" + pUri + ")");
        lDb.close();
    }

    public int getSkipped(String pUri) {
        SQLiteDatabase lDb = this.getReadableDatabase();
        Cursor lC = lDb.rawQuery("SELECT COUNT * FROM" + gTable + "WHERE uri = " + pUri + ";", null);
        if (lC.moveToFirst())
            return lC.getInt(0);
        else
            return -1;
    }

}
