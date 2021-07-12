/*
  ClassName: DatabaseContact.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseContact is used to create, add, modify, delete databases, save
  the history Contact from the server, use the "ContactHistory.class" and "ContactHistoryDetail.class".
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.scp.viewer.Model.Contact;
import com.scp.viewer.API.APIDatabase;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.Global.TAG;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.Database.DatabaseHelper.getInstance;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_ADDRESS_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_CLIENT_CONTACT_TIME;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_COLOR_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_CONTACT_NAME;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_CREATED_DATE_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_DEVICE_ID_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_EMAIL_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_ID_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_ORGANIZATION_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_PHONE_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.COLUMN_ROWINDEX_CONTACT;
import static com.scp.viewer.Database.Entity.ContactEntity.TABLE_CONTACT_HISTORY;

public class DatabaseContact  {

    private Context context;
    private DatabaseHelper database;
    public DatabaseContact(Context context) {
        this.context = context;
        this.database = getInstance(context);
        if(!database.checkTableExist(TABLE_CONTACT_HISTORY))
            createTable();
    }

    private void createTable() {

        Log.i(TAG, "DatabaseCall.onCreate ... " + TABLE_CONTACT_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_CONTACT_HISTORY + "(" + COLUMN_ROWINDEX_CONTACT + " LONG ," + COLUMN_ID_CONTACT + " LONG,"
                + COLUMN_DEVICE_ID_CONTACT + " TEXT," + COLUMN_CLIENT_CONTACT_TIME + " TEXT," + COLUMN_CONTACT_NAME + " TEXT,"
                + COLUMN_PHONE_CONTACT + " TEXT," + COLUMN_EMAIL_CONTACT + " INTEGER," + COLUMN_ORGANIZATION_CONTACT + " INTEGER," +
                COLUMN_ADDRESS_CONTACT + " TEXT," + COLUMN_CREATED_DATE_CONTACT + " TEXT," + COLUMN_COLOR_CONTACT + " INTEGER" + ")";
        database.getWritableDatabase().execSQL(scriptTable);
    }

    public void addDevice_Contact(List<Contact> contact)
    {

        database.getWritableDatabase().beginTransaction();
        try {
            for (int i = 0; i < contact.size(); i++) {

                if(!checkItemExist(database.getWritableDatabase(),TABLE_CONTACT_HISTORY,COLUMN_DEVICE_ID_CONTACT,contact.get(i).getDevice_ID(),COLUMN_ID_CONTACT,contact.get(i).getID()))
                {
                    //  contentValues1 receives the value from the method API_Add_Database()
                    ContentValues contentValues1 = APIDatabase.API_Add_Database(contact.get(i),false);
                    Log.d("ContactHistory"," Add Contact = "+  contentValues1);
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(TABLE_CONTACT_HISTORY, null, contentValues1);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();
        } finally {
            database.getWritableDatabase().endTransaction();
        }

        //  Close the database connection.
        database.close();

    }

    /**
     * checkItemExist This is a support method to check whether this record already exists in the database or not and add it to the database.
     */
    public static boolean checkItemExist(SQLiteDatabase database, String tableName,String rawDeviceID, String deviceID,String rawIdContact, long idContact){

        String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = %s", tableName, rawDeviceID, deviceID, rawIdContact, idContact);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public List<Contact> getAll_Contact_ID_History(String deviceID, int offSET) {
        Log.i(TAG, "DatabaseCall.getAll_Location ... " + TABLE_CONTACT_HISTORY);
        List<Contact> contact_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT_HISTORY +" WHERE Device_ID = '"+deviceID+ "' ORDER BY " + COLUMN_CLIENT_CONTACT_TIME + " DESC LIMIT "+ NumberLoad +" OFFSET "+ offSET;
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                //if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_CONTACT)).equals(deviceID)) {

                    Contact contact = new Contact();
                    contact.setRowIndex(cursor.getLong(cursor.getColumnIndex(COLUMN_ROWINDEX_CONTACT)));
                    contact.setID(cursor.getLong(cursor.getColumnIndex(COLUMN_ID_CONTACT)));
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

    public int get_ContactCount_DeviceID(String deviceID) {
        Log.i(TAG, "DatabaseSMS.getSMSCount ... " + TABLE_CONTACT_HISTORY);

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(TABLE_CONTACT_HISTORY, new String[]{COLUMN_DEVICE_ID_CONTACT
                }, COLUMN_DEVICE_ID_CONTACT + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void delete_Contact_History(Contact contact) {
        Log.i(TAG, "DatabaseCall.deleteLocation ... " + contact.getID());
        database.getWritableDatabase().delete(TABLE_CONTACT_HISTORY, COLUMN_ID_CONTACT + " = ?",
                new String[]{String.valueOf(contact.getID())});
        database.close();
    }

}
