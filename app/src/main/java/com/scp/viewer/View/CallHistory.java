/*
  ClassName: CallHistory.java
  author  Lucas Walker (lucas.walker@jexpa.com)
  see AppCompatActivity
  Created Date: 2018-06-05
  Description: Class CallHistory used to display the history of the phone users dialed,
  retrieving data from the sever on display on RecyclerView in the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.Adapter.AdapterCallHistory;
import com.scp.viewer.Database.DatabaseCallHistory;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Model.Call;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIMethod.GetJsonFeature;
import static com.scp.viewer.API.APIMethod.PostJsonClearDataToServer;
import static com.scp.viewer.API.APIMethod.alertDialogDeleteItems;
import static com.scp.viewer.API.APIMethod.getSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.APIMethod.updateViewCounterAll;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.APIURL.noInternet;
import static com.scp.viewer.API.Global.CALL_PULL_ROW;
import static com.scp.viewer.API.Global.CALL_TOTAL;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.NEW_ROW;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_CALL;
import static com.scp.viewer.API.Global._TOTAL;
import static com.scp.viewer.API.Global.time_Refresh_Device;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CALL;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

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
    private boolean checkRefresh = false;
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
        Log.d("android10above", getDeviceIDAndroid10Above());// 358697535677244  357697898677243
                                                                 //  358697535677244
       // Log.d("android10above", getIMEIAndroid10Above());//
    }

    public  static String getIMEIAndroid10Above() {
        String uniquePseudoID = "35" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
        String serial = Build.getRadioVersion();
        String uuid = new UUID(uniquePseudoID.hashCode(), serial.hashCode()).toString();
        Log.d("Device ID", uuid);
        return uuid;
    }

    public  static String getDeviceIDAndroid10Above(){
        try {
            String devIDShort = "35" + //we make this look like a valid IMEI
                    Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                    Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                    Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                    Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                    Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                    Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                    Build.USER.length()%10 ; //13 digits
            int random = new Random().nextInt(1000);
            Log.d("android10above", "Build.BOARD.length() = " + Build.BOARD.length() + " Build.BOARD.length()%10 = " + Build.BOARD.length()%10
                                               + "Build.BRAND.length()%10 = " + Build.BRAND.length()%10);
            Log.d("android10above", "devIDShort= "+ devIDShort + " uuid = "+ random);

            return  devIDShort + random;
        }catch (Exception e)
        {
            e.getMessage();
            return String.valueOf(System.currentTimeMillis());
        }
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

    /**
     * This is a method to get data from the server to the device and display it in Recyclerview.
     * If there is no internet, get data from SQLite stored on the device and display it in Recyclerview.
     */
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
            int i = database_call.getCallCount(table.getDevice_Identifier());
            if (i == 0) {
                //txt_No_Data_Call.setVisibility(View.VISIBLE);
                txt_No_Data_Call.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data_Call.setText("0");

            } else {
                mData.clear();
                mData = database_call.getAll_Call_ID_History(table.getID(),0);
                mAdapter = new AdapterCallHistory(this, (ArrayList<Call>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if(mData.size()>= NumberLoad)
                {
                    initScrollListener();
                }
                txt_Total_Data_Call.setText(getSharedPreferLong(getApplicationContext(), CALL_TOTAL + table.getDevice_Identifier())+"");
                txt_No_Data_Call.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
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
                        progressBar_Call.setVisibility(View.VISIBLE);
                       //loadMore();

                        if(!checkRefresh)
                        {
                            loadMore();
                        }
                        else {
                            isLoading = false;
                            endLoading = false;
                            progressBar_Call.setVisibility(View.GONE);
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
                public void run()
                {
                    currentSize =  mData.size();
                    if(isConnected(getApplicationContext()))
                    {
                        // Here is the total item value contact of device current has on CPanel
                        long totalCall = getSharedPreferLong(getApplicationContext(), CALL_TOTAL + table.getDevice_Identifier());
                        new getCallAsyncTask(currentSize+1).execute();

                        if((mData.size()+1) >= totalCall)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;

                    }
                    else {
                        List<Call> mDataCall = database_call.getAll_Call_ID_History(table.getID(),currentSize);
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
            Log.d("callId", table.getDevice_Identifier() + "");
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
                    setToTalLog(GPSJsonTable1, CALL_TOTAL  + table.getDevice_Identifier(), getApplicationContext());
                    setSharedPreferLong(getApplicationContext(), CALL_PULL_ROW+_TOTAL+ table.getDevice_Identifier() + NEW_ROW, 0);
                    Log.d("CallHistory"," GPSJson = "+  GPSJson);
                    Log.d("TotalRoS"," CALL_TOTAL = "+  CALL_TOTAL  + table.getDevice_Identifier());
                    if (GPSJson.length() != 0)
                    {

                        for (int i = 0; i < GPSJson.length(); i++) {

                            Gson gson = new Gson();
                            Call callHistory = gson.fromJson(String.valueOf(GPSJson.get(i)), Call.class);
                            listCall.add(callHistory);
                            Log.d("CallHistory"," Add Call = "+  callHistory.getContact_Name());

                        }
                        if (listCall.size() != 0) {
                            database_call.addDevice_Call_Fast(listCall);
                        }
                    }
                    //mData.clear();
                    Log.d("CallHistory"," currentSize CallHistory = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                    List<Call> mDataTamp = database_call.getAll_Call_ID_History(table.getID(),currentSize);
                    //mData.addAll(mDataTamp);

                    if(checkLoadMore)
                    {
                        int insertIndex = mData.size();
                       // mData.addAll(insertIndex, mDataTamp);
                        mData.addAll(insertIndex, mDataTamp);
                        Log.d("checkdata"," MData Call = "+ mDataTamp.size());
                        mAdapter.notifyItemRangeInserted(insertIndex-1, mDataTamp.size() );
                        Log.d("CallHistory"," checkLoadMore Call = "+ true);
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
                    database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_CALL, date_Max, table.getDevice_Identifier());
                    lnl_Total.setVisibility(View.VISIBLE);
                    if (mData.size() == 0) {
                        //txt_No_Data_Call.setVisibility(View.VISIBLE);
                        txt_No_Data_Call.setText(MyApplication.getResourcses().getString(R.string.NoData));
                        txt_Total_Data_Call.setText("0");
                    }
                    else {
                        txt_No_Data_Call.setText("Last update: " + getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CALL, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
                        txt_Total_Data_Call.setText(getSharedPreferLong(getApplicationContext(), CALL_TOTAL  + table.getDevice_Identifier())+"");
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
                // getProgressDialogDelete();
                //boolean checkDelete = alertDialogDeleteItems(CallHistory.this, getApplicationContext().getResources().getString(R.string.question_Select));//question_Select
                alertDialogDeleteItems(CallHistory.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new ClearMultiCall());
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
       /*     String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" List_ID=\"" + listID + "\" />";
            String function = POST_CLEAR_MULTI_CALL;*/
            return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_CALL);


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

    /**
     * swipeRefreshLayout is a method that reloads the page and updates it further if new data has been added to the server.
     */
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
                        //listCall.clear();
                        //Method for refresh recycle view
                        if (!listCall.isEmpty())
                        {
                            listCall.clear(); //The list for update recycle view
                            mAdapter.notifyDataSetChanged();
                        }
                        clearActionMode();
                        new getCallAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_CallHistory.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_CallHistory.setRefreshing(false);
                    }
                } else {
                    swp_CallHistory.setRefreshing(false);
                    noInternet(CallHistory.this);
                }
            }
        });
    }
}
