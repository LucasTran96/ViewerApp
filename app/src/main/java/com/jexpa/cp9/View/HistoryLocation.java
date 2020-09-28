/*
  ClassName: HistoryLocation.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class HistoryLocation used to display the history list of phone calls from the sever on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.cp9.View;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.jexpa.cp9.API.APIDatabase;
import com.jexpa.cp9.API.APIMethod;
import com.jexpa.cp9.API.APIURL;
import com.jexpa.cp9.Adapter.AdapterHistoryLocation;
import com.jexpa.cp9.Database.DatabaseGetLocation;
import com.jexpa.cp9.Database.DatabaseLastUpdate;
import com.jexpa.cp9.Model.GPS;
import com.jexpa.cp9.Model.Table;
import com.jexpa.cp9.R;
import com.google.gson.Gson;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.jexpa.cp9.API.APIMethod.GetJsonFeature;
import static com.jexpa.cp9.API.APIMethod.alertDialogDeleteItems;
import static com.jexpa.cp9.API.APIMethod.getProgressDialog;
import static com.jexpa.cp9.API.APIMethod.getSharedPreferLong;
import static com.jexpa.cp9.API.APIMethod.setToTalLog;
import static com.jexpa.cp9.API.APIMethod.startAnim;
import static com.jexpa.cp9.API.APIMethod.stopAnim;
import static com.jexpa.cp9.API.APIMethod.updateViewCounterAll;
import static com.jexpa.cp9.API.APIURL.getTimeNow;
import static com.jexpa.cp9.API.APIURL.isConnected;
import static com.jexpa.cp9.API.APIURL.noInternet;
import static com.jexpa.cp9.API.Global.GPS_TOTAL;
import static com.jexpa.cp9.API.Global.LIMIT_REFRESH;
import static com.jexpa.cp9.API.Global.NumberLoad;
import static com.jexpa.cp9.API.Global.time_Refresh_Device;
import static com.jexpa.cp9.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_LOCATION;
import static com.jexpa.cp9.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class HistoryLocation extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    public  static List<GPS> mData = new ArrayList<>();
    // action mode
    public static boolean isInActionMode;
    public static ArrayList<GPS> selectionList;
    private DatabaseGetLocation databaseGetLocation;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_Location, txt_Total_Data;
    private LinearLayout lnl_Total;
    private ProgressBar progressBar_Locations;
    private SwipeRefreshLayout swp_History_Location;
    List<GPS> gpsListAdd = new ArrayList<>();
    boolean endLoading = false;
    boolean isLoading = false;
    private boolean checkLoadMore = false;
    private int currentSize = 0;
    boolean selectAll = false;
    //aviLocation
    private AVLoadingIndicatorView aviLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_location);
        setID();
        selectionList = new ArrayList<>();
        isInActionMode = false;
        Slidr.attach(this);
        databaseGetLocation = new DatabaseGetLocation(this);
        database_last_update = new DatabaseLastUpdate(this);
        //logger =  Log4jHelper.getLogger("History_Location.class");
        table = (Table) getIntent().getSerializableExtra("table");
        getLocationInfo();
        swipeRefreshLayout();

    }

    private void setID()
    {
        toolbar = findViewById(R.id.toolbar_Location);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.LOCATION_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_No_Data_Location = findViewById(R.id.txt_No_Data_Location);
        txt_Total_Data = findViewById(R.id.txt_Total_Data);
        aviLocation = findViewById(R.id.aviLocation);
        swp_History_Location = findViewById(R.id.swp_History_Location);
        progressBar_Locations = findViewById(R.id.progressBar_Locations);
        progressBar_Locations.setVisibility(View.GONE);
        //txt_No_Data_Location.setVisibility(View.GONE);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_GPS_Locations);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @SuppressLint("SetTextI18n")
    private void getLocationInfo() {
        //if there is a network call method
        //logger.debug("internet = "+isConnected(this)+"\n==================End!");
        if (APIURL.isConnected(this)) {
            aviLocation.setVisibility(View.VISIBLE);
            startAnim(aviLocation);
            new LocationAsyncTask(0).execute();
        } else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = databaseGetLocation.getLocationCount(table.getDevice_ID());
            if (i == 0) {
                //txt_No_Data_Location.setVisibility(View.VISIBLE);
                txt_No_Data_Location.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
            } else {
                mData.clear();
                mData = databaseGetLocation.getAll_LocationID(table.getDevice_ID(),0);
                mAdapter = new AdapterHistoryLocation(this, (ArrayList<GPS>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if(mData.size()>= NumberLoad)
                {
                    initScrollListener();
                }
                txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), GPS_TOTAL + table.getDevice_ID())+"");
                txt_No_Data_Location.setText("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
            }
        }
    }

    // location get method from sever8
    private class LocationAsyncTask extends AsyncTask<String, Void, String> {

        long startIndex;

        public LocationAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected String doInBackground(String... strings)
        {

            Log.d("locationId", table.getDevice_ID() + "");
            return GetJsonFeature(table, this.startIndex,"GetLocations");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                APIURL.deviceObject(s);
                JSONObject jsonObj = new JSONObject(APIURL.bodyLogin.getData());
                JSONArray jsonArray = jsonObj.getJSONArray("GPS");
                JSONArray GPSJsonPaging = jsonObj.getJSONArray("Paging");
                setToTalLog(GPSJsonPaging, GPS_TOTAL + table.getDevice_ID(), getApplicationContext());

                if (jsonArray.length() != 0)
                {

                    for (int i = 0; i < jsonArray.length(); i++) {

                        Gson gson = new Gson();
                        GPS gps = gson.fromJson(String.valueOf(jsonArray.get(i)), GPS.class);
                        gpsListAdd.add(gps);
                    }
                    if (gpsListAdd.size() != 0) {
                        databaseGetLocation.addDevice_GPS(gpsListAdd);
                    }
                }

                Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<GPS> mDataTamp = databaseGetLocation.getAll_LocationID(table.getDevice_ID(),currentSize);

                if(checkLoadMore)
                {
                    int insertIndex = mData.size();
                    mData.addAll(insertIndex, mDataTamp);
                    mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                    Log.d("CallHistory"," checkLoadMore Contact = "+ true);
                    progressBar_Locations.setVisibility(View.GONE);
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
                    mAdapter = new AdapterHistoryLocation(HistoryLocation.this, (ArrayList<GPS>) mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_LOCATION, getTimeNow(), table.getDevice_ID());
                String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID());
                if (mData.size() == 0)
                {
                    txt_No_Data_Location.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), GPS_TOTAL + table.getDevice_ID())+"");
                    txt_No_Data_Location.setText("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                }
                stopAnim(aviLocation);
                aviLocation.setVisibility(View.GONE);
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
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

                if (!isLoading && (!endLoading)) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mData.size() - 1) {
                        //bottom of list!
                        isLoading = true;
                        progressBar_Locations.setVisibility(View.VISIBLE);
                        loadMore();
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
                        long totalContact = getSharedPreferLong(getApplicationContext(), GPS_TOTAL + table.getDevice_ID());
                        new LocationAsyncTask(currentSize+1).execute();

                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        isLoading = false;

                    }
                    else {
                        List<GPS> mDataCall = databaseGetLocation.getAll_LocationID(table.getDevice_ID(),currentSize);
                        // Here is the total item value contact of device current has on Cpanel
                        int insertIndex = mData.size();
                        mData.addAll(insertIndex,mDataCall);
                        mAdapter.notifyItemRangeInserted(insertIndex-1,mDataCall.size() );
                        if(mDataCall.size()< NumberLoad)
                        {
                            endLoading = true;
                        }
                        isLoading = false;
                        progressBar_Locations.setVisibility(View.GONE);
                    }
                }
            }, 100);

        }catch (Exception e)
        {
            e.getMessage();
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

    // Lightning events have already been selected, delete is not available then added
    public void prepareSelection(int position) {

        if (!selectionList.contains(mData.get(position)))
        {
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
            if (APIURL.isConnected(HistoryLocation.this)) {
                //                ((AdapterHistoryLocation) mAdapter).removeData(selectionList);
                //getProgressDialogDelete();
//                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
//                new clear_Location().execute();

                alertDialogDeleteItems(HistoryLocation.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new clear_Location());
            } else {
                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
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
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class clear_Location extends AsyncTask<String, Void, String> {
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
            String function = "ClearMultiGPS";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {
            APIURL.deviceObject(s);
            Log.d("ClearLocation", APIURL.bodyLogin.getDescription() + "==" + "" + APIURL.bodyLogin.getResultId() + "" + APIURL.bodyLogin.getIsSuccess());
            if (APIURL.bodyLogin.getIsSuccess().equals("1") && APIURL.bodyLogin.getResultId().equals("1")) {
                ((AdapterHistoryLocation) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();

            } else {

                Toast.makeText(HistoryLocation.this, APIURL.bodyLogin.getDescription() + "", Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
            // get Method getThread()
            //getThread(progressDialog);
            APIMethod.progressDialog.dismiss();
        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<GPS> selectionList) {
        for (GPS gps : selectionList) {
            databaseGetLocation.deleteLocation(gps);
        }
    }

    // back toolbar home, clear List selectionList
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
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.LOCATION_HISTORY));
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
        swp_History_Location.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;

                if (isConnected(getApplicationContext()))
                {
                    checkLoadMore = false;
                    currentSize = 0;
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        gpsListAdd.clear();
                        clearActionMode();
                        new LocationAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_History_Location.setRefreshing(false);
                                //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_History_Location.setRefreshing(false);
                        //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(ManagementDevice.this, calendar.getTimeInMillis()- timeRefresh_Device +"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swp_History_Location.setRefreshing(false);
                    noInternet(HistoryLocation.this);
                }
            }
        });
    }
}
