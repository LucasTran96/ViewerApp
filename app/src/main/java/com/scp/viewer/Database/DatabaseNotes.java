/*
  ClassName: DatabaseNotes.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseNotes is used to create, add, modify, delete databases, save
  the history Notes from the server, use the "NotesHistory.class" and "NotesHistoryDetail.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.scp.viewer.Model.Notes;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.Global;
import java.util.ArrayList;
import static com.scp.viewer.API.Global.TAG;
import java.util.List;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.NotesEntity.COLUMN_CLIENT_NOTE_TIME;
import static com.scp.viewer.Database.Entity.NotesEntity.COLUMN_CONTENT_NOTE;
import static com.scp.viewer.Database.Entity.NotesEntity.COLUMN_CREATED_DATE_NOTE;
import static com.scp.viewer.Database.Entity.NotesEntity.TABLE_NOTE_HISTORY;

public class DatabaseNotes  {
    private DatabaseHelper database;

    public DatabaseNotes(Context context) {
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_NOTE_HISTORY))
            createTable();
    }

    public void createTable() {

        Log.i(Global.TAG, "DatabaseNotes.onCreate ... " + TABLE_NOTE_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_NOTE_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ," + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT," + COLUMN_CLIENT_NOTE_TIME + " TEXT," + COLUMN_CONTENT_NOTE + " TEXT,"
                + COLUMN_CREATED_DATE_NOTE + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    public void addDevice_Notes(List<Notes> notes) {

        database.getWritableDatabase().beginTransaction();
        Log.i("addNotes", "dataURL add: " + notes.get(0).getID());
        try {
            for (int i = 0; i < notes.size(); i++) {
                //  contentValues1 receives the value from the method API_Add_Database()
                ContentValues contentValues1 = APIDatabase.API_Add_Database(notes.get(i),false);
                // Insert a row of data into the table.
                database.getWritableDatabase().insert(TABLE_NOTE_HISTORY, null, contentValues1);
            }

            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
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

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)).equals(deviceID)) {


                    Notes notes = new Notes();
                    notes.setRowIndex(cursor.getLong(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                    notes.setID(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    notes.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
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
    public List<Long> getAll_Location_ID_History_Date(String deviceID, String date) {

        Log.i(TAG, "DatabaseLocation.getAll_Location_ID_History_Date... " + TABLE_NOTE_HISTORY);
        List<Long> location_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE_HISTORY + " WHERE " + COLUMN_DEVICE_ID + " = '" + deviceID + "'";
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NOTE_TIME)).substring(0, 10).equals(date)) {
                    location_List.add(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
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

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_NOTE_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Contact_History(Notes notes) {
        Log.i(Global.TAG, "DatabaseNotes.delete_Contact_History ... " + notes.getID());

        database.getWritableDatabase().delete(TABLE_NOTE_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(notes.getID())});
        database.close();
    }

}
