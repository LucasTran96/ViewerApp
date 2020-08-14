/*
  ClassName: DatabaseGetLocation.java
  Project: SecondClone
  author:  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseGetLocation is used to create, add, modify, delete databases, save
  the history Location from the server, use the "HistoryLocation.class".
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

import com.jexpa.secondclone.Model.GPS;

import java.util.ArrayList;
import java.util.List;

import static com.jexpa.secondclone.API.APIDatabase.API_Add_Database;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_ACCURACY;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_CLIENT_GPS_TIME;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_CREATED_DATE;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_DEVICE_ID;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_ID;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_LATITUDE;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_LONGITUDE;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.COLUMN_GETLOCATION_ROWINDEX;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.DATABASE_NAME_GETLOCATION;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.DATABASE_VERSION_GETLOCATION;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.TABLE_GETLOCATION;

public class DatabaseGetLocation extends SQLiteOpenHelper {
    SQLiteDatabase database;

    public DatabaseGetLocation(Context context) {
        super(context, DATABASE_NAME_GETLOCATION, null, DATABASE_VERSION_GETLOCATION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "DatabaseUser.onCreate ... " + TABLE_GETLOCATION);
        String scriptTable = " CREATE TABLE " + TABLE_GETLOCATION + "(" + COLUMN_GETLOCATION_ROWINDEX + " INTEGER ," + COLUMN_GETLOCATION_ID + " INTEGER,"
                + COLUMN_GETLOCATION_DEVICE_ID + " TEXT," + COLUMN_GETLOCATION_CLIENT_GPS_TIME + " TEXT," + COLUMN_GETLOCATION_LATITUDE + " DOUBLE,"
                + COLUMN_GETLOCATION_LONGITUDE + " DOUBLE," + COLUMN_GETLOCATION_ACCURACY + " INTEGER," +
                COLUMN_GETLOCATION_CREATED_DATE + " TEXT" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GETLOCATION);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_GPS(List<GPS> gps) {
        database = this.getWritableDatabase();
        database.beginTransaction();
        try {
            for (int i = 0; i < gps.size(); i++) {
                //  contentValues1 receives the value from the method API_Add_Database()
                ContentValues contentValues1 = API_Add_Database(gps.get(i),false);
                // Insert a row of data into the table.
                database.insert(TABLE_GETLOCATION, null, contentValues1);
            }
            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }
        //  Close the database connection.
        database.close();

    }

    public List<GPS> getAll_LocationID(String ID, int offSet) {
        Log.i(TAG, "DatabaseLocation.getAll_Location ... " + TABLE_GETLOCATION);
        List<GPS> location_List = new ArrayList<>();
        // Select All Query +" WHERE RowIndex >0 and RowIndex <10 "
        String selectQuery = "SELECT  * FROM " + TABLE_GETLOCATION + " WHERE Device_ID = '"+ ID +"' ORDER BY " + COLUMN_GETLOCATION_CLIENT_GPS_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET  "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                //if (cursor.getString(2).equals(ID)) {
                    GPS gps = new GPS();
                    gps.setRowIndex(cursor.getInt(0));
                    gps.setID(cursor.getInt(1));
                    gps.setDevice_ID(cursor.getString(2));
                    gps.setClient_GPS_Time(cursor.getString(3));
                    gps.setLatitude(cursor.getDouble(4));
                    gps.setLongitude(cursor.getDouble(5));
                    gps.setAccuracy(cursor.getInt(6));
                    gps.setCreated_Date(cursor.getString(7));
                    // Add in List.
                    location_List.add(gps);
                //}

            } while (cursor.moveToNext());
        }

        // return note list
        return location_List;
    }

   /* public List<GPS> getAll_LocationID(String ID) {
        Log.i(TAG, "DatabaseLocation.getAll_Location ... " + TABLE_GETLOCATION);
        List<GPS> location_List = new ArrayList<>();
        // Select All Query +" WHERE RowIndex >0 and RowIndex <10 "
        String selectQuery = "SELECT  * FROM " + TABLE_GETLOCATION + " WHERE Device_ID = "+ ID +" ORDER BY " + COLUMN_GETLOCATION_CLIENT_GPS_TIME + " DESC LIMIT 20 OFFSET  "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                //if (cursor.getString(2).equals(ID)) {
                GPS gps = new GPS();
                gps.setRowIndex(cursor.getInt(0));
                gps.setID(cursor.getInt(1));
                gps.setDevice_ID(cursor.getString(2));
                gps.setClient_GPS_Time(cursor.getString(3));
                gps.setLatitude(cursor.getDouble(4));
                gps.setLongitude(cursor.getDouble(5));
                gps.setAccuracy(cursor.getInt(6));
                gps.setCreated_Date(cursor.getString(7));
                // Add in List.
                location_List.add(gps);
                //}

            } while (cursor.moveToNext());
        }

        // return note list
        return location_List;
    }*/
    // Method retrieving data by date to compare.
    public List<Integer> getAll_Location_ID_History_Date(String deviceID, String date) {

        Log.i(TAG, "DatabaseLocation.getAll_Location_ID_History_Date... " + TABLE_GETLOCATION);
        List<Integer> location_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GETLOCATION + " WHERE " + COLUMN_GETLOCATION_DEVICE_ID + " = '" + deviceID + "'";//+"' AND " +COLUMN_CLIENT_CAPTURED_DATE_PHOTO+" = '"+date+"'", String date
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_GETLOCATION_CLIENT_GPS_TIME)).substring(0, 10).equals(date)) {
                    location_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_GETLOCATION_ID)));
                }
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return location_List;
    }

    public int getLocationCount(String deviceID) {
        Log.i(TAG, "DatabaseLocation.getLocationCount ... " + TABLE_GETLOCATION);
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_GETLOCATION, new String[]{COLUMN_GETLOCATION_DEVICE_ID
                }, COLUMN_GETLOCATION_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteLocation(GPS gps) {
        Log.i(TAG, "DatabaseLocation.deleteLocation ... " + gps.getDevice_ID());
        database = this.getWritableDatabase();
        database.delete(TABLE_GETLOCATION, COLUMN_GETLOCATION_ID + " = ?",
                new String[]{String.valueOf(gps.getID())});
        database.close();
    }

}
