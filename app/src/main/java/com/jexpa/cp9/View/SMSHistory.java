/*
  ClassName: SMSHistory.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class SMSHistory used to display the SMS list from the server to the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.cp9.View;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jexpa.cp9.API.APIMethod;
import com.jexpa.cp9.API.APIURL;
import com.jexpa.cp9.Adapter.AdapterSMSHistory;
import com.jexpa.cp9.Database.DatabaseGetSMS;
import com.jexpa.cp9.Database.DatabaseLastUpdate;
import com.jexpa.cp9.Model.SMS;
import com.jexpa.cp9.Model.Table;
import com.jexpa.cp9.R;
import com.jexpa.cp9.API.APIDatabase;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.jexpa.cp9.API.APIDatabase.getTimeItem;
import static com.jexpa.cp9.API.APIMethod.alertDialogDeleteItems;
import static com.jexpa.cp9.API.APIMethod.getProgressDialog;
import static com.jexpa.cp9.API.APIMethod.getTotalLongForSMS;
import static com.jexpa.cp9.API.APIMethod.startAnim;
import static com.jexpa.cp9.API.APIMethod.stopAnim;
import static com.jexpa.cp9.API.APIMethod.updateViewCounterAll;
import static com.jexpa.cp9.API.APIURL.bodyLogin;
import static com.jexpa.cp9.API.APIURL.deviceObject;
import static com.jexpa.cp9.API.APIURL.getDateNowInMaxDate;
import static com.jexpa.cp9.API.APIURL.getTimeNow;
import static com.jexpa.cp9.API.APIURL.isConnected;
import static com.jexpa.cp9.API.APIURL.noInternet;
import static com.jexpa.cp9.API.Global.LIMIT_REFRESH;
import static com.jexpa.cp9.API.Global.time_Refresh_Device;
import static com.jexpa.cp9.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class SMSHistory extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rcl_SMS;
    private RecyclerView.Adapter adapter_SMS;
    List<SMS> list_SMS = new ArrayList<>();
    List<SMS> listName = new ArrayList<>();
    // action mode
    public static boolean isInActionMode_SMS = false;
    public static ArrayList<SMS> selectionList;
    private DatabaseGetSMS databaseGetSMS;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    public static String style = "50";
    private String nameFeature;
    private String nameTable;
    private TextView txt_No_Data_SMS, txt_Total_Data;
    private LinearLayout lnl_Total;
    private SwipeRefreshLayout swp_SMS;
    private List<SMS> smsList = new ArrayList<>();
    //private Logger logger;
    private String min_time = "";
    private String date_max = "";
    public static String name_Table_SMSHistory;
    boolean selectAll = false;
    public static int styleNowOfSMS = 50;
    //aviSMS
    private AVLoadingIndicatorView aviSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_history);
        Slidr.attach(this);
        selectionList = new ArrayList<>();
        isInActionMode_SMS = false;
        style = getIntent().getStringExtra("style");
        nameTable = getIntent().getStringExtra("nameTable");
        name_Table_SMSHistory = nameTable;
        nameFeature = getIntent().getStringExtra("nameFeature");
        Log.d("style", style + "=" + nameTable);
        table = (Table) getIntent().getSerializableExtra("table_SMS");
        toolbar = findViewById(R.id.toolbar_SMS);
        setTitle(toolbar, nameTable);
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        databaseGetSMS = new DatabaseGetSMS(this);
        database_last_update = new DatabaseLastUpdate(this);
        //logger =  Log4jHelper.getLogger("SMSHistory.class");
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_No_Data_SMS = findViewById(R.id.txt_No_Data_SMS);
        txt_Total_Data = findViewById(R.id.txt_Total_Data);
        swp_SMS = findViewById(R.id.swp_SMS);
        aviSMS = findViewById(R.id.aviSMS);
        rcl_SMS = findViewById(R.id.rcl_SMS_History);
        rcl_SMS.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rcl_SMS.setLayoutManager(mLayoutManager);
        get_SMS_Info();
        swipeRefreshLayout();
    }

    private void setTitle(Toolbar toolbar, String nameTable) {

        switch (nameTable) {
            case "SMS_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.SMS_HISTORY));
                break;
            case "WhatsApp_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.WHATSAPP_HISTORY));
                break;
            case "Viber_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.VIBER_HISTORY));
                break;
            case "Facebook_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.FACEBOOK_HISTORY));
                break;
            case "Skype_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.SKYPE_HISTORY));
                break;
            case "Hangouts_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.HANGOUTS_HISTORY));
                break;
            case "BBM_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.BBM_HISTORY));
                break;
            case "LINE_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.LINE_HISTORY));
                break;
            case "KIK_Table":
                toolbar.setTitle(MyApplication.getResourcses().getString(R.string.KIK_HISTORY));
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void get_SMS_Info()
    {
        //if there is a network call method
        if (APIURL.isConnected(this)) {
            aviSMS.setVisibility(View.VISIBLE);
            startAnim(aviSMS);
            new getSMS_AsyncTask().execute();
        } else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            int SMSCount = databaseGetSMS.getSMSCount(nameTable, table.getDevice_ID());
            if (SMSCount == 0) {
                APIDatabase.getThread(APIMethod.progressDialog);
                txt_No_Data_SMS.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
            } else {
                list_SMS.clear();
                list_SMS = databaseGetSMS.get_DISTINCT_SMS_Name(table.getDevice_ID(), nameTable);
                adapter_SMS = new AdapterSMSHistory(this, list_SMS);
                rcl_SMS.setAdapter(adapter_SMS);
                adapter_SMS.notifyDataSetChanged();
                txt_Total_Data.setText(getTotalLongForSMS(style, getApplicationContext(),table.getDevice_ID()));
                txt_No_Data_SMS.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
            }
        }
    }

    /**
     * getSMS_AsyncTask This is the AsyncTask method used to get SMS data from the server to display to the user.
     */
    @SuppressLint("StaticFieldLeak")
    private class getSMS_AsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("SMSId", table.getDevice_ID() + "");
            // max_Date is get all the location from the min_date to the max_Date days
            //min_time = database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE,table.getDevice_ID());
            min_time = database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            String max_time = getDateNowInMaxDate();
            date_max = getTimeNow();
            Log.d("min_time", min_time + "");
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Start=\"0\" Length=\"3000\" Min_Date=\"" + min_time + " \" Max_Date=\"" + max_time + " \" Type=\"" + style + "\" />";
            String function = "GetSMSByDateTime";
            Log.d("SMSId",  "value = "+ value);
            return APIURL.POST(value, function);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {

                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray SMS_Json = jsonObj.getJSONArray("Table");
                list_SMS.clear();
                if (SMS_Json.length() != 0)
                {
                    for (int i = 0; i < SMS_Json.length(); i++) {
                        Gson gson = new Gson();
                        SMS sms = gson.fromJson(String.valueOf(SMS_Json.get(i)), SMS.class);
                        //databaseGetSMS.addDevice(sms,nameTable);
                        smsList.add(sms);
                    }
                    if (smsList.size() != 0) {
                        databaseGetSMS.addDevice_SMS(smsList, nameTable);
                    }
                }
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, nameFeature, date_max, table.getDevice_ID());
                String max_time = database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID());
                Log.d("max_time", max_time + "");
                int SMSCount = databaseGetSMS.getSMSCount(nameTable, table.getDevice_ID());
                lnl_Total.setVisibility(View.VISIBLE);
                if (SMSCount != 0) {
                    list_SMS = databaseGetSMS.get_DISTINCT_SMS_Name(table.getDevice_ID(), nameTable);
                    // When assigning this List to another, the RecyclerView must be initialized
                    adapter_SMS = new AdapterSMSHistory(SMSHistory.this, list_SMS);
                    rcl_SMS.setAdapter(adapter_SMS);
                    adapter_SMS.notifyDataSetChanged();
                    txt_Total_Data.setText(getTotalLongForSMS(style, getApplicationContext(), table.getDevice_ID()));
                    txt_No_Data_SMS.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                } else {
                    txt_No_Data_SMS.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }
                stopAnim(aviSMS);
                aviSMS.setVisibility(View.GONE);
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
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_delete);
        isInActionMode_SMS = true;
        adapter_SMS.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        }
        prepareSelection(position);
    }

    public void prepareSelection(int position) {

        if (!selectionList.contains(list_SMS.get(position))) {
            selectionList.add(list_SMS.get(position));
        } else {
            selectionList.remove(list_SMS.get(position));
        }

        updateViewCounter();
    }

    private void updateViewCounter() {
        int counter = selectionList.size();
        updateViewCounterAll(toolbar, counter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_delete)
        {
            if (APIURL.isConnected(SMSHistory.this)) {
//                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
//                new clear_SMS().execute();

                alertDialogDeleteItems(SMSHistory.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new clear_SMS());

            } else {
                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                clearActionMode();
                adapter_SMS.notifyDataSetChanged();
            }
        }
        else if(item.getItemId() ==  R.id.item_select_all)
        {
            if(!selectAll)
            {
                selectAll = true;
                selectionList.clear();
                selectionList.addAll(list_SMS);
                updateViewCounter();
                adapter_SMS.notifyDataSetChanged();

            }
            else {
                selectAll = false;
                selectionList.clear();
                updateViewCounter();
                adapter_SMS.notifyDataSetChanged();
            }

        }
        else if (item.getItemId() == android.R.id.home) {
            if(isInActionMode_SMS)
            {
                clearActionMode();
                adapter_SMS.notifyDataSetChanged();
            }
            else {
                super.onBackPressed();
            }
        }
        return true;
    }

    // Method clear to sever
    @SuppressLint("StaticFieldLeak")
    private class clear_SMS extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("clearSMS", table.getDevice_ID() + "");
            StringBuilder listID = new StringBuilder();
            for (int i = 0; i < selectionList.size(); i++) {
                //if(i != selectionList.size()-1){
                // listID = listID + selectionList.get(i).getID() + ",";
                listName = databaseGetSMS.getSMS_Contact_Name(selectionList.get(i).getContact_Name(), nameTable);
                Log.d("listName", listName.size() + "");
                for (int j = 0; j < listName.size(); j++) {

                    if (j != listName.size() - 1) {
                        listID.append(listName.get(j).getID()).append(",");
                        //Log.d("listID",listID+"");
                    } else {
                        listID.append(listName.get(j).getID());
                        //Log.d("listID",listID+"");

                    }
                }
            }
            Log.d("listID", listID + "");
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" List_ID=\"" + listID + "\"  Chat_Type=\"" + style + "\"/>";
            String function = "DeleteMultiChatDataLog";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);


            if (APIURL.bodyLogin.getCode().startsWith("S") || APIURL.bodyLogin.getCode().startsWith("s")) {
                ((AdapterSMSHistory) adapter_SMS).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                clearActionMode();
                Toast.makeText(SMSHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
            }
            APIMethod.progressDialog.dismiss();

        }
    }

    // Method of deleting columns by id
    public void clearDataSQLite(ArrayList<SMS> selectionList) {

        for (int i = 0; i < selectionList.size(); i++) {

            listName = databaseGetSMS.getSMS_Contact_Name(selectionList.get(i).getContact_Name(), nameTable);
            for (SMS sms : listName) {
                databaseGetSMS.deleteSMS(sms, nameTable);
            }
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode()
    {
        if(isInActionMode_SMS)
        {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_main);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            setTitle(toolbar, nameTable);
            selectionList.clear();
            isInActionMode_SMS = false;
        }
    }

    // Check out the escape without the option will always exit,
    // the opposite will cancel the selection, not exit.
    @Override
    public void onBackPressed() {

        if (isInActionMode_SMS) {
            clearActionMode();
            isInActionMode_SMS = false;
            adapter_SMS.notifyDataSetChanged();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isConnected(getApplicationContext()))
        {
            smsList.clear();
            clearActionMode();
            new getSMS_AsyncTask().execute();
        }
    }


    public void swipeRefreshLayout() {
        swp_SMS.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                if (isConnected(getApplicationContext()))
                {
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        smsList.clear();
                        clearActionMode();
                        new getSMS_AsyncTask().execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_SMS.setRefreshing(false);
                                //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();

                            }
                        }, 1000);
                    } else {
                        swp_SMS.setRefreshing(false);
                        //Toast.makeText(HistoryLocation.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(ManagementDevice.this, calendar.getTimeInMillis()- timeRefresh_Device +"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swp_SMS.setRefreshing(false);
                    noInternet(SMSHistory.this);
                }
            }
        });
    }

}
