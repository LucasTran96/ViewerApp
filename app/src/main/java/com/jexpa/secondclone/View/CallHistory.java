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
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterCallHistory;
import com.jexpa.secondclone.Database.DatabaseCallHistory;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CALL;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;


public class CallHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<com.jexpa.secondclone.Model.CallHistory> mData = new ArrayList<>();
    // action mode
    public static boolean isInActionMode = false;
    public static ArrayList<com.jexpa.secondclone.Model.CallHistory> selectionList = new ArrayList<>();
    private DatabaseCallHistory database_call;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_Call;
    private SwipeRefreshLayout swp_CallHistory;
    private String min_Time = "", date_Max = "";
    boolean isLoading = false;
    private ProgressBar progressBar_Call;
    boolean endLoading = false;
    private List<com.jexpa.secondclone.Model.CallHistory> listCall = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);
        toolbar = findViewById(R.id.toolbar_Call);
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.CALL_HISTORY));
        toolbar.setLogo(R.drawable.call_store);
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        database_call = new DatabaseCallHistory(this);
        database_last_update = new DatabaseLastUpdate(this);
        table = (Table) getIntent().getSerializableExtra("call");
        // show dialog Loading...
        getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        txt_No_Data_Call = findViewById(R.id.txt_No_Data_Call);
        swp_CallHistory = findViewById(R.id.swp_CallHistory);
        progressBar_Call = findViewById(R.id.progressBar_Call);
        progressBar_Call.setVisibility(View.GONE);
        //txt_No_Data_Call.setVisibility(View.GONE);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_Call_History);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getCallHistoryInfo();
        // adapter
        mAdapter = new AdapterCallHistory(this, (ArrayList<com.jexpa.secondclone.Model.CallHistory>) mData);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout();
        if(mData.size()>= NumberLoad)
        {
            initScrollListener();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void getCallHistoryInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            //new getCallAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"getCallAsyncTask");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new getCallAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                new getCallAsyncTask().execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_call.getCallCount(table.getDevice_ID());
            if (i == 0) {
                //txt_No_Data_Call.setVisibility(View.VISIBLE);
                txt_No_Data_Call.setText(MyApplication.getResourcses().getString(R.string.NoData)+"  "+"Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = database_call.getAll_Call_ID_History(table.getDevice_ID(),0);
                mAdapter = new AdapterCallHistory(this, (ArrayList<com.jexpa.secondclone.Model.CallHistory>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_No_Data_Call.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
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
                        progressBar_Call.setVisibility(View.VISIBLE);
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
                    List<com.jexpa.secondclone.Model.CallHistory> mDataStamp = database_call.getAll_Call_ID_History(table.getDevice_ID(),currentSize);

                    mData.addAll(mDataStamp);
                    if(mDataStamp.size()< NumberLoad)
                    {
                        endLoading = true;
                    }
                    Toast.makeText(getApplicationContext(), mData.size()+" = size", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    //progressBar_Locations.setVisibility(View.GONE);
                    isLoading = false;
                    progressBar_Call.setVisibility(View.GONE);
                }
            }, 2000);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class getCallAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("callId", table.getDevice_ID() + "");
            // max_Date is get all the location from the min_date to the max_Date days
            min_Time = database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            String max_Date = getTimeNow().substring(0, 10) + " 23:59:59";
            date_Max = getTimeNow();
            Log.d("min_time", min_Time + "");
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Start=\"0\" Length=\"1000\" Min_Date=\"" + min_Time + "\" Max_Date=\"" + max_Date + "\" />";
            String function = "GetCalls";
            return APIURL.POST(value, function);
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
                    if (GPSJson.length() != 0) {
                        //
                        List<Integer> listDateCheck = database_call.getAll_Call_ID_History_Date(table.getDevice_ID(), min_Time.substring(0, 10));//min_Time.substring(0,10)

                        int save;
                        Log.d("DateCheck", "CallHistory = " + listDateCheck.size());
                        for (int i = 0; i < GPSJson.length(); i++) {
                            Gson gson = new Gson();
                            com.jexpa.secondclone.Model.CallHistory callHistory = gson.fromJson(String.valueOf(GPSJson.get(i)), com.jexpa.secondclone.Model.CallHistory.class);
                            mAdapter.notifyDataSetChanged();
                            Log.d("Call", callHistory.getRowIndex() + "");
                            //database_call.addDevice_Call(gps);
                            save = 0;
                            if (listDateCheck.size() != 0) {
                                for (Integer listCheck : listDateCheck) {
                                    if (callHistory.getID() == listCheck) {
                                        save = 1;
                                        break;
                                    }
                                }
                                if (save == 0) {
                                    listCall.add(callHistory);
                                }
                            } else {
                                listCall.add(callHistory);
                            }
                        }
                        if (listCall.size() != 0) {
                            database_call.addDevice_Call_Fast(listCall);
                        }

                    }
                    mData.clear();
                    mData = database_call.getAll_Call_ID_History(table.getDevice_ID(),0);
                    mAdapter = new AdapterCallHistory(CallHistory.this, (ArrayList<com.jexpa.secondclone.Model.CallHistory>) mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_CALL, date_Max, table.getDevice_ID());
                    String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID());
                    Log.d("min_time1", min_Time1 + "");
                    txt_No_Data_Call.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                    if (mData.size() == 0) {
                        //txt_No_Data_Call.setVisibility(View.VISIBLE);
                        txt_No_Data_Call.setText(MyApplication.getResourcses().getString(R.string.NoData)+"  "+"Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                    }
                    // get Method getThread()
                    getThread(APIMethod.progressDialog);
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
        updateViewCounter();
    }

    private void updateViewCounter() {
        int counter = selectionList.size();
        if (counter == 0) {
            clearActionMode();
            //toolbar.getMenu().getItem(0).setVisible(true);
        } else {
            //toolbar.getMenu().getItem(0).setVisible(false);
            toolbar.setTitle("  " + counter + " item selected");
            toolbar.setLogo(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_delete) {
            isInActionMode = false;
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


        } else if (item.getItemId() == android.R.id.home) {
            clearActionMode();
            mAdapter.notifyDataSetChanged();
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
    public void clearDataSQLite(ArrayList<com.jexpa.secondclone.Model.CallHistory> selectionList) {
        for (com.jexpa.secondclone.Model.CallHistory gps : selectionList) {
            database_call.delete_Call_History(gps);
        }
    }

    // back toolbar home, clear List selectionList
    /**
     * Delete the list of selected columns of the callHistory table in SQLite
     */
    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.CALL_HISTORY));
        toolbar.setLogo(R.drawable.call_store);
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

    public void swipeRefreshLayout() {
        swp_CallHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                if (isConnected(getApplicationContext()))
                {
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        listCall.clear();
                        clearActionMode();
                        new getCallAsyncTask().execute();
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
