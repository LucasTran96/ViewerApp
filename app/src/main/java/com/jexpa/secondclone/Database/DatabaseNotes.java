/*
  ClassName: DatabaseNotes.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseNotes is used to create, add, modify, delete databases, save
  the history Notes from the server, use the "NotesHistory.class" and "NotesHistoryDetail.class".
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

import com.jexpa.secondclone.Model.Notes;
import com.jexpa.secondclone.API.APIDatabase;
import com.jexpa.secondclone.API.Global;

import java.util.ArrayList;

import static com.jexpa.secondclone.API.Global.TAG;

import java.util.List;

import static com.jexpa.secondclone.Database.Entity.NotesEntity.COLUMN_CLIENT_NOTE_TIME;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.COLUMN_CONTENT_NOTE;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.COLUMN_CREATED_DATE_NOTE;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.COLUMN_DEVICE_ID_NOTE;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.COLUMN_ID_NOTE;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.COLUMN_ROWINDEX_NOTE;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.DATABASE_NAME_NOTE_HISTORY;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.DATABASE_VERSION_NOTE_HISTORY;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.TABLE_NOTE_HISTORY;

public class DatabaseNotes extends SQLiteOpenHelper {
    SQLiteDatabase database;

    public DatabaseNotes(Context context) {
        super(context, DATABASE_NAME_NOTE_HISTORY, null, DATABASE_VERSION_NOTE_HISTORY);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i(Global.TAG, "DatabaseNotes.onCreate ... " + TABLE_NOTE_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_NOTE_HISTORY + "(" + COLUMN_ROWINDEX_NOTE + " INTEGER ," + COLUMN_ID_NOTE + " INTEGER,"
                + COLUMN_DEVICE_ID_NOTE + " TEXT," + COLUMN_CLIENT_NOTE_TIME + " TEXT," + COLUMN_CONTENT_NOTE + " TEXT,"
                + COLUMN_CREATED_DATE_NOTE + " TEXT" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_HISTORY);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_Notes(List<Notes> notes) {
        database = this.getWritableDatabase();
        database.beginTransaction();
        Log.i("addNotes", "dataURL add: " + notes.get(0).getID());
        try {
            for (int i = 0; i < notes.size(); i++) {
                //  contentValues1 receives the value from the method API_Add_Database()
                ContentValues contentValues1 = APIDatabase.API_Add_Database(notes.get(i),false);
                // Insert a row of data into the table.
                database.insert(TABLE_NOTE_HISTORY, null, contentValues1);
            }

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }
        //  Close the database connection.
        database.close();

    }


    public List<Notes> getAll_Notes_ID_History(String deviceID) {
        Log.i(Global.TAG, "DatabaseNotes.getAll_URL_ID_History ... " + TABLE_NOTE_HISTORY);
        List<Notes> notes_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE_HISTORY + " ORDER BY " + COLUMN_CLIENT_NOTE_TIME + " DESC ";
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_NOTE)).equals(deviceID)) {


                    Notes notes = new Notes();
                    notes.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROWINDEX_NOTE)));
                    notes.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_NOTE)));
                    notes.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_NOTE)));
                    notes.setClient_Note_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NOTE_TIME)));
                    notes.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_NOTE)));
                    notes.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_NOTE)));
                    // Add in List.
                    notes_List.add(notes);
                }

            } while (cursor.moveToNext());
        }
        // return note list
        return notes_List;
    }

    // Method retrieving data by date to compare.
    public List<Integer> getAll_Location_ID_History_Date(String deviceID, String date) {

        Log.i(TAG, "DatabaseLocation.getAll_Location_ID_History_Date... " + TABLE_NOTE_HISTORY);
        List<Integer> location_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE_HISTORY + " WHERE " + COLUMN_DEVICE_ID_NOTE + " = '" + deviceID + "'";
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NOTE_TIME)).substring(0, 10).equals(date)) {
                    location_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_NOTE)));
                }
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return location_List;
    }

    public int get_NotesCount_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseNotes.get_NotesCount_DeviceID ... " + TABLE_NOTE_HISTORY);
        database = this.getReadableDatabase();
        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.query(TABLE_NOTE_HISTORY, new String[]{COLUMN_DEVICE_ID_NOTE
                }, COLUMN_DEVICE_ID_NOTE + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Contact_History(Notes notes) {
        Log.i(Global.TAG, "DatabaseNotes.delete_Contact_History ... " + notes.getID());
        database = this.getWritableDatabase();
        database.delete(TABLE_NOTE_HISTORY, COLUMN_ID_NOTE + " = ?",
                new String[]{String.valueOf(notes.getID())});
        database.close();
    }

}
