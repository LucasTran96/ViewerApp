/*
  ClassName: URLHistory.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class URLHistory used to display the URL list of the phone being called from the sever to the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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
import com.jexpa.secondclone.Adapter.AdapterURLHistory;
import com.jexpa.secondclone.Database.DatabaseURL;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.Model.URL;
import com.jexpa.secondclone.R;
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
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.URL_TOTAL;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_URL;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class URLHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<URL> mData = new ArrayList<>();
    List<URL> urlListAdd = new ArrayList<>();
    // action mode
    public static boolean isInActionMode = false;
    public static ArrayList<URL> selectionList = new ArrayList<>();
    private DatabaseURL database_url;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_URL;
    private SwipeRefreshLayout swp_URL;
    private String Date_max;
    boolean isLoading = false;
    private ProgressBar progressBar_URL;
    boolean endLoading = false;
    private boolean checkLoadMore = false;
    private int currentSize = 0;
    boolean selectAll = false;
    //aviURL
    private AVLoadingIndicatorView aviURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_history);
        toolbar = findViewById(R.id.toolbar_URL);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.URL_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.d("hhhh", "\t\t\t\theight = "+ height);
        Log.d("hhhh", "\t\t\t\twidth = "+ width);
        database_url = new DatabaseURL(this);
        database_last_update = new DatabaseLastUpdate(this);
        //logger =  Log4jHelper.getLogger("URLHistory.class");
        table = (Table) getIntent().getSerializableExtra("tableURL");
        // show dialog Loading...
        //getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        txt_No_Data_URL = findViewById(R.id.txt_No_Data_URL);
        aviURL = findViewById(R.id.aviURL);
        progressBar_URL = findViewById(R.id.progressBar_URL);
        progressBar_URL.setVisibility(View.GONE);
        swp_URL = findViewById(R.id.swp_URL);
        //txt_No_Data_URL.setVisibility(View.GONE);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_URL_History);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getBrowserInfo();
        // adapter
        mAdapter = new AdapterURLHistory(this, (ArrayList<URL>) mData);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout();
    }

    @SuppressLint({"ObsoleteSdkInt", "SetTextI18n"})
    private void getBrowserInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            //new getURLAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            aviURL.setVisibility(View.VISIBLE);
            startAnim(aviURL);
            new getURLAsyncTask(0).execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_url.get_URLCount_DeviceID(table.getDevice_ID());
            if (i == 0) {
                //txt_No_Data_URL.setVisibility(View.VISIBLE);
                txt_No_Data_URL.setText(MyApplication.getResourcses().getString(R.string.NoData));
                //getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = database_url.getAll_URL_ID_History(table.getDevice_ID(),0);
                mAdapter = new AdapterURLHistory(this, (ArrayList<URL>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_No_Data_URL.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_URL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
            }
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class getURLAsyncTask extends AsyncTask<String, Void, String>
    {
        long startIndex;

        public getURLAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }
        @Override
        protected String doInBackground(String... strings) {
            return GetJsonFeature(table, this.startIndex,"GetURL");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                //Log.i("URL",s);
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray GPSJson = jsonObj.getJSONArray("DataList");
                setToTalLog(jsonObj, URL_TOTAL, getApplicationContext());

                if (GPSJson.length() != 0)
                {
                    for (int i = 0; i < GPSJson.length(); i++) {

                        Gson gson = new Gson();
                        URL url = gson.fromJson(String.valueOf(GPSJson.get(i)), URL.class);
                        urlListAdd.add(url);
                        Log.d("URLHistory"," Add URL = "+  url.getURL_Link());

                    }
                    if (urlListAdd.size() != 0) {
                        database_url.addDevice_URL(urlListAdd);
                    }
                }
                //mData.clear();
                Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<URL> mDataTamp = database_url.getAll_URL_ID_History(table.getDevice_ID(),currentSize);
                //mData.addAll(mDataTamp);

                if(checkLoadMore)
                {
                    int insertIndex = mData.size();
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
                    mAdapter = new AdapterURLHistory(URLHistory.this, (ArrayList<URL>) mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_URL, getTimeNow(), table.getDevice_ID());
                txt_No_Data_URL.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_URL, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                // get Method getThread()
                //progressDialog.dismiss();
                if (mData.size() == 0) {
                    //txt_No_Data_URL.setVisibility(View.VISIBLE);
                    txt_No_Data_URL.setText(MyApplication.getResourcses().getString(R.string.NoData));
                }
                stopAnim(aviURL);
                aviURL.setVisibility(View.GONE);
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
        toolbar.inflateMenu(R.menu.menu_action_delete);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        }
        prepareSelection(position);
    }

    // Lightning events have already been selected, delete is not available then added.
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
        updateViewCounterAll(toolbar, counter);
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
                        progressBar_URL.setVisibility(View.VISIBLE);
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
                        long totalContact = getSharedPreferLong(getApplicationContext(), URL_TOTAL);
                        new getURLAsyncTask(currentSize+1).execute();
                        Log.d("dđsd", "mData.size() = "+ mData.size() + " ==== "+ totalContact);
                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;
                        progressBar_URL.setVisibility(View.GONE);
                    }
                    else {
                        List<URL> mDataCall = database_url.getAll_URL_ID_History(table.getDevice_ID(),currentSize);
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
                        progressBar_URL.setVisibility(View.GONE);
                    }
                }
            }, 1000);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.item_delete) {
            if (isConnected(URLHistory.this)) {
                // ((AdapterHistoryLocation) mAdapter).removeData(selectionList);
                //getProgressDialogDelete();
                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
                new clear_Location().execute();
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

    // method to delete data on the server.
    @SuppressLint("StaticFieldLeak")
    private class clear_Location extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("URL_Id", table.getDevice_ID() + "");
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
            String function = "ClearMultiURL";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                ((AdapterURLHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                Toast.makeText(URLHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
            }
            // get Method getThread()
            //getThread(progressDialog);
            APIMethod.progressDialog.dismiss();
            clearActionMode();
        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<URL> selectionList) {
        for (URL url : selectionList) {
            database_url.delete_Contact_History(url);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {

        if(isInActionMode)
        {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_main);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.URL_HISTORY));
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
        swp_URL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                currentSize = 0;
                checkLoadMore = false;
                endLoading = false;
                if (isConnected(getApplicationContext()))
                {
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        urlListAdd.clear();
                        clearActionMode();

                        new getURLAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_URL.setRefreshing(false);
                                //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_URL.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "The data has been updated.", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(ManagementDevice.this, calendar.getTimeInMillis()- timeRefresh_Device +"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swp_URL.setRefreshing(false);
                    noInternet(getApplicationContext());
                }
            }
        });
    }
}
