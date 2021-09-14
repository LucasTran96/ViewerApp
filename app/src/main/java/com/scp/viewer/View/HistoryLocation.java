/*
  ClassName: HistoryLocation.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class HistoryLocation used to display the history list of phone calls from the sever on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.API.APIURL;
import com.scp.viewer.Adapter.AdapterHistoryLocation;
import com.scp.viewer.Database.DatabaseGetLocation;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Model.GPS;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.google.gson.Gson;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import static com.scp.viewer.API.APIMethod.GetJsonFeature;
import static com.scp.viewer.API.APIMethod.GetJsonNowFeature;
import static com.scp.viewer.API.APIMethod.PostJsonClearDataToServer;
import static com.scp.viewer.API.APIMethod.alertDialogDeleteItems;
import static com.scp.viewer.API.APIMethod.getSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.APIMethod.updateViewCounterAll;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.APIURL.noInternet;
import static com.scp.viewer.API.Global.GPS_PULL_ROW;
import static com.scp.viewer.API.Global.GPS_TOTAL;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.NEW_ROW;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_GPS;
import static com.scp.viewer.API.Global.TYPE_CHECK_CONNECTION;
import static com.scp.viewer.API.Global.TYPE_GET_GPS_NOW;
import static com.scp.viewer.API.Global._TOTAL;
import static com.scp.viewer.API.Global.time_Refresh_Device;
import static com.scp.viewer.Adapter.AdapterHistoryLocation.getAddress;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_LOCATION;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

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
    private boolean checkRefresh = false;
    private int currentSize = 0;
    boolean selectAll = false;
    //aviLocation
    private AVLoadingIndicatorView aviLocation;
    //private AVLoadingIndicatorView avi_Loading_Get_GPS_Now;

    // Dialog
    private AlertDialog.Builder mBuilder;
    private AlertDialog dialog;
    private ProgressBar PrB_Get_GPS_Now;
    private LinearLayout ln_Current_Position, ln_Progress_Get_GPS_Now;
    private TextView txt_Percent, txt_Seconds, txt_Result_Get_GPS;
    public static TextView txt_Current_Position;

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
        // prepare action mode
        toolbar.getMenu().clear();
        //toolbar.inflateMenu(R.menu.menu_action_mode);
        toolbar.inflateMenu(R.menu.menu_action_getgpsnow);
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

    /**
     * This is a method to get data from the server to the device and display it in Recyclerview.
     * If there is no internet, get data from SQLite stored on the device and display it in Recyclerview.
     */
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
            int i = databaseGetLocation.getLocationCount(table.getDevice_Identifier());
            if (i == 0) {
                //txt_No_Data_Location.setVisibility(View.VISIBLE);
                txt_No_Data_Location.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
            } else {
                mData.clear();
                mData = databaseGetLocation.getAll_LocationID(table.getID(),0);
                mAdapter = new AdapterHistoryLocation(this, (ArrayList<GPS>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if(mData.size()>= NumberLoad)
                {
                    initScrollListener();
                }
                txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), GPS_TOTAL + table.getDevice_Identifier())+"");
                txt_No_Data_Location.setText("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
            }
        }
    }

    // location get method from severe
    private class LocationAsyncTask extends AsyncTask<String, Void, String> {

        long startIndex;

        public LocationAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected String doInBackground(String... strings)
        {

            Log.d("locationId", table.getDevice_Identifier() + "");
            return GetJsonFeature(table, this.startIndex,"GetLocations");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                APIURL.deviceObject(s);
                JSONObject jsonObj = new JSONObject(APIURL.bodyLogin.getData());
                JSONArray jsonArray = jsonObj.getJSONArray("Table");
                JSONArray GPSJsonPaging = jsonObj.getJSONArray("Table1");
                setToTalLog(GPSJsonPaging, GPS_TOTAL + table.getDevice_Identifier(), getApplicationContext());
                setSharedPreferLong(getApplicationContext(), GPS_PULL_ROW +_TOTAL + table.getDevice_Identifier() + NEW_ROW, 0);
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
                List<GPS> mDataTamp = databaseGetLocation.getAll_LocationID(table.getID(),currentSize);

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

                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_LOCATION, getTimeNow(), table.getDevice_Identifier());
                String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_Identifier());
                if (mData.size() == 0)
                {
                    txt_No_Data_Location.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), GPS_TOTAL + table.getDevice_Identifier())+"");
                    txt_No_Data_Location.setText("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_LOCATION, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
                }
                stopAnim(aviLocation);
                aviLocation.setVisibility(View.GONE);
            } catch (JSONException e) {
                //MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * getGPSNowAsyncTask this is the AsyncTask method that calls to the server to get the latest location of the target device
     * If the location is available, the location has been obtained from Get-GPS-Now
     * If the location cannot be obtained, the device may be offline or the device has GPS off
     */
    private class getGPSNowAsyncTask extends AsyncTask<String, Void, String> {

        String minDate;

        public getGPSNowAsyncTask(String minDate) {
            this.minDate = minDate;
        }

        @Override
        protected String doInBackground(String... strings)
        {

            Log.d("locationId", table.getDevice_Identifier() + "");
            return GetJsonNowFeature(table, minDate,"GetLocations");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {

                setProgressNow(90, txt_Percent, HistoryLocation.this);

                // Show custom process Dialog at 70% Get gps now...
                APIURL.deviceObject(s);
                JSONObject jsonObj = new JSONObject(APIURL.bodyLogin.getData());
                JSONArray jsonArray = jsonObj.getJSONArray("Table");

                List<GPS> gpsList = new ArrayList<>();
                if (jsonArray.length() == 1)
                {
                    Gson gson = new Gson();
                    GPS gps = gson.fromJson(String.valueOf(jsonArray.get(0)), GPS.class);
                    gpsList.add(gps);
                    // handle the latest gps display
                    if(gpsList.size() == 1)
                    {
                        setProgressNow(100, txt_Percent, HistoryLocation.this);
                        ln_Current_Position.setVisibility(View.VISIBLE);
                        ln_Progress_Get_GPS_Now.setVisibility(View.GONE);
                        txt_Current_Position.setText(getAddress(gps.getLatitude(), gps.getLongitude(),HistoryLocation.this));
                        // Display custom process Dialog at 100% Apply new location...
                        // assign detailed location name to dialog with TextView
                        // Click on TextView to open Map to see details
                        // handle assigning position name to Dialog for users to see
                    }
                    else {
                        // check internet of the target app
                        // The processor obtains information about the current state of the target device.
                        new PhoneCallRecordHistory.checkConnectAsyncTask(minDate, table.getDevice_Identifier(), TYPE_GET_GPS_NOW, HistoryLocation.this).execute();
                        ln_Current_Position.setVisibility(View.VISIBLE);
                        // error when converting GPS data
                        //txt_Current_Position.setText(getApplicationContext().getResources().getString(R.string.device_offline));
                    }

                }
                else {

                    // check internet of the target app
                    // The processor obtains information about the current state of the target device.
                    new PhoneCallRecordHistory.checkConnectAsyncTask(minDate, table.getDevice_Identifier(), TYPE_GET_GPS_NOW, HistoryLocation.this).execute();

                    ln_Current_Position.setVisibility(View.VISIBLE);
                    ln_Progress_Get_GPS_Now.setVisibility(View.GONE);
                    setProgressNow(100, txt_Percent, HistoryLocation.this);
                    txt_Result_Get_GPS.setText("Get GPS now failed");
                    txt_Current_Position.setText(getApplicationContext().getResources().getString(R.string.device_offline));
                    // error getting gps now
                    // gọi AsyncTask check Connection
                }


            } catch (JSONException e) {
                //MyApplication.getInstance().trackException(e);
                e.printStackTrace();
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

                if (!isLoading && (!endLoading)) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mData.size() - 1) {
                        //bottom of list!
                        isLoading = true;
                        progressBar_Locations.setVisibility(View.VISIBLE);
                       // loadMore();

                        if(!checkRefresh)
                        {
                            loadMore();
                        }
                        else {
                            isLoading = false;
                            endLoading = false;
                            progressBar_Locations.setVisibility(View.GONE);
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
                        long totalContact = getSharedPreferLong(getApplicationContext(), GPS_TOTAL + table.getDevice_Identifier());
                        new LocationAsyncTask(currentSize+1).execute();

                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        isLoading = false;

                    }
                    else {
                        List<GPS> mDataCall = databaseGetLocation.getAll_LocationID(table.getID(),currentSize);
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
        getMenuInflater().inflate(R.menu.menu_action_getgpsnow, menu);
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
        else if(item.getItemId() ==  R.id.item_Get_GPS_Now)//item_Get_GPS_Now
        {

            if (APIURL.isConnected(this))
            {
                setDialog(HistoryLocation.this);
                // Dialog includes: Process name, Progress, Detailed address name when obtained (or display GPS off or Network Offline)
                // Display custom process Dialog at 0% Starting...
                // handle get gps now
                //Toast.makeText(HistoryLocation.this, "Start sending get gps now", Toast.LENGTH_LONG).show();

                setProgressNow(20, txt_Percent, HistoryLocation.this);
                final String minDate = getTimeNow();
                new APIMethod.PushNotification(table.getID(), TYPE_GET_GPS_NOW, table.getDevice_Identifier(), 0).execute();
                // handle check connection
                new APIMethod.PushNotification(table.getID(), TYPE_CHECK_CONNECTION, table.getDevice_Identifier(), 0).execute();

                // Display custom process Dialog at 25% Push notification to the target app
                setProgressNow(30, txt_Percent, HistoryLocation.this);
                countDownTimer(txt_Seconds, txt_Percent, HistoryLocation.this);
                setDePlay(minDate);

            } else {
                Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            }

        }
        return true;
    }

    /**
     * countDownTimer count down timer 20000 milliseconds
     * Use: HistoryLocation.java
     *      DashBoard.java
     */
    public static void countDownTimer(final TextView txt_Seconds, final TextView text_Percent, final Activity mActivity)
    {
        new CountDownTimer(20000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long duration) {
                // Duration
                long second = (duration / 1000) % 60;
                txt_Seconds.setText(second +" "+ mActivity.getResources().getString(R.string.seconds));

                if (second == 18)
                    setProgressNow(40, text_Percent, mActivity);
                else if(second == 16)
                    setProgressNow(45, text_Percent, mActivity);
                else if(second == 14)
                    setProgressNow(50, text_Percent, mActivity);
                else if(second == 12)
                    setProgressNow(55, text_Percent, mActivity);
                else if(second == 10)
                    setProgressNow(60, text_Percent, mActivity);
                else if(second == 8)
                    setProgressNow(65, text_Percent, mActivity);
                else if(second == 6)
                    setProgressNow(70, text_Percent, mActivity);
                else if(second == 4)
                    setProgressNow(75, text_Percent, mActivity);
                else if(second == 2)
                    setProgressNow(80, text_Percent, mActivity);
                else if(second == 0)
                    setProgressNow(85, text_Percent, mActivity);
            }

            public void onFinish() {
                txt_Seconds.setText(mActivity.getResources().getString(R.string.done));
            }
        }.start();
    }

    /**
     * setDePlay is the method to wait for how many seconds before proceeding to the next steps.
     */
    public void setDePlay( final String minDate)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.setCancelable(true);
                // process to get gps from this time take up to 1 gps and check if it is, display it, if not, the message can't be obtained
                new getGPSNowAsyncTask(minDate).execute();

            }
        }, 20000);
    }

    /**
     * setProgressGetGPSNow set progress Get-GPS-Now
     * @param percent percent
     */
    @SuppressLint("SetTextI18n")
    public static void setProgressNow(int percent, TextView txt_Percent, Activity mActivity)
    {
        //PrB_Get_GPS_Now.setProgress(percent);

        if(percent == 100)
            txt_Percent.setText(mActivity.getResources().getString(R.string.completed));
        else
            txt_Percent.setText(mActivity.getResources().getString(R.string.to_complete, percent)+ "%");
    }

    /**
     * setDialog this is the Dialog constructor for the get GPS now method.
     */
    @SuppressLint("SetTextI18n")
    public void setDialog(final Activity mActivity)
    {
            mBuilder = new AlertDialog.Builder(mActivity);
            @SuppressLint("InflateParams") View mView = LayoutInflater.from(mActivity).inflate(R.layout.item_dialog_pushnotification, null);

            PrB_Get_GPS_Now = mView.findViewById(R.id.PrB_Get_GPS_Now);
            txt_Percent = mView.findViewById(R.id.txt_Percent);
            txt_Result_Get_GPS = mView.findViewById(R.id.txt_Result_Get_GPS);
            txt_Seconds = mView.findViewById(R.id.txt_Seconds);
            txt_Current_Position = mView.findViewById(R.id.txt_Current_Position);
            ln_Current_Position = mView.findViewById(R.id.ln_Current_Position);
            ln_Progress_Get_GPS_Now = mView.findViewById(R.id.ln_Progress_Get_GPS_Now);
            ln_Current_Position.setVisibility(View.GONE);
            mBuilder.setView(mView);
            dialog = mBuilder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.show();
            //txt_Percent.setText(0 + mActivity.getResources().getString(R.string.to_complete));
            setProgressNow(0, txt_Percent, HistoryLocation.this);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                   // handling when exiting dialog
                    //Toast.makeText(mActivity, "Close Dialog", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @SuppressLint("StaticFieldLeak")
    private class clear_Location extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("locationId", table.getDevice_Identifier() + "");
            StringBuilder listID = new StringBuilder();
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList.get(i).getID());
                }
            }
            /*String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" List_ID=\"" + listID + "\" />";
            String function = POST_CLEAR_MULTI_GPS;*/
            return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_GPS);
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
            toolbar.inflateMenu(R.menu.menu_action_getgpsnow);
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

    /**
     * swipeRefreshLayout is a method that reloads the page and updates it further if new data has been added to the server.
     */
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
                    checkRefresh = true;

                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        //gpsListAdd.clear();
                        //Method for refresh recycle view
                        if (!gpsListAdd.isEmpty())
                        {
                            gpsListAdd.clear(); //The list for update recycle view
                            mAdapter.notifyDataSetChanged();
                        }
                        clearActionMode();
                        new LocationAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_History_Location.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_History_Location.setRefreshing(false);
                    }
                } else {
                    swp_History_Location.setRefreshing(false);
                    noInternet(HistoryLocation.this);
                }
            }
        });
    }
}
