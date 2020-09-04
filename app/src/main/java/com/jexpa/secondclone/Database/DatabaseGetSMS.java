/*
  ClassName: DatabaseGetSMS.java
  Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabaseGetSMS is used to create, add, modify, delete databases, save
  the history SMS from the server, use the "SMSHistory.class" and "SMSHistoryDetail.class".
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

import com.jexpa.secondclone.Model.SMS;

import java.util.ArrayList;
import java.util.List;

import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.Database.DatabaseContact.checkItemExist;
import static com.jexpa.secondclone.Database.DatabaseHelper.getInstance;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.TABLE_GET_SETTING;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_CLIENT_MESSAGE_TIME_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_CONTACT_NAME_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_CREATED_DATE_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_DEVICE_ID_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_DIRECTION_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_ID_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_PHONE_NUMBER_SIM_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_PHONE_NUMBER_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.COLUMN_TEXT_MESSAGE_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.DATABASE_NAME_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.DATABASE_VERSION_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_BBM;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_FACEBOOK;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_HANGOUTS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_KIK;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_LINE;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_SKYPE;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_SMS;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_VIBER;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.TABLE_GET_WHATSAPP;

public class DatabaseGetSMS
{

    private Context context;
    private DatabaseHelper database;

    public DatabaseGetSMS(Context context) {
        this.context = context;
        this.database = getInstance(context);
        createTable();
    }

    private static final String CREATE_TABLE_SMS = " CREATE TABLE " + TABLE_GET_SMS + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";
    private static final String CREATE_TABLE_WHATSAPP = " CREATE TABLE " + TABLE_GET_WHATSAPP + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";

    private static final String CREATE_TABLE_VIBER = " CREATE TABLE " + TABLE_GET_VIBER + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";

    private static final String CREATE_TABLE_FACEBOOK = " CREATE TABLE " + TABLE_GET_FACEBOOK + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";

    private static final String CREATE_TABLE_SKYPE = " CREATE TABLE " + TABLE_GET_SKYPE + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";

    private static final String CREATE_TABLE_HANGOUTS = " CREATE TABLE " + TABLE_GET_HANGOUTS + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";

    private static final String CREATE_TABLE_BBM = " CREATE TABLE " + TABLE_GET_BBM + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";

    private static final String CREATE_TABLE_LINE = " CREATE TABLE " + TABLE_GET_LINE + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";

    private static final String CREATE_TABLE_KIK = " CREATE TABLE " + TABLE_GET_KIK + "(" + COLUMN_ID_SMS + " INTEGER  ," + COLUMN_DEVICE_ID_SMS + " TEXT,"
            + COLUMN_CLIENT_MESSAGE_TIME_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SIM_SMS + " TEXT," + COLUMN_PHONE_NUMBER_SMS + " TEXT,"
            + COLUMN_DIRECTION_SMS + " INTEGER," + COLUMN_TEXT_MESSAGE_SMS + " TEXT," + COLUMN_CONTACT_NAME_SMS + " TEXT," +
            COLUMN_CREATED_DATE_SMS + " TEXT" + ")";


    public void createTable() {
        Log.i(TAG, "DatabaseSMS.onCreate ... " + TABLE_GET_SMS);
        if(!database.checkTableExist(TABLE_GET_SMS))
            database.getWritableDatabase().execSQL(CREATE_TABLE_SMS);
        if(!database.checkTableExist(TABLE_GET_WHATSAPP))
            database.getWritableDatabase().execSQL(CREATE_TABLE_WHATSAPP);
        if(!database.checkTableExist(TABLE_GET_VIBER))
            database.getWritableDatabase().execSQL(CREATE_TABLE_VIBER);
        if(!database.checkTableExist(TABLE_GET_FACEBOOK))
            database.getWritableDatabase().execSQL(CREATE_TABLE_FACEBOOK);
        if(!database.checkTableExist(TABLE_GET_SKYPE))
            database.getWritableDatabase().execSQL(CREATE_TABLE_SKYPE);
        if(!database.checkTableExist(TABLE_GET_HANGOUTS))
            database.getWritableDatabase().execSQL(CREATE_TABLE_HANGOUTS);
        if(!database.checkTableExist(TABLE_GET_BBM))
            database.getWritableDatabase().execSQL(CREATE_TABLE_BBM);
        if(!database.checkTableExist(TABLE_GET_LINE))
            database.getWritableDatabase().execSQL(CREATE_TABLE_LINE);
        if(!database.checkTableExist(TABLE_GET_KIK))
            database.getWritableDatabase().execSQL(CREATE_TABLE_KIK);
    }


    public void addDevice_SMS(List<SMS> sms, String tableName) {
        Log.i(TAG, "DatabaseSMS.addDevice ... " + tableName);
        database.getWritableDatabase().beginTransaction();
        //  contentValues1 receives the value from the method API_Add_Database()
        //ContentValues contentValues_SMS = API_Add_Database(sms);
        try {

            for (int i = 0; i < sms.size(); i++) {

                if(!checkItemExist(database.getWritableDatabase(),tableName,COLUMN_DEVICE_ID_SMS,sms.get(i).getDevice_ID(),COLUMN_ID_SMS,sms.get(i).getID()))
                {
                    ContentValues contentValues_SMS = new ContentValues();
                    contentValues_SMS.put(COLUMN_ID_SMS, sms.get(i).getID());
                    contentValues_SMS.put(COLUMN_DEVICE_ID_SMS, sms.get(i).getDevice_ID());
                    contentValues_SMS.put(COLUMN_CLIENT_MESSAGE_TIME_SMS, sms.get(i).getClient_Message_Time());
                    contentValues_SMS.put(COLUMN_PHONE_NUMBER_SIM_SMS, sms.get(i).getPhone_Number_SIM());
                    contentValues_SMS.put(COLUMN_PHONE_NUMBER_SMS, sms.get(i).getPhone_Number());
                    contentValues_SMS.put(COLUMN_DIRECTION_SMS, sms.get(i).getDirection());
                    contentValues_SMS.put(COLUMN_TEXT_MESSAGE_SMS, sms.get(i).getText_Message());
                    contentValues_SMS.put(COLUMN_CONTACT_NAME_SMS, sms.get(i).getContact_Name());
                    contentValues_SMS.put(COLUMN_CREATED_DATE_SMS, sms.get(i).getCreated_Date());
                    // Insert a row of data into the table.
                    database.getWritableDatabase().insert(tableName, null, contentValues_SMS);
                }
            }
            database.getWritableDatabase().setTransactionSuccessful();
        } finally {
            database.getWritableDatabase().endTransaction();
        }
        //  Close the database connection.
        database.close();

    }
    public List<SMS> getAll_SMS_Name(String Name, String tableName) {
        Log.i(TAG, "DatabaseSMS.getAll_SMS_Name ... " + tableName);
        List<SMS> list_SMS = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableName + " ORDER BY " + COLUMN_CLIENT_MESSAGE_TIME_SMS + " ";
        //String selectQuery = "SELECT  * FROM " + tableName +" WHERE Device_ID = '"+deviceID+ "' AND Device_ID = '"+ Name + "' ORDER BY " + COLUMN_CLIENT_MESSAGE_TIME_SMS + " ASC LIMIT "+NumberLoad+" OFFSET "+ offSET;
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(7).equals(Name)) {
                    SMS sms = new SMS();
                    sms.setID(cursor.getInt(0));
                    sms.setDevice_ID(cursor.getString(1));
                    sms.setClient_Message_Time(cursor.getString(2));
                    sms.setPhone_Number_SIM(cursor.getString(3));
                    sms.setPhone_Number(cursor.getString(4));
                    sms.setDirection(cursor.getInt(5));
                    sms.setText_Message(cursor.getString(6));
                    sms.setContact_Name(cursor.getString(7));
                    sms.setCreated_Date(cursor.getString(8));
                    // Add in List.
                    list_SMS.add(sms);
                }

            } while (cursor.moveToNext());
        }

        // return note list
        return list_SMS;
    }

    public List<SMS> getAll_SMS_Name_Offset(String Name, String tableName, int offSet) {
        Log.i(TAG, "DatabaseSMS.getAll_SMS_Name ... " + tableName);
        List<SMS> list_SMS = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableName +" WHERE Contact_Name = '"+ Name + "' ORDER BY " + COLUMN_CLIENT_MESSAGE_TIME_SMS + " DESC LIMIT "+NumberLoad+" OFFSET "+ offSet;
        //String selectQuery = "SELECT  * FROM " + tableName +" WHERE Device_ID = '"+deviceID+ "' AND Contact_Name = '"+ Name + "' ORDER BY " + COLUMN_CLIENT_MESSAGE_TIME_SMS + " ASC LIMIT "+NumberLoad+" OFFSET "+ offSet;
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);
        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                    SMS sms = new SMS();
                    sms.setID(cursor.getInt(0));
                    sms.setDevice_ID(cursor.getString(1));
                    sms.setClient_Message_Time(cursor.getString(2));
                    sms.setPhone_Number_SIM(cursor.getString(3));
                    sms.setPhone_Number(cursor.getString(4));
                    sms.setDirection(cursor.getInt(5));
                    sms.setText_Message(cursor.getString(6));
                    sms.setContact_Name(cursor.getString(7));
                    sms.setCreated_Date(cursor.getString(8));
                    // Add in List.
                    list_SMS.add(0,sms);
            } while (cursor.moveToNext());
        }
        // return note list
        return list_SMS;
    }

    //SELECT DISTINCT Country FROM Customers;
    // The method of obtaining names does not coincide
    public List<SMS> get_DISTINCT_SMS_Name(String device_ID, String tableName) {
        Log.i(TAG, "DatabaseSMS.get_DISTINCT_SMS_Name ... " + tableName);
        List<SMS> list_SMS = new ArrayList<>();
        // Select All Query
        String selectQuery1 = "SELECT * FROM " + tableName + " WHERE " + COLUMN_CLIENT_MESSAGE_TIME_SMS + " IN( SELECT MAX(" + COLUMN_CLIENT_MESSAGE_TIME_SMS  + ") FROM " + tableName  + " GROUP BY  " + COLUMN_CONTACT_NAME_SMS + ")" + " ORDER BY " + COLUMN_CLIENT_MESSAGE_TIME_SMS + " DESC ";

        String selectQuery = "select tb.* from "+ tableName +" tb inner join (select Contact_Name, max(Client_Message_Time) as maxtime from "+ tableName +" group by Contact_Name ) filter on tb.Contact_Name = filter.Contact_Name and tb.Client_Message_Time = filter.maxtime "+ " ORDER BY " + COLUMN_CLIENT_MESSAGE_TIME_SMS + " DESC " ;

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);
        // COLUMN_CLIENT_MESSAGE_TIME_SMS

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(1).equals(device_ID)) {
                    SMS sms = new SMS();
                    sms.setID(cursor.getInt(0));
                    sms.setDevice_ID(cursor.getString(1));
                    sms.setClient_Message_Time(cursor.getString(2));
                    sms.setPhone_Number_SIM(cursor.getString(3));
                    sms.setPhone_Number(cursor.getString(4));
                    sms.setDirection(cursor.getInt(5));
                    sms.setText_Message(cursor.getString(6));
                    sms.setContact_Name(cursor.getString(7));
                    sms.setCreated_Date(cursor.getString(8));
                    // Add in List.
                    list_SMS.add(sms);
                }
            } while (cursor.moveToNext());
        }

        // return note list
        return list_SMS;
    }

    // Method retrieving data by date to compare.
    public List<Integer> getAll_SMS_ID_History_Date(String deviceID, String date, String tableName) {

        Log.i(TAG, "DatabaseSMS.getAll_SMS_ID_History_Date... " + tableName);

        List<Integer> sms_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableName + " WHERE " + COLUMN_DEVICE_ID_SMS + " = '" + deviceID + "'";//+"' AND " +COLUMN_CLIENT_CAPTURED_DATE_PHOTO+" = '"+date+"'", String date
        //SQLiteDatabase database = this.getWritableDatabase();

        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_MESSAGE_TIME_SMS)).substring(0, 10).equals(date)) {
                    sms_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_SMS)));
                }
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return sms_List;
    }

    // The method get a row of data into the table
    public List<SMS> getSMS_Contact_Name(String name, String tableName) {
        Log.i(TAG, "DatabaseSMS.getSMS ... " + name);

        List<SMS> list_SMS = new ArrayList<>();
        //  Cursor retrieves the values ​​of 1 row in the Location table via the cast id
        @SuppressLint("Recycle") Cursor cursor = database.getWritableDatabase().query(tableName, new String[]{COLUMN_ID_SMS, COLUMN_DEVICE_ID_SMS, COLUMN_CLIENT_MESSAGE_TIME_SMS,
                        COLUMN_PHONE_NUMBER_SIM_SMS, COLUMN_PHONE_NUMBER_SMS, COLUMN_DIRECTION_SMS, COLUMN_TEXT_MESSAGE_SMS, COLUMN_CONTACT_NAME_SMS, COLUMN_CREATED_DATE_SMS
                }, COLUMN_CONTACT_NAME_SMS + "=?",
                new String[]{name}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(7).equals(name)) {
                    SMS sms = new SMS();
                    sms.setID(cursor.getInt(0));
                    sms.setDevice_ID(cursor.getString(1));
                    sms.setClient_Message_Time(cursor.getString(2));
                    sms.setPhone_Number_SIM(cursor.getString(3));
                    sms.setPhone_Number(cursor.getString(4));
                    sms.setDirection(cursor.getInt(5));
                    sms.setText_Message(cursor.getString(6));
                    sms.setContact_Name(cursor.getString(7));
                    sms.setCreated_Date(cursor.getString(8));
                    // Add in List.
                    list_SMS.add(sms);
                }
            } while (cursor.moveToNext());
        }

        // return table
        return list_SMS;
    }

    // Method of counting the number of columns in table TABLE_GET_SMS.
    public int getSMSCount(String tableName, String deviceID) {
        Log.i(TAG, "DatabaseSMS.getSMSCount ... " + tableName);
        //String countQuery = "SELECT  * FROM " + tableName;

        //Cursor cursor = database.rawQuery(countQuery, null);
        Cursor cursor = database.getWritableDatabase().query(tableName, new String[]{COLUMN_DEVICE_ID_SMS
                }, COLUMN_DEVICE_ID_SMS + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // method delete rows by each row when ID_SMS = sms.getID ().
    public void deleteSMS(SMS sms, String tableName) {
        Log.i(TAG, "DatabaseSMS.deleteSMS ... " + sms.getID());

        //String countQuery = "DELETE FROM Students WHERE DEVICE_ID_SMS = "+sms.getDevice_ID()+" AND "+ CONTACT_NAME_SMS + " = "+ " ";
        database.getWritableDatabase().delete(tableName, COLUMN_CONTACT_NAME_SMS + " = ?",
                new String[]{String.valueOf(sms.getContact_Name())});
        database.close();
    }

    public void deleteSMS_ID(SMS sms, String tableName) {
        Log.i(TAG, "DatabaseSMS.deleteSMS ... " + sms.getID());

        database.getWritableDatabase().delete(tableName, COLUMN_ID_SMS + " = ?",
                new String[]{String.valueOf(sms.getID())});
        database.close();
    }

}
