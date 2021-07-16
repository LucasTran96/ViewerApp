/*
  ClassName: DatabaseURL.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseURL is used to create, add, modify, delete databases, save
  the history URL from the server, use the "URLHistory.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.scp.viewer.Model.URL;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.APIDatabase.API_Add_Database;
import static com.scp.viewer.API.Global.TAG;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.URLEntity.COLUMN_CLIENT_URL_TIME;
import static com.scp.viewer.Database.Entity.URLEntity.COLUMN_CREATED_DATE_URL;
import static com.scp.viewer.Database.Entity.URLEntity.COLUMN_URL_LINK;
import static com.scp.viewer.Database.Entity.URLEntity.TABLE_URL_HISTORY;

public class DatabaseURL
{
    private DatabaseHelper database;

    public DatabaseURL(Context context) {
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_URL_HISTORY))
            createTable();
    }

    public void createTable() {

        Log.i(TAG, "DatabaseNotes.onCreate ... " + TABLE_URL_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_URL_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ," + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT," + COLUMN_CLIENT_URL_TIME + " TEXT," + COLUMN_URL_LINK + " TEXT,"
                + COLUMN_CREATED_DATE_URL + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    public void addDevice_URL(List<URL> url) {

        Log.i("addURL", "dataURL add: " + url.get(0).getID());
        //  contentValues1 receives the value from the method API_Add_Database()
        database.getWritableDatabase().beginTransaction();
        try {
            for (int i = 0; i < url.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(),TABLE_URL_HISTORY,COLUMN_DEVICE_ID,url.get(i).getDevice_ID(),COLUMN_ID,url.get(i).getID()))
                {
                    ContentValues contentValues1 = API_Add_Database(url.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_URL_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();
        } finally {
            database.getWritableDatabase().endTransaction();
        }
    }

    public List<URL> getAll_URL_ID_History(String deviceID, int offSet) {
        Log.i(TAG, "DatabaseNotes.getAll_URL_ID_History ... " + TABLE_URL_HISTORY);
        List<URL> url_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_URL_HISTORY + " WHERE Device_ID = '"+ deviceID +"' ORDER BY " + COLUMN_CLIENT_URL_TIME + " DESC LIMIT "+ NumberLoad + " OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                    URL url = new URL();
                    url.setRowIndex(cursor.getLong(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                    url.setID(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    url.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                    url.setClient_URL_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_URL_TIME)));
                    url.setURL_Link(cursor.getString(cursor.getColumnIndex(COLUMN_URL_LINK)));
                    url.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_URL)));
                    // Add in List.
                    url_List.add(url);
            } while (cursor.moveToNext());
        }
        // return note list
        return url_List;
    }

    public int get_URLCount_DeviceID(String deviceID) {
        Log.i(TAG, "DatabaseURL.get_URLCount_DeviceID ... " + TABLE_URL_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_URL_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Contact_History(URL url) {
        Log.i(TAG, "DatabaseNotes.delete_Contact_History ... " + url.getID());
        database.getWritableDatabase().delete(TABLE_URL_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(url.getID())});
        database.close();
    }

}
