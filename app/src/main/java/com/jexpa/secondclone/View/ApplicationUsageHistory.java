/*
  ClassName: ApplicationUsageHistory.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class ApplicationUsageHistory used to display the application that the user has downloaded the phone,
  call from the sever on display on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.jexpa.secondclone.API.APIDatabase;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterApplicationHistory;
import com.jexpa.secondclone.Database.DatabaseApplicationUsage;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.ApplicationUsage;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIMethod.GetJsonFeature;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIMethod.getSharedPreferLong;
import static com.jexpa.secondclone.API.APIMethod.setToTalLog;
import static com.jexpa.secondclone.API.APIMethod.startAnim;
import static com.jexpa.secondclone.API.APIMethod.stopAnim;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.APP_USAGE_TOTAL;
import static com.jexpa.secondclone.API.Global.CALL_TOTAL;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_APPLICATION;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class ApplicationUsageHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<ApplicationUsage> mData = new ArrayList<>();
    List<ApplicationUsage> usageListAdd = new ArrayList<>();
    // action mode
    public static boolean isInActionMode = false;
    public static ArrayList<ApplicationUsage> selectionList = new ArrayList<>();
    private DatabaseApplicationUsage database_application_usage;
    private SwipeRefreshLayout swp_AppUsageHistory;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private String max_Date = "";
    private TextView txt_No_Data_App;
    private ProgressBar progressBar_AppUsage;
    private boolean checkLoadMore = false;
    boolean isLoading = false;
    private int currentSize = 0;
    boolean endLoading = false;
    // This is the value to store the temporary variable when you choose to select all item or remove all selected items.
    boolean selectAll = false;
    private AVLoadingIndicatorView avLoadingIndicatorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_usage_history);

        toolbar = findViewById(R.id.toolbar_Application_History);
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.APPLICATION_USAGE));
        toolbar.setLogo(R.drawable.app_icon_big);
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        database_application_usage = new DatabaseApplicationUsage(this);
        database_last_update = new DatabaseLastUpdate(this);
        table = (Table) getIntent().getSerializableExtra("tableApplication");
        // show dialog Loading...
        txt_No_Data_App = findViewById(R.id.txt_No_Data_App);
        progressBar_AppUsage = findViewById(R.id.progressBar_AppUsage);
        progressBar_AppUsage.setVisibility(View.GONE);
        swp_AppUsageHistory = findViewById(R.id.swp_AppUsageHistory);
        avLoadingIndicatorView = findViewById(R.id.aviAppUsage);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl__Application_History);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getAppUsageInfo();
        swipeRefreshLayout();
    }

    private void getAppUsageInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new getAppAsyncTask(0).execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_application_usage.get_ApplicationCount_DeviceID(table.getDevice_ID());
            if (i == 0) {
                txt_No_Data_App.setVisibility(View.VISIBLE);
                txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.NoData));
            } else {
                mData.clear();
                mData = database_application_usage.getAll_Application_ID_History(table.getDevice_ID(),0);
                mAdapter = new AdapterApplicationHistory(this, (ArrayList<ApplicationUsage>) mData);
                if(mData.size() >= NumberLoad)
                {
                    initScrollListener();
                }
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.TurnOn));
            }
        }
    }

    public void swipeRefreshLayout() {
        swp_AppUsageHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                checkLoadMore = false;
                currentSize = 0;
                if (isConnected(getApplicationContext()))
                {
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        mData.clear();
                        clearActionMode();
                        new getAppAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                swp_AppUsageHistory.setRefreshing(false);
                                //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();
                            }
                        }, 1000);
                    } else {
                        swp_AppUsageHistory.setRefreshing(false);
                    }
                } else {
                    swp_AppUsageHistory.setRefreshing(false);
                    noInternet(ApplicationUsageHistory.this);
                }
            }
        });
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
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mData.size() - 1)
                    {
                        //bottom of list!
                        isLoading = true;
                        progressBar_AppUsage.setVisibility(View.VISIBLE);
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
                public void run()
                {
                    currentSize =  mData.size();
                    if(isConnected(getApplicationContext()))
                    {
                        // Here is the total item value contact of device current has on CPanel
                        long totalContact = getSharedPreferLong(getApplicationContext(), APP_USAGE_TOTAL);
                        new getAppAsyncTask(currentSize+1).execute();
                        Log.d("dđsd", "mData.size() = "+ mData.size() + " ==== "+ totalContact);
                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;
                        progressBar_AppUsage.setVisibility(View.GONE);
                    }
                    else {
                        List<ApplicationUsage> mDataCall = database_application_usage.getAll_Application_ID_History(table.getDevice_ID(),currentSize);
                        // Here is the total item value contact of device current has on Cpanel
                        int insertIndex = mData.size();
                        mData.addAll(insertIndex,mDataCall);
                        mAdapter.notifyItemRangeInserted(insertIndex-1,mDataCall.size() );
                        if(mDataCall.size()< NumberLoad)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;
                        progressBar_AppUsage.setVisibility(View.GONE);
                    }
                }
            }, 1000);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }


    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class getAppAsyncTask extends AsyncTask<String, Void, String>
    {
        long startIndex;

        public getAppAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }
        @Override
        protected String doInBackground(String... strings) {

            Log.d("Application_Id", table.getDevice_ID() + "");

            return GetJsonFeature(table, this.startIndex,"GetApps");
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray GPSJson = jsonObj.getJSONArray("Table");
                JSONArray GPSJsonTable1 = jsonObj.getJSONArray("Table1");
                setToTalLog(GPSJsonTable1, APP_USAGE_TOTAL, getApplicationContext());

                if (GPSJson.length() != 0) {

                    for (int i = 0; i < GPSJson.length(); i++) {

                        Gson gson = new Gson();
                        ApplicationUsage application_usage = gson.fromJson(String.valueOf(GPSJson.get(i)), ApplicationUsage.class);
                        usageListAdd.add(application_usage);
                        Log.d("ContactHistory"," Add Contact = "+  application_usage.getApp_Name());

                    }
                    if (usageListAdd.size() != 0) {
                        database_application_usage.addDevice_Application(usageListAdd);
                    }
                }
                //mData.clear();
                Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<ApplicationUsage> mDataTamp = database_application_usage.getAll_Application_ID_History(table.getDevice_ID(),currentSize);
                //mData.addAll(mDataTamp);

                if(checkLoadMore)
                {
                    int insertIndex = mData.size();
                    // mData.addAll(insertIndex, mDataTamp);
                    mData.addAll(insertIndex, mDataTamp);
                    Log.d("checkdata"," MData Call = "+ mDataTamp.size());
                    mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                }
                else {

                    mData.clear();
                    mData.addAll(mDataTamp);
                    if(mData.size() >= NumberLoad)
                    {
                        initScrollListener();
                    }
                    mAdapter = new AdapterApplicationHistory(ApplicationUsageHistory.this, (ArrayList<ApplicationUsage>) mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                if (mData.size() == 0) {
                    txt_No_Data_App.setVisibility(View.VISIBLE);
                    txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.NoData));
                }else {
                    String max_Date = getTimeNow();
                    database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_APPLICATION, max_Date, table.getDevice_ID());
                    String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_APPLICATION, TABLE_LAST_UPDATE, table.getDevice_ID());
                    Log.d("min_time1", min_Time1 + "");
                    txt_No_Data_App.setText(("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_APPLICATION, TABLE_LAST_UPDATE, table.getDevice_ID()),null)));
                }
                stopAnim(avLoadingIndicatorView);
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
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
        toolbar.inflateMenu(R.menu.menu_action_delete);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        prepareSelection(position);
    }

    // Lightning current event selection has been deleted, there is no added, then
    public void prepareSelection(int position) {

        if (!selectionList.contains(mData.get(position))) {
            selectionList.add(mData.get(position));
        } else {
            selectionList.remove(mData.get(position));
        }

        updateViewCounter();
    }

    private void updateViewCounter() {
        int counter = selectionList.size();
        if (counter == 0) {
            toolbar.setTitle("  " + counter + " item selected");
        } else {
            toolbar.setTitle("  " + counter + " item selected");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_delete) {
            isInActionMode = false;
            if (isConnected(ApplicationUsageHistory.this)) {
                //  ((AdapterHistoryLocation) mAdapter).removeData(selectionList);
                //getProgressDialogDelete();
                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
                new clear_App().execute();
            } else {
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
                clearActionMode();
                mAdapter.notifyDataSetChanged();
            }
        }
        else if(item.getItemId() ==  R.id.item_select_all)
        {
            if(!selectAll)
            {
                selectAll = true;
                selectionList.clear();
                selectionList.addAll(mData);
                updateViewCounter();
                mAdapter.notifyDataSetChanged();

            }
            else {
                selectAll = false;
                selectionList.clear();
                updateViewCounter();
                mAdapter.notifyDataSetChanged();
            }

        }
        else if (item.getItemId() == android.R.id.home) {
            clearActionMode();
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    // Method clear data to sever
    @SuppressLint("StaticFieldLeak")
    private class clear_App extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("App_Id", table.getDevice_ID() + "");
            StringBuilder listID = new StringBuilder();
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList.get(i).getID());
                }
            }
            Log.i("listID", listID.toString());
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" List_ID=\"" + listID + "\" />";
            String function = "ClearMultiApp";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getResultId().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                ((AdapterApplicationHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                clearActionMode();
                Toast.makeText(ApplicationUsageHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
            }
            // get Method getThread()
            APIMethod.progressDialog.dismiss();
        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<ApplicationUsage> selectionList) {
        for (ApplicationUsage application_usage : selectionList) {
            database_application_usage.delete_Application_History(application_usage);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.APPLICATION_USAGE));
        selectionList.clear();
    }

    // Check out the escape without the option will always exit,
    // the opposite will cancel the selection, not exit.
    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            clearActionMode();
            mAdapter.notifyDataSetChanged();
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
}
