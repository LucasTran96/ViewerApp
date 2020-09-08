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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.jexpa.secondclone.Adapter.AdapterPhoneCallRecordHistory;
import com.jexpa.secondclone.Database.DatabaseAmbientRecord;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Database.DatabasePhoneCallRecord;
import com.jexpa.secondclone.Model.AmbientRecord;
import com.jexpa.secondclone.Model.AudioGroup;
import com.jexpa.secondclone.Model.PhoneCallRecordJson;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.GetJsonFeature;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIMethod.getSharedPreferLong;
import static com.jexpa.secondclone.API.APIMethod.setSharedPreferLong;
import static com.jexpa.secondclone.API.APIMethod.setToTalLog;
import static com.jexpa.secondclone.API.APIMethod.startAnim;
import static com.jexpa.secondclone.API.APIMethod.stopAnim;
import static com.jexpa.secondclone.API.APIMethod.updateViewCounterAll;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.AMBIENT_RECORDING_TOTAL;
import static com.jexpa.secondclone.API.Global.File_PATH_SAVE_PHONE_CALL_RECORD;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.PHONE_CALL_RECORDING_TOTAL;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_PHONE_CALL_RECORDING;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

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
    private int currentSize = 0;
    boolean selectAll = false;
    private AVLoadingIndicatorView avLoadingIndicatorView;

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
        setID();
        table = (Table) getIntent().getSerializableExtra("tablePhoneCallRecord");
        functionName =  getIntent().getStringExtra("nameFeature");
        Log.d("AmbientMediaLink", functionName);
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
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.PHONE_CALL_RECORDING));
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


    @SuppressLint("SetTextI18n")
    private void getPhoneCallHistoryInfo() {

        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new getPhoneCallRecordAsyncTask(0).execute();
        } else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            int i = databasePhoneCallRecord.getPhoneCallRecordCount(table.getDevice_ID());
            if (i == 0) {
                txt_No_Data_PhoneCallRecord.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
            } else {
                mData.clear();
                mData = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
                mAdapter = new AdapterPhoneCallRecordHistory(this, mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if(mData.size() >= NumberLoad)
                {
                    initScrollListener();
                }
                if(functionName.equals("GetPhoneRecording"))
                {
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHONE_CALL_RECORDING_TOTAL)+"");
                }else
                {
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), AMBIENT_RECORDING_TOTAL)+"");
                }

                txt_No_Data_PhoneCallRecord.setText("Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
            }
        }
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
                        progressBar_PhoneCall.setVisibility(View.VISIBLE);
                        loadMore();
                    }
                }
            }
        });
    }

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

                        Log.d("ddsd", "mData.size() = "+ mData.size() + " ==== "+ totalPhoneCall);
                        if((mData.size()+1) >= totalPhoneCall)
                        {
                            endLoading = true;
                        }
                        isLoading = false;
                        progressBar_PhoneCall.setVisibility(View.GONE);
                    }
                    else {
                        List<AudioGroup> mDataStamp = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),currentSize);
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
            }, 2000);

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
                     setToTalLog(GPSJsonTable1, PHONE_CALL_RECORDING_TOTAL, getApplicationContext());

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
                     mDataTamp = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),currentSize);
                     txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), PHONE_CALL_RECORDING_TOTAL)+"");
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
                     String jsonObjAmbientMediaLink = jsonObj.getString("AmbientMediaLink");
                     JSONArray GPSJson = jsonObj.getJSONArray("Rows");

                     try {
                         String totalRows = jsonObj.getString("TotalRows");
                         if(totalRows != null)
                         {
                             setSharedPreferLong(getApplicationContext(),AMBIENT_RECORDING_TOTAL,Long.parseLong(totalRows));
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
                             ambientRecord.setDeviceID(table.getDevice_ID());
                             Log.d("phoneCallRecordJson"," Add Contact = "+  ambientRecord.getFileName());
                             ambientRecordList.add(ambientRecord);
                         }
                         if (ambientRecordList.size() != 0) {
                             databaseAmbientRecord.addDevice_AmbientRecord_Fast(ambientRecordList);
                         }
                     }

                     Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                     mDataTamp = databaseAmbientRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),currentSize);
                     txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), AMBIENT_RECORDING_TOTAL)+"");
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
                    Log.d("checkdata"," MData Call = "+ mDataTamp.size());
                    mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                    Log.d("CallHistory"," checkLoadMore Contact = "+ true);
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
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_PHONE_CALL_RECORDING, Date_max, table.getDevice_ID());

                if (mData.size() == 0) {

                    txt_No_Data_PhoneCallRecord.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    txt_No_Data_PhoneCallRecord.setText("Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                }
                stopAnim(avLoadingIndicatorView);

            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_CONSTANT: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete),this);
                new clear_PhoneCallRecordAsyncTask().execute();

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

            Log.d("PhoneCallRecordHistory", table.getDevice_ID() + "");
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
                value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" List_ID=\"" + listID + "\" />";
                function = "ClearMultiPhoneRecording";//ClearMultiAmbient da70c0862faf50cf.mp4
                Log.d("tsdds","value = "+value+ " function = "+ function);
            }
            else {
                value = "<RequestParams List_File=\"" + listID + "\" />";
                function = "ClearMultiAmbient";//ClearMultiAmbient da70c0862faf50cf.mp4
                Log.d("tsdds","value = "+value+ " function = "+ function);
            }
            return APIURL.POST(value, function);
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
            mdatabase = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
        }
        else
        {
            mdatabase = databaseAmbientRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
        }
        return mdatabase;
    }

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
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        phoneCallRecordList.clear();
                        clearActionMode();
                        new getPhoneCallRecordAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_PhoneCallRecord.setRefreshing(false);
                                //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_PhoneCallRecord.setRefreshing(false);
                        //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(ManagementDevice.this, calendar.getTimeInMillis()- timeRefresh_Device +"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swp_PhoneCallRecord.setRefreshing(false);
                    noInternet(PhoneCallRecordHistory.this);
                }
            }
        });
    }
}



