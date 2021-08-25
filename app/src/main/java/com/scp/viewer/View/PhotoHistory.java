/*
  ClassName: PhotoHistory.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-11-16
  Description:
  History:2018-11-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.API.APIURL;
import com.scp.viewer.Adapter.AdapterPhotoHistory;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Database.DatabasePhotos;
import com.scp.viewer.Model.DeviceStatus;
import com.scp.viewer.Model.Photo;
import com.scp.viewer.Model.PhotoJson;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIMethod.GetJsonCheckConnectionFeature;
import static com.scp.viewer.API.APIMethod.GetJsonFeature;
import static com.scp.viewer.API.APIMethod.GetJsonNowFeature;
import static com.scp.viewer.API.APIMethod.PostJsonClearDataToServer;
import static com.scp.viewer.API.APIMethod.alertDialogDeleteItems;
import static com.scp.viewer.API.APIMethod.getMilliFromDate;
import static com.scp.viewer.API.APIMethod.getSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.setToTalLogTable1;
import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.APIMethod.updateViewCounterAll;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.APIURL.noInternet;
import static com.scp.viewer.API.Global.File_PATH_SAVE_IMAGE;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.NEW_ROW;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.PHONE_CALL_RECORDING_PULL_ROW;
import static com.scp.viewer.API.Global.PHOTO_PULL_ROW;
import static com.scp.viewer.API.Global.PHOTO_TOTAL;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_PHOTO;
import static com.scp.viewer.API.Global.PRT_GET_PHOTO;
import static com.scp.viewer.API.Global.TYPE_CHECK_CONNECTION;
import static com.scp.viewer.API.Global.TYPE_TAKE_A_PICTURE;
import static com.scp.viewer.API.Global._TOTAL;
import static com.scp.viewer.API.Global.time_Refresh_Device;
import static com.scp.viewer.Adapter.AdapterPhotoHistory.positionLastSelected;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_PHOTO;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;
import static com.scp.viewer.View.HistoryLocation.countDownTimer;
import static com.scp.viewer.View.HistoryLocation.setProgressNow;

public class PhotoHistory extends AppCompatActivity {

    private Toolbar toolbar;
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter; // this is AdapterPhotoHistory
    public static List<Photo> mData = new ArrayList<>();
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
    private boolean checkRefresh = false;
    private int currentSize = 0;
    private ProgressBar progressBar_Photo;
    boolean endLoading = false;
    public static int countImageDownload = 0;
    public static int countImageDownloaded = 0;
    //aviPhoto
    private AVLoadingIndicatorView aviPhoto;
    private String minDateCheck;

    // Dialog
    AlertDialog.Builder mBuilder;
    AlertDialog dialog;
    ProgressBar PrB_Take_A_Photo;
    ImageView img_Result;
    LinearLayout ln_Show_Photo, ln_Progress_Take_A_Photo;
    TextView txt_Percent, txt_Seconds, txt_Result;

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
     * This is a feature load more for user view data in the type as page as on web each time only see 30 items after that when the last scod down, new load data after.
     */
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
                       // loadMore();
                        if(!checkRefresh)
                        {
                            loadMore();
                        }
                        else {
                            isLoading = false;
                            endLoading = false;
                            progressBar_Photo.setVisibility(View.GONE);
                            checkRefresh = false;
                        }
                    }
                }
            }
        });
    }

    /**
     * loadMore this is the parship support each page are display the 30 items
     * and after that when user load down the same same will going to load more 30 items to when the all.
     */
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
                        long totalContact = getSharedPreferLong(getApplicationContext(), PHOTO_TOTAL + table.getDevice_Identifier());
                        new getPhotoAsyncTask(currentSize+1).execute();

                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }

                        isLoading = false;

                    }
                    else {
                        List<Photo> mDataCall = databasePhotos.getAll_Photo_ID_History(table.getID(),currentSize,true,NumberLoad);
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
     *  This is a method to get data from the server to the device and display it in Recyclerview.
     *  If there is no internet, get data from SQLite stored on the device and display it in Recyclerview.
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
            int i = databasePhotos.getPhotoCount(table.getDevice_Identifier());
            if (i == 0) {
                //txt_No_Data_Photo.setVisibility(View.VISIBLE);
                txt_No_Data_Photo.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
                //getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = databasePhotos.getAll_Photo_ID_History(table.getID(),0, true,NumberLoad);
                mAdapter = new AdapterPhotoHistory(this, mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHOTO_TOTAL + table.getDevice_Identifier())+"");
                txt_No_Data_Photo.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHOTO, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
                if(mData.size()>= NumberLoad)
                {
                    initScrollListener();
                }
            }
        }
    }

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

            return GetJsonFeature(table, this.startIndex, PRT_GET_PHOTO);
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
                JSONArray GPSJson = jsonObjListImg.getJSONArray("Table");
                setToTalLogTable1(jsonObjListImg, PHOTO_TOTAL  + table.getDevice_Identifier(), getApplicationContext());
                setSharedPreferLong(getApplicationContext(), PHOTO_PULL_ROW +_TOTAL+ table.getDevice_Identifier() + NEW_ROW, 0);

                if (GPSJson.length() != 0)
                {

                    for (int i = 0; i < GPSJson.length(); i++)
                    {
                        Log.d("PhotoHistory"," GPSJson.get(i) = "+ GPSJson.get(i));
                        Gson gson = new Gson();
                        PhotoJson photoJsonHistory = gson.fromJson(String.valueOf(GPSJson.get(i)), PhotoJson.class);
                        Photo photo = new Photo();
                        photo.setRowIndex(photoJsonHistory.getID());
                        photo.setID(photoJsonHistory.getID());
                        photo.setIsLoaded(0);
                        photo.setDevice_ID(photoJsonHistory.getDevice_ID());
                        photo.setClient_Captured_Date(photoJsonHistory.getClientCapturedDate());
                        photo.setCaption(photoJsonHistory.getCaption());
                        photo.setFile_Name(photoJsonHistory.getFileName());
                        photo.setExt(photoJsonHistory.getExt());
                        photo.setMedia_URL(photoJsonHistory.getMediaURL());
                        photo.setCreated_Date(photoJsonHistory.getCreatedDate());
                        photo.setCDN_URL(jsonObjCDN_URL);
                        listPhoto.add(photo);
                        Log.d("PhotoHistory"," Add Photo = "+  photo.getDevice_ID() + " Add Name = "+  photo.getFile_Name());
                    }
                    if (listPhoto.size() != 0) {
                        databasePhotos.addDevice_Photos_Fast(listPhoto);
                    }
                }

                Log.d("PhotoHistory"," CurrentSize PhotoHistory = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<Photo> mDataTamp = databasePhotos.getAll_Photo_ID_History(table.getID(),currentSize, false,NumberLoad);

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

                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_PHOTO, getTimeNow(), table.getDevice_Identifier());
                if (mData.size() == 0) {
                    //txt_No_Data_Photo.setVisibility(View.VISIBLE);
                    txt_No_Data_Photo.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }
                else {
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHOTO_TOTAL  + table.getDevice_Identifier())+"");
                    txt_No_Data_Photo.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHOTO, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
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
        getMenuInflater().inflate(R.menu.menu_action_take_a_photo, menu);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return true;
    }

    public void prepareToolbar(int position) {
        try {
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
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    public void prepareSelection(int position) {

        try {
            if (!selectionList.contains(mData.get(position))) {
                selectionList.add(mData.get(position));
            } else {

                selectionList.remove(mData.get(position));
            }
            updateCounter();
        }
        catch (Exception e)
        {
            e.getMessage();
        }

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
                    clearActionMode(true);
                } else {

                    alertDialogDeleteItems(PhotoHistory.this,
                            getApplicationContext().getResources().getString(R.string.question_Select),
                            new clear_PhotoAsyncTask());
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                clearActionMode(true);
            }
        } else if (item.getItemId() == android.R.id.home) {

            if(isInActionMode)
            {
                clearActionMode(true);
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
        else if (item.getItemId() == R.id.item_Take_a_Photo_Font)
        {
            //1: Take_a_Photo_Font
            takeAPhoto(1);
        }
        else if (item.getItemId() == R.id.item_Take_a_Photo_Back)
        {
            // Take_a_Photo_Back
            takeAPhoto(2); //2: Take_a_Photo_Back
        }
        else if (item.getItemId() == R.id.item_Download_selected_images)
        {

            if (isConnected(PhotoHistory.this)) {
                if (checkPermissions)
                {
                    if(selectionList.size() <= 5)
                    {
                        if (selectionList.size() != 0)
                        {

                            for (Photo photo:selectionList) {
                                if(photo.getIsLoaded() == 0)
                                {
                                    countImageDownload++;
                                }
                            }

                            for (int i = 0; i < selectionList.size(); i++)
                            {
                                fileName = selectionList.get(i).getFile_Name();
                                CDN_URL = selectionList.get(i).getCDN_URL();
                                Media_URL = selectionList.get(i).getMedia_URL();
                                Device_ID = selectionList.get(i).getDevice_ID();
                                ID = selectionList.get(i).getID();

                                if (selectionList.get(i).getIsLoaded() == 0) {

                                    try {
                                        File myDir = new File(File_PATH_SAVE_IMAGE);
                                        if (!myDir.exists()) {
                                            myDir.mkdirs();
                                        }
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                    String URL_Image = CDN_URL + Media_URL+ "/thumb/l/" + fileName; //thumb/l/
                                    Log.d("URLD", URL_Image);
                                    new  DownloadPhotosTask(PhotoHistory.this,fileName, URL_Image, ID, Device_ID).execute();
                                    //saveImage(fileName, CDN_URL, Media_URL, 1, Device_ID, ID, selectionList.get(selectionList.size()-1).getID());
                                }
                                else {
                                    if(selectionList.get(i).getID() == selectionList.get(selectionList.size()-1).getID())
                                    {
                                        clearActionMode(true);
                                    }
                                    Toast.makeText(this, getResources().getString(R.string.Image_Downloading), Toast.LENGTH_SHORT).show();
                                }
                            }
                            //Toast.makeText(this, getResources().getString(R.string.Image_Downloading), Toast.LENGTH_SHORT).show();
                        }
                        Log.d("URLD", "clearActionMode()");
                        clearActionMode(false);
                    }
                    else {
                        Toast.makeText(this, getResources().getString(R.string.Maximum_Download), Toast.LENGTH_LONG).show();
                    }
                }
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                clearActionMode(true);
            }
        }
        return true;
    }

    /**
     * takeAPhoto this is the method to handle the remote photography event
     * step 1: pushNotification to target app, 1: capture with front camera; 2: taken with the back camera
     * step 2: wait 20s
     * step 3: after waiting for 30 seconds, get a new photo and display it on the custom Dialog for users to see directly.
     * @param camera_Use 1 or 2
     */
    private void takeAPhoto(int camera_Use)
    {
        //Take_a_Photo_Font
        minDateCheck = getTimeNow();
        // minDateCheck is the time to compare with last online to see if it's too big to show the device is online or offline.
        setDialog(PhotoHistory.this);
        // Dialog includes: Process name, Progress, Detailed target device information (Display can't get device information when Network Offline)
        // Display custom process Dialog at 0% Starting...
        // handle get gps now
        setProgressNow(20, txt_Percent, PhotoHistory.this);
        final String minDate = minDateCheck;
        // handle check connection
        new APIMethod.PushNotification(table.getID(), TYPE_TAKE_A_PICTURE, table.getDevice_Identifier(), camera_Use).execute();

        // Show Dialog custom process at 30% Push notification to the target app.
        setProgressNow(30, txt_Percent, PhotoHistory.this);
        countDownTimer(txt_Seconds, txt_Percent, PhotoHistory.this);
        setDePlay(minDate);
    }

    /**
     * setDialog this is the Dialog constructor for the take-a-photo method.
     */
    @SuppressLint("SetTextI18n")
    public void setDialog(final Activity mActivity)
    {
        mBuilder = new AlertDialog.Builder(mActivity);
        @SuppressLint("InflateParams") View mView = LayoutInflater.from(mActivity).inflate(R.layout.item_dialog_pushnotification_take_a_picture, null);

        // Progress Bar
        PrB_Take_A_Photo = mView.findViewById(R.id.PrB_Take_A_Photo);

        // TextView
        txt_Percent = mView.findViewById(R.id.txt_Percent);
        txt_Seconds = mView.findViewById(R.id.txt_Seconds);
        txt_Result = mView.findViewById(R.id.txt_Result);

        // ImageView
        img_Result = mView.findViewById(R.id.img_Result);
        // LinearLayout
        ln_Show_Photo = mView.findViewById(R.id.ln_Show_Photo);
        ln_Progress_Take_A_Photo = mView.findViewById(R.id.ln_Progress_Take_A_Photo);
        ln_Show_Photo.setVisibility(View.GONE);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        txt_Percent.setText(0 + getApplicationContext().getResources().getString(R.string.to_complete));

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // handle when exiting dialog
                //Toast.makeText(mActivity, "Close Dialog", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * setDePlay is the method to wait for how many seconds before performing the next steps.
     */
    public void setDePlay( final String minDate)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // The processor obtains information about the current state of the target device.
                new getPhotoNowAsyncTask(minDate).execute();
            }
        }, 20000);
    }

    /**
     * checkConnectAsyncTask this is the AsyncTask method that calls the server to get the latest status information of the target device.
     */
    @SuppressLint("StaticFieldLeak")
    private class getPhotoNowAsyncTask extends AsyncTask<String, Void, String>
    {

        String minDate;

        public getPhotoNowAsyncTask(String minDate) {
            this.minDate = minDate;
        }

        @Override
        protected String doInBackground(String... strings)
        {

            Log.d("locationId", table.getDevice_Identifier() + "");
            return GetJsonNowFeature(table, minDate,PRT_GET_PHOTO);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {

                setProgressNow(90, txt_Percent, PhotoHistory.this);

                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONObject jsonObjListImg = jsonObj.getJSONObject("ListImg");
                String jsonObjCDN_URL = jsonObj.getString("CDN_URL");
                JSONArray GPSJson = jsonObjListImg.getJSONArray("Table");
                ln_Show_Photo.setVisibility(View.VISIBLE);
                ln_Progress_Take_A_Photo.setVisibility(View.GONE);

                if (GPSJson.length() != 0)
                {
                    Log.d("PhotoHistory"," GPSJson.get(i) = "+ GPSJson.get(0));
                    Gson gson = new Gson();
                    PhotoJson photoJsonHistory = gson.fromJson(String.valueOf(GPSJson.get(0)), PhotoJson.class);
                    Photo photo = new Photo();
                    photo.setRowIndex(photoJsonHistory.getID());
                    photo.setID(photoJsonHistory.getID());
                    photo.setIsLoaded(0);
                    photo.setDevice_ID(photoJsonHistory.getDevice_ID());
                    photo.setClient_Captured_Date(photoJsonHistory.getClientCapturedDate());
                    photo.setCaption(photoJsonHistory.getCaption());
                    photo.setFile_Name(photoJsonHistory.getFileName());
                    photo.setExt(photoJsonHistory.getExt());
                    photo.setMedia_URL(photoJsonHistory.getMediaURL());
                    photo.setCreated_Date(photoJsonHistory.getCreatedDate());
                    photo.setCDN_URL(jsonObjCDN_URL);

                   // Log.d("PhotoHistory"," Add Photo = "+  photo.getDevice_ID() + " Add Name = "+  photo.getFile_Name());

                    if (photo.getMedia_URL() != null && photo.getFile_Name() != null && photo.getCDN_URL() != null)
                    {
                        img_Result.setVisibility(View.VISIBLE);
                        txt_Result.setVisibility(View.GONE);

                        String url = photo.getCDN_URL() + photo.getMedia_URL()  + "/" + photo.getFile_Name();//+ "/thumb/l"
                        //photo.getCDN_URL() + photo.getMedia_URL() + "/thumb/l" + "/" + photo.getFile_Name();

                        Glide.with(PhotoHistory.this)
                                .load(url) //Edit
                                .placeholder(R.drawable.spinner)
                                .error(R.drawable.no_image)
                                .into(img_Result);
                    }
                    else {
                        setProgressNow(100, txt_Percent, PhotoHistory.this);
                        img_Result.setVisibility(View.GONE);
                        txt_Result.setVisibility(View.VISIBLE);
                        txt_Result.setText(getResources().getString(R.string.device_offline_photo));
                    }
                }
                else {
                    setProgressNow(100, txt_Percent, PhotoHistory.this);
                    ln_Show_Photo.setVisibility(View.VISIBLE);
                    ln_Progress_Take_A_Photo.setVisibility(View.VISIBLE);
                    img_Result.setVisibility(View.GONE);
                    txt_Result.setVisibility(View.VISIBLE);
                    txt_Result.setText(getResources().getString(R.string.device_offline_photo));
                }



            } catch (JSONException e) {
                e.printStackTrace();
                // error when get Check-Connection data
                ln_Show_Photo.setVisibility(View.VISIBLE);
                ln_Progress_Take_A_Photo.setVisibility(View.GONE);
                txt_Result.setText(getResources().getString(R.string.device_offline_photo));
            }
        }
    }

    /**
     * clear_PhotoAsyncTask()
     * delete image from sever and next clearDataSQLite(), clearFileImage(), clearActionMode()
     */
    @SuppressLint("StaticFieldLeak")
    private class clear_PhotoAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("photoHistory", table.getDevice_Identifier() + "");
            StringBuilder listID = new StringBuilder();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {
                    listID.append(selectionList.get(i).getID());
                }
            }
           /* String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" List_ID=\"" + listID + "\" List_URL=\"\"/>";
            String function = POST_CLEAR_MULTI_PHOTO;*/
           return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_PHOTO);
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
                clearActionMode(true);
            } else {
                clearActionMode(true);
            }
            APIMethod.progressDialog.dismiss();
        }
    }

    /**
     * clearDataSQLite()
     * Delete the selected image list in SQLite.
     * @param selectionList List image selected for deletion in SQLite.
     */
    public void clearDataSQLite(ArrayList<com.scp.viewer.Model.Photo> selectionList) {
        for (com.scp.viewer.Model.Photo photo : selectionList) {

            databasePhotos.delete_Photos_History(photo);
        }
    }

    /**
     * clearFileImage()
     * Delete the selected image list in internal memory.
     * @param selectionList List image selected for deletion in internal memory.
     */
    public void clearFileImage(ArrayList<com.scp.viewer.Model.Photo> selectionList) {
        for (com.scp.viewer.Model.Photo photo : selectionList) {

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
    public void clearActionMode(boolean checkDataChanged)
    {
        if(isInActionMode)
        {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_action_take_a_photo);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.PHOTO_HISTORY));
            selectionList.clear();
            isInActionMode = false;
            counter = 0;
            if(checkDataChanged)
            {
                //AdapterPhotoHistory.itemStateArrayPhoto = new SparseBooleanArray();
                mAdapter.notifyDataSetChanged(); // refresh AdapterPhotoHistory.
            }
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
            clearActionMode(true);
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
                if (isConnected(getApplicationContext()))
                {
                    checkRefresh = true;
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        //listPhoto.clear();
                        if (!listPhoto.isEmpty())
                        {
                            listPhoto.clear(); //The list for update recycle view
                            mAdapter.notifyDataSetChanged();
                        }
                        clearActionMode(true);
                        checkLoadMore = false;
                        currentSize = 0;
                        new getPhotoAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_PhotoHistory.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_PhotoHistory.setRefreshing(false);
                    }
                } else {
                    swp_PhotoHistory.setRefreshing(false);
                    noInternet(PhotoHistory.this);
                }
            }
        });
    }


    /**
     * DownloadAudioLessonTask Asynchronous method supports downloading lessons from the server to the device's internal memory.
     */
    public static class DownloadPhotosTask extends AsyncTask<Void, Void, Void> {

        Activity  context;
        String fileName;
        //ImageView imageView_Downloaded;
        String deviceID;
        File apkStorage = null;
        File outputFile = null;
        String URL_Image;
        long ID;
        Random getRamDom  = new Random();
        int ranDomID = getRamDom.nextInt(1000);

        public DownloadPhotosTask(Activity context, String fileName, String URL_Image, long ID, String deviceID) {
            this.context = context;
            this.deviceID = deviceID;
            this.ID = ID;
            this.URL_Image = URL_Image;
            this.fileName = fileName;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void result) {
            try {

                // Display a notification bar to let users know that the lesson has been successfully downloaded.
                NotificationDownloadPictures.createNotification(context,0,0," Download completed.","Downloading: "+ fileName,ranDomID);

                if (outputFile != null) {
                    //tb_lesson.getInstance(context).updatePathDownloadLesson(lessonDownload.getId(),fileName);
                    databasePhotos.update_Photos_History(1, deviceID, ID);
                    //Toast.makeText(context, "Downloading image completed!", Toast.LENGTH_SHORT).show();
                } else
                {
                    // Downloading lesson failed!
                    Toast.makeText(context, "Downloading image failed!", Toast.LENGTH_SHORT).show();
                    Log.e("CheckDownload", result + "");

                }

                try {

                    Log.d("URLD",positionLastSelected.size()+"");
                    for (int i=0; i < positionLastSelected.size(); i++)
                    {
                        Log.d("URLD",  " positionLastSelected " +  mData.get(positionLastSelected.get(i)).getID() +"===== "+ positionLastSelected.get(i));
                        if(mData.get(positionLastSelected.get(i)).getID() == ID )
                        {
                            Log.d("URLD",  " positionLastSelected ===== "+ positionLastSelected.get(i));
                            int positionChaged = 0;
                            Log.d("URLD",  " ID === "+ ID);
                            Photo photo = mData.get(positionLastSelected.get(i));
                            photo.setIsLoaded(1);
                            Log.d("URLD",  " getIsLoaded ===== "+ photo.getIsLoaded());
                            mData.set(positionLastSelected.get(i), photo);
                            mAdapter.notifyDataSetChanged();
                            mAdapter.notifyItemChanged(positionLastSelected.get(i));
                        }

                    }

                }
                catch (Exception e)
                {
                    e.getMessage();
                }

                // When the lesson download succeeds or fails we will deduct a value
                // so that users can download other lessons because only 3 streams can be downloaded at the same time.
                // delay for a second so that the user will see the download bar have successfully downloaded
                // and then cancel the notification with this ID.

                Thread.sleep(1000);
                NotificationManager notificationManager =
                        (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.cancel(ranDomID);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Download the lesson error!", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                URL url = new URL(URL_Image);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                c.connect();//connect the URL Connection
                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Server returned", "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }
                long fileLength = c.getContentLength();
                //Get File if SD card is present
                if (isSDCardPresent()) {
                    apkStorage = new File(File_PATH_SAVE_IMAGE);
                } else {
                    Toast.makeText(context, "There is no SD Card!", Toast.LENGTH_SHORT).show();
                }
                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e("Directory Created.", "Directory Created.");
                }

                Log.d("fname",fileName);
                outputFile = new File(apkStorage, fileName);//Create Output file in Main File
                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e("File Created", "File Created");
                }
                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1;//init length
                long downloadedSize = 0;
                while ((len1 = is.read(buffer)) != -1) {


                    downloadedSize += len1;
                    // update the progressbar //
                    fos.write(buffer, 0, len1);//Write new file
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                    {
                        NotificationDownloadPictures.createNotification(context,100,(int) (downloadedSize * 100 / fileLength), (int) (downloadedSize * 100 / fileLength)+"%","Downloading: "+ fileName,ranDomID);
                        //publishProgress((int) (downloadedSize * 100 / fileLength));
                    }

                }
                //Close all connection after doing task
                fos.close();
                is.close();
            } catch (Exception e) {
                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e("failed", "Download Error Exception " + e.getMessage());
            }
            return null;
        }

    }

    /**
     * isSDCardPresent is a method that checks whether the user's current device has internal memory or not.
     */
    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}



