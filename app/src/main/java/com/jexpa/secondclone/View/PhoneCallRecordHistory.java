/*
  ClassName: PhotoHistory.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-11-16
  Description:
  History:2018-11-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterPhoneCallRecordHistory;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Database.DatabasePhoneCallRecord;
import com.jexpa.secondclone.Model.PhoneCallRecord;
import com.jexpa.secondclone.Model.PhoneCallRecordJson;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.File_PATH_SAVE_PHONE_CALL_RECORD;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_PHONE_CALL_RECORDING;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class PhoneCallRecordHistory extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<PhoneCallRecord> mData = new ArrayList<>();
    public static boolean isInActionMode = false;
    public static ArrayList<PhoneCallRecord> selectionList = new ArrayList<>();
    public static DatabasePhoneCallRecord databasePhoneCallRecord;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 26;
    private TextView txt_No_Data_PhoneCallRecord;
    private SwipeRefreshLayout swp_PhoneCallRecord;
    private  List<PhoneCallRecord> phoneCallRecordList = new ArrayList<>();
    private String min_Time = "",Date_max;
    public static boolean request;
    boolean isLoading = false;
    private ProgressBar progressBar_PhoneCall;
    boolean endLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_call_record);
        databasePhoneCallRecord = new DatabasePhoneCallRecord(this);
        database_last_update = new DatabaseLastUpdate(this);
        toolbar = findViewById(R.id.toolbar_PhoneCallRecord);
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.PHONE_CALL_RECORDING));
        toolbar.setLogo(R.drawable.call_record_store);
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.rcl_PhoneCallRecord_History);
        table = (Table) getIntent().getSerializableExtra("tablePhoneCallRecord");
        txt_No_Data_PhoneCallRecord = findViewById(R.id.txt_No_Data_PhoneCallRecord);
        progressBar_PhoneCall = findViewById(R.id.progressBar_PhoneCall);
        progressBar_PhoneCall.setVisibility(View.GONE);
        swp_PhoneCallRecord = findViewById(R.id.swp_PhoneCallRecord);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (ContextCompat.checkSelfPermission(PhoneCallRecordHistory.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhoneCallRecordHistory.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
        } else {

            request = true;
        }
        getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        getPhotoHistoryInfo();
        mData = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
        mAdapter = new AdapterPhoneCallRecordHistory(PhoneCallRecordHistory.this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout();
        if(mData.size() >= NumberLoad)
        {
            initScrollListener();
        }
    }

    private void getPhotoHistoryInfo() {

        if (isConnected(this)) {
            new getPhoneCallRecordAsyncTask().execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            int i = databasePhoneCallRecord.getPhoneCallRecordCount(table.getDevice_ID());
            if (i == 0) {

                txt_No_Data_PhoneCallRecord.setText(MyApplication.getResourcses().getString(R.string.NoData)+"  "+"Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
                mAdapter = new AdapterPhoneCallRecordHistory(this, mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_No_Data_PhoneCallRecord.setText("Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                getThread(APIMethod.progressDialog);
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
            mData.add(null);
            mAdapter.notifyItemInserted(mData.size() - 1);
            //progressBar_Locations.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mData.remove(mData.size() - 1);
                    int scrollPosition = mData.size();
                    mAdapter.notifyItemRemoved(scrollPosition);
                    int currentSize = scrollPosition;
                    List<PhoneCallRecord> mDataStamp = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),currentSize);

                    mData.addAll(mDataStamp);
                    if(mDataStamp.size()< NumberLoad)
                    {
                        endLoading = true;
                    }
                    Toast.makeText(getApplicationContext(), mData.size()+" = size", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    //progressBar_Locations.setVisibility(View.GONE);
                    isLoading = false;
                    progressBar_PhoneCall.setVisibility(View.GONE);
                }
            }, 2000);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getPhoneCallRecordAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            min_Time = database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            String max_Date = getTimeNow().substring(0, 10) + " 23:59:59";
            Date_max = getTimeNow();
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Min_Date=\"" + min_Time + "\" Max_Date= \"" + max_Date + " \" Start=\"0\" Length=\"1000\" />";
            String function = "GetPhoneRecording";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONObject jsonObjData = jsonObj.getJSONObject("Data");
                String jsonObjCDN_URL = jsonObj.getString("CDN_URL");
                JSONArray GPSJson = jsonObjData.getJSONArray("Table");
                if (GPSJson.length() != 0) {

                    List<Integer> listDateCheck = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History_Date(table.getDevice_ID(), min_Time.substring(0, 10));
                    Log.d("DateCheck", "PhoneCallRecordHistory = " + listDateCheck.size());
                    int save;
                    for (int i = 0; i < GPSJson.length(); i++) {

                        Gson gson = new Gson();
                        PhoneCallRecordJson phoneCallRecordJson = gson.fromJson(String.valueOf(GPSJson.get(i)), PhoneCallRecordJson.class);
                        PhoneCallRecord phoneCallRecord = new PhoneCallRecord();
                        phoneCallRecord.setRowIndex(phoneCallRecordJson.getRowIndex());
                        phoneCallRecord.setID(phoneCallRecordJson.getID());
                        phoneCallRecord.setIsSaved(0);
                        phoneCallRecord.setDevice_ID(phoneCallRecordJson.getDevice_ID());
                        phoneCallRecord.setClient_Recorded_Date(phoneCallRecordJson.getClient_Recorded_Date());
                        phoneCallRecord.setAudio_Name(phoneCallRecordJson.getAudio_Name());
                        phoneCallRecord.setContent_Type(phoneCallRecordJson.getContent_Type());
                        phoneCallRecord.setDuration(phoneCallRecordJson.getDuration());
                        phoneCallRecord.setDirection(phoneCallRecordJson.getDirection());
                        phoneCallRecord.setPhone_Number(phoneCallRecordJson.getPhone_Number());
                        phoneCallRecord.setContact_Name(phoneCallRecordJson.getContact_Name());
                        phoneCallRecord.setAudio_Size(phoneCallRecordJson.getAudio_Size());
                        phoneCallRecord.setExt(phoneCallRecordJson.getExt());
                        phoneCallRecord.setMedia_URL(phoneCallRecordJson.getMedia_URL());
                        phoneCallRecord.setCreated_Date(phoneCallRecordJson.getCreated_Date());
                        phoneCallRecord.setCDN_URL(jsonObjCDN_URL);
                        save = 0;
                        if (listDateCheck.size() != 0) {
                            for (Integer listCheck : listDateCheck) {
                                if (phoneCallRecord.getID() == listCheck) {

                                    save = 1;
                                    break;
                                }
                            }
                            if (save == 0) {

                                phoneCallRecordList.add(phoneCallRecord);
                            }
                        } else {

                            phoneCallRecordList.add(phoneCallRecord);
                        }
                    }
                    if (phoneCallRecordList.size() != 0) {
                        databasePhoneCallRecord.addDevice_PhoneCallRecord_Fast(phoneCallRecordList);
                    }
                }
                mData.clear();
                mData = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
                mAdapter = new AdapterPhoneCallRecordHistory(PhoneCallRecordHistory.this, mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_PHONE_CALL_RECORDING, Date_max, table.getDevice_ID());

                if (mData.size() == 0) {

                    txt_No_Data_PhoneCallRecord.setText(MyApplication.getResourcses().getString(R.string.NoData)+"  "+"Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                }else {
                    txt_No_Data_PhoneCallRecord.setText("Last update: "+ getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_PHONE_CALL_RECORDING, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                }
                getThread(APIMethod.progressDialog);
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
                    Toast.makeText(this, "You please accept the file read permission to save image!", Toast.LENGTH_LONG).show();
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
        toolbar.inflateMenu(R.menu.menu_action_mode);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (counter == 0) {
            clearActionMode();
        } else {
            toolbar.setTitle(" \t" + counter + " item selected");
            toolbar.setLogo(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_delete) {
            isInActionMode = false;
            if (isConnected(PhoneCallRecordHistory.this)) {

                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete),this);
                new clear_PhoneCallRecordAsyncTask().execute();

            } else {

                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
        } else if (item.getItemId() == android.R.id.home) {

            clearActionMode();
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

                    listID.append(selectionList.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList.get(i).getID());
                }
            }
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" List_ID=\"" + listID + "\" />";
            String function = "ClearMultiPhoneRecording";
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

    public void clearDataSQLite(ArrayList<PhoneCallRecord> selectionList) {
        for (PhoneCallRecord phoneCallRecord : selectionList) {

            databasePhoneCallRecord.delete_PhoneCallRecord_History(phoneCallRecord);
        }
    }

    public void clearFileAudio(ArrayList<PhoneCallRecord> selectionList) {
        for (PhoneCallRecord phoneCallRecord : selectionList) {

            for (int i = 0; i < selectionList.size(); i++) {

                String fileName = phoneCallRecord.getMedia_URL().substring(phoneCallRecord.getMedia_URL().lastIndexOf("/", phoneCallRecord.getMedia_URL().length()) + 1, phoneCallRecord.getMedia_URL().length()) + "." + phoneCallRecord.getExt();
                File file = new File(File_PATH_SAVE_PHONE_CALL_RECORD + "/" + fileName);
                file.delete();
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Log.d("fileNames", file + " == ");
            }
        }
    }

    public void clearActionMode() {

        isInActionMode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.PHONE_CALL_RECORDING));
        toolbar.setLogo(R.drawable.call_record_store);
        selectionList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (isInActionMode) {

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
        mData = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
        mAdapter = new AdapterPhoneCallRecordHistory(PhoneCallRecordHistory.this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {

        mData.clear();
        mData = databasePhoneCallRecord.getAll_PhoneCallRecord_ID_History(table.getDevice_ID(),0);
        mAdapter = new AdapterPhoneCallRecordHistory(PhoneCallRecordHistory.this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    public void swipeRefreshLayout() {
        swp_PhoneCallRecord.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                if (isConnected(getApplicationContext()))
                {
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        phoneCallRecordList.clear();
                        clearActionMode();
                        new getPhoneCallRecordAsyncTask().execute();
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



