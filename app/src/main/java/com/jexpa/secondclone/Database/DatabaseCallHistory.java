/*
  ClassName: DatabaseCall.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseCall is used to create, add, modify, delete databases, save
  the history Call from the server, use the "CallHistory.class" and "CallHistoryDetail.class".
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
import com.jexpa.secondclone.Model.CallHistory;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.API_Add_Database;
import static com.jexpa.secondclone.API.Global.DEFAULT_TIME_END;
import static com.jexpa.secondclone.API.Global.DEFAULT_TIME_START;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_CLIENT_CALL_TIME_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_CONTACT_NAME;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_CREATED_DATE;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_DEVICE_ID_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_DIRECTION_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_DURATION_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_ID_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_PHONE_NUMBER_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_PHONE_NUMBER_SIM_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.COLUMN_ROWINDEX_CALL;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.DATABASE_NAME_CALL_HISTORY;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.DATABASE_VERSION_CALL_HISTORY;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.TABLE_CALL_HISTORY;

public class DatabaseCallHistory extends SQLiteOpenHelper {
    SQLiteDatabase database;

    public DatabaseCallHistory(Context context) {
        super(context, DATABASE_NAME_CALL_HISTORY, null, DATABASE_VERSION_CALL_HISTORY);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i(TAG, "DatabaseCall.onCreate ... " + TABLE_CALL_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_CALL_HISTORY + "(" + COLUMN_ROWINDEX_CALL + " INTEGER ," + COLUMN_ID_CALL + " INTEGER,"
                + COLUMN_DEVICE_ID_CALL + " TEXT," + COLUMN_CLIENT_CALL_TIME_CALL + " TEXT," + COLUMN_PHONE_NUMBER_SIM_CALL + " TEXT,"
                + COLUMN_PHONE_NUMBER_CALL + " TEXT," + COLUMN_DIRECTION_CALL + " INTEGER," + COLUMN_DURATION_CALL + " INTEGER," +
                COLUMN_CONTACT_NAME + " TEXT," + COLUMN_CREATED_DATE + " TEXT" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_HISTORY);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_Call_Fast(List<CallHistory> call) {
        database = this.getWritableDatabase();
        database.beginTransaction();
        Log.i("addURL", "dataURLCall add: " + call.get(0).getID());
        try {
            for (int i = 0; i < call.size(); i++) {
                ContentValues contentValues1 = API_Add_Database(call.get(i), false);
                // Insert a row of data into the table.
                database.insert(TABLE_CALL_HISTORY, null, contentValues1);
            }

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }


    }


    public List<CallHistory> getAll_Call_ID_History(String deviceID, int offSet) {
        Log.i(TAG, "DatabaseCall.getAll_Location ... " + TABLE_CALL_HISTORY);
        List<CallHistory> calls_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CALL_HISTORY + " WHERE Device_ID = '"+ deviceID + "' ORDER BY " + COLUMN_CLIENT_CALL_TIME_CALL + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                //if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_CALL)).equals(deviceID)) {
                    CallHistory call = new CallHistory();
                    call.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROWINDEX_CALL)));
                    call.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_CALL)));
                    call.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_CALL)));
                    call.setClient_Call_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CALL_TIME_CALL)));
                    call.setPhone_Number_SIM(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER_SIM_CALL)));
                    call.setPhone_Number(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER_CALL)));
                    call.setDirection(cursor.getInt(cursor.getColumnIndex(COLUMN_DIRECTION_CALL)));
                    call.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION_CALL)));
                    call.setContact_Name(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME)));
                    call.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE)));
                    // Add in List.
                    calls_List.add(call);
                //}

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return calls_List;
    }

    // Method retrieving data by date to compare.
    public List<Integer> getAll_Call_ID_History_Date(String deviceID, String dateStart) {

        Log.i(TAG, "DatabaseCall.getAll_Call_ID_History_Date... " + TABLE_CALL_HISTORY);

        List<Integer> call_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CALL_HISTORY + " WHERE " + COLUMN_DEVICE_ID_CALL + " = '" + deviceID + "' AND " + COLUMN_CLIENT_CALL_TIME_CALL + " BETWEEN " + "'" + dateStart + DEFAULT_TIME_START + "' AND " + "'" + dateStart + DEFAULT_TIME_END + "'";
        // SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) // Add in List.
            do
                call_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_CALL))); while (cursor.moveToNext());
        // return note list
        database.close();
        return call_List;
    }

    public int getCallCount(String deviceID) {
        Log.i(TAG, "DatabaseCall.getCallCount ... " + TABLE_CALL_HISTORY);
        database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_CALL_HISTORY, new String[]{COLUMN_DEVICE_ID_CALL
                }, COLUMN_DEVICE_ID_CALL + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count

        return count;

    }



    public void delete_Call_History(CallHistory call) {
        Log.i(TAG, "DatabaseCall.deleteLocation ... " + call.getDevice_ID());
        database = this.getWritableDatabase();
        database.delete(TABLE_CALL_HISTORY, COLUMN_ID_CALL + " = ?",
                new String[]{String.valueOf(call.getID())});
        database.close();
    }

}
