/*
  ClassName: DatabasePhotos.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class DatabasePhotos is used to create, add, modify, delete databases, save
  the history Photo from the server, use the "PhotoHistory.class" and "PhotoHistoryDetail.class".
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

import com.jexpa.secondclone.Model.Photo;

import java.util.ArrayList;
import java.util.List;

import static com.jexpa.secondclone.API.APIDatabase.API_Add_Database;
import static com.jexpa.secondclone.API.Global.TAG;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_CAPTION_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_CDN_URL_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_CLIENT_CAPTURED_DATE_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_CREATED_DATE_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_DEVICE_ID_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_EXT_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_FILE_NAME_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_ID_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_ISLOADED_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_MEDIA_URL_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.COLUMN_ROWINDEX_PHOTO;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.DATABASE_NAME_PHOTO_HISTORY;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.DATABASE_VERSION_PHOTO_HISTORY;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.TABLE_PHOTO_HISTORY;

public class DatabasePhotos extends SQLiteOpenHelper {

    SQLiteDatabase database;


    public DatabasePhotos(Context context) {
        super(context, DATABASE_NAME_PHOTO_HISTORY, null, DATABASE_VERSION_PHOTO_HISTORY);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        Log.i(TAG, "DatabaseCall.onCreate ... " + TABLE_PHOTO_HISTORY);
        String scriptTable = " CREATE TABLE " + TABLE_PHOTO_HISTORY + "(" + COLUMN_ROWINDEX_PHOTO + " INTEGER ," + COLUMN_ID_PHOTO + " INTEGER,"
                + COLUMN_ISLOADED_PHOTO + " INTEGER," + COLUMN_DEVICE_ID_PHOTO + " TEXT," + COLUMN_CLIENT_CAPTURED_DATE_PHOTO + " TEXT," + COLUMN_CAPTION_PHOTO + " TEXT,"
                + COLUMN_FILE_NAME_PHOTO + " TEXT," + COLUMN_EXT_PHOTO + " TEXT," + COLUMN_MEDIA_URL_PHOTO + " TEXT," +
                COLUMN_CREATED_DATE_PHOTO + " TEXT," + COLUMN_CDN_URL_PHOTO + " TEXT" + ")";
        sqLiteDatabase.execSQL(scriptTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Delete old table if it already exists.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO_HISTORY);
        // And recreate the table.
        onCreate(sqLiteDatabase);

    }

    public void addDevice_Photos_Fast(List<Photo> photos) {
        database = this.getWritableDatabase();
        database.beginTransaction();
        Log.i("addPhoto", "dataURLPhotos add: " + photos.get(0).getID());
        try {
            for (int i = 0; i < photos.size(); i++) {
                ContentValues contentValues1 = API_Add_Database(photos.get(i),false);
                // Insert a row of data into the table.
                database.insert(TABLE_PHOTO_HISTORY, null, contentValues1);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }


    public List<Photo> getAll_Photo_ID_History(String deviceID, int offSet) {

        Log.i(TAG, "DatabasePhotos.getAll_Photo... " + TABLE_PHOTO_HISTORY);

        List<Photo> photos_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PHOTO_HISTORY +" WHERE Device_ID = '"+deviceID+ "' ORDER BY " + COLUMN_CLIENT_CAPTURED_DATE_PHOTO + " DESC LIMIT "+ NumberLoad +" OFFSET "+offSet;
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_PHOTO)).equals(deviceID)) {

                    Photo photo = new Photo();
                    photo.setRowIndex(cursor.getInt(cursor.getColumnIndex(COLUMN_ROWINDEX_PHOTO)));
                    photo.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PHOTO)));
                    photo.setIsLoaded(cursor.getInt(cursor.getColumnIndex(COLUMN_ISLOADED_PHOTO)));
                    photo.setDevice_ID(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_ID_PHOTO)));
                    photo.setClient_Captured_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CAPTURED_DATE_PHOTO)));
                    photo.setCaption(cursor.getString(cursor.getColumnIndex(COLUMN_CAPTION_PHOTO)));
                    photo.setFile_Name(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME_PHOTO)));
                    photo.setExt(cursor.getString(cursor.getColumnIndex(COLUMN_EXT_PHOTO)));
                    photo.setMedia_URL(cursor.getString(cursor.getColumnIndex(COLUMN_MEDIA_URL_PHOTO)));
                    photo.setCreated_Date(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE_PHOTO)));
                    photo.setCDN_URL(cursor.getString(cursor.getColumnIndex(COLUMN_CDN_URL_PHOTO)));
                    // photo.setCheckDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CHECK)));
                    // Add in List.
                    photos_List.add(photo);
                }

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return photos_List;
    }

    public void update_Photos_History(int value, String nameDeviceID, int photoID) {
        database = this.getWritableDatabase();
        // contentValues1 receives the value from the method API_Add_Database()
        Log.d("isLoading = ", COLUMN_ISLOADED_PHOTO + "=" + value + "");
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COLUMN_ISLOADED_PHOTO, value);
        database.update(TABLE_PHOTO_HISTORY, contentValues1, COLUMN_DEVICE_ID_PHOTO + " = ?" + " AND " + COLUMN_ID_PHOTO + "=?",
                new String[]{String.valueOf(nameDeviceID), String.valueOf(photoID)});
        //  Close the database connection.
        database.close();
    }

    public int getPhotoCount(String deviceID) {
        Log.i(TAG, "DatabasePhotos.getPhotoCount ... " + TABLE_PHOTO_HISTORY);

        //String countQuery = "SELECT  * FROM " + TABLE_PHOTO_HISTORY;
        database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_PHOTO_HISTORY, new String[]{COLUMN_DEVICE_ID_PHOTO
                }, COLUMN_DEVICE_ID_PHOTO + "=?",
                new String[]{String.valueOf(deviceID)}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;

    }

    public void delete_Photos_History(Photo photo) {
        Log.i("deletePhoto", "DatabasePhotos.deletePhoto ... " + photo.getID() + "== " + photo.getCaption());
        database = this.getWritableDatabase();
        database.delete(TABLE_PHOTO_HISTORY, COLUMN_ID_PHOTO + " = ?",
                new String[]{String.valueOf(photo.getID())});
        database.close();
    }

    public void delete_Photos_History_File(int id) {
        Log.i("deletePhoto", "DatabasePhotos.deletePhoto ... " + id);
        database = this.getWritableDatabase();
        database.delete(TABLE_PHOTO_HISTORY, COLUMN_ID_PHOTO + " = ?",
                new String[]{String.valueOf(id)});
        database.close();
    }

    public List<Integer> getAll_Photo_ID_History_Date(String deviceID, String date) {

        Log.i(TAG, "DatabasePhotos.getAll_Photo... " + TABLE_PHOTO_HISTORY);

        List<Integer> photos_List = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PHOTO_HISTORY + " WHERE " + COLUMN_DEVICE_ID_PHOTO + " = '" + deviceID + "'";//+"' AND " +COLUMN_CLIENT_CAPTURED_DATE_PHOTO+" = '"+date+"'", String date
        //SQLiteDatabase database = this.getWritableDatabase();
        database = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(selectQuery, null);

        // Browse on the cursor, and add it to the list.
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_CAPTURED_DATE_PHOTO)).substring(0, 10).equals(date)) {
                    photos_List.add(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PHOTO)));
                }
                // Add in List.

            } while (cursor.moveToNext());
        }
        // return note list
        database.close();
        return photos_List;
    }


}
