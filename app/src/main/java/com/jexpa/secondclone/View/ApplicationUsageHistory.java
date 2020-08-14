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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterApplicationHistory;
import com.jexpa.secondclone.Database.DatabaseApplicationUsage;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.ApplicationUsage;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
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
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private String max_Date = "";
    private TextView txt_No_Data_App;


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
        getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        txt_No_Data_App = findViewById(R.id.txt_No_Data_App);
        txt_No_Data_App.setVisibility(View.GONE);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl__Application_History);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getAppInfo();

        // adapter
        mAdapter = new AdapterApplicationHistory(this, (ArrayList<ApplicationUsage>) mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getAppInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            new getAppAsyncTask().execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_application_usage.get_ApplicationCount_DeviceID(table.getDevice_ID());
            if (i == 0) {
                txt_No_Data_App.setVisibility(View.VISIBLE);
                txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.NoData));
                getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = database_application_usage.getAll_Application_ID_History(table.getDevice_ID());
                mAdapter = new AdapterApplicationHistory(this, (ArrayList<ApplicationUsage>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                getThread(APIMethod.progressDialog);
            }
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class getAppAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            Log.d("Application_Id", table.getDevice_ID() + "");
            // max_Date is get all the location from the min_date to the max_Date days
            String min_Time = database_last_update.getLast_Time_Update(COLUMN_LAST_APPLICATION, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            max_Date = getTimeNow().substring(0, 10) + " 23:59:59";
            Log.d("min_time", min_Time + "");
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Start=\"0\" Length=\"1000\" Min_Date=\"" + min_Time + "\" Max_Date=\"" + max_Date + "\"  />";
            String function = "GetApps";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray GPSJson = jsonObj.getJSONArray("Table");
                if (GPSJson.length() != 0) {

                    List<Integer> listDateCheck = database_application_usage.getAll_App_ID_History_Date(table.getDevice_ID(), database_last_update.getLast_Time_Update(COLUMN_LAST_APPLICATION, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10));
                    int save;
                    Log.d("DateCheck", listDateCheck.size() + "");
                    for (int i = 0; i < GPSJson.length(); i++) {
                        Gson gson = new Gson();
                        ApplicationUsage application_usage = gson.fromJson(String.valueOf(GPSJson.get(i)), ApplicationUsage.class);
                        mAdapter.notifyDataSetChanged();
                        Log.d("Application", application_usage.getRowIndex() + "");
                        //database_application_usage.addDevice_Application(application_usage);
                        save = 0;
                        if (listDateCheck.size() != 0) {
                            for (Integer listCheck : listDateCheck) {
                                if (application_usage.getID() == listCheck) {
                                    save = 1;
                                    break;
                                }
                            }
                            if (save == 0) {
                                usageListAdd.add(application_usage);
                            }
                        } else {
                            usageListAdd.add(application_usage);
                        }

                    }
                    if (usageListAdd.size() != 0) {
                        database_application_usage.addDevice_Application(usageListAdd);
                    }

                }
                mData.clear();
                mData = database_application_usage.getAll_Application_ID_History(table.getDevice_ID());
                mAdapter = new AdapterApplicationHistory(ApplicationUsageHistory.this, (ArrayList<ApplicationUsage>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if (mData.size() == 0) {
                    txt_No_Data_App.setVisibility(View.VISIBLE);
                    txt_No_Data_App.setText(MyApplication.getResourcses().getString(R.string.NoData));
                }
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_APPLICATION, max_Date, table.getDevice_ID());
                String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_APPLICATION, TABLE_LAST_UPDATE, table.getDevice_ID());
                Log.d("min_time1", min_Time1 + "");
                // get Method getThread()
                //progressDialog.dismiss();
                getThread(APIMethod.progressDialog);
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
        toolbar.inflateMenu(R.menu.menu_action_mode);
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
        } else if (item.getItemId() == android.R.id.home) {
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
