/*
  ClassName: DatabaseLastUpdate.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseLastUpdate is used to create, add, modify, delete databases, save
  the history Time last update from the server, use the "Dashboard.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.scp.viewer.Model.LastTimeGetUpdate;

import static com.scp.viewer.API.APIDatabase.API_Add_Database;
import static com.scp.viewer.API.Global.TAG;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_AMBIENT_VOICE_RECORDING;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_APPLICATION;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_APP_INSTALLATION;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_BBM;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CALENDAR;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CALL;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CLIPBOARD;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CONTACT;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_DEVICE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_FACEBOOK;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_HANGOUTS;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_KEYLOGGER;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_KIK;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_LINE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_LOCATION;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_NETWORK;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_NOTES;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_PHONE_CALL_RECORDING;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_PHOTO;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_SKYPE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_SMS;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_URL;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_VIBER;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_VIDEO;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_VOICE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_WHATSAPP;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_YOUTUBE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_PUSH_UPDATE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class DatabaseLastUpdate
{
    private DatabaseHelper database;

    public DatabaseLastUpdate(Context context) {
        this.database = getInstance(context);
        createTable();
    }

    // Create tables name Device.
    public void createTable() {

            String scriptTable_LastGetUpdate = "CREATE TABLE " + TABLE_LAST_UPDATE + "("
                + COLUMN_LAST_DEVICE + " TEXT ," +
                    COLUMN_LAST_CALL + " TEXT ," +
                    COLUMN_LAST_SMS + " TEXT," +
                    COLUMN_LAST_LOCATION + " TEXT," +
                    COLUMN_LAST_URL + " TEXT," +
                    COLUMN_LAST_CONTACT + " TEXT," +
                    COLUMN_LAST_PHOTO + " TEXT," +
                    COLUMN_LAST_APPLICATION + " TEXT," +
                COLUMN_LAST_PHONE_CALL_RECORDING + " TEXT," +
                    COLUMN_LAST_WHATSAPP + " TEXT," +
                    COLUMN_LAST_VIBER + " TEXT," +
                    COLUMN_LAST_FACEBOOK + " TEXT," +
                    COLUMN_LAST_SKYPE + " TEXT," +
                COLUMN_LAST_NOTES + " TEXT," +
                    COLUMN_LAST_VIDEO + " TEXT," +
                    COLUMN_LAST_VOICE + " TEXT," +
                    COLUMN_LAST_AMBIENT_VOICE_RECORDING + " TEXT," +
                    COLUMN_LAST_KEYLOGGER + " TEXT," +
                    COLUMN_LAST_HANGOUTS + " TEXT," +
                COLUMN_LAST_BBM + " TEXT," +
                    COLUMN_LAST_LINE + " TEXT," +
                    COLUMN_LAST_KIK + " TEXT," +
                    COLUMN_LAST_APP_INSTALLATION + " TEXT," +
                    COLUMN_LAST_CLIPBOARD + " TEXT," +
                    COLUMN_LAST_CALENDAR + " TEXT," +
                    COLUMN_LAST_YOUTUBE + " TEXT," +
                    COLUMN_LAST_NETWORK + " TEXT" + ")";

        String scriptTable_LastPushUpdate = "CREATE TABLE " + TABLE_LAST_PUSH_UPDATE + "("
                + COLUMN_LAST_DEVICE + " TEXT ," +
                COLUMN_LAST_CALL + " TEXT ," +
                COLUMN_LAST_SMS + " TEXT," +
                COLUMN_LAST_LOCATION + " TEXT," +
                COLUMN_LAST_URL + " TEXT," +
                COLUMN_LAST_CONTACT + " TEXT," +
                COLUMN_LAST_PHOTO + " TEXT," +
                COLUMN_LAST_APPLICATION + " TEXT," +
                COLUMN_LAST_PHONE_CALL_RECORDING + " TEXT," +
                COLUMN_LAST_WHATSAPP + " TEXT," +
                COLUMN_LAST_VIBER + " TEXT," +
                COLUMN_LAST_FACEBOOK + " TEXT," +
                COLUMN_LAST_SKYPE + " TEXT," +
                COLUMN_LAST_NOTES + " TEXT," +
                COLUMN_LAST_VIDEO + " TEXT," +
                COLUMN_LAST_VOICE + " TEXT," +
                COLUMN_LAST_AMBIENT_VOICE_RECORDING + " TEXT," +
                COLUMN_LAST_KEYLOGGER + " TEXT," +
                COLUMN_LAST_HANGOUTS + " TEXT," +
                COLUMN_LAST_BBM + " TEXT," +
                COLUMN_LAST_LINE + " TEXT," +
                COLUMN_LAST_KIK + " TEXT," +
                COLUMN_LAST_APP_INSTALLATION + " TEXT," +
                COLUMN_LAST_CLIPBOARD + " TEXT," +
                COLUMN_LAST_CALENDAR + " TEXT," +
                COLUMN_LAST_YOUTUBE + " TEXT," +
                COLUMN_LAST_NETWORK + " TEXT" + ")";

        if(!database.checkTableExist(TABLE_LAST_UPDATE))
        {
            // Run create table command.
            database.getWritableDatabase().execSQL(scriptTable_LastGetUpdate);
        }
        if(!database.checkTableExist(TABLE_LAST_PUSH_UPDATE))
        {
            //sqLiteDatabase.execSQL(scriptTable_LastGetUpdateOld);
            database.getWritableDatabase().execSQL(scriptTable_LastPushUpdate);
        }

    }

    // The method
    // the object to a row in TABLE_LAST_UPDATE
    public void addLast_Time_Get_Update(LastTimeGetUpdate last_time_get_update) {

        //  contentValues1 receives the value from the method API_Add_Database()
        ContentValues contentValues1 = API_Add_Database(last_time_get_update,false);
        // Insert a row of data into the table.
        database.getWritableDatabase().insert(TABLE_LAST_UPDATE, null, contentValues1);
        //  Close the database connection.
        database.close();
    }

    // The method of retrieving the time value of a feature (eg, SMS, Call, Facebook)
    public String getLast_Time_Update(String name, String nameTable, String nameDeviceID) {
        Log.i(TAG, "DatabaseLastUpdate.getLast_Time_Update ... " + nameTable);
        String time = "";
        //SQLiteDatabase database = this.getWritableDatabase();

        // Cursor cursor = database.rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().query(TABLE_LAST_UPDATE, new String[]{name
                }, COLUMN_LAST_DEVICE + "=?",
                new String[]{String.valueOf(nameDeviceID)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                time = cursor.getString(cursor.getColumnIndex(name));
            } while (cursor.moveToNext());
        }
        // return note list
        return time;
    }

    // Update 1 last feature (eg SMS, Call, Facebook)
    public void update_Last_Time_Get_Update(String table, String name, String value, String nameDeviceID) {

        // contentValues1 receives the value from the method API_Add_Database()
        Log.d("min_time_Update", name + " = " + value + "");
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(name, value);

        // Update a row of data into the table.
        //database.update(table, contentValues1, name+" = "+value, null);
        //database.update(table, contentValues1, name+"=? ", new String[]{valueOld});
        //UPDATE Students
        //SET DepartmentId = 3
        //WHERE StudentId = 6;
        //database.update(table, contentValues1, LAST_DEVICE+" = "+ nameDeviceID  , null);
        database.getWritableDatabase().update(table, contentValues1, COLUMN_LAST_DEVICE + " = ?",
                new String[]{String.valueOf(nameDeviceID)});
        //  Close the database connection.
        database.close();
    }

    // The method checks whether the device object exists or not
    public int test_Last_Time_Get_Update(String deviceID) {
        Log.i(TAG, "DatabaseLastUpdate.getNotesCount ... ");

        Cursor cursor = database.getWritableDatabase().query(TABLE_LAST_UPDATE, new String[]{COLUMN_LAST_DEVICE, COLUMN_LAST_CALL
                }, COLUMN_LAST_DEVICE + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        //        Cursor cursor = database.rawQuery(countQuery, null,null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


}
