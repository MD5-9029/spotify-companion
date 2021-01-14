package com.spotifycompanion.management;

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
                ColumnURI + " STRING NOT NULL)";
        pDB.execSQL(lCreateTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * @param pUri uri of the song to be added to database
     */
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

    /**
     * @param pUri uri of the track you seek
     * @return number of times skipped
     */
    public int getSkipped(String pUri) {
        SQLiteDatabase lDb = this.getReadableDatabase();
        String lQuery = "SELECT COUNT(*) FROM " + gTable + " WHERE uri = \"" + pUri + "\";";
        Cursor lCursor = lDb.rawQuery(lQuery, null);


        if (lCursor.moveToFirst()) {
            int lCount = lCursor.getInt(0);
            lDb.close();
            lCursor.close();
            return lCount;
        } else {
            lDb.close();
            lCursor.close();
            return -1;
        }
    }

    /**
     * removes a single entry with matching uri
     *
     * @param pUri uri entry to remove
     */
    public void removeOneSkipped(String pUri) {
        SQLiteDatabase lDb = this.getReadableDatabase();
        String lQuery = "DELETE FROM " + gTable + " WHERE id = (SELECT id FROM " + gTable + " WHERE uri = \"" + pUri + "\" LIMIT 1);";
        lDb.execSQL(lQuery);
        lDb.close();
    }

    /**
     * removes all entries with matching uri
     *
     * @param pUri uri entries to remove
     */

    public void removeAllSkipped(String pUri) {
        SQLiteDatabase lDb = this.getReadableDatabase();
        String lQuery = "DELETE FROM " + gTable + " WHERE uri = \"" + pUri + "\";";
        lDb.execSQL(lQuery);
        lDb.close();
    }

    /**
     * empties entire database
     */
    public void removeAllSkipped() {
        SQLiteDatabase lDb = this.getReadableDatabase();
        String lQuery = "DELETE FROM " + gTable + ";";
        lDb.execSQL(lQuery);
        lDb.close();

    }

}
