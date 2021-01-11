package com.spotifycompanion.Management;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String gDatabase = "companion.db";
    private static final String gTable = "skipped";
    private static final String ColumnURI = "uri";

    public DatabaseHandler(@Nullable Context pContext) {
        super(pContext, gDatabase, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase pDB) {
        String lCreateTableString = "CREATE TABLE skipped (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                ColumnURI +  " STRING NOT NULL)";
        pDB.execSQL(lCreateTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addSkipped(String pUri) {
        SQLiteDatabase lDb = this.getWritableDatabase();
        //String lQuery = "INSERT INTO " + gTable + " (uri) VALUES (" + pUri + ")";
        //lDb.rawQuery(lQuery, null);

        ContentValues lCV = new ContentValues();
        lCV.put(ColumnURI, pUri);
        long insert = lDb.insert(gTable, null, lCV);

        Log.e("DH: test", "insert done");
        lDb.close();
        }

    public int getSkipped(String pUri) {
        SQLiteDatabase lDb = this.getReadableDatabase();
        String lQuery = "SELECT COUNT(*) FROM " + gTable + " WHERE uri = \"" + pUri + "\";";
        Cursor lCursor = lDb.rawQuery(lQuery, null);


        if (lCursor.moveToFirst()) {
            int lCount = lCursor.getInt(0);
            lCursor.close();
            return lCount;
        } else {
            lCursor.close();
            return -1;
        }
    }

}
