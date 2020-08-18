/*
  ClassName: DatabaseUserInfo.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseUser is used to create, add, modify, delete databases, save
  the history User from sever, use the "ManagementDevice.class" and "Dashboard.class".
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
import com.jexpa.secondclone.Model.AccountInFo;
import java.util.ArrayList;
import java.util.List;
import static android.content.ContentValues.TAG;
import static com.jexpa.secondclone.API.APIDatabase.API_Add_Database;
import static com.jexpa.secondclone.Database.DatabaseHelper.getInstance;
import static com.jexpa.secondclone.Database.Entity.AccountEntity.TABLE_USER;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_CREATED_DATE_USER;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_EXPIRY_DATE;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_ID_USERINFO;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_LAST_IP_ACCESS;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_LAST_TIME_ACCESS;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_LOGIN_NAME_USER;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_MAX_DEVICE;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_MODIFIED_DATE;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_NICK_NAME;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_PACKAGE_ID;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_PASSWORD_USER;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_STATUS;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_TIME_ZONE_ID;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_TRACKING_LEVEL;
import static com.jexpa.secondclone.Database.Entity.UserEntity.COLUMN_USER_TYPE;
import static com.jexpa.secondclone.Database.Entity.UserEntity.DATABASE_NAME_USER_INFO;
import static com.jexpa.secondclone.Database.Entity.UserEntity.DATABASE_VERSION_USER_INFO;
import static com.jexpa.secondclone.Database.Entity.UserEntity.TABLE_USER_INFO;

public class DatabaseUserInfo
{
    private DatabaseHelper database;

    public DatabaseUserInfo(Context context) {
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_USER_INFO))
            createTable();
    }

    // Create tables.
    public void createTable() {
        Log.i(TAG, "DatabaseUser.onCreate ... " + TABLE_USER_INFO);
        // Script create table.
        String scriptTable = "CREATE TABLE " + TABLE_USER_INFO + "("
                + COLUMN_ID_USERINFO + " INTEGER PRIMARY KEY," + COLUMN_LOGIN_NAME_USER + " TEXT,"
                + COLUMN_PASSWORD_USER + " TEXT," + COLUMN_EXPIRY_DATE + " TEXT," + COLUMN_STATUS + " TEXT,"
                + COLUMN_CREATED_DATE_USER + " TEXT," + COLUMN_MODIFIED_DATE + " TEXT," +
                COLUMN_USER_TYPE + " TEXT," + COLUMN_NICK_NAME + " TEXT," + COLUMN_MAX_DEVICE + " TEXT,"
                + COLUMN_PACKAGE_ID + " TEXT," + COLUMN_TRACKING_LEVEL + " TEXT," +
                COLUMN_LAST_IP_ACCESS + " TEXT," + COLUMN_LAST_TIME_ACCESS + " TEXT," + COLUMN_TIME_ZONE_ID + " TEXT" + ")";
        // Run create table command.
        database.getWritableDatabase().execSQL(scriptTable);
    }


    public void addUserInfo(AccountInFo accountInFo) {
        ContentValues values ;
        values = API_Add_Database(accountInFo,false);
        // Insert a row of data into the table.
        database.getWritableDatabase().insert(TABLE_USER_INFO, null, values);
        //Close the database connection.
        database.close();


    }

    // User user1=getNote(1);

    public List<AccountInFo> getAllUserInfo() {

        Log.i(TAG, "DatabaseUser.getAllDevice ... ");
        List<AccountInFo> listAccountInfo = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_INFO;

        @SuppressLint("Recycle") Cursor cursor =  database.getWritableDatabase().rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                AccountInFo accountInFo = new AccountInFo();
                accountInFo.setID(String.valueOf(cursor.getInt(0)));
                accountInFo.setLogin_Name(cursor.getString(1));
                accountInFo.setPassword(cursor.getString(2));
                accountInFo.setExpiry_Date(cursor.getString(3));
                accountInFo.setStatus(cursor.getString(4));
                accountInFo.setCreated_Date(cursor.getString(5));
                accountInFo.setModified_Date(cursor.getString(6));
                accountInFo.setUser_Type(cursor.getString(7));
                accountInFo.setNick_Name(cursor.getString(8));
                accountInFo.setMax_Device(cursor.getString(9));
                accountInFo.setPackage_ID(cursor.getString(10));
                accountInFo.setTracking_Level(cursor.getString(11));
                accountInFo.setLast_IP_Access(cursor.getString(12));
                accountInFo.setLast_Time_Access(cursor.getString(13));
                accountInFo.setTime_Zone_ID(cursor.getString(14));
                // Add in List.
                listAccountInfo.add(accountInFo);
            } while (cursor.moveToNext());
        }
        // return note list
        return listAccountInfo;
    }

    public int getUserInfoCount() {
        // Log.i(TAG, "DatabaseUser.getNotesCount ... " );
        String countQuery = "SELECT  * FROM " + TABLE_USER_INFO;
        Cursor cursor =  database.getWritableDatabase().rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteUserInfo(AccountInFo accountInFo) {
        Log.i(TAG, "DatabaseUser.updateNote ... " + accountInFo.getLogin_Name());

        database.getWritableDatabase().delete(TABLE_USER_INFO, COLUMN_ID_USERINFO + " = ?",
                new String[]{String.valueOf(accountInFo.getID())});
        database.close();
    }
}
