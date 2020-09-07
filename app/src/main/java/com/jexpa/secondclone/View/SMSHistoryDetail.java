/*
  ClassName: SMSHistoryDetail.java
  AppName: SecondClone
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class SMSHistoryDetail use to show detailed SMS and SMS delete function.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.jexpa.secondclone.API.APIDatabase;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Adapter.AdapterSMSDetail;
import com.jexpa.secondclone.Database.DatabaseGetSMS;
import com.jexpa.secondclone.Model.SMS;
import com.jexpa.secondclone.R;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIMethod.updateViewCounterAll;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.View.SMSHistory.style;

public class SMSHistoryDetail extends AppCompatActivity implements View.OnLongClickListener {
    // This class is used to display detailed messages :))
    private Toolbar toolbar;
    private RecyclerView rcl_SMS_Detail;
    private RecyclerView.Adapter adapter_SMS_Detail;
    public List<SMS> list_SMS_Detail = new ArrayList<>();
    // Variable test mode action
    public static boolean isInActionMode_SMS_Detail = false;
    public static boolean isInActionLong = false;
    // The SMS object array stores the checked items
    public static ArrayList<SMS> selectionList_Detail;
    private DatabaseGetSMS databaseGetSMS;
    private String SMS_Contact_Name, nameTable, nameDevice;
    private ProgressBar progressBar_SMS;
    private RecyclerView.LayoutManager mLayoutManager_Detail;
    //private Logger logger;
    // Declare the Count variable to count the number of items checked
    int counter = 0;
    // This is the value to store the temporary variable when you choose to select all item or remove all selected items.
    boolean selectAll = false;
    // progressDialog to check the load process
    boolean isLoading = false;
    boolean endLoading = false;
    boolean onCreateCroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_history_detail);
        Slidr.attach(this);
        selectionList_Detail = new ArrayList<>();
        isInActionLong = false;
        SMS_Contact_Name = getIntent().getStringExtra("sms_History");
        nameTable = getIntent().getStringExtra("nameTable");
        nameDevice = getIntent().getStringExtra("nameDevice");
        Log.d("sms_History", SMS_Contact_Name);
        toolbar = findViewById(R.id.toolbar_SMS_Detail);
        // Set a title to the toolbar.
        toolbar.setTitle(SMS_Contact_Name);
        // Set a logo to the toolbar.
        //toolbar.setLogo(R.drawable.user_small);
        toolbar.setBackgroundResource(R.drawable.custombgshopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        progressBar_SMS = findViewById(R.id.progressBar_SMS);
        progressBar_SMS.setVisibility(View.GONE);
        databaseGetSMS = new DatabaseGetSMS(this);
        //  RecyclerView
        rcl_SMS_Detail = findViewById(R.id.rcl__SMS_Detail);
        rcl_SMS_Detail.setHasFixedSize(true);
        mLayoutManager_Detail = new LinearLayoutManager(this);
        rcl_SMS_Detail.setLayoutManager(mLayoutManager_Detail);
        get_SMS_Info();
        // adapter
        adapter_SMS_Detail = new AdapterSMSDetail(this, list_SMS_Detail);
        if(list_SMS_Detail.size()>=5)
        {
            ((LinearLayoutManager) mLayoutManager_Detail).setStackFromEnd(true);
        }else {
            ((LinearLayoutManager) mLayoutManager_Detail).setStackFromEnd(false);
        }
        rcl_SMS_Detail.setAdapter(adapter_SMS_Detail);
        adapter_SMS_Detail.notifyDataSetChanged();

    }

    // This is the method to load data from the server or from SQLite to RecyclerView
    private void get_SMS_Info() {
        //if there is a network call method
        //logger.info("internet = "+isConnected(this)+"\n==================End!");
        //If we check the network using TRUE, we get the data from the server

        int i = databaseGetSMS.getSMSCount(nameTable, nameDevice);
        if (i == 0) {
            Toast.makeText(this, "Data empty", Toast.LENGTH_SHORT).show();
            APIDatabase.getThread(APIMethod.progressDialog);
        } else {
            list_SMS_Detail.clear();
            list_SMS_Detail = databaseGetSMS.getAll_SMS_Name_Offset(SMS_Contact_Name, nameTable,0);
            if (list_SMS_Detail.size() != 0) {
                // When assigning this List to another, the RecyclerView must be initialized
                adapter_SMS_Detail = new AdapterSMSDetail(this, list_SMS_Detail);
                if(list_SMS_Detail.size()>=5)
                {
                    ((LinearLayoutManager) mLayoutManager_Detail).setStackFromEnd(true);
                }else {
                    ((LinearLayoutManager) mLayoutManager_Detail).setStackFromEnd(false);
                }
                rcl_SMS_Detail.setAdapter(adapter_SMS_Detail);
                adapter_SMS_Detail.notifyDataSetChanged();
                if(list_SMS_Detail.size()>= NumberLoad)
                {
                    initScrollListener();
                }

            } else {
                APIURL.alertDialog(SMSHistoryDetail.this, "Messages", "Messages Empty!");
            }
        }
        //else If we test the network with false we get the data from SQLite
        /*else {
            int i = databaseGetSMS.getSMSCount(nameTable, nameDevice);
            if (i == 0) {
                Toast.makeText(this, "Data empty", Toast.LENGTH_SHORT).show();
                APIDatabase.getThread(APIMethod.progressDialog);
            } else {
                list_SMS_Detail.clear();
                list_SMS_Detail = databaseGetSMS.getAll_SMS_Name_Offset(SMS_Contact_Name, nameTable,0);
                if (list_SMS_Detail.size() != 0) {
                    // When assigning this List to another, the RecyclerView must be initialized
                    adapter_SMS_Detail = new AdapterSMSDetail(this, list_SMS_Detail);
                    ((LinearLayoutManager) mLayoutManager_Detail).setStackFromEnd(true);
                    rcl_SMS_Detail.setAdapter(adapter_SMS_Detail);
                    adapter_SMS_Detail.notifyDataSetChanged();
                    if(list_SMS_Detail.size()>= NumberLoad)
                    {
                        initScrollListener();
                    }
                } else {
                    APIURL.alertDialog(SMSHistoryDetail.this, "Messages", "Messages Empty!");
                }
            }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Lightning event select if exist then delete does not exist then add to RecyclerView
    public void prepareSelection(int position) {
        if (!selectionList_Detail.contains(list_SMS_Detail.get(position))) {
            selectionList_Detail.add(list_SMS_Detail.get(position));
            counter = counter + 1;
        } else {
            selectionList_Detail.remove(list_SMS_Detail.get(position));
            counter = counter - 1;
        }
        updateCounter();
        //updateViewCounter();
    }


    private void initScrollListener()
    {
        rcl_SMS_Detail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if( onCreateCroll)
                {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    if (!isLoading && (!endLoading))
                    {
                        Log.d("txx", " findFirstVisibleItemPosition() = " + linearLayoutManager.findFirstVisibleItemPosition());
                        if (linearLayoutManager != null && linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            //bottom of list!
                            isLoading = true;
                            progressBar_SMS.setVisibility(View.VISIBLE);
                            loadMore();
                        }
                    }
                }
                onCreateCroll = true;
            }

        });
    }

    private void loadMore() {
        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    int currentSize = list_SMS_Detail.size();
                    List<SMS> mDataStamp = databaseGetSMS.getAll_SMS_Name_Offset(SMS_Contact_Name, nameTable,currentSize);

                    list_SMS_Detail.addAll(0,mDataStamp);
                    if(mDataStamp.size()< NumberLoad)
                    {
                        endLoading = true;
                    }

                    adapter_SMS_Detail.notifyItemRangeInserted(0, mDataStamp.size());
                    Log.d("currentSize", "currentSize = "+mDataStamp.size()/2);
                    Log.d("currentSize", "currentSize = "+ list_SMS_Detail.get(mDataStamp.size()).getText_Message());
                    isLoading = false;
                    progressBar_SMS.setVisibility(View.GONE);
                }
            }, 2000);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    public void updateCounter() {
        int counter = selectionList_Detail.size();
        updateViewCounterAll(toolbar, counter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (APIMethod.progressDialog != null && APIMethod.progressDialog.isShowing()) {
            APIMethod.progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_delete) {
            if (APIURL.isConnected(SMSHistoryDetail.this)) {
                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
                new clear_SMS().execute();
            }
            else {
                // If there is no internet we do not let users delete SMS :)
                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
                clearActionMode();
                adapter_SMS_Detail.notifyDataSetChanged();
            }
        }
        else if(item.getItemId() ==  R.id.item_select_all)
        {

            Log.d("TestselectAll", "TestselectAll = "+selectAll+"");
            if(!selectAll)
            {
                selectionList_Detail.clear();
                selectionList_Detail.addAll(list_SMS_Detail);
                for (int i = 0; i< list_SMS_Detail.size();i++)
                {
                    AdapterSMSDetail.itemStateArray.put(i, true);
                }
                updateCounter();
                adapter_SMS_Detail.notifyDataSetChanged();
                selectAll = true;
            }
            else {
                for (int i = 0; i< list_SMS_Detail.size();i++)
                {
                    AdapterSMSDetail.itemStateArray.put(i, false);
                }
                selectionList_Detail.clear();
                //selectionList_Detail.addAll(list_SMS_Detail);
                updateCounter();
                adapter_SMS_Detail.notifyDataSetChanged();
                selectAll = false;
            }
        }
        else if (item.getItemId() == android.R.id.home) {
            if(isInActionMode_SMS_Detail)
            {
                clearActionMode();
                adapter_SMS_Detail.notifyDataSetChanged();
            }
            else {
                super.onBackPressed();
            }
        }
        return true;
    }

    //Method clear to sever
    @SuppressLint("StaticFieldLeak")
    private class clear_SMS extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            StringBuilder listID = new StringBuilder();
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < selectionList_Detail.size(); i++) {
                if (i != selectionList_Detail.size() - 1) {
                    listID.append(selectionList_Detail.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList_Detail.get(i).getID());
                }
            }
            String value = "<RequestParams Device_ID=\"" + selectionList_Detail.get(0).getDevice_ID() + "\" List_ID=\"" + listID + "\"  Chat_Type=\"" + style + "\" />";
            String function = "DeleteMultiChatDataLog";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {
            APIURL.deviceObject(s);
            Log.d("sms", APIURL.bodyLogin.getDescription());
            if (APIURL.bodyLogin.getCode().startsWith("S") || APIURL.bodyLogin.getCode().startsWith("s")) {
                ((AdapterSMSDetail) adapter_SMS_Detail).removeData(selectionList_Detail);
                clearDataSQLite(selectionList_Detail);
                clearActionMode();
            } else {
                Toast.makeText(SMSHistoryDetail.this, APIURL.bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
            APIMethod.progressDialog.dismiss();
        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<SMS> selectionList) {
        for (SMS sms : selectionList) {
            databaseGetSMS.deleteSMS_ID(sms, nameTable);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {

        if(isInActionMode_SMS_Detail)
        {
            selectionList_Detail.clear();
            isInActionLong = false;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_main);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            toolbar.setTitle(SMS_Contact_Name);
            //toolbar.setLogo(R.drawable.user_small);
            AdapterSMSDetail.itemStateArray.clear();
            AdapterSMSDetail.itemStateArray = new SparseBooleanArray();
            isInActionMode_SMS_Detail = false;
        }
    }

    // Check out the escape without the option will always exit,
    // the opposite will cancel the selection, not exit.
    @Override
    public void onBackPressed() {
        if (isInActionMode_SMS_Detail) {
            isInActionMode_SMS_Detail = false;
            clearActionMode();
            adapter_SMS_Detail.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        toolbar.getMenu().clear();
        toolbar.setTitle(" \t" + "0" + " item selected");
        isInActionLong = true;
        toolbar.setLogo(null);
        toolbar.inflateMenu(R.menu.menu_action_delete);
        isInActionMode_SMS_Detail = true;
        adapter_SMS_Detail.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        }
        return true;
    }
}

