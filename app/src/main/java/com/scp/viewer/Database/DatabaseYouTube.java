/*
  ClassName: DatabaseYouTube.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-19
  Description: Class DatabaseYouTube is used to create, add, modify, delete databases, save
  the YouTube history values from the server, use the "YouTubeHistory.class".
  History: 2021-07-19
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
import com.scp.viewer.Model.YouTube;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_DEVICE_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ID;
import static com.scp.viewer.Database.Entity.CalendarEntity.COLUMN_ROW_INDEX;
import static com.scp.viewer.Database.Entity.YouTubeEntity.COLUMN_CHANNEL_NAME_YOUTUBE;
import static com.scp.viewer.Database.Entity.YouTubeEntity.COLUMN_CLIENT_YOUTUBE_TIME;
import static com.scp.viewer.Database.Entity.YouTubeEntity.COLUMN_CREATED_DATE_YOUTUBE;
import static com.scp.viewer.Database.Entity.YouTubeEntity.COLUMN_VIDEO_NAME_YOUTUBE;
import static com.scp.viewer.Database.Entity.YouTubeEntity.COLUMN_VIEWS_YOUTUBE;
import static com.scp.viewer.Database.Entity.YouTubeEntity.TABLE_YOUTUBE_HISTORY;

public class DatabaseYouTube
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseYouTube(Context context) {

        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_YOUTUBE_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(Global.TAG, "DatabaseYouTube.onCreate ... " + TABLE_YOUTUBE_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_YOUTUBE_HISTORY + "(" + COLUMN_ROW_INDEX + " LONG ,"
                + COLUMN_ID + " LONG,"
                + COLUMN_DEVICE_ID + " TEXT,"
                + COLUMN_VIDEO_NAME_YOUTUBE + " TEXT,"
                + COLUMN_CLIENT_YOUTUBE_TIME + " TEXT,"
                + COLUMN_CHANNEL_NAME_YOUTUBE + " TEXT,"
                + COLUMN_VIEWS_YOUTUBE + " TEXT,"
                + COLUMN_CREATED_DATE_YOUTUBE + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    public void addYouTube(List<YouTube> youTubeList) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < youTubeList.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_YOUTUBE_HISTORY,
                        COLUMN_DEVICE_ID, youTubeList.get(i).getDevice_ID(),
                        COLUMN_ID, youTubeList.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(youTubeList.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_YOUTUBE_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();
        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    public List<YouTube> getAll_YouTube_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseYouTube.getAll_URL_ID_History ... " + TABLE_YOUTUBE_HISTORY);
        List<YouTube> youTubeArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_YOUTUBE_HISTORY + " WHERE Device_ID = '"+ deviceID
                + "' ORDER BY " + COLUMN_CLIENT_YOUTUBE_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                YouTube youTube = new YouTube();
                    youTube.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX)));
                    youTube.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    youTube.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID)));
                    youTube.setVideo_Name(cursor.getString(cursor.getColumnIndex(COLUMN_VIDEO_NAME_YOUTUBE)));
                    youTube.setClient_Youtube_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_YOUTUBE_TIME)));
                    youTube.setChannel_Name(cursor.getString(cursor.getColumnIndex(COLUMN_CHANNEL_NAME_YOUTUBE)));
                    youTube.setViews(cursor.getString(cursor.getColumnIndex(COLUMN_VIEWS_YOUTUBE)));
                    youTube.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_YOUTUBE)));

                youTubeArrayList.add(youTube);

            } while (cursor.moveToNext());
        }
        // return note list
        return youTubeArrayList;
    }

    public int get_YouTube_Count_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseYouTube.get_YouTubeCount_DeviceID ... " + TABLE_YOUTUBE_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_YOUTUBE_HISTORY, new String[]{COLUMN_DEVICE_ID
                }, COLUMN_DEVICE_ID + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_YouTube_History(YouTube youTube) {
        Log.i(Global.TAG, "DatabaseYouTube.delete_YouTube_History... " + youTube.getID());

        database.getWritableDatabase().delete(TABLE_YOUTUBE_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(youTube.getID())});
        database.close();
    }

}
