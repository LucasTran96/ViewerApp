/*
  ClassName: DatabaseApplicationUsage.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseApplicationUsage is used to create, add, modify, delete databases, save
  the Application Usage values from the server, use the "ApplicationUsageHistory.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database.Entity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.Global;
import com.scp.viewer.Database.DatabaseHelper;
import com.scp.viewer.Model.AppInstallation;
import com.scp.viewer.Model.ApplicationUsage;

import java.util.ArrayList;
import java.util.List;

import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseContact.checkItemExist;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_APP_ID_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_APP_NAME_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_APP_PLAY_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_APP_SOURCE_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_CLIENT_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_CREATED_DATE_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_DEVICE_ID_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_ID_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.COLUMN_ROW_INDEX_APP_INSTALL;
import static com.scp.viewer.Database.Entity.AppInstallationEntity.TABLE_APP_INSTALLATION_HISTORY;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_APP_ID_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_APP_NAME_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_APP_TYPE_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_CLIENT_APPLICATION_TIME;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_CREATED_DATE_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_DEVICE_ID_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_ID_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.COLUMN_ROWINDEX_APPLICATION;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.TABLE_APPLICATION_HISTORY;

public class DatabaseAppInstallation
{
    private Context context;
    private DatabaseHelper database;
    public DatabaseAppInstallation(Context context) {

        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_APP_INSTALLATION_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(Global.TAG, "DatabaseApplicationUsage.onCreate ... " + TABLE_APP_INSTALLATION_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_APP_INSTALLATION_HISTORY + "(" + COLUMN_ROW_INDEX_APP_INSTALL + " LONG ," + COLUMN_ID_APP_INSTALL + " LONG,"
                + COLUMN_DEVICE_ID_APP_INSTALL + " TEXT,"
                + COLUMN_APP_NAME_APP_INSTALL + " TEXT,"
                + COLUMN_CLIENT_APP_INSTALL + " TEXT,"
                + COLUMN_APP_ID_APP_INSTALL + " TEXT,"
                + COLUMN_APP_SOURCE_APP_INSTALL + " TEXT,"
                + COLUMN_APP_PLAY_APP_INSTALL + " INTEGER,"
                + COLUMN_CREATED_DATE_APP_INSTALL + " TEXT" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }


    public void addDevice_AppInstall(List<AppInstallation> appInstallations) {

        database.getWritableDatabase().beginTransaction();

        try {
            for (int i = 0; i < appInstallations.size(); i++) {
                if(!checkItemExist(database.getWritableDatabase(), TABLE_APP_INSTALLATION_HISTORY, COLUMN_DEVICE_ID_APP_INSTALL, appInstallations.get(i).getDevice_ID(), COLUMN_ID_APP_INSTALL, appInstallations.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(appInstallations.get(i),false);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_APP_INSTALLATION_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();

        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();
    }


    public List<AppInstallation> getAll_AppInstall_ID_History(String deviceID, int offSet) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.getAll_URL_ID_History ... " + TABLE_APP_INSTALLATION_HISTORY);
        List<AppInstallation> notes_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_APP_INSTALLATION_HISTORY + " WHERE Device_ID = '"+ deviceID + "' ORDER BY " + COLUMN_CLIENT_APP_INSTALL + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSet;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                    AppInstallation appInstallation = new AppInstallation();
                    appInstallation.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROW_INDEX_APP_INSTALL)));
                    appInstallation.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_APP_INSTALL)));
                    appInstallation.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_APP_INSTALL)));
                    appInstallation.setApp_Name(cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME_APP_INSTALL)));
                    appInstallation.setClient_App_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_APP_INSTALL)));
                    appInstallation.setApp_ID(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID_APP_INSTALL)));
                    appInstallation.setSource(cursor.getString(cursor.getColumnIndex(COLUMN_APP_SOURCE_APP_INSTALL)));
                    appInstallation.setFrom_Play_Store(cursor.getInt(cursor.getColumnIndex(COLUMN_APP_PLAY_APP_INSTALL)));
                    appInstallation.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_APP_INSTALL)));

                    // Add in List.
                    notes_List.add(appInstallation);

            } while (cursor.moveToNext());
        }
        // return note list
        return notes_List;
    }

    public int get_ApplicationCount_DeviceID(String deviceID) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.get_ApplicationCount_DeviceID ... " + TABLE_APP_INSTALLATION_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_APP_INSTALLATION_HISTORY, new String[]{COLUMN_DEVICE_ID_APP_INSTALL
                }, COLUMN_DEVICE_ID_APP_INSTALL + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void delete_Application_History(AppInstallation appInstallation) {
        Log.i(Global.TAG, "DatabaseApplicationUsage.delete_Application_History... " + appInstallation.getID());

        database.getWritableDatabase().delete(TABLE_APP_INSTALLATION_HISTORY, COLUMN_ID_APPLICATION + " = ?",
                new String[]{String.valueOf(appInstallation.getID())});
        database.close();
    }

}
