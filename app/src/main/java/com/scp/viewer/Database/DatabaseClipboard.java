/*
  ClassName: DatabaseClipboard.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseClipboard is used to create, add, modify, delete databases, save
  the Clipboard Log values from the server, use the "ClipboardHistory.class".
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
import com.scp.viewer.Model.Clipboard;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_CLIENT_CLIPBOARD_TIME;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_CONTENT_CLIPBOARD;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_CREATED_DATE_CLIPBOARD;
import static com.scp.viewer.Database.Entity.ClipboardEntity.COLUMN_FROM_APP_CLIPBOARD;
import static com.scp.viewer.Database.Entity.ClipboardEntity.TABLE_CLIPBOARD_HISTORY;

public class DatabaseClipboard
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseClipboard(Context context) {

        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_CLIPBOARD_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(Global.TAG, "DatabaseClipboard.onCreate ... " + TABLE_CLIPBOARD_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_CLIPBOARD_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ,"
                + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT,"
                + COLUMN_CONTENT_CLIPBOARD + " TEXT,"
                + COLUMN_CLIENT_CLIPBOARD_TIME + " TEXT,"
                + COLUMN_FROM_APP_CLIPBOARD + " TEXT,"
                + COLUMN_CREATED_DATE_CLIPBOARD + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }


    public void addClipboard(List<Clipboard> clipboardList) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < clipboardList.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_CLIPBOARD_HISTORY,
                        COLUMN_DEVICE_ID, clipboardList.get(i).getDevice_ID(),
                        COLUMN_ID, clipboardList.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(clipboardList.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_CLIPBOARD_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    public List<Clipboard> getAll_Clipboard_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseClipboard.getAll_URL_ID_History ... " + TABLE_CLIPBOARD_HISTORY);
        List<Clipboard> clipboardList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CLIPBOARD_HISTORY + " WHERE Device_ID = '"+ deviceID
                + "' ORDER BY " + COLUMN_CLIENT_CLIPBOARD_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                    Clipboard appInstallation = new Clipboard();
                    appInstallation.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                    appInstallation.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    appInstallation.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                    appInstallation.setClipboard_Content(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_CLIPBOARD)));
                    appInstallation.setClient_Clipboard_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CLIPBOARD_TIME)));
                    appInstallation.setFrom_App(cursor.getString(cursor.getColumnIndex(COLUMN_FROM_APP_CLIPBOARD)));
                    appInstallation.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_CLIPBOARD)));

                clipboardList.add(appInstallation);

            } while (cursor.moveToNext());
        }
        // return note list
        return clipboardList;
    }

    public int get_ClipboardCount_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseClipboard.get_ClipboardCount_DeviceID ... " + TABLE_CLIPBOARD_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_CLIPBOARD_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Clipboard_History(Clipboard clipboard) {
        Log.i(Global.TAG, "DatabaseClipboard.delete_Clipboard_History... " + clipboard.getID());

        database.getWritableDatabase().delete(TABLE_CLIPBOARD_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(clipboard.getID())});
        database.close();
    }

}
