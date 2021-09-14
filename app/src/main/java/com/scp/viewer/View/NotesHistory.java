/*
  ClassName: NotesHistory.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class NotesHistory used to display the Notes history list from the sever on the RecyclerView of the class.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.API.APIURL;
import com.scp.viewer.Adapter.AdapterNoteHistory;
import com.scp.viewer.Database.DatabaseNotes;
import com.scp.viewer.Database.DatabaseLastUpdate;
import com.scp.viewer.Model.Notes;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.r0adkll.slidr.Slidr;
//import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.APIDatabase.getThread;
import static com.scp.viewer.API.APIMethod.PostJsonClearDataToServer;
import static com.scp.viewer.API.APIMethod.alertDialogDeleteItems;
import static com.scp.viewer.API.APIMethod.getProgressDialog;
import static com.scp.viewer.API.APIMethod.updateViewCounterAll;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.Global.POST_CLEAR_MULTI_NOTE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_NOTES;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class NotesHistory extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    List<Notes> mData = new ArrayList<>();
    List<Notes> notesListAdd = new ArrayList<>();
    // action mode
    public static boolean isInActionMode;
    public static ArrayList<Notes> selectionList;
    private DatabaseNotes database_notes;
    private DatabaseLastUpdate database_last_update;
    private Table table;
    private TextView txt_No_Data_Notes;
    //private Logger logger;
    private String max_Date = "";
    private String min_Time = "";
    boolean selectAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_history);
        Slidr.attach(this);
        selectionList = new ArrayList<>();
        isInActionMode = false;
        toolbar = findViewById(R.id.toolbar_Notes_History);
        toolbar.setTitle("  " + MyApplication.getResourcses().getString(R.string.NOTES_HISTORY));
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        database_notes = new DatabaseNotes(this);
        database_last_update = new DatabaseLastUpdate(this);
        //logger =  Log4jHelper.getLogger("NotesHistory.class");
        table = (Table) getIntent().getSerializableExtra("tableNotes");
        // show dialog Loading...
        getProgressDialog(MyApplication.getResourcses().getString(R.string.Loading)+"...",this);
        txt_No_Data_Notes = findViewById(R.id.txt_No_Data_Notes);
        txt_No_Data_Notes.setVisibility(View.GONE);
        // recyclerView
        mRecyclerView = findViewById(R.id.rcl_Notes_History);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getNoteInfo();

        // adapter
        mAdapter = new AdapterNoteHistory(this, (ArrayList<Notes>) mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This is a method to get data from the server to the device and display it in Recyclerview.
     * If there is no internet, get data from SQLite stored on the device and display it in Recyclerview.
     */
    private void getNoteInfo() {
        //if there is a network call method
        //logger.debug("internet = "+isConnected(this)+"\n==================End!");
        if (isConnected(this)) {
            new ContactAsyncTask().execute();
        } else {
            Toast.makeText(this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
            //int i= databaseDevice.getDeviceCount();
            int i = database_notes.get_NotesCount_DeviceID(table.getDevice_Identifier());
            if (i == 0) {
                txt_No_Data_Notes.setVisibility(View.VISIBLE);
                txt_No_Data_Notes.setText(MyApplication.getResourcses().getString(R.string.NoData));
                getThread(APIMethod.progressDialog);
            } else {
                mData.clear();
                mData = database_notes.getAll_Notes_ID_History(table.getID());
                mAdapter = new AdapterNoteHistory(this, (ArrayList<Notes>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                getThread(APIMethod.progressDialog);
            }
        }
    }

    // location get method from sever
    @SuppressLint("StaticFieldLeak")
    private class ContactAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            // max_Date is get all the location from the min_date to the max_Date days
            min_Time = database_last_update.getLast_Time_Update(COLUMN_LAST_NOTES, TABLE_LAST_UPDATE, table.getDevice_Identifier()).substring(0, 10) + " 00:00:00";
            max_Date = getTimeNow().substring(0, 10) + " 23:59:59";
            Log.d("min_time", min_Time + "");
            String value = "<RequestParams Device_ID=\"" + table.getDevice_Identifier() + "\" Start=\"0\" Length=\"1000\" Min_Date=\"" + min_Time + "\" Max_Date=\"" + max_Date + "\"  />";
            String function = "GetNotes";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray GPSJson = jsonObj.getJSONArray("Table");
                if (GPSJson.length() != 0) {
                    List<Long> listDateCheck = database_notes.getAll_Location_ID_History_Date(table.getDevice_Identifier(), min_Time.substring(0, 10));
                    int save;
                    for (int i = 0; i < GPSJson.length(); i++) {
                        Gson gson = new Gson();
                        Notes notes = gson.fromJson(String.valueOf(GPSJson.get(i)), Notes.class);
                        mAdapter.notifyDataSetChanged();
                        Log.d("notes", notes.getRowIndex() + "");
                        //database_notes.addDevice_Application(notes);
                        save = 0;
                        if (listDateCheck.size() != 0) {
                            for (long listCheck : listDateCheck) {
                                if (notes.getID() == listCheck) {
                                    save = 1;
                                    break;
                                }
                            }
                            if (save == 0) {
                                notesListAdd.add(notes);
                            }
                        } else {
                            notesListAdd.add(notes);
                        }
                    }
                    if (notesListAdd.size() != 0) {
                        database_notes.addDevice_Notes(notesListAdd);
                    }
                }
                mData.clear();
                mData = database_notes.getAll_Notes_ID_History(table.getID());
                mAdapter = new AdapterNoteHistory(NotesHistory.this, (ArrayList<Notes>) mData);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if (mData.size() == 0) {
                    txt_No_Data_Notes.setVisibility(View.VISIBLE);
                    txt_No_Data_Notes.setText(MyApplication.getResourcses().getString(R.string.NoData));
                }
                database_last_update.update_Last_Time_Get_Update(TABLE_LAST_UPDATE, COLUMN_LAST_NOTES, max_Date, table.getDevice_Identifier());
                String min_Time1 = database_last_update.getLast_Time_Update(COLUMN_LAST_NOTES, TABLE_LAST_UPDATE, table.getDevice_Identifier());
                Log.d("min_time1", min_Time1 + "");
                // get Method getThread()
                //progressDialog.dismiss();
                getThread(APIMethod.progressDialog);
            } catch (JSONException e) {
                //MyApplication.getInstance().trackException(e);
                e.printStackTrace();
                //logger.error("\n\n\n\tContactAsyncTask =="+ e+"\n================End");
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
            if (isConnected(NotesHistory.this)) {
//                getProgressDialog(MyApplication.getResourcses().getString(R.string.delete)+"...",this);
//                new clear_Notes().execute();

                alertDialogDeleteItems(NotesHistory.this,
                        getApplicationContext().getResources().getString(R.string.question_Select),
                        new clear_Notes());

            } else {
                Toast.makeText(this, getString(R.string.NoData)+"", Toast.LENGTH_SHORT).show();
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


    @SuppressLint("StaticFieldLeak")
    private class clear_Notes extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
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
            String function = POST_CLEAR_MULTI_NOTE;*/
            return PostJsonClearDataToServer(table.getDevice_Identifier(), listID, POST_CLEAR_MULTI_NOTE);

        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getResultId().equals("1")) {
                ((AdapterNoteHistory) mAdapter).removeData(selectionList);
                clearDataSQLite(selectionList);
                clearActionMode();
            } else {
                Toast.makeText(NotesHistory.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
                clearActionMode();
            }
            // get Method getThread()
            //getThread(progressDialog);
            APIMethod.progressDialog.dismiss();

        }
    }

    // delete on SQLite
    public void clearDataSQLite(ArrayList<Notes> selectionList) {
        for (Notes notes : selectionList) {
            database_notes.delete_Contact_History(notes);
        }
    }

    // back toolbar home, clear List selectionList
    public void clearActionMode() {
        if(isInActionMode)
        {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_main);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(null);
            }
            toolbar.setTitle(MyApplication.getResourcses().getString(R.string.NOTES_HISTORY));
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

}
