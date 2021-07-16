package com.scp.viewer.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.scp.viewer.Database.Entity.AccountEntity.TABLE_USER;
import static com.scp.viewer.Database.Entity.AmbientRecordEntity.TABLE_AMBIENTRECORD_HISTORY;
import static com.scp.viewer.Database.Entity.ApplicationUsageEntity.TABLE_APPLICATION_HISTORY;
import static com.scp.viewer.Database.Entity.CallHistoryEntity.TABLE_CALL_HISTORY;
import static com.scp.viewer.Database.Entity.ContactEntity.TABLE_CONTACT_HISTORY;
import static com.scp.viewer.Database.Entity.DeviceEntity.TABLE_GET_SETTING;
import static com.scp.viewer.Database.Entity.GPSEntity.TABLE_GETLOCATION;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_PUSH_UPDATE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;
import static com.scp.viewer.Database.Entity.ManagementDeviceEntity.TABLE_DEVICE;
import static com.scp.viewer.Database.Entity.NotesEntity.TABLE_NOTE_HISTORY;
import static com.scp.viewer.Database.Entity.PhoneCallRecordEntity.TABLE_PHONECALLRECORD_HISTORY;
import static com.scp.viewer.Database.Entity.PhotoHistoryEntity.TABLE_PHOTO_HISTORY;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_BBM;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_FACEBOOK;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_HANGOUTS;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_KIK;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_LINE;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_SKYPE;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_SMS;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_VIBER;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_WHATSAPP;
import static com.scp.viewer.Database.Entity.URLEntity.TABLE_URL_HISTORY;
import static com.scp.viewer.Database.Entity.UserEntity.TABLE_USER_INFO;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: DatabaseHelper
 * History: 8/18/2020
 * Project: CP9
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "db_DViewerApp";
    public static final int DATABASE_VERSION = 12; // last up date: 2021-07-15

    private static DatabaseHelper instance;


    public static DatabaseHelper getInstance(Context context){
        if(instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AMBIENTRECORD_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATION_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GETLOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_SETTING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_SMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_WHATSAPP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_VIBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_FACEBOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_SKYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_HANGOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_BBM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_LINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_KIK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAST_UPDATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAST_PUSH_UPDATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONECALLRECORD_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URL_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_INFO);
    }

    public boolean checkTableExist(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();

        if (tableName == null || db == null || !db.isOpen())
            return false;

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }

        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

}
