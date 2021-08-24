/*
  ClassName: ContactHistory.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class ContactHistory used to display the phone's history of phone calls from the sever on display on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
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
import com.scp.viewer.Adapter.AdapterContactHistory;
import com.scp.viewer.Database.DatabaseContact;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Model.Contact;
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
import static com.scp.viewer.API.Global.CLIPBOARD_PULL_ROW;
import static com.scp.viewer.API.Global.CONTACT_PULL_ROW;
import static com.scp.viewer.API.Global.CONTACT_TOTAL;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.NEW_ROW;
import static com.scp.viewer.API.Global.NumberLoad;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_CONTACT;
import static com.scp.viewer.API.Global._TOTAL;
import static com.scp.viewer.API.Global.time_Refresh_Device;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CONTACT;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class ContactHistory extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private AdapterContactHistory mAdapter;
    List<Contact> mData = new ArrayList<>();
    List<Contact> contactListAdd = new ArrayList<>();
    // action mode
    public static boolean isInActionMode;
    public static ArrayList<Contact> selectionList;
    private DatabaseContact database_contact;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_Contact,txt_Total_Data;
    private ProgressBar progressBar_Contacts;
    private LinearLayout lnl_Total;
    private SwipeRefreshLayout swp_Contact;
    private boolean checkLoadMore = false;
    private boolean checkRefresh = false;
    boolean isLoading = false;
    boolean endLoading = false;
    private int currentSize = 0;
    // This is the value to store the temporary variable when you choose to select all item or remove all selected items.
    boolean selectAll = false;
    private AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_history);
        Slidr.attach(this);
        setID();
        selectionList = new ArrayList<>();
        isInActionMode = false;
        database_contact = new DatabaseContact(this);
        database_last_update = new DatabaseLastUpdate(this);
//        logger =  Log4jHelper.getLogger("ContactHistory.class");
        table = (Table) getIntent().getSerializableExtra("tableContact");
        // show dialog Loading...
        getContactsInfo();
        swipeRefreshLayout();
    }

    private void setID()
    {
        toolbar = findViewById(R.id.toolbar_Contact);
        toolbar.setTitle(MyApplication.getResourcses().getString(R.string.CONTACT_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        avLoadingIndicatorView = findViewById(R.id.avi);
        lnl_Total = findViewById(R.id.lnl_Total);
        lnl_Total.setVisibility(View.INVISIBLE);
        txt_No_Data_Contact = findViewById(R.id.txt_No_Data_Contacts);
        txt_Total_Data = findViewById(R.id.txt_Total_Data);
        progressBar_Contacts = findViewById(R.id.progressBar_Contacts);
        progressBar_Contacts.setVisibility(View.GONE);
        swp_Contact = findViewById(R.id.swp_Contact);
        //txt_No_Data_Contact.setVisibility(View.GONE);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_Contact_History);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setItemAnimator(null);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * This is a method to get data from the server to the device and display it in Recyclerview.
     * If there is no internet, get data from SQLite stored on the device and display it in Recyclerview.
     */
    @SuppressLint("SetTextI18n")
    private void getContactsInfo() {
        //if there is a network call method
        //logger.debug("internet = "+isConnected(this)+"\n==================End!");
        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new contactAsyncTask(0).execute();
        }
        else {
            lnl_Total.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_contact.get_ContactCount_DeviceID(table.getDevice_Identifier());
            if (i == 0) {
                //txt_No_Data_Contact.setVisibility(View.VISIBLE);
                txt_No_Data_Contact.setText(MyApplication.getResourcses().getString(R.string.NoData));
                txt_Total_Data.setText("0");
            } else {
                mData.clear();
                mData = database_contact.getAll_Contact_ID_History(table.getID(),0);
                mAdapter = new AdapterContactHistory(this, (ArrayList<Contact>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if(mData.size() >= NumberLoad)
                {
                    initScrollListener();
                }
                txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), CONTACT_TOTAL + table.getDevice_Identifier())+"");
                txt_No_Data_Contact.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CONTACT, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
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
                        progressBar_Contacts.setVisibility(View.VISIBLE);
                       // loadMore();
                        if(!checkRefresh)
                        {
                            loadMore();
                        }
                        else {
                            isLoading = false;
                            endLoading = false;
                            progressBar_Contacts.setVisibility(View.GONE);
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
            //progressBar_Locations.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    currentSize =  mData.size();

                    if(isConnected(getApplicationContext()))
                    {
                        checkLoadMore = true;
                        // Here is the total item value contact of device current has on CPanel
                        long totalContact = getSharedPreferLong(getApplicationContext(), CONTACT_TOTAL+ table.getDevice_Identifier());
                        new contactAsyncTask(currentSize+1).execute();
                        if((mData.size()+1) >= totalContact)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;

                    }
                    else {
                        List<Contact> mDataStamp = database_contact.getAll_Contact_ID_History(table.getID(),currentSize);
                        // Here is the total item value contact of device current has on Cpanel
                        int insertIndex = mData.size();
                        mData.addAll(insertIndex,mDataStamp);
                        mAdapter.notifyItemRangeInserted(insertIndex-1,mDataStamp.size() );
                        if(mDataStamp.size() < NumberLoad)
                        {
                            endLoading = true;
                        }
                        //mAdapter.notifyDataSetChanged();
                        //progressBar_Locations.setVisibility(View.GONE);
                        isLoading = false;
                        progressBar_Contacts.setVisibility(View.GONE);
                    }
                }
            }, 100);

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.getFilter().filter(query);
        mAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        mAdapter.notifyDataSetChanged();
        return false;
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    public class contactAsyncTask extends AsyncTask<String, Void, String> {

        long startIndex;

        public contactAsyncTask(long startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected String doInBackground(String... strings) {

            return GetJsonFeature(table,this.startIndex, "GetContacts");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray GPSJson = jsonObj.getJSONArray("Table");
                JSONArray GPSJsonTable1 = jsonObj.getJSONArray("Table1");
                setToTalLog(GPSJsonTable1, CONTACT_TOTAL + table.getDevice_Identifier(), getApplicationContext());
                setSharedPreferLong(getApplicationContext(), CONTACT_PULL_ROW +_TOTAL+ table.getDevice_Identifier() + NEW_ROW, 0);

                if (GPSJson.length() != 0) {

                    //List<Integer> listDateCheck = database_contact.getAll_Contact_ID_History_Date(table.getDevice_ID());
                    //int save;
                    //Log.d("DateCheck", "ContactHistory = " + listDateCheck.size());

                    for (int i = 0; i < GPSJson.length(); i++) {

                        Gson gson = new Gson();
                        Contact contact = gson.fromJson(String.valueOf(GPSJson.get(i)), Contact.class);
                        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                        contact.setColor(randomAndroidColor);
                        contactListAdd.add(contact);
                        Log.d("ContactHistory"," Add Contact = "+  contact.getContact_Name());
                    }
                    if (contactListAdd.size() != 0) {
                        database_contact.addDevice_Contact(contactListAdd);
                    }
                }
                //mData.clear();
                Log.d("ContactHistory"," currentSize Contact = "+  currentSize+ " checkLoadMore = "+ checkLoadMore);
                List<Contact> mDataTamp = database_contact.getAll_Contact_ID_History(table.getID(),currentSize);
                Log.d("ContactHistory"," currentSize "+ mDataTamp.size());
                //mData.addAll(mDataTamp);


                if(checkLoadMore)
                {
                    int insertIndex = mData.size();

                    Log.d("checkdata"," MData Contact = "+ mDataTamp.size());
                    if(mDataTamp.size() >= 20)
                    {
                        mData.addAll(insertIndex, mDataTamp);
                    }else {
                        mData.addAll(insertIndex-1, mDataTamp);
                    }
                    mAdapter.notifyItemRangeInserted(insertIndex-1,mDataTamp.size() );
                    Log.d("ContactHistory"," checkLoadMore Contact = "+ true);
                    progressBar_Contacts.setVisibility(View.GONE);
                }
                else {
                    lnl_Total.setVisibility(View.VISIBLE);
                    Log.d("ContactHistory"," checkLoadMore Contact = "+ false);
                    mData.addAll(mDataTamp);
                    if(mData.size() >= NumberLoad)
                    {
                        initScrollListener();
                    }
                    mAdapter = new AdapterContactHistory(ContactHistory.this, (ArrayList<Contact>) mData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                String Date_max = getTimeNow();
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_CONTACT, Date_max, table.getDevice_Identifier());
                String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_CONTACT, TABLE_LAST_UPDATE, table.getDevice_Identifier());
                Log.d("min_time1", min_Time1 + "");
                if (mData.size() == 0) {
                    txt_No_Data_Contact.setText(MyApplication.getResourcses().getString(R.string.NoData));
                    txt_Total_Data.setText("0");
                }else {
                    txt_No_Data_Contact.setText("Last update: "+getTimeItem(database_last_update.getLast_Time_Update(COLUMN_LAST_CONTACT, TABLE_LAST_UPDATE, table.getDevice_Identifier()),null));
                    txt_Total_Data.setText(getSharedPreferLong(getApplicationContext(), CONTACT_TOTAL + table.getDevice_Identifier())+"");
                }
                //getThread(APIMethod.progressDialog);
                stopAnim(avLoadingIndicatorView);
                avLoadingIndicatorView.setVisibility(View.GONE);
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_searchview, menu);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_SearchView)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search name contact");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    public void prepareToolbar(Contact contact, int position) {

        // prepare action mode
        toolbar.getMenu().clear();
        //toolbar.inflateMenu(R.menu.menu_action_mode);
        toolbar.inflateMenu(R.menu.menu_action_delete);
        isInActionMode = true;
        mAdapter.notifyItemChanged(position);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);
        }

        prepareSelection(contact,position);
    }

    public void prepareSelection(Contact contact,int position) {
        long contactID = contact.getID();
        int add = 0;
       for (Contact row : selectionList){
           if(row.getID()==(contactID)){
               add = 1;
           }
       }
        if (add == 0) {
            selectionList.add(contact);

        } else {
            selectionList.remove(contact);
        }
        Log.i("zseach",contact.getContact_Name());

       /* if (!selectionList.contains(mData.get(position))) {
            selectionList.add(mData.get(position));
        } else {
            selectionList.remove(mData.get(position));
        }*/

        updateViewCounter();
    }

    private void updateViewCounter() {
        int counter = selectionList.size();
        updateViewCounterAll(toolbar, counter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_delete) {
            if (isConnected(ContactHistory.this)) {
                //                ((AdapterHistoryLocation) mAdapter).removeData(selectionList);
                //getProgressDialogDelete();
//                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
//                new clear_Contact().execute();

                alertDialogDeleteItems(ContactHistory.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new clear_Contact());

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
        /*if (item.getItemId() == R.id.item_SearchView) {
            return true;
        }*/
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class clear_Contact extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("ContactId", table.getDevice_Identifier() + "");
            StringBuilder listID = new StringBuilder();
            //Toast.makeText(HistoryLocation.this, selectionList.get(0).getID()+"", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < selectionList.size(); i++) {
                if (i != selectionList.size() - 1) {
                    listID.append(selectionList.get(i).getID()).append(",");
                } else {

                    listID.append(selectionList.get(i).getID());
                }
            }
           /* String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" List_ID=\"" + listID + "\" />";
            String function = POST_CLEAR_MULTI_CONTACT;*/
            return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_CONTACT);
        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getResultId().equals("1")) {
                ((AdapterContactHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                Toast.makeText(ContactHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
            // get Method getThread()
            //getThread(progressDialog);
            APIMethod.progressDialog.dismiss();
        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<Contact> selectionList) {
        for (Contact contact : selectionList) {
            database_contact.delete_Contact_History(contact);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {

        if(isInActionMode)
        {
            isInActionMode = false;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_action_searchview);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            toolbar.setTitle("  "+MyApplication.getResourcses().getString(R.string.CONTACT_HISTORY));
            selectionList.clear();
            supportInvalidateOptionsMenu();
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
        swp_Contact.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                endLoading = false;
                if (isConnected(getApplicationContext()))
                {
                    checkRefresh = true;
                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
                        //contactListAdd.clear();
                        clearActionMode();
                        //mData.clear();
//                        mRecyclerView.getRecycledViewPool().clear();
//                        mAdapter.notifyDataSetChanged();
                        //Method for refresh recycle view
                        if (!mData.isEmpty())
                        {
                            mData.clear(); //The list for update recycle view
                            mAdapter.notifyDataSetChanged();
                        }
                        checkLoadMore = false;
                        currentSize = 0;
                        new contactAsyncTask(0).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_Contact.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Device = calendar1.getTimeInMillis();
                            }
                        }, 1000);

                    } else {
                        swp_Contact.setRefreshing(false);
                    }
                } else {
                    swp_Contact.setRefreshing(false);
                    noInternet(ContactHistory.this);
                }
            }
        });
    }
}
