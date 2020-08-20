/*
  ClassName: SMSHistory.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class SMSHistory used to display the SMS list from the server to the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

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
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterSMSHistory;
import com.jexpa.secondclone.Database.DatabaseGetSMS;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Model.SMS;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.API.APIDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIMethod.startAnim;
import static com.jexpa.secondclone.API.APIMethod.stopAnim;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.getDateNowInMaxDate;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class SMSHistory extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rcl_SMS;
    private RecyclerView.Adapter adapter_SMS;
    List<SMS> list_SMS = new ArrayList<>();
    List<SMS> listName = new ArrayList<>();
    // action mode
    public static boolean isInActionMode_SMS = false;
    public static ArrayList<SMS> selectionList = new ArrayList<>();
    private DatabaseGetSMS databaseGetSMS;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    public static String style;
    private String nameFeature;
    private String nameTable;
    private TextView txt_No_Data_SMS;
    private SwipeRefreshLayout swp_SMS;
    private List<SMS> smsList = new ArrayList<>();
    //private Logger logger;
    private String min_time = "";
    private String date_max = "";
    public static String name_Table_SMSHistory;
    //aviSMS
    private AVLoadingIndicatorView aviSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_history);
        style = getIntent().getStringExtra("style");
        nameTable = getIntent().getStringExtra("nameTable");
        name_Table_SMSHistory = nameTable;
        nameFeature = getIntent().getStringExtra("nameFeature");
        Log.d("style", style + "=" + nameTable);
        table = (Table) getIntent().getSerializableExtra("table_SMS");
        toolbar = findViewById(R.id.toolbar_SMS);
        // toolbar.setTitle( "  "+SMS_HISTORY);
        setTitle(toolbar, nameTable);
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        //toolbar.setVisibility(View.GONE);
        databaseGetSMS = new DatabaseGetSMS(this);
        database_last_update = new DatabaseLastUpdate(this);
        //logger =  Log4jHelper.getLogger("SMSHistory.class");
        txt_No_Data_SMS = findViewById(R.id.txt_No_Data_SMS);
        swp_SMS = findViewById(R.id.swp_SMS);
        aviSMS = findViewById(R.id.aviSMS);
        //txt_No_Data_SMS.setVisibility(View.GONE);
        // show dialog Loading...
        //getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        // recyclerView
        rcl_SMS = findViewById(R.id.rcl_SMS_History);
        rcl_SMS.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        rcl_SMS.setLayoutManager(mLayoutManager);
        get_SMS_Info();
        //databaseGetSMS.delete_AllDevice_SMS(table.getDevice_ID(),TABLE_GET_SMS);
        // adapter
        adapter_SMS = new AdapterSMSHistory(this,  list_SMS);
        rcl_SMS.setAdapter(adapter_SMS);
        adapter_SMS.notifyDataSetChanged();
        swipeRefreshLayout();
    }

    private void setTitle(Toolbar toolbar, String nameTable) {

        switch (nameTable) {
            case "SMS_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.SMS_HISTORY));
                toolbar.setLogo(R.drawable.sms_store);
                break;
            case "WhatsApp_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.WHATSAPP_HISTORY));
                toolbar.setLogo(R.drawable.whatsapp_store);
                break;
            case "Viber_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.VIBER_HISTORY));
                toolbar.setLogo(R.drawable.viber_store);
                break;
            case "Facebook_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.FACEBOOK_HISTORY));
                toolbar.setLogo(R.drawable.facebook_store);
                break;
            case "Skype_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.SKYPE_HISTORY));
                toolbar.setLogo(R.drawable.skype_store);
                break;
            case "Hangouts_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.HANGOUTS_HISTORY));
                toolbar.setLogo(R.drawable.hangout_store);
                break;
            case "BBM_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.BBM_HISTORY));
                toolbar.setLogo(R.drawable.bbm_store);
                break;
            case "LINE_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.LINE_HISTORY));
                toolbar.setLogo(R.drawable.line_store);
                break;
            case "KIK_Table":
                toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.KIK_HISTORY));
                toolbar.setLogo(R.drawable.kik_store);
                break;
        }

    }

    @SuppressLint("SetTextI18n")
    private void get_SMS_Info() {
        //if there is a network call method
        //logger.info("internet = "+isConnected(this)+"\n==================End!");
        if (APIURL.isConnected(this)) {
            aviSMS.setVisibility(View.VISIBLE);
            startAnim(aviSMS);
            new getSMS_AsyncTask().execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            int SMSCount = databaseGetSMS.getSMSCount(nameTable, table.getDevice_ID());
            if (SMSCount == 0) {
                APIDatabase.getThread(APIMethod.progressDialog);
               // txt_No_Data_SMS.setVisibility(View.VISIBLE);
                txt_No_Data_SMS.setText(MyApplication.getResourcses().getString(R.string.NoData)+"  "+ "Last update: "+getTimeItem(database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                //APIDatabase.getThread(APIMethod.progressDialog);
            } else {
                list_SMS.clear();
                list_SMS = databaseGetSMS.get_DISTINCT_SMS_Name(table.getDevice_ID(), nameTable);
                adapter_SMS = new AdapterSMSHistory(this, list_SMS);
                rcl_SMS.setAdapter(adapter_SMS);
                adapter_SMS.notifyDataSetChanged();
                txt_No_Data_SMS.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                //APIDatabase.getThread(APIMethod.progressDialog);
            }
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class getSMS_AsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("locationId", table.getDevice_ID() + "");
            // max_Date is get all the location from the min_date to the max_Date days
            //min_time = database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE,table.getDevice_ID());
            min_time = database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()).substring(0, 10) + " 00:00:00";
            String max_time = getDateNowInMaxDate();
            date_max = getTimeNow();
            Log.d("min_time", min_time + "");
            String value = "<RequestParams Device_ID=\"" + table.getDevice_ID() + "\" Start=\"0\" Length=\"1000\" Min_Date=\"" + min_time + " \" Max_Date=\"" + max_time + " \" Type=\"" + style + "\" />";
            String function = "GetSMSByDateTime";
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
                //databaseGetSMS.delete_AllDevice_SMS(table.getDevice_ID(),TABLE_GET_SMS);
                if (SMS_Json.length() != 0)
                {

                    List<Integer> listDateCheck = databaseGetSMS.getAll_SMS_ID_History_Date(table.getDevice_ID(), min_time.substring(0, 10), nameTable);
                    int save;
                    Log.d("DateCheck", nameFeature + " = " + listDateCheck.size() + "");
                    for (int i = 0; i < SMS_Json.length(); i++) {
                        Gson gson = new Gson();
                        SMS sms = gson.fromJson(String.valueOf(SMS_Json.get(i)), SMS.class);
                        //databaseGetSMS.addDevice(sms,nameTable);
                        save = 0;

                        if (listDateCheck.size() != 0) {
                            for (Integer listCheck : listDateCheck) {
                                if (sms.getID() == listCheck) {
                                    save = 1;
                                    break;
                                }
                            }
                            if (save == 0) {
                                smsList.add(sms);
                            }
                        } else {
                            smsList.add(sms);
                        }
                    }
                    if (smsList.size() != 0) {
                        databaseGetSMS.addDevice_SMS(smsList, nameTable);
                    }

                }
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, nameFeature, date_max, table.getDevice_ID());
                String max_time = database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID());
                Log.d("max_time", max_time + "");
                int SMSCount = databaseGetSMS.getSMSCount(nameTable, table.getDevice_ID());
                if (SMSCount != 0) {
                    list_SMS = databaseGetSMS.get_DISTINCT_SMS_Name(table.getDevice_ID(), nameTable);
                    // When assigning this List to another, the RecyclerView must be initialized
                    adapter_SMS = new AdapterSMSHistory(SMSHistory.this, list_SMS);
                    rcl_SMS.setAdapter(adapter_SMS);
                    adapter_SMS.notifyDataSetChanged();
                    txt_No_Data_SMS.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                    //APIDatabase.getThread(APIMethod.progressDialog);
                } else {
                    //txt_No_Data_SMS.setVisibility(View.VISIBLE);
                    txt_No_Data_SMS.setText(MyApplication.getResourcses().getString(R.string.NoData)+"  "+ "Last update: "+getTimeItem(database_last_update.getLast_Time_Update(nameFeature, TABLE_LAST_UPDATE, table.getDevice_ID()),null));
                    //Toast.makeText(SMSHistory.this, "SMS Empty", Toast.LENGTH_SHORT).show();
                }
                // get Method getThread()
                //progressDialog.dismiss();
                stopAnim(aviSMS);
                aviSMS.setVisibility(View.GONE);
                //APIDatabase.getThread(APIMethod.progressDialog);
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
        //toolbar.setVisibility(View.VISIBLE);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_mode);
        isInActionMode_SMS = true;
        adapter_SMS.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            isInActionMode_SMS = false;
            if (APIURL.isConnected(SMSHistory.this)) {

                //getProgressDialogDelete();
                //getProgressDialog("Deleting....");
                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
                new clear_SMS().execute();

            } else {
                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
                clearActionMode();
                adapter_SMS.notifyDataSetChanged();
            }


        } else if (item.getItemId() == android.R.id.home) {
            clearActionMode();
            adapter_SMS.notifyDataSetChanged();
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
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
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
    public void clearActionMode() {

        isInActionMode_SMS = false;
        //toolbar.setVisibility(View.GONE);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        //toolbar.setTitle(SMS_HISTORY);
        setTitle(toolbar, nameTable);

        selectionList.clear();
    }

    // Check out the escape without the option will always exit,
    // the opposite will cancel the selection, not exit.
    @Override
    public void onBackPressed() {

        if (isInActionMode_SMS) {
            clearActionMode();
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
