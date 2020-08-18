/*
  ClassName: DatabaseUser.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseUser is used to create, add, modify, delete databases, save
  the history User from memory in the phone, use the "ManagementDevice.class" and "LaunchScreen.class" and "Authentication.class".
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

import com.jexpa.secondclone.Model.User;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.jexpa.secondclone.Database.DatabaseHelper.getInstance;
import static com.jexpa.secondclone.Database.Entity.AccountEntity.COLUMN_USER_EMAIL;
import static com.jexpa.secondclone.Database.Entity.AccountEntity.COLUMN_USER_ID;
import static com.jexpa.secondclone.Database.Entity.AccountEntity.COLUMN_USER_PASSWORD;
import static com.jexpa.secondclone.Database.Entity.AccountEntity.DATABASE_NAME;
import static com.jexpa.secondclone.Database.Entity.AccountEntity.DATABASE_VERSION;
import static com.jexpa.secondclone.Database.Entity.AccountEntity.TABLE_USER;
import static com.jexpa.secondclone.Database.Entity.URLEntity.TABLE_URL_HISTORY;

public class DatabaseUser
{
    private DatabaseHelper database;

    public DatabaseUser(Context context) {
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_USER))
            createTable();
    }

    // Create tables.
    public void createTable() {
        Log.i(TAG, "DatabaseUser.onCreate ... ");
        String script = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY," + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_PASSWORD + " TEXT" + ")";
        // Run create table command.
        database.getWritableDatabase().execSQL(script);
    }

    public void addUser(User user) {
        Log.i(TAG, "DatabaseUser.addNote ... " + user.getEmail());

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        // Insert a row of data into the table.
        database.getWritableDatabase().insert(TABLE_USER, null, values);
        //Close the database connection.
        database.close();
    }

    // User user1=getNote(1);
    public User getNote(int id) {
        Log.i(TAG, "DatabaseUser.getNote ... " + id);

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().query(TABLE_USER, new String[]{COLUMN_USER_ID,
                        COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD}, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        // return note
        return new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
    }

    public int getNotesCount() {
        Log.i(TAG, "DatabaseUser.getNotesCount ... ");
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        Cursor cursor = database.getWritableDatabase().rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void updateUser(User user) {
        Log.i(TAG, "DatabaseUser.updateNote ... " + user.getEmail());

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        // updating row
        database.getWritableDatabase().update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }
}
