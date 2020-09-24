/*
  ClassName: DatabasePhoneCallRecord.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabasePhoneCallRecord is used to create, add, modify, delete databases, save
  the history Phone Call Recording from the server, use the "PhoneCallRecordHistory.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.jexpa.cp9.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.jexpa.cp9.Model.AudioGroup;
import com.jexpa.cp9.Model.PhoneCallRecordJson;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.cp9.API.APIDatabase.API_Add_Database;
import static com.jexpa.cp9.API.Global.TAG;
import static com.jexpa.cp9.API.Global.NumberLoad;
import static com.jexpa.cp9.Database.DatabaseContact.checkItemExist;
import static com.jexpa.cp9.Database.DatabaseHelper.getInstance;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_AUDIO_NAME_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_AUDIO_SIZE_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_CDN_URL_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_CONTACT_NAME_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_CONTENT_TYPE_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_CREATED_DATE_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_DEVICE_ID_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_DIRECTION_TYPE_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_DURATION_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_EXT_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_ID_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_ISSAVED_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_MEDIA_URL_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_PHONE_NUMBER_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.COLUMN_ROWINDEX_PHONECALLRECORD;
import static com.jexpa.cp9.Database.Entity.PhoneCallRecordEntity.TABLE_PHONECALLRECORD_HISTORY;

public class DatabasePhoneCallRecord {

    private DatabaseHelper database;

    public DatabasePhoneCallRecord(Context context) {
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_PHONECALLRECORD_HISTORY))
            createTable();
    }


    public void createTable() {

        Log.i(TAG, "DatabasePhoneCallRecord.onCreate ... " + TABLE_PHONECALLRECORD_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_PHONECALLRECORD_HISTORY + "(" + COLUMN_ROWINDEX_PHONECALLRECORD + " LONG ," + COLUMN_ID_PHONECALLRECORD + " LONG,"
                + COLUMN_ISSAVED_PHONECALLRECORD + " INTEGER," + COLUMN_DEVICE_ID_PHONECALLRECORD + " TEXT," + COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD + " TEXT," + COLUMN_AUDIO_NAME_PHONECALLRECORD + " TEXT,"
                + COLUMN_CONTENT_TYPE_PHONECALLRECORD + " TEXT," + COLUMN_DURATION_PHONECALLRECORD + " INTEGER," + COLUMN_DIRECTION_TYPE_PHONECALLRECORD + " INTEGER," +
                COLUMN_PHONE_NUMBER_PHONECALLRECORD + " TEXT," + COLUMN_CONTACT_NAME_PHONECALLRECORD + " TEXT," + COLUMN_AUDIO_SIZE_PHONECALLRECORD + " INTEGER," + COLUMN_EXT_PHONECALLRECORD + " TEXT," + COLUMN_MEDIA_URL_PHONECALLRECORD + " TEXT," +
                COLUMN_CREATED_DATE_PHONECALLRECORD + " TEXT," + COLUMN_CDN_URL_PHONECALLRECORD + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);

    }

    boolean checkTableExist(String tableName){

        if (tableName == null || database == null || !database.getWritableDatabase().isOpen())
            return false;

        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }

        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public void addDevice_PhoneCallRecord_Fast(List<PhoneCallRecordJson> phoneCallRecords) {

        database.getWritableDatabase().beginTransaction();
        Log.i("addPhoneCallRecord", "dataPhoneCallRecord add: " + phoneCallRecords.get(0).getID());
        try {
            for (int i = 0; i < phoneCallRecords.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(),TABLE_PHONECALLRECORD_HISTORY, COLUMN_DEVICE_ID_PHONECALLRECORD,phoneCallRecords.get(i).getDevice_ID(), COLUMN_ID_PHONECALLRECORD, phoneCallRecords.get(i).getID()))
                {
                    ContentValues contentValues1 = API_Add_Database(phoneCallRecords.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_PHONECALLRECORD_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();
        } finally {
            database.getWritableDatabase().endTransaction();
        }
    }

    public List<AudioGroup> getAll_PhoneCallRecord_ID_History(String deviceID, int offSet) {

        Log.i(TAG, "DatabasePhoneCallRecord.getAll_PhoneCallRecord... " + TABLE_PHONECALLRECORD_HISTORY);
        List<AudioGroup> audioGroups = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PHONECALLRECORD_HISTORY +" WHERE Device_ID = '"+ deviceID + "' ORDER BY " + COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD + " DESC LIMIT "+ NumberLoad + " OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {

                AudioGroup audioGroup = new AudioGroup();
                audioGroup.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_PHONECALLRECORD)));
                audioGroup.setDeviceID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_PHONECALLRECORD)));
                audioGroup.setDuration(String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION_PHONECALLRECORD))));
                audioGroup.setContactName(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME_PHONECALLRECORD)));
                audioGroup.setAudioName(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_NAME_PHONECALLRECORD)));
                audioGroup.setURL_Audio(cursor.getString(cursor.getColumnIndex(COLUMN_CDN_URL_PHONECALLRECORD)) + cursor.getString(cursor.getColumnIndex(COLUMN_MEDIA_URL_PHONECALLRECORD))+ "." + cursor.getString(cursor.getColumnIndex(COLUMN_EXT_PHONECALLRECORD)));
                audioGroup.setIsSave(cursor.getInt(cursor.getColumnIndex(COLUMN_ISSAVED_PHONECALLRECORD)));
                audioGroup.setID(cursor.getLong(cursor.getColumnIndex(COLUMN_ID_PHONECALLRECORD)));
                audioGroup.setIsAmbient(0);
                    // Add in List.
                audioGroups.add(audioGroup);
                //}

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return audioGroups;
    }


    public void update_PhoneCallRecord_History(int value, String nameDeviceID, long phoneCallRecordID) {

        Log.d("isLoading = ", COLUMN_ISSAVED_PHONECALLRECORD + "=" + value + "");
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COLUMN_ISSAVED_PHONECALLRECORD, value);
        database.getWritableDatabase().update(TABLE_PHONECALLRECORD_HISTORY, contentValues1, COLUMN_DEVICE_ID_PHONECALLRECORD + " = ?" + " AND " + COLUMN_ID_PHONECALLRECORD + "=?",
                new String[]{String.valueOf(nameDeviceID), String.valueOf(phoneCallRecordID)});
        // Close the database connection.
        database.close();
    }

    public int getPhoneCallRecordCount(String deviceID) {
        Log.i(TAG, "DatabasePhoneCallRecord.getPhoneCallRecordCount ... " + TABLE_PHONECALLRECORD_HISTORY);

        //String countQuery = "SELECT  * FROM " + TABLE_PHOTO_HISTORY;

        Cursor cursor = database.getWritableDatabase().query(TABLE_PHONECALLRECORD_HISTORY, new String[]{COLUMN_DEVICE_ID_PHONECALLRECORD
                }, COLUMN_DEVICE_ID_PHONECALLRECORD + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;

    }

    public void delete_PhoneCallRecord_History(AudioGroup phoneCallRecord) {
        Log.i("deletePhoneCallRecord", "DatabasePhoneCallRecord.deletePhoneCallRecord ... " + phoneCallRecord.getID() + "== " + phoneCallRecord.getAudioName());

        database.getWritableDatabase().delete(TABLE_PHONECALLRECORD_HISTORY, COLUMN_ID_PHONECALLRECORD + " = ?",
                new String[]{String.valueOf(phoneCallRecord.getID())});
        database.close();
    }

    public List<Long> getAll_PhoneCallRecord_ID_History_Date(String deviceID, String date) {

        Log.i(TAG, "DatabasePhoneCallRecord.getAll_PhoneCallRecord... " + TABLE_PHONECALLRECORD_HISTORY);
        List<Long> phoneCallRecord_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PHONECALLRECORD_HISTORY + " WHERE " + COLUMN_DEVICE_ID_PHONECALLRECORD + " = '" + deviceID + "'";//+"' AND " +COLUMN_CLIENT_CAPTURED_DATE_PHOTO+" = '"+date+"'", String date
        //SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD)).substring(0, 10).equals(date)) {
                    phoneCallRecord_List.add(cursor.getLong(cursor.getColumnIndex(COLUMN_ID_PHONECALLRECORD)));
                }
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return phoneCallRecord_List;
    }


}
