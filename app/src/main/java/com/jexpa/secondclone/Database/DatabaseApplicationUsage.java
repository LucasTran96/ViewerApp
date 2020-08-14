/*
  ClassName: DatabaseApplicationUsage.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseApplicationUsage is used to create, add, modify, delete databases, save
  the Application Usage values from the server, use the "ApplicationUsageHistory.class".
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
import com.jexpa.secondclone.API.APIDatabase;
import com.jexpa.secondclone.Model.ApplicationUsage;
import com.jexpa.secondclone.API.Global;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.Global.DEFAULT_TIME_END;
import static com.jexpa.secondclone.API.Global.DEFAULT_TIME_START;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_APP_ID_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_APP_NAME_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_APP_TYPE_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_CLIENT_APPLICATION_TIME;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_CREATED_DATE_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_DEVICE_ID_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_ID_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.COLUMN_ROWINDEX_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.DATABASE_NAME_APPLICATION_HISTORY;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.DATABASE_VERSION_APPLICATION_HISTORY;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.TABLE_APPLICATION_HISTORY;

public class DatabaseApplicationUsage extends SQLiteOpenHelper {
    SQLiteDatabase database;

    public DatabaseApplicationUsage(Context context) {

        super(context, DATABASE_NAME_APPLICATION_HISTORY, null, DATABASE_VERSION_APPLICATION_HISTORY);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        Log.i(Global.TAG, "DatabaseApplicationUsage.onCreate ... " + TABLE_APPLICATION_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_APPLICATION_HISTORY + "(" + COLUMN_ROWINDEX_APPLICATION + " INTEGER ," + COLUMN_ID_APPLICATION + " INTEGER,"
                + COLUMN_DEVICE_ID_APPLICATION + " TEXT," + COLUMN_APP_TYPE_APPLICATION + " INTEGER,"
                + COLUMN_APP_NAME_APPLICATION + " TEXT," + COLUMN_CLIENT_APPLICATION_TIME + " TEXT,"
                + COLUMN_APP_ID_APPLICATION + " TEXT," +
                COLUMN_CREATED_DATE_APPLICATION + " TEXT" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATION_HISTORY);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_Application(List<ApplicationUsage> application_usage) {
        database = this.getWritableDatabase();
        database.beginTransaction();

        try {
            for (int i = 0; i < application_usage.size(); i++) {
                //  contentValues1 receives the value from the method API_Add_Database()
                ContentValues contentValues1 = APIDatabase.API_Add_Database(application_usage.get(i),false);
                // Insert a row of data into the table.
                database.insert(TABLE_APPLICATION_HISTORY, null, contentValues1);
            }

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }
        //  Close the database connection.
        database.close();

    }


    public List<ApplicationUsage> getAll_Application_ID_History(String deviceID) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.getAll_URL_ID_History ... " + TABLE_APPLICATION_HISTORY);
        List<ApplicationUsage> notes_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_APPLICATION_HISTORY + " ORDER BY " + COLUMN_CLIENT_APPLICATION_TIME + " DESC ";
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_APPLICATION)).equals(deviceID)) {

                    ApplicationUsage application_usage = new ApplicationUsage();
                    application_usage.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROWINDEX_APPLICATION)));
                    application_usage.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_APPLICATION)));
                    application_usage.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_APPLICATION)));
                    application_usage.setApp_Type(cursor.getInt(cursor.getColumnIndex(COLUMN_APP_TYPE_APPLICATION)));
                    application_usage.setApp_Name(cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME_APPLICATION)));
                    application_usage.setClient_App_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_APPLICATION_TIME)));
                    application_usage.setApp_ID(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID_APPLICATION)));
                    application_usage.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_APPLICATION)));
                    // Add in List.
                    notes_List.add(application_usage);
                }

            } while (cursor.moveToNext());
        }
        // return note list
        return notes_List;
    }

    // Method retrieving data by date to compare.
    public List<Integer> getAll_App_ID_History_Date(String deviceID, String dateStart) {

        Log.i(TAG, "DatabaseApp.getAll_App_ID_History_Date... " + TABLE_APPLICATION_HISTORY);

        List<Integer> app_List = new ArrayList<>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_APPLICATION_HISTORY +" WHERE "+ COLUMN_DEVICE_ID_APPLICATION+ " = '"+deviceID+"'";//+"' AND " +COLUMN_CLIENT_CAPTURED_DATE_PHOTO+" = '"+date+"'", String date
        String selectQuery = "SELECT  * FROM " + TABLE_APPLICATION_HISTORY + " WHERE " + COLUMN_DEVICE_ID_APPLICATION + " = '" + deviceID + "' AND " + COLUMN_CLIENT_APPLICATION_TIME + " BETWEEN " + "'" + dateStart + DEFAULT_TIME_START + "' AND " + "'" + dateStart + DEFAULT_TIME_END + "'";
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                app_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_APPLICATION)));
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return app_List;
    }

    public int get_ApplicationCount_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.get_ApplicationCount_DeviceID ... " + TABLE_APPLICATION_HISTORY);
        database = this.getReadableDatabase();
        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.query(TABLE_APPLICATION_HISTORY, new String[]{COLUMN_DEVICE_ID_APPLICATION
                }, COLUMN_DEVICE_ID_APPLICATION + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Application_History(ApplicationUsage application_usage) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.delete_Application_History... " + application_usage.getID());
        database = this.getWritableDatabase();
        database.delete(TABLE_APPLICATION_HISTORY, COLUMN_ID_APPLICATION + " = ?",
                new String[]{String.valueOf(application_usage.getID())});
        database.close();
    }

}
