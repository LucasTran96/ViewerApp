/*
  ClassName: DatabaseCall.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseCall is used to create, add, modify, delete databases, save
  the history Call from the server, use the "CallHistory.class" and "CallHistoryDetail.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.cp9.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.jexpa.cp9.Model.Call;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.cp9.API.APIDatabase.API_Add_Database;
import static com.jexpa.cp9.API.Global.DEFAULT_TIME_END;
import static com.jexpa.cp9.API.Global.DEFAULT_TIME_START;
import static com.jexpa.cp9.API.Global.TAG;
import static com.jexpa.cp9.API.Global.NumberLoad;
import static com.jexpa.cp9.Database.DatabaseContact.checkItemExist;
import static com.jexpa.cp9.Database.DatabaseHelper.getInstance;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_CLIENT_CALL_TIME_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_CONTACT_NAME;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_CREATED_DATE;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_DEVICE_ID_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_DIRECTION_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_DURATION_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_ID_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_PHONE_NUMBER_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_PHONE_NUMBER_SIM_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.COLUMN_ROWINDEX_CALL;
import static com.jexpa.cp9.Database.Entity.CallHistoryEntity.TABLE_CALL_HISTORY;

public class DatabaseCallHistory {

    private Context context;
    private DatabaseHelper database;
    public DatabaseCallHistory(Context context) {
        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_CALL_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(TAG, "DatabaseCall.onCreate ... " + TABLE_CALL_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_CALL_HISTORY + "(" + COLUMN_ROWINDEX_CALL + " LONG ," + COLUMN_ID_CALL + " LONG,"
                + COLUMN_DEVICE_ID_CALL + " TEXT," + COLUMN_CLIENT_CALL_TIME_CALL + " TEXT," + COLUMN_PHONE_NUMBER_SIM_CALL + " TEXT,"
                + COLUMN_PHONE_NUMBER_CALL + " TEXT," + COLUMN_DIRECTION_CALL + " INTEGER," + COLUMN_DURATION_CALL + " INTEGER," +
                COLUMN_CONTACT_NAME + " TEXT," + COLUMN_CREATED_DATE + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    public void addDevice_Call_Fast(List<Call> call) {

        database.getWritableDatabase().beginTransaction();
        Log.i("addURL", "dataURLCall add: " + call.get(0).getID());
        try {
            for (int i = 0; i < call.size(); i++) {

                if(!checkItemExist(database.getWritableDatabase(),TABLE_CALL_HISTORY,COLUMN_DEVICE_ID_CALL,call.get(i).getDevice_ID(),COLUMN_ID_CALL,call.get(i).getID()))
                {
                    ContentValues contentValues1 = API_Add_Database(call.get(i), false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_CALL_HISTORY, null, contentValues1);
                }
            }

            database.getWritableDatabase().setTransactionSuccessful();
        } finally {
            database.getWritableDatabase().endTransaction();
        }
    }


    public List<Call> getAll_Call_ID_History(String deviceID, int offSet) {
        Log.i(TAG, "DatabaseCall.getAll_Location ... " + TABLE_CALL_HISTORY);
        List<Call> calls_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CALL_HISTORY + " WHERE Device_ID = '"+ deviceID + "' ORDER BY " + COLUMN_CLIENT_CALL_TIME_CALL + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                //if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_CALL)).equals(deviceID)) {
                    Call call = new Call();
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

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

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

        Cursor cursor = database.getWritableDatabase().query(TABLE_CALL_HISTORY, new String[]{COLUMN_DEVICE_ID_CALL
                }, COLUMN_DEVICE_ID_CALL + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count

        return count;

    }



    public void delete_Call_History(Call call) {
        Log.i(TAG, "DatabaseCall.deleteLocation ... " + call.getDevice_ID());

        database.getWritableDatabase().delete(TABLE_CALL_HISTORY, COLUMN_ID_CALL + " = ?",
                new String[]{String.valueOf(call.getID())});
        database.close();
    }

}
