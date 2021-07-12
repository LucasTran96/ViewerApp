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
package com.scp.viewer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.scp.viewer.Model.AmbientRecord;
import com.scp.viewer.Model.AudioGroup;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.APIDatabase.API_Add_Database;
import static com.scp.viewer.API.APIMethod.checkItemExistString;
import static com.scp.viewer.API.APIMethod.checkItemExistWithDeviceIDString;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.TAG;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_AUDIO_NAME_AMBIENTRECORD;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_AUDIO_SIZE_AMBIENTRECORD;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_CDN_URL_AMBIENTRECORD;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_CREATED_DATE_AMBIENTRECORD;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_DEVICE_ID_AMBIENTRECORD;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_DURATION_AMBIENTRECORD;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.COLUMN_ISSAVED_AMBIENTRECORD;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.TABLE_AMBIENTRECORD_HISTORY;
import static com.scp.viewer.Database.Entity.PhoneCallRecordEntity.COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD;
import static com.scp.viewer.View.PhoneCallRecordHistory.deleteFileAudioWithName;

public class DatabaseAmbientRecord {

    private Context context;
    private DatabaseHelper database;

    public DatabaseAmbientRecord(Context context) {
        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_AMBIENTRECORD_HISTORY))
            createTable();
    }

    private void createTable()
    {
        Log.i(TAG, "DatabasePhoneCallRecord.onCreate ... " + TABLE_AMBIENTRECORD_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_AMBIENTRECORD_HISTORY + "(" + COLUMN_DEVICE_ID_AMBIENTRECORD + " TEXT ,"
                + COLUMN_AUDIO_NAME_AMBIENTRECORD + " TEXT,"
                + COLUMN_CLIENT_CAPTURED_DATE_PHONECALLRECORD + " TEXT,"
                + COLUMN_DURATION_AMBIENTRECORD + " TEXT,"
                + COLUMN_AUDIO_SIZE_AMBIENTRECORD + " LONG,"
                + COLUMN_CREATED_DATE_AMBIENTRECORD + " TEXT,"
                + COLUMN_CDN_URL_AMBIENTRECORD + " TEXT,"
                + COLUMN_ISSAVED_AMBIENTRECORD + " INTEGER" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    public void addDevice_AmbientRecord_Fast(List<AmbientRecord> ambientRecordsList) {

        database.getWritableDatabase().beginTransaction();
        Log.i("AmbientMediaLink", "dataPhoneCallRecord add: " + ambientRecordsList.get(0).getFileName());
        try {
            for (int i = 0; i < ambientRecordsList.size(); i++)
            {
                if(!ambientRecordsList.get(i).getFileName().contains("_"))
                {
                    String timeOld = checkItemExistWithDeviceIDString(database.getWritableDatabase(),TABLE_AMBIENTRECORD_HISTORY, COLUMN_DEVICE_ID_AMBIENTRECORD,ambientRecordsList.get(i).getDeviceID(), COLUMN_AUDIO_NAME_AMBIENTRECORD, ambientRecordsList.get(i).getFileName(),ambientRecordsList.get(i));
                    if(!timeOld.equals(""))
                    {
                        if(!ambientRecordsList.get(i).getDate().equals(timeOld))
                        {
                            try {

                                // delete record
                                database.getWritableDatabase().delete(TABLE_AMBIENTRECORD_HISTORY, COLUMN_AUDIO_NAME_AMBIENTRECORD + " = ?",
                                        new String[]{String.valueOf(ambientRecordsList.get(i).getFileName())});
                                // add new record
                                ContentValues contentValues1 = API_Add_Database(ambientRecordsList.get(i),false);
                                Log.d("AmbientMediaLink", contentValues1.toString());

                                // Insert a row of data into the table.
                                database.getWritableDatabase().insert(TABLE_AMBIENTRECORD_HISTORY, null, contentValues1);
                                // delete file audio
                                deleteFileAudioWithName(context, ambientRecordsList.get(i).getFileName());
                            }
                            catch (Exception e)
                            {
                                e.getMessage();
                            }
                        }
                    }
                    else {
                        ContentValues contentValues1 = API_Add_Database(ambientRecordsList.get(i),false);
                        // Insert a row of data into the table.
                        Log.d("AmbientMediaLink", contentValues1.toString());
                        database.getWritableDatabase().insert(TABLE_AMBIENTRECORD_HISTORY, null, contentValues1);
                    }
                }else {
                    if(!checkItemExistString(database.getWritableDatabase(),TABLE_AMBIENTRECORD_HISTORY, COLUMN_DEVICE_ID_AMBIENTRECORD,ambientRecordsList.get(i).getDeviceID(), COLUMN_AUDIO_NAME_AMBIENTRECORD, ambientRecordsList.get(i).getFileName()))
                    {
                        //  contentValues1 receives the value from the method API_Add_Database()
                        ContentValues contentValues1 = API_Add_Database(ambientRecordsList.get(i),false);
                        Log.d("AmbientMediaLink", contentValues1.toString());
                        // Insert a row of data into the table.
                        database.getWritableDatabase().insert(TABLE_AMBIENTRECORD_HISTORY, null, contentValues1);
                    }
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
    }

    public List<AudioGroup> getAll_PhoneCallRecord_ID_History(String deviceID, int offSet) {

        Log.i(TAG, "DatabasePhoneCallRecord.getAll_PhoneCallRecord... " + TABLE_AMBIENTRECORD_HISTORY);
        List<AudioGroup> ambientRecordArrayList = new ArrayList<>();
        // Select All Query
        Log.d("AmbientMediaLink","deviceID === "+ deviceID);
        String selectQuery = "SELECT  * FROM " + TABLE_AMBIENTRECORD_HISTORY +" WHERE deviceID = '"+ deviceID + "' ORDER BY " + COLUMN_CREATED_DATE_AMBIENTRECORD + " DESC LIMIT "+ NumberLoad + " OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {

                AudioGroup audioGroup = new AudioGroup();
                audioGroup.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_AMBIENTRECORD)));
                audioGroup.setDeviceID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_AMBIENTRECORD)));
                audioGroup.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_DURATION_AMBIENTRECORD)));
                audioGroup.setContactName(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_NAME_AMBIENTRECORD)));
                audioGroup.setAudioName(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_NAME_AMBIENTRECORD)));
                audioGroup.setURL_Audio(cursor.getString(cursor.getColumnIndex(COLUMN_CDN_URL_AMBIENTRECORD))+"/" + cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_NAME_AMBIENTRECORD)));
                audioGroup.setIsSave(cursor.getInt(cursor.getColumnIndex(COLUMN_ISSAVED_AMBIENTRECORD)));
                audioGroup.setID(0);
                audioGroup.setIsAmbient(1);
                Log.d("AmbientMediaLink",audioGroup.getAudioName() +" === "+ audioGroup.getDate());
                // Add in List.
                ambientRecordArrayList.add(audioGroup);
            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return ambientRecordArrayList;
    }

    public void update_AmbientRecord_History(int value, String nameDeviceID, String nameRecord)
    {

        Log.d("isLoading", COLUMN_ISSAVED_AMBIENTRECORD + " = " + value + "");
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COLUMN_ISSAVED_AMBIENTRECORD, value);
        database.getWritableDatabase().update(TABLE_AMBIENTRECORD_HISTORY, contentValues1, COLUMN_DEVICE_ID_AMBIENTRECORD + " = ?" + " AND " + COLUMN_AUDIO_NAME_AMBIENTRECORD + "=?",
                new String[]{String.valueOf(nameDeviceID), String.valueOf(nameRecord)});
        //  Close the database connection.
        database.close();
    }

    public int getPhoneCallRecordCount(String deviceID) {
        Log.i(TAG, "DatabasePhoneCallRecord.getPhoneCallRecordCount ... " + TABLE_AMBIENTRECORD_HISTORY);

        //String countQuery = "SELECT  * FROM " + TABLE_PHOTO_HISTORY;

        Cursor cursor = database.getWritableDatabase().query(TABLE_AMBIENTRECORD_HISTORY, new String[]{COLUMN_DEVICE_ID_AMBIENTRECORD
                }, COLUMN_DEVICE_ID_AMBIENTRECORD + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_PhoneCallRecord_History(AudioGroup ambientRecord) {
        Log.i("deletePhoneCallRecord", "DatabasePhoneCallRecord.deletePhoneCallRecord ... " + ambientRecord.getContactName());

        database.getWritableDatabase().delete(TABLE_AMBIENTRECORD_HISTORY, COLUMN_AUDIO_NAME_AMBIENTRECORD + " = ?",
                new String[]{String.valueOf(ambientRecord.getContactName())});
        database.close();
    }
}
