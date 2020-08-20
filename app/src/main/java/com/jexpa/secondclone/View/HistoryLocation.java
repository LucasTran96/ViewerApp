/*
  ClassName: HistoryLocation.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class HistoryLocation used to display the history list of phone calls from the sever on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.jexpa.secondclone.API.APIDatabase;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterHistoryLocation;
import com.jexpa.secondclone.Database.DatabaseGetLocation;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.GPS;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIMethod.startAnim;
import static com.jexpa.secondclone.API.APIMethod.stopAnim;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_LOCATION;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class HistoryLocation extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<GPS> mData = new ArrayList<>();
    // action mode
    public static boolean isInActionMode = false;
    public static ArrayList<GPS> selectionList = new ArrayList<>();
    private DatabaseGetLocation databaseGetLocation;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_Location;
    private ProgressBar progressBar_Locations;
    private SwipeRefreshLayout swp_History_Location;
    //private Logger logger;
    private String max_Date = "";
    private String min_Time = "";
    List<GPS> gpsListAdd = new ArrayList<>();
    boolean endLoading = false;
    boolean isLoading = false;
    //aviLocation
    private AVLoadingIndicatorView aviLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_location);
        toolbar = findViewById(R.id.toolbar_Location);
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.LOCATION_HISTORY));
        toolbar.setLogo(R.drawable.location);
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        databaseGetLocation = new DatabaseGetLocation(this);
        database_last_update = new DatabaseLastUpdate(this);
        //logger =  Log4jHelper.getLogger("History_Location.class");
        table = (Table) getIntent().getSerializableExtra("table");
        // show dialog Loading...
        //getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        txt_No_Data_Location = findViewById(R.id.txt_No_Data_Location);
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
        getLocationInfo();

        // adapter
        mAdapter = new AdapterHistoryLocation(this, (ArrayList<GPS>) mData);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout();

    }

    private void getLocationInfo() {
        //if there is a network call method
        //logger.debug("internet = "+isConnected(this)+"\n==================End!");
        if (APIURL.isConnected(this)) {
            aviLocation.setVisibility(View.VISIBLE);
            startAnim(aviLocation);
            new LocationAsyncTask().execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = databaseGetLocation.getLocationCount(table.getDevice_ID());
            if (i == 0) {
                //txt_No_Data_Location.setVisibility(View.VISIBLE);
                txt_No_Data_Location.setText(MyApplication.getResourcses().getString(R.string.NoData)+ "  "+"Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                //APIDatabase.getThread(APIMethod.progressDialog);
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
                txt_No_Data_Location.setText("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                //APIDatabase.getThread(APIMethod.progressDialog);
            }
        }
    }

    // location get method from sever8
    private class LocationAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            Log.d("locationId", table.getDevice_ID() + "");
            // max_Date is get all the location from the min_date to the max_Date days
           String min = database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID());
            min_Time = database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            max_Date = getTimeNow().substring(0, 10) + " 23:59:59";
            Log.d("min_time", min + "="+min_Time);
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Start=\"0\" Length=\"1000\" Min_Date=\"" + min_Time + "\" Max_Date=\"" + max_Date + "\"/>";
            String function = "GetLocations";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                APIURL.deviceObject(s);
                JSONObject jsonObj = new JSONObject(APIURL.bodyLogin.getData());
                JSONArray jsonArray = jsonObj.getJSONArray("GPS");
                if (jsonArray.length() != 0) {

                    List<Integer> listDateCheck = databaseGetLocation.getAll_Location_ID_History_Date(table.getDevice_ID(), min_Time.substring(0, 10));
                    int save;
                    Log.d("DateCheck", "HistoryLocation = " + listDateCheck.size());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Gson gson = new Gson();
                        GPS gps = gson.fromJson(String.valueOf(jsonArray.get(i)), GPS.class);

                        //databaseGetLocation.addDevice(gps);
                        save = 0;
                        if (listDateCheck.size() != 0) {
                            for (Integer listCheck : listDateCheck) {
                                if (gps.getID() == listCheck) {
                                    save = 1;
                                    break;
                                }
                            }
                            if (save == 0) {
                                gpsListAdd.add(gps);
                                Log.i("zgetID",gps.getID()+"=");
                            }
                        } else {
                            gpsListAdd.add(gps);
                        }
                    }
                    if (gpsListAdd.size() != 0) {
                        databaseGetLocation.addDevice_GPS(gpsListAdd);
                    }
                }
                mData.clear();
                mData = databaseGetLocation.getAll_LocationID(table.getDevice_ID(),0);
                mAdapter = new AdapterHistoryLocation(HistoryLocation.this, (ArrayList<GPS>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if(mData.size()>= NumberLoad)
                {
                    initScrollListener();
                }
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_LOCATION, getTimeNow(), table.getDevice_ID());
                String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID());
                Log.d("min_time1", min_Time1 + "");
                if (mData.size() == 0) {
                    //txt_No_Data_Location.setVisibility(View.VISIBLE);
                    txt_No_Data_Location.setText(MyApplication.getResourcses().getString(R.string.NoData)+ "  "+"Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                }else {
                    txt_No_Data_Location.setText("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                }
                // get Method getThread()
                //progressDialog.dismiss();
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
            progressBar_Locations.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mData.remove(mData.size() - 1);
                    int scrollPosition = mData.size();
                    mAdapter.notifyItemRemoved(scrollPosition);
                    int currentSize = scrollPosition;
                    List<GPS> mDataStamp = databaseGetLocation.getAll_LocationID(table.getDevice_ID(),currentSize);
                    mData.addAll(mDataStamp);
                    if(mDataStamp.size()< NumberLoad)
                    {
                        endLoading = true;
                    }
                    Toast.makeText(getApplicationContext(), mData.size()+" = size", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    progressBar_Locations.setVisibility(View.GONE);
                    isLoading = false;
                }
            }, 2000);

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
        toolbar.inflateMenu(R.menu.menu_action_mode);
        isInActionMode = true;
        mAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prepareSelection(position);
    }

    // Lightning events have already been selected, delete is not available then added
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_delete) {
            isInActionMode = false;
            if (APIURL.isConnected(HistoryLocation.this)) {
                //                ((AdapterHistoryLocation) mAdapter).removeData(selectionList);
                //getProgressDialogDelete();
                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
                new clear_Location().execute();
            } else {
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
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
    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.LOCATION_HISTORY));
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
        swp_History_Location.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                if (isConnected(getApplicationContext()))
                {
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        gpsListAdd.clear();
                        clearActionMode();
                        new LocationAsyncTask().execute();
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
