/*
  ClassName: DatabaseDevice.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseDevice is used to create, add, modify, delete databases, save
  the Device from the server, use the "ManagementDevice.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
*/


package com.jexpa.cp9.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jexpa.cp9.Model.Table;

import java.util.ArrayList;

import static com.jexpa.cp9.API.APIDatabase.API_Add_Database;
import static com.jexpa.cp9.Database.DatabaseHelper.getInstance;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_APP_VERSION_NUMBER;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_BATTERY;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_BRAND_ID;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_ID;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_COUNTRY;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_CREATED_BY;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_CREATED_DATE;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_LAST_ONLINE;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_WIFI_ENABLED;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_Device_ID;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_DEVICE_NAME;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_DEVICE_TOKEN;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_GPS_OPTION_TURNED;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_ICCID;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_IMEI;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_IMSI;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_IS_ROOTED_OR_JAILBROKEN;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_LOGIN_NAME;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_MODIFIED_BY;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_MODIFIED_DATE;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_OS_DEVICE;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_PHONE_NUMBER;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_SIM_CARD_INFO;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.COLUMN_STATUS_MESSAGE;
import static com.jexpa.cp9.Database.Entity.ManagementDeviceEntity.TABLE_DEVICE;
import static com.jexpa.cp9.API.Global.TAG;

public class DatabaseDevice
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseDevice(Context context) {
        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_DEVICE))
            createTable();
    }

    // Create tables name Device.

    private void createTable() {
        Log.i(TAG, "DatabaseUser.onCreate ... " + TABLE_DEVICE);
        // Script create tables.
        String scriptTable = "CREATE TABLE " + TABLE_DEVICE + "("
                + COLUMN_ID + " LONG ,"
                + COLUMN_LOGIN_NAME + " TEXT,"
                + COLUMN_Device_ID + " TEXT,"
                + COLUMN_DEVICE_NAME + " TEXT,"
                + COLUMN_DEVICE_TOKEN + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT,"
                + COLUMN_OS_DEVICE + " TEXT,"
                + COLUMN_APP_VERSION_NUMBER + " TEXT,"
                + COLUMN_ICCID + " TEXT,"
                + COLUMN_IMSI + " TEXT,"
                + COLUMN_IMEI + " TEXT,"
                + COLUMN_COUNTRY + " TEXT,"
                + COLUMN_SIM_CARD_INFO + " TEXT,"
                + COLUMN_STATUS_MESSAGE + " TEXT,"
                + COLUMN_IS_ROOTED_OR_JAILBROKEN + " TEXT,"
                + COLUMN_GPS_OPTION_TURNED + " TEXT,"
                + COLUMN_MODIFIED_BY + " TEXT,"
                + COLUMN_MODIFIED_DATE + " TEXT,"
                + COLUMN_CREATED_BY + " TEXT,"
                + COLUMN_CREATED_DATE + " TEXT,"
                + COLUMN_BRAND_ID + " TEXT,"
                + COLUMN_WIFI_ENABLED + " INTEGER,"
                + COLUMN_BATTERY + " TEXT,"
                + COLUMN_LAST_ONLINE + " TEXT"+")";
        // Run create table command.
        database.getWritableDatabase().execSQL(scriptTable);
    }

    //  The method adds the Table object to the device table
    public void addDevice(Table table) {

        //  contentValues1 receives the value from the method API_Add_Database()
        ContentValues contentValues1 = API_Add_Database(table,false);
        // Insert a row of data into the table.
        database.getWritableDatabase().insert(TABLE_DEVICE, null, contentValues1);
        //  Close the database connection.
        database.close();

    }

    // User user1=getNote(1);
    public ArrayList<Table> getAllDevice() {
        Log.i(TAG, "DatabaseUser.getAllDevice ... " + TABLE_DEVICE);
        ArrayList<Table> tableList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_DEVICE;
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                Table table = new Table();
                table.setID(String.valueOf(cursor.getInt(0)));
                table.setLogin_Name(cursor.getString(1));
                table.setDevice_ID(cursor.getString(2));
                table.setDevice_Name(cursor.getString(3));
                table.setDevice_Token(cursor.getString(4));
                table.setPhone_Number(cursor.getString(5));
                table.setOS_Device(cursor.getString(6));
                table.setApp_Version_Number(cursor.getString(7));
                table.setICCID(cursor.getString(8));
                table.setIMSI(cursor.getString(9));
                table.setIMEI(cursor.getString(10));
                table.setCountry(cursor.getString(11));
                table.setSimCard_Info(cursor.getString(12));
                table.setStatus_Message(cursor.getString(13));
                table.setIs_Rooted_Or_Jailbroken(cursor.getString(14));
                table.setGPS_Option_Turned(cursor.getString(15));
                table.setModified_By(cursor.getString(16));
                table.setModified_Date(cursor.getString(17));
                table.setCreated_By(cursor.getString(18));
                table.setCreated_Date(cursor.getString(19));
                table.setBrand_ID(cursor.getString(20));
                table.setWifi_Enabled(cursor.getInt(21) == 1);
                table.setBattery(cursor.getString(22));
                table.setLast_Online(cursor.getString(23));
                // Add in List.
                tableList.add(table);
            } while (cursor.moveToNext());
        }
        // return note list
        database.close();//
        return tableList;

    }

    public int getDeviceCount() {
        Log.i(TAG, "DatabaseUser.getDeviceCount ... " + TABLE_DEVICE);
        String countQuery = "SELECT  * FROM " + TABLE_DEVICE;

        Cursor cursor = database.getWritableDatabase().rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        database.close();//
        return count;
    }

    public void deleteDevice(Table table) {
        Log.i(TAG, "DatabaseUser.updateNote ... " + table.getDevice_Name());

        database.getWritableDatabase().delete(TABLE_DEVICE, COLUMN_ID + " = ?",
                new String[]{String.valueOf(table.getID())});
        database.close();
    }


}
