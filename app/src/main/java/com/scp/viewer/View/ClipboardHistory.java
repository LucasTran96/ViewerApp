/*
  ClassName: ApplicationUsageHistory.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class ApplicationUsageHistory used to display the application that the user has downloaded the phone,
  call from the sever on display on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.r0adkll.slidr.Slidr;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.Adapter.AdapterClipboardHistory;
import com.scp.viewer.Database.DatabaseClipboard;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Model.Clipboard;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.scp.viewer.API.APIMethod.GetJsonFeature;
import static com.scp.viewer.API.APIMethod.PostJsonClearDataToServer;
import static com.scp.viewer.API.APIMethod.alertDialogDeleteItems;
import static com.scp.viewer.API.APIMethod.getSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.APIURL.noInternet;
import static com.scp.viewer.API.Global.CLIPBOARD_TOTAL;
import static com.scp.viewer.API.Global.GET_CLIPBOARD_HISTORY;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_CLIPBOARD;
import static com.scp.viewer.API.Global.time_Refresh_Device;
import static com.scp.viewer.Database.Entity.ClipboardEntity.TABLE_CLIPBOARD_HISTORY;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CLIPBOARD;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class ClipboardHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<Clipboard> mData = new ArrayList<>();
    List<Clipboard> usageListAdd = new ArrayList<>();
    // action mode
    public static boolean isInActionMode;
    public static ArrayList<Clipboard> selectionList;
    private DatabaseClipboard database_Clipboard;
    private SwipeRefreshLayout swp_ClipboardHistory;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private LinearLayout lnl_Total;
    private TextView txt_No_Data_App, txt_Total_Data;
    private ProgressBar progressBar_Clipboard;
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
        setContentView(R.layout.activity_clipboard_history);
        Slidr.attach(this);
        setID();
        selectionList = new ArrayList<>();
        isInActionMode = false;
        database_Clipboard = new DatabaseClipboard(this);
        database_last_update = new DatabaseLastUpdate(this);
        table = (Table) getIntent().getSerializableExtra(TABLE_CLIPBOARD_HISTORY);
        // show dialog Loading...
        getClipboardInfo();
        swipeRefreshLayout();
    }

    private void setID() {
        toolbar = findViewById(R.id.toolbar_Clipboard_History);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.CLIPBOARD_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_Total_Data = findViewById(R.id.txt_Total_Data);
        txt_No_Data_App = findViewById(R.id.txt_No_Data_Clipboard);
        progressBar_Clipboard = findViewById(R.id.progressBar_Clipboard);
        progressBar_Clipboard.setVisibility(View.GONE);
        swp_ClipboardHistory = findViewById(R.id.swp_ClipboardHistory);
        avLoadingIndicatorView = findViewById(R.id.aviClipboard);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_Clipboard_History);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @SuppressLint("SetTextI18n")
    private void getClipboardInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new getClipboardAsyncTask(0).execute();
        } else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_Clipboard.get_ClipboardCount_DeviceID(table.getID());
            if (i == 0) {
                txt_No_Data_App.setVisibility(View.VISIBLE);
                txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
            } else {
                mData.clear();
                mData = database_Clipboard.getAll_Clipboard_ID_History(table.getID(),0);
                mAdapter = new AdapterClipboardHistory(this, (ArrayList<Clipboard>) mData);
                if(mData.size() >= NumberLoad)
                {
                    initScrollListener();
                }
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.TurnOn));
                txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), CLIPBOARD_TOTAL + table.getDevice_Identifier())+"");
            }
        }
    }

    public void swipeRefreshLayout() {
        swp_ClipboardHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                        new getClipboardAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                swp_ClipboardHistory.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();
                            }
                        }, 1000);
                    } else {
                        swp_ClipboardHistory.setRefreshing(false);
                    }
                } else {
                    swp_ClipboardHistory.setRefreshing(false);
                    noInternet(ClipboardHistory.this);
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
                        progressBar_Clipboard.setVisibility(View.VISIBLE);
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
                public void run()
                {
                    currentSize =  mData.size();
                    if(isConnected(getApplicationContext()))
                    {
                        // Here is the total item value contact of device current has on CPanel
                        long totalContact = getSharedPreferLong(getApplicationContext(), CLIPBOARD_TOTAL + table.getDevice_Identifier());
                        new getClipboardAsyncTask(currentSize+1).execute();

                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;

                    }
                    else {
                        List<Clipboard> mDataCall = database_Clipboard.getAll_Clipboard_ID_History(table.getID(),currentSize);
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
                        progressBar_Clipboard.setVisibility(View.GONE);
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
    private class getClipboardAsyncTask extends AsyncTask<String, Void, String>
    {
        long startIndex;

        public getClipboardAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }
        @Override
        protected String doInBackground(String... strings) {

            Log.d("Clipboard_Id", table.getDevice_Identifier() + "");

            return GetJsonFeature(table, this.startIndex,GET_CLIPBOARD_HISTORY);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray ClipboardJson = jsonObj.getJSONArray("Table");
                JSONArray ClipboardJsonTable1 = jsonObj.getJSONArray("Table1");
                setToTalLog(ClipboardJsonTable1, CLIPBOARD_TOTAL + table.getDevice_Identifier(), getApplicationContext());

                if (ClipboardJson.length() != 0) {

                    for (int i = 0; i < ClipboardJson.length(); i++) {

                        Gson gson = new Gson();
                        Clipboard clipboard = gson.fromJson(String.valueOf(ClipboardJson.get(i)), Clipboard.class);
                        usageListAdd.add(clipboard);
                        Log.d("ContactHistory"," Add Contact = "+  clipboard.getClipboard_Content());

                    }
                    if (usageListAdd.size() != 0) {
                        database_Clipboard.addClipboard(usageListAdd);
                    }
                }
                //mData.clear();
                Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<Clipboard> mDataTamp = database_Clipboard.getAll_Clipboard_ID_History(table.getID(),currentSize);
                //mData.addAll(mDataTamp);

                if(checkLoadMore)
                {
                    int insertIndex = mData.size();
                    // mData.addAll(insertIndex, mDataTamp);
                    mData.addAll(insertIndex, mDataTamp);
                    Log.d("checkdata"," MData Call = "+ mDataTamp.size());
                    mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                    progressBar_Clipboard.setVisibility(View.GONE);
                }
                else {

                    mData.clear();
                    mData.addAll(mDataTamp);
                    if(mData.size() >= NumberLoad)
                    {
                        initScrollListener();
                    }
                    mAdapter = new AdapterClipboardHistory(ClipboardHistory.this, (ArrayList<Clipboard>) mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                lnl_Total.setVisibility(View.VISIBLE);
                if (mData.size() == 0) {
                    txt_No_Data_App.setVisibility(View.VISIBLE);
                    txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    String max_Date = getTimeNow();
                    database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_CLIPBOARD, max_Date, table.getDevice_Identifier());
                    String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_CLIPBOARD, TABLE_LAST_UPDATE, table.getDevice_Identifier());
                    Log.d("min_time1", min_Time1 + "");
                    txt_No_Data_App.setText(("Last update: "+ APIDatabase.getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CLIPBOARD, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null)));
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), CLIPBOARD_TOTAL + table.getDevice_Identifier())+"");
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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
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
            if (isConnected(ClipboardHistory.this)) {

                alertDialogDeleteItems(ClipboardHistory.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new clear_App());
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

    // Method clear data to sever
    @SuppressLint("StaticFieldLeak")
    private class clear_App extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("App_Id", table.getDevice_Identifier() + "");
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
           /* String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" List_ID=\"" + listID + "\" />";
            String function = POST_CLEAR_MULTI_APP;*/
            return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_CLIPBOARD);
        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getResultId().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                ((AdapterClipboardHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                clearActionMode();
                Toast.makeText(ClipboardHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
            }
            // get Method getThread()
            APIMethod.progressDialog.dismiss();
        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<Clipboard> selectionList) {
        for (Clipboard clipboard : selectionList) {
            database_Clipboard.delete_Clipboard_History(clipboard);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(null);
        }
        toolbar.setTitle( MyApplication.getResourcses().getString(R.string.CLIPBOARD_HISTORY));
        selectionList.clear();
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
}
