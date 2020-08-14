/*
  ClassName: DatabaseContact.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseContact is used to create, add, modify, delete databases, save
  the history Contact from the server, use the "ContactHistory.class" and "ContactHistoryDetail.class".
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
import com.jexpa.secondclone.Model.Contact;
import com.jexpa.secondclone.API.APIDatabase;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_ADDRESS_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_CLIENT_CONTACT_TIME;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_COLOR_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_CONTACT_NAME;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_CREATED_DATE_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_DEVICE_ID_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_EMAIL_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_ID_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_ORGANIZATION_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_PHONE_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.COLUMN_ROWINDEX_CONTACT;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.DATABASE_NAME_CONTACT_HISTORY;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.DATABASE_VERSION_CONTACT_HISTORY;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.TABLE_CONTACT_HISTORY;

public class DatabaseContact extends SQLiteOpenHelper {
    SQLiteDatabase database;

    public DatabaseContact(Context context) {
        super(context, DATABASE_NAME_CONTACT_HISTORY, null, DATABASE_VERSION_CONTACT_HISTORY);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i(TAG, "DatabaseCall.onCreate ... " + TABLE_CONTACT_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_CONTACT_HISTORY + "(" + COLUMN_ROWINDEX_CONTACT + " INTEGER ," + COLUMN_ID_CONTACT + " INTEGER,"
                + COLUMN_DEVICE_ID_CONTACT + " TEXT," + COLUMN_CLIENT_CONTACT_TIME + " TEXT," + COLUMN_CONTACT_NAME + " TEXT,"
                + COLUMN_PHONE_CONTACT + " TEXT," + COLUMN_EMAIL_CONTACT + " INTEGER," + COLUMN_ORGANIZATION_CONTACT + " INTEGER," +
                COLUMN_ADDRESS_CONTACT + " TEXT," + COLUMN_CREATED_DATE_CONTACT + " TEXT," + COLUMN_COLOR_CONTACT + " INTEGER" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_HISTORY);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_Contact(List<Contact> contact) {
        database = this.getWritableDatabase();
        database.beginTransaction();
        try {
            for (int i = 0; i < contact.size(); i++) {
                //  contentValues1 receives the value from the method API_Add_Database()
                ContentValues contentValues1 = APIDatabase.API_Add_Database(contact.get(i),false);
                // Insert a row of data into the table.
                database.insert(TABLE_CONTACT_HISTORY, null, contentValues1);
            }

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }

        //  Close the database connection.
        database.close();
    }

    public List<Contact> getAll_Contact_ID_History(String deviceID, int offSET) {
        Log.i(TAG, "DatabaseCall.getAll_Location ... " + TABLE_CONTACT_HISTORY);
        List<Contact> contact_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT_HISTORY +" WHERE Device_ID = '"+deviceID+ "' ORDER BY " + COLUMN_CLIENT_CONTACT_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSET;
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                //if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_CONTACT)).equals(deviceID)) {

                    Contact contact = new Contact();
                    contact.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROWINDEX_CONTACT)));
                    contact.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_CONTACT)));
                    contact.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_CONTACT)));
                    contact.setClient_Contact_Time(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CONTACT_TIME)));
                    contact.setContact_Name(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME)));
                    contact.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_CONTACT)));
                    contact.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_CONTACT)));
                    contact.setOrganization(cursor.getString(cursor.getColumnIndex(COLUMN_ORGANIZATION_CONTACT)));
                    contact.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS_CONTACT)));
                    contact.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_CONTACT)));
                    contact.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR_CONTACT)));
                    // Add in List.
                    contact_List.add(contact);
                //}
            } while (cursor.moveToNext());
        }
        // return note list
        return contact_List;
    }

    // Method retrieving data by date to compare.
    public List<Integer> getAll_Contact_ID_History_Date(String deviceID, String date) {

        Log.i(TAG, "DatabaseContact.getAll_Contact_ID_History_Date..." + TABLE_CONTACT_HISTORY);

        List<Integer> contact_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT_HISTORY + " WHERE " + COLUMN_DEVICE_ID_CONTACT + " = '" + deviceID + "'";//+"' AND " +COLUMN_CLIENT_CAPTURED_DATE_PHOTO+" = '"+date+"'", String date
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) do {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CONTACT_TIME)).substring(0, 10).equals(date)) {
                contact_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_CONTACT)));
            }
            // Add in List.

        } while (cursor.moveToNext());
        database.close();
        return contact_List;
    }

    public int get_Contact_Count(String deviceID) {
        Log.i(TAG, "DatabaseContact.get_Contact_Count ... " + TABLE_CONTACT_HISTORY);
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_CONTACT_HISTORY, new String[]{COLUMN_DEVICE_ID_CONTACT
                }, COLUMN_DEVICE_ID_CONTACT + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int get_ContactCount_DeviceID(String deviceID) {
        Log.i(TAG, "DatabaseSMS.getSMSCount ... " + TABLE_CONTACT_HISTORY);
        database = this.getReadableDatabase();
        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.query(TABLE_CONTACT_HISTORY, new String[]{COLUMN_DEVICE_ID_CONTACT
                }, COLUMN_DEVICE_ID_CONTACT + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void delete_Contact_History(Contact contact) {
        Log.i(TAG, "DatabaseCall.deleteLocation ... " + contact.getID());
        database = this.getWritableDatabase();
        database.delete(TABLE_CONTACT_HISTORY, COLUMN_ID_CONTACT + " = ?",
                new String[]{String.valueOf(contact.getID())});
        database.close();
    }

}
