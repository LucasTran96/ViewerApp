/*
  ClassName: PhotoHistory.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-11-16
  Description:
  History:2018-11-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterPhotoHistory;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Database.DatabasePhotos;
import com.jexpa.secondclone.Model.Photo;
import com.jexpa.secondclone.Model.PhotoJson;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.GetJsonFeature;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIMethod.getSharedPreferLong;
import static com.jexpa.secondclone.API.APIMethod.setToTalLog;
import static com.jexpa.secondclone.API.APIMethod.startAnim;
import static com.jexpa.secondclone.API.APIMethod.stopAnim;
import static com.jexpa.secondclone.API.APIMethod.updateViewCounterAll;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.File_PATH_SAVE_IMAGE;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.PHOTO_TOTAL;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_PHOTO;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class PhotoHistory extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter; // this is AdapterPhotoHistory
    List<Photo> mData = new ArrayList<>();
    int counter = 0; // counter used to count the number of images selected to be deleted from the device and server.
    private boolean checkPermissions = false; // checkPermissions is variable to check if the user has saved file permissions.
    public static boolean isInActionMode; // Check if you are in action mode.
    public static ArrayList<Photo> selectionList; // selectionList is the list of selected images for deletion.
    public static DatabasePhotos databasePhotos;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 26;
    private List<Photo> listPhoto = new ArrayList<>();
    private TextView txt_No_Data_Photo, txt_Total_Data;
    private LinearLayout lnl_Total;
    private SwipeRefreshLayout swp_PhotoHistory;
    private String fileName, CDN_URL, Media_URL, Device_ID;
    private long ID;
    boolean isLoading = false;
    private boolean checkLoadMore = false;
    private int currentSize = 0;
    private ProgressBar progressBar_Photo;
    boolean endLoading = false;
    //aviPhoto
    private AVLoadingIndicatorView aviPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_history);
        Slidr.attach(this);
        selectionList = new ArrayList<>();
        isInActionMode = false;
        databasePhotos = new DatabasePhotos(this);
        database_last_update = new DatabaseLastUpdate(this);
        toolbar = findViewById(R.id.toolbar_Photo);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.PHOTO_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (ActivityCompat.checkSelfPermission(PhotoHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoHistory.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
        } else {
            checkPermissions = true;
        }
        table = (Table) getIntent().getSerializableExtra("tablePhoto");
        //getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_No_Data_Photo = findViewById(R.id.txt_No_Data_Photo);
        txt_Total_Data = findViewById(R.id.txt_Total_Data);
        swp_PhotoHistory = findViewById(R.id.swp_PhotoHistory);
        aviPhoto = findViewById(R.id.aviPhoto);
        progressBar_Photo = findViewById(R.id.progressBar_Photo);
        progressBar_Photo.setVisibility(View.GONE);
        //txt_No_Data_Photo.setVisibility(View.GONE);
        mRecyclerView = findViewById(R.id.rcl_Photo_History);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getPhotoHistoryInfo();
        swipeRefreshLayout();

    }

    /**
     * saveImage() - Method save images to internal memory.
     * @param name name of image
     * @param CDN_URL url of image
     * @param deviceID Save the image to the deviceID panel.
     * @param photoID ID of image use update saved status image.
     */
    public void saveImage(final String name, String CDN_URL, String Media_URL, final int value,
                          final String deviceID, final long photoID) {

        Picasso.with(PhotoHistory.this).load(CDN_URL + Media_URL + "/thumb/l/" + name)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {
                                  File myDir = new File(File_PATH_SAVE_IMAGE);
                                  if (!myDir.exists()) {
                                      myDir.mkdirs();
                                  }

                                  myDir = new File(myDir, name);
                                  FileOutputStream out = new FileOutputStream(myDir);
                                  bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                  out.flush();
                                  out.close();
                                  databasePhotos.update_Photos_History(value, deviceID, photoID);

                                  //Toast.makeText(getApplicationContext(), getResources().getString(R.string.Image_Downloaded), Toast.LENGTH_SHORT).show();
                              } catch (Exception e) {
                                  MyApplication.getInstance().trackException(e);
                              }
                          }
                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                              Log.i("BitmapFailed", name);
//                              Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.Image_Download_Failed), name ) , Toast.LENGTH_SHORT).show();
                          }
                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                          }
                      }
                );
    }

    private void initScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // && !endLoading
                if (!isLoading && (!endLoading))
                {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mData.size() - 1) {
                        //bottom of list!
                        isLoading = true;
                        progressBar_Photo.setVisibility(View.VISIBLE);
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        try {
            checkLoadMore = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    currentSize =  mData.size();
                    if(isConnected(getApplicationContext()))
                    {
                        // Here is the total item value contact of device current has on CPanel
                        long totalContact = getSharedPreferLong(getApplicationContext(), PHOTO_TOTAL + table.getDevice_ID());
                        new getPhotoAsyncTask(currentSize+1).execute();
                        Log.d("dđsd", "mData.size() = "+ mData.size() + " ==== "+ totalContact);
                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }

                        isLoading = false;

                    }
                    else {
                        List<Photo> mDataCall = databasePhotos.getAll_Photo_ID_History(table.getDevice_ID(),currentSize,false);
                        // Here is the total item value contact of device current has on Cpanel
                        int insertIndex = mData.size();
                        mData.addAll(insertIndex,mDataCall);
                        mAdapter.notifyItemRangeInserted(insertIndex,mDataCall.size() );
                        if(mDataCall.size()< NumberLoad)
                        {
                            endLoading = true;
                        }

                        isLoading = false;
                        progressBar_Photo.setVisibility(View.GONE);
                    }
                }
            }, 100);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    /**
     * getPhotoHistoryInfo() - get image taking method when there is Connected internet or no Connected internet.
     */
    @SuppressLint("SetTextI18n")
    private void getPhotoHistoryInfo() {

        if (isConnected(this)) {
            aviPhoto.setVisibility(View.VISIBLE);
            startAnim(aviPhoto);
            new getPhotoAsyncTask(0).execute(); // If there is network, then take the image from the server.
        } else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            // If there is no network, then take the image from SQLite.
            int i = databasePhotos.getPhotoCount(table.getDevice_ID());
            if (i == 0) {
                //txt_No_Data_Photo.setVisibility(View.VISIBLE);
                txt_No_Data_Photo.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
                //getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = databasePhotos.getAll_Photo_ID_History(table.getDevice_ID(),0, true);
                mAdapter = new AdapterPhotoHistory(this, mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHOTO_TOTAL + table.getDevice_ID())+"");
                txt_No_Data_Photo.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHOTO, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                if(mData.size()>= NumberLoad)
                {
                    initScrollListener();
                }
            }
        }
    }

    /**
     * onLongClick()
     * When the user long press item will setLogo(null), isInActionMode = true, change menu to menu_action_mode.
     * Refresh AdapterPhotoHistory().
     */
//    @Override
//    public boolean onLongClick(View view) {
//
//        toolbar.getMenu().clear();
//        toolbar.setTitle(" \t" + "0" + " item selected");
//        isInActionMode = true;
//        toolbar.setLogo(null);
//        toolbar.inflateMenu(R.menu.menu_action_download);
//        mAdapter.notifyDataSetChanged();
//        if (getSupportActionBar() != null) {
//
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        return true;
//    }

    /**
     * getPhotoAsyncTask()
     * AsyncTask class handles get images from the server to the device.
     */
    @SuppressLint("StaticFieldLeak")
    private class getPhotoAsyncTask extends AsyncTask<String, Void, String> {

        long startIndex;

        public getPhotoAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected String doInBackground(String... strings) {

            /*min_Time = database_last_update.getLast_Time_Update(COLUMN_LAST_PHOTO, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            max_Date = getTimeNow().substring(0, 10) + " 23:59:59";
            Date_max = getTimeNow();
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Min_Date=\"" + min_Time + "\" Max_Date= \"" + max_Date + " \" Start=\"0\" Length=\"1000\" />";
            String function = "GetPhotos";*/

            return GetJsonFeature(table, this.startIndex,"GetPhotos");
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {

                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONObject jsonObjListImg = jsonObj.getJSONObject("ListImg");
                String jsonObjCDN_URL = jsonObj.getString("CDN_URL");
                JSONArray GPSJson = jsonObjListImg.getJSONArray("DataList");
                setToTalLog(jsonObjListImg, PHOTO_TOTAL  + table.getDevice_ID(), getApplicationContext());

                if (GPSJson.length() != 0)
                {

                    for (int i = 0; i < GPSJson.length(); i++)
                    {
                        Gson gson = new Gson();
                        PhotoJson photoJsonHistory = gson.fromJson(String.valueOf(GPSJson.get(i)), PhotoJson.class);
                        Photo photo = new Photo();
                        photo.setRowIndex(photoJsonHistory.getID());
                        photo.setID(photoJsonHistory.getID());
                        photo.setIsLoaded(0);
                        photo.setDevice_ID(photoJsonHistory.getDeviceId());
                        photo.setClient_Captured_Date(photoJsonHistory.getClientCapturedDate());
                        photo.setCaption(photoJsonHistory.getCaption());
                        photo.setFile_Name(photoJsonHistory.getFileName());
                        photo.setExt(photoJsonHistory.getExt());
                        photo.setMedia_URL(photoJsonHistory.getMediaURL());
                        photo.setCreated_Date(photoJsonHistory.getCreatedDate());
                        photo.setCDN_URL(jsonObjCDN_URL);
                        listPhoto.add(photo);
                        Log.d("ContactHistory"," Add Contact = "+  photo.getFile_Name());
                    }
                    if (listPhoto.size() != 0) {
                        databasePhotos.addDevice_Photos_Fast(listPhoto);
                    }
                }

                Log.d("PhotoHistory"," CurrentSize PhotoHistory = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<Photo> mDataTamp = databasePhotos.getAll_Photo_ID_History(table.getDevice_ID(),currentSize, false);

                if(checkLoadMore)
                {
                    int insertIndex = mData.size();
                    mData.addAll(insertIndex, mDataTamp);
                    Log.d("checkdata"," MData Call = "+ mDataTamp.size());
                    mAdapter.notifyItemRangeInserted(insertIndex,mDataTamp.size() );
                    progressBar_Photo.setVisibility(View.GONE);
                }
                else
                {
                    lnl_Total.setVisibility(View.VISIBLE);
                    mData.clear();
                    mData.addAll(mDataTamp);
                    if(mData.size() >= NumberLoad)
                    {
                        initScrollListener();
                    }
                    mAdapter =  new AdapterPhotoHistory(PhotoHistory.this, mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_PHOTO, getTimeNow(), table.getDevice_ID());
                if (mData.size() == 0) {
                    //txt_No_Data_Photo.setVisibility(View.VISIBLE);
                    txt_No_Data_Photo.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }
                else {
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHOTO_TOTAL  + table.getDevice_ID())+"");
                    txt_No_Data_Photo.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHOTO, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                }
                aviPhoto.setVisibility(View.GONE);
                stopAnim(aviPhoto);
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    // check Permission EXTERNAL_STORAGE_PERMISSION.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_CONSTANT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkPermissions = true;

                } else {
                    checkPermissions = false;
                    Toast.makeText(this, "You please accept the file read permission to save image!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return true;
    }

    public void prepareToolbar(int position) {
        // prepare action mode
        toolbar.getMenu().clear();
        //toolbar.inflateMenu(R.menu.menu_action_mode);
        toolbar.inflateMenu(R.menu.menu_action_download);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        }
        prepareSelection(position);
    }

    public void prepareSelection(int position) {
//        if (!selectionList.contains(mData.get(position))) {
//            selectionList.add(mData.get(position));
//            counter = counter + 1;
//            updateCounter(counter);
//
//        } else {
//            selectionList.remove(mData.get(position));
//            counter = counter - 1;
//            updateCounter(counter);
//        }
        if (!selectionList.contains(mData.get(position))) {
            selectionList.add(mData.get(position));
        } else {

            selectionList.remove(mData.get(position));
        }
        updateCounter();
    }

    public void updateCounter() {
        int counter = selectionList.size();
        updateViewCounterAll(toolbar, counter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_delete)
        {
            if (isConnected(PhotoHistory.this)) {
                if (selectionList.size() == 0) {
                    clearActionMode();
                } else {
                    getProgressDialog("Deleting....",this);
                    new clear_PhotoAsyncTask().execute();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
        } else if (item.getItemId() == android.R.id.home) {

            if(isInActionMode)
            {
                clearActionMode();
                mAdapter.notifyDataSetChanged();
            }
            else {
                super.onBackPressed();
            }
        } else if (item.getItemId() == R.id.item_edit) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_action_download);
            isInActionMode = true;
            mAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
            }
        }
        else if (item.getItemId() == R.id.item_Download_selected_images) {

            if (isConnected(PhotoHistory.this)) {
                if (checkPermissions) {

                    if (selectionList.size() != 0) {

                        for (Photo photo : selectionList)
                        {
                            fileName = photo.getFile_Name();
                            CDN_URL = photo.getCDN_URL();
                            Media_URL = photo.getMedia_URL();
                            Device_ID = photo.getDevice_ID();
                            ID = photo.getID();

                            if (photo.getIsLoaded() == 0) {

                                try {
                                    File myDir = new File(File_PATH_SAVE_IMAGE);
                                    if (!myDir.exists()) {
                                        myDir.mkdirs();
                                    }
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                                saveImage(fileName, CDN_URL, Media_URL, 1, Device_ID, ID);
                                }
                        }
                        Toast.makeText(this, getResources().getString(R.string.Image_Downloading), Toast.LENGTH_SHORT).show();

                    }
                    clearActionMode();
                }
            } else {
                //Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
        }
        return true;
    }

    /**
     * clear_PhotoAsyncTask()
     * delete image from sever and next clearDataSQLite(), clearFileImage(), clearActionMode()
     */
    @SuppressLint("StaticFieldLeak")
    private class clear_PhotoAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("photoHistory", table.getDevice_ID() + "");
            StringBuilder listID = new StringBuilder();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {
                    listID.append(selectionList.get(i).getID());
                }
            }
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" List_ID=\"" + listID + "\" List_URL=\"\"/>";
            String function = "ClearMultiPhoto";
            return APIURL.POST(value, function);
        }

        /**
         * onPostExecute()
         * if delete image from sever successfully and next clearDataSQLite() and clearFileImage(), clearActionMode()
         * @param s String json from server
         */
        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);
            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                ((AdapterPhotoHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearFileImage(selectionList);
                clearActionMode();
            } else {
                clearActionMode();
            }
            APIMethod.progressDialog.dismiss();
        }
    }

    /**
     * clearDataSQLite()
     * Delete the selected image list in SQLite.
     * @param selectionList List image selected for deletion in SQLite.
     */
    public void clearDataSQLite(ArrayList<com.jexpa.secondclone.Model.Photo> selectionList) {
        for (com.jexpa.secondclone.Model.Photo photo : selectionList) {

            databasePhotos.delete_Photos_History(photo);
        }
    }

    /**
     * clearFileImage()
     * Delete the selected image list in internal memory.
     * @param selectionList List image selected for deletion in internal memory.
     */
    public void clearFileImage(ArrayList<com.jexpa.secondclone.Model.Photo> selectionList) {
        for (com.jexpa.secondclone.Model.Photo photo : selectionList) {

            for (int i = 0; i < selectionList.size(); i++) {

                File file = new File(File_PATH_SAVE_IMAGE + "/" + photo.getFile_Name());
                file.delete(); // delete file selectionList.get(i)
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Log.d("fileName", file + "== ");
            }
        }
    }

    /**
     * clearActionMode()
     * Go back to the default toolbar, clear List selectionList and refresh AdapterPhotoHistory.
     */
    public void clearActionMode()
    {
        if(isInActionMode)
        {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_main);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.PHOTO_HISTORY));
            selectionList.clear();
            isInActionMode = false;
            counter = 0;
            //AdapterPhotoHistory.itemStateArrayPhoto = new SparseBooleanArray();
            mAdapter.notifyDataSetChanged(); // refresh AdapterPhotoHistory.
        }
    }

    /**
     * onBackPressed()
     * event handler press the back key: if isInActionMode = true then clearActionMode ()
     * else will exit activity.
     */
    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            clearActionMode();
            isInActionMode = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (APIMethod.progressDialog != null && APIMethod.progressDialog.isShowing()) {
            APIMethod.progressDialog.dismiss();
        }
    }

    /**
     * onResume()
     * Save the image to internal memory if the image has not been saved before.
     */

    public void swipeRefreshLayout() {
        swp_PhotoHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                if (isConnected(getApplicationContext())) {
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        listPhoto.clear();
                        clearActionMode();
                        checkLoadMore = false;
                        currentSize = 0;
                        new getPhotoAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_PhotoHistory.setRefreshing(false);
                                //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_PhotoHistory.setRefreshing(false);
                        //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(ManagementDevice.this, calendar.getTimeInMillis()- timeRefresh_Device +"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swp_PhotoHistory.setRefreshing(false);
                    noInternet(PhotoHistory.this);
                }
            }
        });
    }
}



