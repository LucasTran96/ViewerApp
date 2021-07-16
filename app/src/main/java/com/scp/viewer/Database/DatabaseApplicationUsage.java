/*
  ClassName: DatabaseApplicationUsage.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseApplicationUsage is used to create, add, modify, delete databases, save
  the Application Usage values from the server, use the "ApplicationUsageHistory.class".
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
import com.scp.viewer.Model.ApplicationUsage;
import com.scp.viewer.API.Global;
import java.util.ArrayList;
import java.util.List;

import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_APP_ID_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_APP_NAME_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_APP_TYPE_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_CLIENT_APPLICATION_TIME;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_CREATED_DATE_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.TABLE_APPLICATION_HISTORY;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;

public class DatabaseApplicationUsage
{

    private Context context;
    private DatabaseHelper database;
    public DatabaseApplicationUsage(Context context) {

        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_APPLICATION_HISTORY))
            createTable();
    }

    private void createTable() {


        Log.i(Global.TAG, "DatabaseApplicationUsage.onCreate ... " + TABLE_APPLICATION_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_APPLICATION_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ," + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT," + COLUMN_APP_TYPE_APPLICATION + " INTEGER,"
                + COLUMN_APP_NAME_APPLICATION + " TEXT," + COLUMN_CLIENT_APPLICATION_TIME + " TEXT,"
                + COLUMN_APP_ID_APPLICATION + " TEXT," +
                COLUMN_CREATED_DATE_APPLICATION + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }


    public void addDevice_Application(List<ApplicationUsage> application_usage) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < application_usage.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_APPLICATION_HISTORY, COLUMN_DEVICE_ID, application_usage.get(i).getDevice_ID(), COLUMN_ID, application_usage.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(application_usage.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_APPLICATION_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    public List<ApplicationUsage> getAll_Application_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.getAll_URL_ID_History ... " + TABLE_APPLICATION_HISTORY);
        List<ApplicationUsage> notes_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_APPLICATION_HISTORY + " WHERE Device_ID = '"+ deviceID + "' ORDER BY " + COLUMN_CLIENT_APPLICATION_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                    ApplicationUsage application_usage = new ApplicationUsage();
                    application_usage.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                    application_usage.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    application_usage.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                    application_usage.setApp_Type(cursor.getInt(cursor.getColumnIndex(COLUMN_APP_TYPE_APPLICATION)));
                    application_usage.setApp_Name(cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME_APPLICATION)));
                    application_usage.setClient_App_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_APPLICATION_TIME)));
                    application_usage.setApp_ID(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID_APPLICATION)));
                    application_usage.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_APPLICATION)));

                    // Add in List.
                    notes_List.add(application_usage);

            } while (cursor.moveToNext());
        }
        // return note list
        return notes_List;
    }

    public int get_ApplicationCount_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.get_ApplicationCount_DeviceID ... " + TABLE_APPLICATION_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_APPLICATION_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Application_History(ApplicationUsage application_usage) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.delete_Application_History... " + application_usage.getID());

        database.getWritableDatabase().delete(TABLE_APPLICATION_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(application_usage.getID())});
        database.close();
    }

}
