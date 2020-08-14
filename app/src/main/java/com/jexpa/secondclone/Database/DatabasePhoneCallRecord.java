/*
  ClassName: DatabasePhoneCallRecord.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabasePhoneCallRecord is used to create, add, modify, delete databases, save
  the history Phone Call Recording from the server, use the "PhoneCallRecordHistory.class".
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

import com.jexpa.secondclone.Model.PhoneCallRecord;

import java.util.ArrayList;
import java.util.List;

import static com.jexpa.secondclone.API.APIDatabase.API_Add_Database;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_AUDIO_NAME_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_AUDIO_SIZE_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_CDN_URL_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_CONTACT_NAME_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_CONTENT_TYPE_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_CREATED_DATE_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_DEVICE_ID_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_DIRECTION_TYPE_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_DURATION_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_EXT_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_ID_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_ISSAVED_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_MEDIA_URL_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_PHONE_NUMBER_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.COLUMN_ROWINDEX_PHONECALLRECORD;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.DATABASE_NAME_PHONECALLRECORD_HISTORY;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.DATABASE_VERSION_PHONECALLRECORD_HISTORY;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.TABLE_PHONECALLRECORD_HISTORY;

public class DatabasePhoneCallRecord extends SQLiteOpenHelper {

    SQLiteDatabase database;


    public DatabasePhoneCallRecord(Context context) {
        super(context, DATABASE_NAME_PHONECALLRECORD_HISTORY, null, DATABASE_VERSION_PHONECALLRECORD_HISTORY);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        Log.i(TAG, "DatabasePhoneCallRecord.onCreate ... " + TABLE_PHONECALLRECORD_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_PHONECALLRECORD_HISTORY + "(" + COLUMN_ROWINDEX_PHONECALLRECORD + " INTEGER ," + COLUMN_ID_PHONECALLRECORD + " INTEGER,"
                + COLUMN_ISSAVED_PHONECALLRECORD + " INTEGER," + COLUMN_DEVICE_ID_PHONECALLRECORD + " TEXT," + COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD + " TEXT," + COLUMN_AUDIO_NAME_PHONECALLRECORD + " TEXT,"
                + COLUMN_CONTENT_TYPE_PHONECALLRECORD + " TEXT," + COLUMN_DURATION_PHONECALLRECORD + " INTEGER," + COLUMN_DIRECTION_TYPE_PHONECALLRECORD + " INTEGER," +
                COLUMN_PHONE_NUMBER_PHONECALLRECORD + " TEXT," + COLUMN_CONTACT_NAME_PHONECALLRECORD + " TEXT," + COLUMN_AUDIO_SIZE_PHONECALLRECORD + " INTEGER," + COLUMN_EXT_PHONECALLRECORD + " TEXT," + COLUMN_MEDIA_URL_PHONECALLRECORD + " TEXT," +
                COLUMN_CREATED_DATE_PHONECALLRECORD + " TEXT," + COLUMN_CDN_URL_PHONECALLRECORD + " TEXT" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONECALLRECORD_HISTORY);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_PhoneCallRecord_Fast(List<PhoneCallRecord> phoneCallRecords) {
        database = this.getWritableDatabase();
        database.beginTransaction();
        Log.i("addPhoneCallRecord", "dataPhoneCallRecord add: " + phoneCallRecords.get(0).getID());
        try {
            for (int i = 0; i < phoneCallRecords.size(); i++) {
                ContentValues contentValues1 = API_Add_Database(phoneCallRecords.get(i),false);
                // Insert a row of data into the table.
                database.insert(TABLE_PHONECALLRECORD_HISTORY, null, contentValues1);
            }

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }

    }


    public List<PhoneCallRecord> getAll_PhoneCallRecord_ID_History(String deviceID, int offSet) {

        Log.i(TAG, "DatabasePhoneCallRecord.getAll_PhoneCallRecord... " + TABLE_PHONECALLRECORD_HISTORY);
        List<PhoneCallRecord> phoneCallRecords = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PHONECALLRECORD_HISTORY +" WHERE Device_ID = '"+ deviceID + "' ORDER BY " + COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD + " DESC LIMIT "+ NumberLoad + " OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                //if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_PHONECALLRECORD)).equals(deviceID)) {

                    PhoneCallRecord phoneCallRecord = new PhoneCallRecord();
                    phoneCallRecord.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROWINDEX_PHONECALLRECORD)));
                    phoneCallRecord.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PHONECALLRECORD)));
                    phoneCallRecord.setIsSaved(cursor.getInt(cursor.getColumnIndex(COLUMN_ISSAVED_PHONECALLRECORD)));
                    phoneCallRecord.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_PHONECALLRECORD)));
                    phoneCallRecord.setClient_Recorded_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD)));
                    phoneCallRecord.setAudio_Name(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_NAME_PHONECALLRECORD)));
                    phoneCallRecord.setContent_Type(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_TYPE_PHONECALLRECORD)));
                    phoneCallRecord.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION_PHONECALLRECORD)));
                    phoneCallRecord.setDirection(cursor.getInt(cursor.getColumnIndex(COLUMN_DIRECTION_TYPE_PHONECALLRECORD)));
                    phoneCallRecord.setPhone_Number(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER_PHONECALLRECORD)));
                    phoneCallRecord.setContact_Name(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME_PHONECALLRECORD)));
                    phoneCallRecord.setAudio_Size(cursor.getInt(cursor.getColumnIndex(COLUMN_AUDIO_SIZE_PHONECALLRECORD)));
                    phoneCallRecord.setExt(cursor.getString(cursor.getColumnIndex(COLUMN_EXT_PHONECALLRECORD)));
                    phoneCallRecord.setMedia_URL(cursor.getString(cursor.getColumnIndex(COLUMN_MEDIA_URL_PHONECALLRECORD)));
                    phoneCallRecord.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_PHONECALLRECORD)));
                    phoneCallRecord.setCDN_URL(cursor.getString(cursor.getColumnIndex(COLUMN_CDN_URL_PHONECALLRECORD)));
                    // Add in List.
                    phoneCallRecords.add(phoneCallRecord);
                //}

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return phoneCallRecords;
    }


    public void update_PhoneCallRecord_History(int value, String nameDeviceID, int phoneCallRecordID) {

        database = this.getWritableDatabase();
        Log.d("isLoading = ", COLUMN_ISSAVED_PHONECALLRECORD + "=" + value + "");
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COLUMN_ISSAVED_PHONECALLRECORD, value);
        database.update(TABLE_PHONECALLRECORD_HISTORY, contentValues1, COLUMN_DEVICE_ID_PHONECALLRECORD + " = ?" + " AND " + COLUMN_ID_PHONECALLRECORD + "=?",
                new String[]{String.valueOf(nameDeviceID), String.valueOf(phoneCallRecordID)});
        //  Close the database connection.
        database.close();
    }

    public int getPhoneCallRecordCount(String deviceID) {
        Log.i(TAG, "DatabasePhoneCallRecord.getPhoneCallRecordCount ... " + TABLE_PHONECALLRECORD_HISTORY);

        //String countQuery = "SELECT  * FROM " + TABLE_PHOTO_HISTORY;
        database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_PHONECALLRECORD_HISTORY, new String[]{COLUMN_DEVICE_ID_PHONECALLRECORD
                }, COLUMN_DEVICE_ID_PHONECALLRECORD + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;

    }

    public void delete_PhoneCallRecord_History(PhoneCallRecord phoneCallRecord) {
        Log.i("deletePhoneCallRecord", "DatabasePhoneCallRecord.deletePhoneCallRecord ... " + phoneCallRecord.getID() + "== " + phoneCallRecord.getAudio_Name());
        database = this.getWritableDatabase();
        database.delete(TABLE_PHONECALLRECORD_HISTORY, COLUMN_ID_PHONECALLRECORD + " = ?",
                new String[]{String.valueOf(phoneCallRecord.getID())});
        database.close();
    }

    public List<Integer> getAll_PhoneCallRecord_ID_History_Date(String deviceID, String date) {
        database = this.getWritableDatabase();
        Log.i(TAG, "DatabasePhoneCallRecord.getAll_PhoneCallRecord... " + TABLE_PHONECALLRECORD_HISTORY);
        List<Integer> phoneCallRecord_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PHONECALLRECORD_HISTORY + " WHERE " + COLUMN_DEVICE_ID_PHONECALLRECORD + " = '" + deviceID + "'";//+"' AND " +COLUMN_CLIENT_CAPTURED_DATE_PHOTO+" = '"+date+"'", String date
        //SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD)).substring(0, 10).equals(date)) {
                    phoneCallRecord_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PHONECALLRECORD)));
                }
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return phoneCallRecord_List;
    }


}
