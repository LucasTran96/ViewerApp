/*
  ClassName: CallHistory.java
  @Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  see AppCompatActivity
  Created Date: 2018-06-05
  Description: Class CallHistory used to display the history of the phone users dialed,
  retrieving data from the sever on display on RecyclerView in the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.jexpa.secondclone.Adapter.AdapterCallHistory;
import com.jexpa.secondclone.Database.DatabaseCallHistory;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.Call;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.CALL_TOTAL;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CALL;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;


public class CallHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<Call> mData = new ArrayList<>();
    // action mode
    public static boolean isInActionMode;
    public static ArrayList<Call> selectionList;
    private DatabaseCallHistory database_call;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_Call, txt_Total_Data_Call;
    private LinearLayout lnl_Total;
    private SwipeRefreshLayout swp_CallHistory;
    boolean isLoading = false;
    private ProgressBar progressBar_Call;
    boolean endLoading = false;
    private boolean checkLoadMore = false;
    private int currentSize = 0;
    // This is the value to store the temporary variable when you choose to select all item or remove all selected items.
    boolean selectAll = false;
    private List<Call> listCall = new ArrayList<>();
    private AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);
        Slidr.attach(this);
        selectionList = new ArrayList<>();
        isInActionMode = false;
        toolbar = findViewById(R.id.toolbar_Call);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.CALL_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        database_call = new DatabaseCallHistory(this);
        database_last_update = new DatabaseLastUpdate(this);
        table = (Table) getIntent().getSerializableExtra("call");
        setID();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getCallHistoryInfo();
        // adapter
        mAdapter = new AdapterCallHistory(this, (ArrayList<Call>) mData);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout();
    }

    private void setID()
    {
        txt_No_Data_Call = findViewById(R.id.txt_No_Data_Call);
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_Total_Data_Call = findViewById(R.id.txt_Total_Data_Call);
        avLoadingIndicatorView = findViewById(R.id.aviCall);
        swp_CallHistory = findViewById(R.id.swp_CallHistory);
        progressBar_Call = findViewById(R.id.progressBar_Call);
        progressBar_Call.setVisibility(View.GONE);
        mRecyclerView = findViewById(R.id.rcl_Call_History);
        mRecyclerView.setHasFixedSize(true);
    }

    @SuppressLint({"ObsoleteSdkInt", "SetTextI18n"})
    private void getCallHistoryInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new getCallAsyncTask(0).execute();
        } else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_call.getCallCount(table.getDevice_ID());
            if (i == 0) {
                //txt_No_Data_Call.setVisibility(View.VISIBLE);
                txt_No_Data_Call.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data_Call.setText("0");

            } else {
                mData.clear();
                mData = database_call.getAll_Call_ID_History(table.getDevice_ID(),0);
                mAdapter = new AdapterCallHistory(this, (ArrayList<Call>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if(mData.size()>= NumberLoad)
                {
                    initScrollListener();
                }
                txt_Total_Data_Call.setText(getSharedPreferLong(getApplicationContext(), CALL_TOTAL + table.getDevice_ID())+"");
                txt_No_Data_Call.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
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
                        progressBar_Call.setVisibility(View.VISIBLE);
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
                        long totalContact = getSharedPreferLong(getApplicationContext(), CALL_TOTAL + table.getDevice_ID());
                        new getCallAsyncTask(currentSize+1).execute();

                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;

                    }
                    else {
                        List<Call> mDataCall = database_call.getAll_Call_ID_History(table.getDevice_ID(),currentSize);
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
                        progressBar_Call.setVisibility(View.GONE);
                    }
                }
            }, 100);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class getCallAsyncTask extends AsyncTask<String, Void, String>
    {
        long startIndex;

        public getCallAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("callId", table.getDevice_ID() + "");
            return GetJsonFeature(table, this.startIndex,"GetCalls");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                if(bodyLogin.getData() != null)
                {
                    JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                    JSONArray GPSJson = jsonObj.getJSONArray("Table");
                    JSONArray GPSJsonTable1 = jsonObj.getJSONArray("Table1");
                    setToTalLog(GPSJsonTable1, CALL_TOTAL  + table.getDevice_ID(), getApplicationContext());

                    if (GPSJson.length() != 0)
                    {

                        for (int i = 0; i < GPSJson.length(); i++) {

                            Gson gson = new Gson();
                            Call callHistory = gson.fromJson(String.valueOf(GPSJson.get(i)), Call.class);
                            listCall.add(callHistory);
                            Log.d("ContactHistory"," Add Contact = "+  callHistory.getContact_Name());

                        }
                        if (listCall.size() != 0) {
                            database_call.addDevice_Call_Fast(listCall);
                        }
                    }
                    //mData.clear();
                    Log.d("CallHistory"," currentSize CallHistory = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                    List<Call> mDataTamp = database_call.getAll_Call_ID_History(table.getDevice_ID(),currentSize);
                    //mData.addAll(mDataTamp);

                    if(checkLoadMore)
                    {
                        int insertIndex = mData.size();
                       // mData.addAll(insertIndex, mDataTamp);
                        mData.addAll(insertIndex, mDataTamp);
                        Log.d("checkdata"," MData Call = "+ mDataTamp.size());
                        mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                        Log.d("CallHistory"," checkLoadMore Contact = "+ true);
                        progressBar_Call.setVisibility(View.GONE);
                    }
                    else {
                        Log.d("CallHistory"," checkLoadMore Contact = "+ false);
                        mData.clear();
                        mData.addAll(mDataTamp);
                        if(mData.size() >= NumberLoad)
                        {
                            initScrollListener();
                        }
                        mAdapter = new AdapterCallHistory(CallHistory.this, (ArrayList<Call>) mData);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }

                    String date_Max = getTimeNow();
                    database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_CALL, date_Max, table.getDevice_ID());
                    lnl_Total.setVisibility(View.VISIBLE);
                    if (mData.size() == 0) {
                        //txt_No_Data_Call.setVisibility(View.VISIBLE);
                        txt_No_Data_Call.setText(MyApplication.getResourcses().getString(R.string.NoData));
                        txt_Total_Data_Call.setText("0");
                    }
                    else {
                        txt_No_Data_Call.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                        txt_Total_Data_Call.setText(getSharedPreferLong(getApplicationContext(), CALL_TOTAL  + table.getDevice_ID())+"");
                    }

                    stopAnim(avLoadingIndicatorView);
                }

            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
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
        //toolbar.inflateMenu(R.menu.menu_action_mode);
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
        Log.d("selectionList", selectionList.size()+"");
        if (!selectionList.contains(mData.get(position))) {

            selectionList.add(mData.get(position));
        } else {

            selectionList.remove(mData.get(position));
        }
        updateViewCounter();
    }

    private void updateViewCounter() {
        int counter = selectionList.size();
        updateViewCounterAll(toolbar, counter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_delete) {
            if (isConnected(CallHistory.this)) {
                // ((AdapterHistoryLocation) mAdapter).removeData(selectionList);
                // getProgressDialogDelete();
                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
                new ClearMultiCall().execute();

            } else {
                Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
                clearActionMode();
                mAdapter.notifyDataSetChanged();
            }


        } else if(item.getItemId() ==  R.id.item_select_all)
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
//            clearActionMode();
//            mAdapter.notifyDataSetChanged();
            if(isInActionMode)
            {
                clearActionMode();
                mAdapter.notifyDataSetChanged();
            }
            else {
                super.onBackPressed();
            }
        }

        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class ClearMultiCall extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("locationId", table.getDevice_ID() + "");
            StringBuilder listID = new StringBuilder();
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList.get(i).getID());
                }
            }
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" List_ID=\"" + listID + "\" />";
            String function = "ClearMultiCall";
            return APIURL.POST(value, function);


        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);


            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                ((AdapterCallHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                Toast.makeText(CallHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }

            // get Method getThread()
            //getThread(progressDialog);
            APIMethod.progressDialog.dismiss();
        }
    }

    /**
     * Delete the list of selected columns of the callHistory table in SQLite
     * @param selectionList A variable of type ArrayList.
     */
    public void clearDataSQLite(ArrayList<Call> selectionList) {
        for (Call gps : selectionList) {
            database_call.delete_Call_History(gps);
        }
    }

    // back toolbar home, clear List selectionList
    /**
     * Delete the list of selected columns of the callHistory table in SQLite
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
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.CALL_HISTORY));
            selectionList.clear();
            isInActionMode = false;
        }
    }

    // Check out the escape without the option will always exit,
    // the opposite will cancel the selection, not exit.
    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            clearActionMode();
            isInActionMode = false;
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

    public void swipeRefreshLayout() {
        swp_CallHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;

                if (isConnected(getApplicationContext()))
                {
                    checkLoadMore = false;
                    currentSize = 0;
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        listCall.clear();
                        clearActionMode();
                        new getCallAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_CallHistory.setRefreshing(false);
                                //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_CallHistory.setRefreshing(false);
                        //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(ManagementDevice.this, calendar.getTimeInMillis()- timeRefresh_Device +"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swp_CallHistory.setRefreshing(false);
                    noInternet(CallHistory.this);
                }
            }
        });
    }
}
