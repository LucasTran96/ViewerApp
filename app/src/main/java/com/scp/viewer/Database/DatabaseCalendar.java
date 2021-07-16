/*
  ClassName: DatabaseCalendar.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseCalendar is used to create, add, modify, delete databases, save
  the Calendar Log values from the server, use the "CalendarHistory.class".
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
import com.scp.viewer.Model.Calendars;

import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_CLIENT_CALENDAR_TIME;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_CREATED_DATE_CALENDAR;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_FROM_DATE_CALENDAR;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_LOCATION_CALENDAR;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_REPETITION_CALENDAR;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_TITLE_CALENDAR;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_TO_DATE_CALENDAR;
import static com.scp.viewer.Database.Entity.CalendarEntity.TABLE_CALENDAR_HISTORY;

public class DatabaseCalendar
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseCalendar(Context context) {

        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_CALENDAR_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(Global.TAG, "DatabaseCalendar.onCreate ... " + TABLE_CALENDAR_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_CALENDAR_HISTORY
                + "(" + COLUMN_ROW_INDEX + " LONG ,"
                + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT,"
                + COLUMN_TITLE_CALENDAR + " TEXT,"
                + COLUMN_CLIENT_CALENDAR_TIME + " TEXT,"
                + COLUMN_FROM_DATE_CALENDAR + " TEXT,"
                + COLUMN_TO_DATE_CALENDAR + " TEXT,"
                + COLUMN_LOCATION_CALENDAR + " TEXT,"
                + COLUMN_REPETITION_CALENDAR + " TEXT,"
                + COLUMN_CREATED_DATE_CALENDAR + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);

    }


    public void addCalendar(List<Calendars> calendarsList) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < calendarsList.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_CALENDAR_HISTORY,
                        COLUMN_DEVICE_ID, calendarsList.get(i).getDevice_ID(),
                        COLUMN_ID, calendarsList.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(calendarsList.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_CALENDAR_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    public List<Calendars> getAll_Calendar_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseCalendar.getAll_URL_ID_History ... " + TABLE_CALENDAR_HISTORY);
        List<Calendars> calendarsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CALENDAR_HISTORY + " WHERE Device_ID = '"+ deviceID
                + "' ORDER BY " + COLUMN_CLIENT_CALENDAR_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                Calendars calendars = new Calendars();
                calendars.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                calendars.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                calendars.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                calendars.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_CALENDAR)));
                calendars.setClient_Calendar_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CALENDAR_TIME)));
                calendars.setFrom_Date(cursor.getString(cursor.getColumnIndex(COLUMN_FROM_DATE_CALENDAR)));
                calendars.setTo_Date(cursor.getString(cursor.getColumnIndex(COLUMN_TO_DATE_CALENDAR)));
                calendars.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION_CALENDAR)));
                calendars.setRepetition(cursor.getString(cursor.getColumnIndex(COLUMN_REPETITION_CALENDAR)));
                calendars.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_CALENDAR)));

                calendarsList.add(calendars);

            } while (cursor.moveToNext());
        }
        // return note list
        return calendarsList;
    }

    public int get_CalendarCount_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseCalendar.get_CalendarCount_DeviceID ... " + TABLE_CALENDAR_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_CALENDAR_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Calendar_History(Calendars calendars) {
        Log.i(Global.TAG, "DatabaseCalendar.delete_Calendar_History... " + calendars.getID());

        database.getWritableDatabase().delete(TABLE_CALENDAR_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(calendars.getID())});
        database.close();
    }

}
