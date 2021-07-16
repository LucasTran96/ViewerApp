/*
  ClassName: DatabaseNetwork.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseNetwork is used to create, add, modify, delete databases, save
  the Network values from the server, use the "NetworkHistory.class".
  History:2018-10-08
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
import com.scp.viewer.Model.Clipboard;
import com.scp.viewer.Model.Networks;

import java.util.ArrayList;
import java.util.List;

import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_CLIENT_CLIPBOARD_TIME;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_CONTENT_CLIPBOARD;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_CREATED_DATE_CLIPBOARD;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_FROM_APP_CLIPBOARD;
import static com.scp.viewer.Database.Entity.ClipboardEntity.TABLE_CLIPBOARD_HISTORY;
import static com.scp.viewer.Database.Entity.NetworkEntity.COLUMN_CREATED_DATE_NETWORK;
import static com.scp.viewer.Database.Entity.NetworkEntity.COLUMN_NETWORK_NAME;
import static com.scp.viewer.Database.Entity.NetworkEntity.COLUMN_NETWORK_STATUS;
import static com.scp.viewer.Database.Entity.NetworkEntity.COLUMN_NETWORK_TIME;
import static com.scp.viewer.Database.Entity.NetworkEntity.COLUMN_NETWORK_TYPE;
import static com.scp.viewer.Database.Entity.NetworkEntity.TABLE_NETWORK_HISTORY;

public class DatabaseNetwork
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseNetwork(Context context) {

        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_NETWORK_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(Global.TAG, "DatabaseNetwork.onCreate ... " + TABLE_NETWORK_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_NETWORK_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ,"
                + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT,"
                + COLUMN_NETWORK_NAME + " TEXT,"
                + COLUMN_NETWORK_TIME + " TEXT,"
                + COLUMN_NETWORK_TYPE + " INTEGER,"
                + COLUMN_NETWORK_STATUS + " INTEGER,"
                + COLUMN_CREATED_DATE_NETWORK + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

   /* public static final String COLUMN_NETWORK_NAME = "Network_Connection_Name";
    public static final String COLUMN_NETWORK_TIME = "Client_Network_Connection_Time";
    public static final String COLUMN_NETWORK_TYPE = "Network_Type";
    public static final String COLUMN_NETWORK_STATUS = "Status";
    public static final String COLUMN_CREATED_DATE_NETWORK = "Created_Date";*/

    public void addNetwork(List<Networks> networksList) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < networksList.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_NETWORK_HISTORY,
                        COLUMN_DEVICE_ID, networksList.get(i).getDevice_ID(),
                        COLUMN_ID, networksList.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(networksList.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_NETWORK_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    public List<Networks> getAll_Network_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseNetwork.getAll_Networks_ID_History ... " + TABLE_NETWORK_HISTORY);
        List<Networks> networksArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NETWORK_HISTORY + " WHERE Device_ID = '"+ deviceID
                + "' ORDER BY " + COLUMN_NETWORK_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                Networks networks = new Networks();
                    networks.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                    networks.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    networks.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                    networks.setNetwork_Connection_Name(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORK_NAME)));
                    networks.setClient_Network_Connection_Time(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORK_TIME)));
                    networks.setNetwork_Type(cursor.getInt(cursor.getColumnIndex(COLUMN_NETWORK_TYPE)));
                    networks.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_NETWORK_STATUS)));
                    networks.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_NETWORK)));

               /* + COLUMN_ID + " LONG,"
                        + COLUMN_DEVICE_ID + " TEXT,"
                        + COLUMN_NETWORK_NAME + " TEXT,"
                        + COLUMN_NETWORK_TIME + " TEXT,"
                        + COLUMN_NETWORK_TYPE + " INTEGER,"
                        + COLUMN_NETWORK_STATUS + " INTEGER,"
                        + COLUMN_CREATED_DATE_NETWORK + " TEXT" + ")";*/
                networksArrayList.add(networks);

            } while (cursor.moveToNext());
        }
        // return note list
        return networksArrayList;
    }

    public int get_Network_Count_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseNetwork.get_NetworksCount_DeviceID ... " + TABLE_NETWORK_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_NETWORK_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Network_History(Networks networks) {
        Log.i(Global.TAG, "DatabaseNetwork.delete_Application_History... " + networks.getID());

        database.getWritableDatabase().delete(TABLE_NETWORK_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(networks.getID())});
        database.close();
    }

}
