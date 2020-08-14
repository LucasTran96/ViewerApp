/*
  ClassName: DatabaseURL.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseURL is used to create, add, modify, delete databases, save
  the history URL from the server, use the "URLHistory.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jexpa.secondclone.Model.URL;

import java.util.ArrayList;
import java.util.List;

import static com.jexpa.secondclone.API.APIDatabase.API_Add_Database;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.Database.Entity.URLEntity.COLUMN_CLIENT_URL_TIME;
import static com.jexpa.secondclone.Database.Entity.URLEntity.COLUMN_CREATED_DATE_URL;
import static com.jexpa.secondclone.Database.Entity.URLEntity.COLUMN_DEVICE_ID_URL;
import static com.jexpa.secondclone.Database.Entity.URLEntity.COLUMN_ID_URL;
import static com.jexpa.secondclone.Database.Entity.URLEntity.COLUMN_ROWINDEX_URL;
import static com.jexpa.secondclone.Database.Entity.URLEntity.COLUMN_URL_LINK;
import static com.jexpa.secondclone.Database.Entity.URLEntity.DATABASE_NAME_URL_HISTORY;
import static com.jexpa.secondclone.Database.Entity.URLEntity.DATABASE_VERSION_URL_HISTORY;
import static com.jexpa.secondclone.Database.Entity.URLEntity.TABLE_URL_HISTORY;

public class DatabaseURL extends SQLiteOpenHelper {
    SQLiteDatabase database;

    public DatabaseURL(Context context) {
        // đổi tên
        super(context, DATABASE_NAME_URL_HISTORY, null, DATABASE_VERSION_URL_HISTORY);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i(TAG, "DatabaseNotes.onCreate ... " + TABLE_URL_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_URL_HISTORY + "(" + COLUMN_ROWINDEX_URL + " INTEGER ," + COLUMN_ID_URL + " INTEGER,"
                + COLUMN_DEVICE_ID_URL + " TEXT," + COLUMN_CLIENT_URL_TIME + " TEXT," + COLUMN_URL_LINK + " TEXT,"
                + COLUMN_CREATED_DATE_URL + " TEXT" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_URL_HISTORY);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_Contact(List<URL> url) {
        database = this.getWritableDatabase();

        Log.i("addURL", "dataURL add: " + url.get(0).getID());
        //  contentValues1 receives the value from the method API_Add_Database()
        database.beginTransaction();
        try {
            for (int i = 0; i < url.size(); i++) {
                ContentValues contentValues1 = API_Add_Database(url.get(i),false);
                // Insert a row of data into the table.
                database.insert(TABLE_URL_HISTORY, null, contentValues1);
            }

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }

    }


    public List<URL> getAll_URL_ID_History(String deviceID, int offSet) {
        Log.i(TAG, "DatabaseNotes.getAll_URL_ID_History ... " + TABLE_URL_HISTORY);
        List<URL> url_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_URL_HISTORY + " WHERE Device_ID = '"+ deviceID +"' ORDER BY " + COLUMN_CLIENT_URL_TIME + " DESC LIMIT "+ NumberLoad + " OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                    URL url = new URL();
                    url.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROWINDEX_URL)));
                    url.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_URL)));
                    url.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_URL)));
                    url.setClient_URL_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_URL_TIME)));
                    url.setURL_Link(cursor.getString(cursor.getColumnIndex(COLUMN_URL_LINK)));
                    url.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_URL)));
                    // Add in List.
                    url_List.add(url);

            } while (cursor.moveToNext());
        }
        // return note list
        return url_List;
    }

    // Method retrieving data by date to compare.
    public List<Integer> getAll_URL_ID_History_Date(String deviceID, String date) {

        database = this.getWritableDatabase();
        Log.i(TAG, "DatabaseURL.getAll_URL_ID_History_Date... " + TABLE_URL_HISTORY);
        List<Integer> URL_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_URL_HISTORY + " WHERE " + COLUMN_DEVICE_ID_URL + " = '" + deviceID + "'";
        //SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_URL_TIME)).substring(0, 10).equals(date)) {
                    URL_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_URL)));
                }
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return URL_List;
    }

    public int get_URLCount_DeviceID(String deviceID) {
        Log.i(TAG, "DatabaseURL.get_URLCount_DeviceID ... " + TABLE_URL_HISTORY);
        database = this.getReadableDatabase();
        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.query(TABLE_URL_HISTORY, new String[]{COLUMN_DEVICE_ID_URL
                }, COLUMN_DEVICE_ID_URL + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Contact_History(URL url) {
        Log.i(TAG, "DatabaseNotes.delete_Contact_History ... " + url.getID());
        database = this.getWritableDatabase();
        database.delete(TABLE_URL_HISTORY, COLUMN_ID_URL + " = ?",
                new String[]{String.valueOf(url.getID())});
        database.close();
    }

}
