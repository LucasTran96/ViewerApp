/*
  ClassName: DatabaseNotification.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-19
  Description: Class DatabaseNotification is used to create, add, modify, delete databases, save
  the Notification Log values from the server, use the "NotificationHistory.class".
  History:2021-07-19
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
import com.scp.viewer.Model.Notifications;

import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.NotificationEntity.COLUMN_APP_NAME_NOTIFICATION;
import static com.scp.viewer.Database.Entity.NotificationEntity.COLUMN_CLIENT_NOTIFICATION_TIME;
import static com.scp.viewer.Database.Entity.NotificationEntity.COLUMN_CREATED_DATE_NOTIFICATION;
import static com.scp.viewer.Database.Entity.NotificationEntity.COLUMN_NOTIFICATION_CONTENT;
import static com.scp.viewer.Database.Entity.NotificationEntity.COLUMN_NOTIFICATION_TITLE;
import static com.scp.viewer.Database.Entity.NotificationEntity.TABLE_NOTIFICATION_HISTORY;

public class DatabaseNotification
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseNotification(Context context) {

        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_NOTIFICATION_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(Global.TAG, "DatabaseNotification.onCreate ... " + TABLE_NOTIFICATION_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_NOTIFICATION_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ,"
                + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT,"
                + COLUMN_APP_NAME_NOTIFICATION + " TEXT,"
                + COLUMN_NOTIFICATION_TITLE + " TEXT,"
                + COLUMN_NOTIFICATION_CONTENT + " TEXT,"
                + COLUMN_CLIENT_NOTIFICATION_TIME + " TEXT,"
                + COLUMN_CREATED_DATE_NOTIFICATION + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    /*public static final String COLUMN_CLIENT_NOTIFICATION_TIME = "Client_Notification_Time";
    public static final String COLUMN_APP_NAME_NOTIFICATION = "App_Name";
    public static final String COLUMN_NOTIFICATION_TITLE = "Notification_Title";
    public static final String COLUMN_NOTIFICATION_CONTENT = "Notification_Content";
    public static final String COLUMN_CREATED_DATE_NOTIFICATION = "Created_Date";*/

    public void addNotification(List<Notifications> notificationsList) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < notificationsList.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_NOTIFICATION_HISTORY,
                        COLUMN_DEVICE_ID, notificationsList.get(i).getDevice_ID(),
                        COLUMN_ID, notificationsList.get(i).getID()))
                {
                    Log.d("NotificationHistory"," add Notification = "+  notificationsList.get(i).getNotification_Title());
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(notificationsList.get(i),false);
                    // Insert a row of data into the table.
                    Log.d("NotificationHistory"," add Notification = "+  contentValues1);
                    database.getWritableDatabase().insert(TABLE_NOTIFICATION_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    public List<Notifications> getAll_Notification_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseNotification.getAll_Notification_ID_History ... " + TABLE_NOTIFICATION_HISTORY);
        List<Notifications> notificationsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTIFICATION_HISTORY + " WHERE Device_ID = '"+ deviceID
                + "' ORDER BY " + COLUMN_CLIENT_NOTIFICATION_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                Notifications notifications = new Notifications();
                notifications.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                notifications.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                notifications.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                notifications.setClient_Notification_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NOTIFICATION_TIME)));
                notifications.setApp_Name(cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME_NOTIFICATION)));
                notifications.setNotification_Title(cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_TITLE)));
                notifications.setNotification_Content(cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_CONTENT)));
                notifications.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_NOTIFICATION)));

                notificationsList.add(notifications);

            } while (cursor.moveToNext());
        }
        // return note list
        return notificationsList;
    }

    public int get_Notifications_Count_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseNotification.get_Notification_Count_DeviceID ... " + TABLE_NOTIFICATION_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_NOTIFICATION_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Notifications_History(Notifications notifications) {
        Log.i(Global.TAG, "DatabaseNotification.delete_Notification_History... " + notifications.getID());

        database.getWritableDatabase().delete(TABLE_NOTIFICATION_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(notifications.getID())});
        database.close();
    }

}
