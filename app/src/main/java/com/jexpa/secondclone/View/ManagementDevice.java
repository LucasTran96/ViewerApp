/*
 ClassName: ManagementDevice.java
 AppName: SecondClone
 Created by Lucas Walker (lucas.walker@jexpa.com)
 Created Date: 2018-06-05
 Description: Class ManagementDevice used to display devices belonging to this user, referred from the sever displayed to the RecyclerView of the class.
 History:
 2018-10-28 -- Add renew function
 Copyright © 2018 Jexpa LLC. All rights reserved.
 */


package com.jexpa.secondclone.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jexpa.secondclone.Adapter.AdapterDevice;
import com.jexpa.secondclone.Database.DatabaseDevice;
import com.jexpa.secondclone.Database.DatabaseLastUpdate;
import com.jexpa.secondclone.Database.DatabaseUser;
import com.jexpa.secondclone.Database.DatabaseUserInfo;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.Model.AccountInFo;
import com.jexpa.secondclone.Model.Body;
import com.jexpa.secondclone.Model.LastTimeGetUpdate;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.Model.User;
import com.jexpa.secondclone.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.getFormatDateAM;
import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIMethod.startAnim;
import static com.jexpa.secondclone.API.APIMethod.stopAnim;
import static com.jexpa.secondclone.API.APIMethod.subDate;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.fromJson;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.Global.DATE;
import static com.jexpa.secondclone.API.Global.DEFAULT_COPYRIGHT;
import static com.jexpa.secondclone.API.Global.DEFAULT_LINK_ABOUTUS;
import static com.jexpa.secondclone.API.Global.DEFAULT_LINK_RENEW;
import static com.jexpa.secondclone.API.Global.DEFAULT_LOGO_IMAGE_PATH;
import static com.jexpa.secondclone.API.Global.DEFAULT_PRODUCT_NAME;
import static com.jexpa.secondclone.API.Global.DEFAULT_VERSION_NAME;

import static com.jexpa.secondclone.Database.DatabaseHelper.DATABASE_NAME;
import static com.jexpa.secondclone.Database.Entity.ApplicationUsageEntity.DATABASE_NAME_APPLICATION_HISTORY;
import static com.jexpa.secondclone.Database.Entity.CallHistoryEntity.DATABASE_NAME_CALL_HISTORY;
import static com.jexpa.secondclone.Database.Entity.ContactEntity.DATABASE_NAME_CONTACT_HISTORY;
import static com.jexpa.secondclone.Database.Entity.GPSEntity.DATABASE_NAME_GETLOCATION;
import static com.jexpa.secondclone.Database.Entity.DeviceEntity.DATABASE_NAME_GET_SETTING;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.DATABASE_NAME_LAST_UPDATE;
import static com.jexpa.secondclone.Database.Entity.NotesEntity.DATABASE_NAME_NOTE_HISTORY;
import static com.jexpa.secondclone.Database.Entity.PhoneCallRecordEntity.DATABASE_NAME_PHONECALLRECORD_HISTORY;
import static com.jexpa.secondclone.Database.Entity.PhotoHistoryEntity.DATABASE_NAME_PHOTO_HISTORY;
import static com.jexpa.secondclone.Database.Entity.SMSEntity.DATABASE_NAME_SMS;
import static com.jexpa.secondclone.Database.Entity.URLEntity.DATABASE_NAME_URL_HISTORY;
import static com.jexpa.secondclone.Database.Entity.UserEntity.DATABASE_NAME_USER_INFO;
import static com.jexpa.secondclone.API.Global.ON_BACK;
import static com.jexpa.secondclone.API.Global.TIME_DEFAULT;
import static com.jexpa.secondclone.Database.Entity.ManagementDeviceEntity.DATABASE_NAME_DEVICE;

public class ManagementDevice extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rcvDevice;
    //private List<ModelDevice> deviceList;
    private DatabaseUser databaseUser;
    private DatabaseDevice databaseDevice;
    private DatabaseLastUpdate database_last_update;
    private DatabaseUserInfo databaseUserInfo;
    private AdapterDevice adapterDevice;
    private Button btnLogOut, btn_About;
    private LinearLayout ln_Renew, ln_Number_Device;
    private TextView txtNoDevice;
    private TextView txtUser;
    private TextView txtTime_Expiry;
    private TextView txt_NumberDevice;
    public static String android_id;
    private AccountInFo accountInFo ;
    private String time_Expiry[];
    //private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Table> tableList;
    private List<AccountInFo> accountInFoList;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 99;
    public static int packageID;
    //aviManagement
    private AVLoadingIndicatorView avLoadingIndicatorView;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_device);
        accountInFo = new AccountInFo();
        tableList = new ArrayList<>();
        accountInFoList = new ArrayList<>();
        setId();
        ln_Renew.setVisibility(View.GONE);
        if (ActivityCompat.checkSelfPermission(ManagementDevice.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ManagementDevice.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
        }
        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        Log.d("device_id", android_id + "");
        databaseUserInfo = new DatabaseUserInfo(this);
        databaseUser = new DatabaseUser(this);
        database_last_update = new DatabaseLastUpdate(this);
        databaseDevice = new DatabaseDevice(this);
        //getProgressDialog();
        setEvent();
        getAccountInfo();
        getDeviceInfo();
        ON_BACK = 1;
    }

    private void setHideReNew(String stringExpiry) {

        try {

            long sub_Date = subDate(stringExpiry);
            if (sub_Date / (DATE) > -6) {
                // show button renew
                ln_Renew.setVisibility(View.VISIBLE);

            } else {

                // If before 5 days will hide the button renew
                //hide button renew
                ln_Renew.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.getMessage();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
            }
        }
    }

    // method of obtaining account information
    @SuppressLint("SetTextI18n")
    private void getAccountInfo() {
        if (isConnected(this)) {
//            avLoadingIndicatorView.setVisibility(View.VISIBLE);
//            startAnim(avLoadingIndicatorView);
            new accountAsyncTask().execute();
        } else {
            // int i: Count objects in the User table.
            // i == : the user table is empty.
            int i = databaseUserInfo.getUserInfoCount();
            if (i == 0) {
                //getToast(ManagementDevice.this,"Database empty!");
                //getThread(progressDialog);
            } else {
                accountInFoList.clear();
                accountInFoList = databaseUserInfo.getAllUserInfo();
                accountInFo = accountInFoList.get(0);
                txtUser.setText(accountInFo.getNick_Name());
                time_Expiry = accountInFo.getExpiry_Date().split(" ");
                Log.d("time_Expiry", accountInFo.getExpiry_Date() + "");
                Log.d("time_Expiry", " time_Expiry = "+time_Expiry + "");
                // format date of Expiry_Date
                txtTime_Expiry.setText(" "+getFormatDateAM(accountInFo.getExpiry_Date()));
//                Log.d("time_Expiry", date_Expiry + "");
                setHideReNew(accountInFo.getExpiry_Date());
                packageID = Integer.parseInt(accountInFo.getPackage_ID());
                //getThread(progressDialog);


            }
        }
    }

    // the method of obtaining the device number of this account
    @SuppressLint("SetTextI18n")
    private void getDeviceInfo() {
        //if there is a network call method
        if (isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            new deviceAsyncTask().execute();
        } else {
            int i = databaseDevice.getDeviceCount();
              if (i == 0) {
                txtNoDevice.setVisibility(View.VISIBLE);

            } else {
                tableList.clear();
                tableList = databaseDevice.getAllDevice();
                if (tableList.size() == 1 || tableList.size() == 0) {
                    ln_Number_Device.setVisibility(View.GONE);
                } else {
                    ln_Number_Device.setVisibility(View.VISIBLE);
                    txt_NumberDevice.setText(getString(R.string.YouHaveLinkedDevice,tableList.size()));
                }
                txtNoDevice.setVisibility(View.GONE);
                adapterDevice = new AdapterDevice(tableList, R.layout.item_rcv_management_device, ManagementDevice.this);
                rcvDevice.setAdapter(adapterDevice);
                adapterDevice.notifyDataSetChanged();

            }
        }
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    private void setId() {
        rcvDevice = findViewById(R.id.rclDevice);
        btnLogOut = findViewById(R.id.btnLogOut);
        btn_About = findViewById(R.id.btn_About);
        ln_Renew = findViewById(R.id.ln_Renew);
        ln_Number_Device = findViewById(R.id.ln_Number_Device);
        //
        ln_Number_Device.setVisibility(View.GONE);
        //swipeRefreshLayout = findViewById(R.id.SwipeRefresh);
        txtUser = findViewById(R.id.txt_User_Name_Device);
        txtNoDevice = findViewById(R.id.txtNoDevice);
        txtNoDevice.setVisibility(View.GONE);
        txtTime_Expiry = findViewById(R.id.txt_Time_User_Device);
        txt_NumberDevice = findViewById(R.id.txtNumberDevice);
        TextView txt_version_name = findViewById(R.id.txt_version_name);
        TextView txt_nameAPP_Management = findViewById(R.id.txt_nameAPP_Management);
        ImageView img_Logo_Management = findViewById(R.id.img_Logo_Management);
        TextView txt_Copyright_launch = findViewById(R.id.txt_Copyright_launch);
        Picasso.with(getApplicationContext()).load(DEFAULT_LOGO_IMAGE_PATH).error(R.drawable.no_image).into(img_Logo_Management);
        txt_version_name.setText(" " + DEFAULT_VERSION_NAME);
        txt_nameAPP_Management.setText(DEFAULT_PRODUCT_NAME);
        txt_Copyright_launch.setText(DEFAULT_COPYRIGHT);
    }

    private void setEvent() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapterDevice = new AdapterDevice(tableList, R.layout.item_rcv_management_device, ManagementDevice.this);
        rcvDevice.setLayoutManager(layoutManager);
        avLoadingIndicatorView = findViewById(R.id.aviManagement);
        rcvDevice.setHasFixedSize(true);
        rcvDevice.setAdapter(adapterDevice);
        adapterDevice.notifyDataSetChanged();
        btnLogOut.setOnClickListener(this);
        ln_Renew.setOnClickListener(this);
        btn_About.setOnClickListener(this);
        //swipeRefreshLayout();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();//this.deleteDatabase(DATABASE_NAME);
        //clearApplicationData();
        // Use intent to open link to website of payment.
        // Use intent to open the link to the company's About Us website.

        if (i == R.id.btnLogOut) {
//            this.deleteDatabase(DATABASE_NAME);//
            databaseUser.updateUser(new User(1, "123", "123"));
//            File fileNamePathDelete = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DEFAULT_PRODUCT_NAME);
//            deleteRecursive(fileNamePathDelete);
//            fileNamePathDelete.delete();
            Intent intent = new Intent(getApplicationContext(), Authentication.class);
            startActivity(intent);
            finish();
        } else if (i == R.id.ln_Renew) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(startOpenWebPage(DEFAULT_LINK_RENEW)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (i == R.id.btn_About) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(startOpenWebPage(DEFAULT_LINK_ABOUTUS)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        fileOrDirectory.delete();
    }

    public static String startOpenWebPage(String url) {

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;

    }

    @SuppressLint("StaticFieldLeak")
    private class accountAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            User user = databaseUser.getNote(1);
            String email = user.getEmail();
            String password = user.getPassword();
            String value = "<RequestParams Password=\"" + password + "\" Email=\"" + email + "\"/>";
            String function = "GetAccountInfo";
            return APIURL.POST(value, function);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                Log.d("zzzz", s+" onPostExecute");
                JSONObject jsonObject = new JSONObject(s);
                Body body = fromJson(jsonObject);
                Gson gson = new Gson();
                assert body != null;
                AccountInFo account = gson.fromJson(body.getData(), AccountInFo.class);
                for (AccountInFo a : databaseUserInfo.getAllUserInfo()) {
                    databaseUserInfo.deleteUserInfo(a);
                }
                if(account.getLogin_Name() != null && !account.getLogin_Name().isEmpty())
                {
                    databaseUserInfo.addUserInfo(account);
                    txtUser.setText(account.getNick_Name());
                    time_Expiry = account.getExpiry_Date().split(" ");
                    // format date of Expiry_Date
                    txtTime_Expiry.setText(" "+getFormatDateAM(account.getExpiry_Date()));
                    //Log.d("time_Expiry", date_Expiry + "");
                    //txtTime_Expiry.setText(" "+account.getExpiry_Date());
                    packageID = Integer.parseInt(account.getPackage_ID());
                    setHideReNew(account.getExpiry_Date());
                    Log.d("getPackage_ID", account.getPackage_ID());
                }else {
                    Log.d("getPackage_ID", "AccountInFo = null");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //@SuppressLint("StaticFieldLeak")
    private class deviceAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            User user = databaseUser.getNote(1);
            String email = user.getEmail();
            String password = user.getPassword();
            String value = "<RequestParams Password=\"" + password + "\" UserName=\"" + email + "\"/>";
            String function = "GetDeviceInfo";
            return APIURL.POST(value, function);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                //Log
                deviceObject(s);
                Log.d("zzzz","s = "+ s);
                if(bodyLogin.getData() != null)
                {
                    JSONArray tableJson = new JSONArray(bodyLogin.getData());

                    tableList.clear();
                    Log.d("test_DeviceID", " == " + tableJson.length());
                    if (tableJson.length() != 0) {

                        for (int i = 0; i < tableJson.length(); i++) {
                            Gson gson = new Gson();
                            Log.d("zzzz","table1.getDevice_Name() = "+ String.valueOf(tableJson.get(i)));
                            Table table1 = gson.fromJson(String.valueOf(tableJson.get(i)), Table.class);
                            tableList.add(table1);
                            Log.d("test_DeviceID", table1.getModified_Date() + " == " + tableJson.length());
                        }
                        // loop delete table ManagementDevice
                        for (Table t : databaseDevice.getAllDevice()) {
                            databaseDevice.deleteDevice(t);
                        }

                        //deviceList.clear();
                        for (Table t : tableList) {
                            Log.d("name", t.getDevice_Name() + "");
                            //deviceList.add(new ModelDevice(t.getDevice_Name(),t.getID()));
                            databaseDevice.addDevice(t);
                            if (database_last_update.test_Last_Time_Get_Update(t.getDevice_ID()) == 0) {
                                database_last_update.addLast_Time_Get_Update(new LastTimeGetUpdate(t.getDevice_ID(), TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT
                                        , TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT,
                                        TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT,
                                        TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT, TIME_DEFAULT));
                            }
                        }
                        tableList.clear();
                        tableList = databaseDevice.getAllDevice();
                        adapterDevice = new AdapterDevice(tableList, R.layout.item_rcv_management_device, ManagementDevice.this);
                        rcvDevice.setAdapter(adapterDevice);
                        adapterDevice.notifyDataSetChanged();

                        if (tableList.size() == 1 || tableList.size() == 0) {
                            ln_Number_Device.setVisibility(View.GONE);
                        } else {
                            ln_Number_Device.setVisibility(View.VISIBLE);
                            txt_NumberDevice.setText(getString(R.string.YouHaveLinkedDevice,tableList.size()));
                        }

                    } else {
                        txtNoDevice.setVisibility(View.VISIBLE);

                    }
                    stopAnim(avLoadingIndicatorView);
                    avLoadingIndicatorView.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
                Log.d("zzzz","jsonObj = "+ e.toString() );
            }
        }
    }

//    public void swipeRefreshLayout() {
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Calendar calendar = Calendar.getInstance();
//                if (isConnected(getApplicationContext())) {
//                    if ((calendar.getTimeInMillis() - time_Refresh_Device) > LIMIT_REFRESH) {
//                        new deviceAsyncTask().execute();
//                        new accountAsyncTask().execute();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                swipeRefreshLayout.setRefreshing(false);
//                                Toast.makeText(ManagementDevice.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
//                                Calendar calendar1 = Calendar.getInstance();
//                                time_Refresh_Device = calendar1.getTimeInMillis();
//                            }
//                        }, 1000);
//                    } else {
//                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(ManagementDevice.this, "The data has been updated.", Toast.LENGTH_SHORT).show();
//                        // Toast.makeText(ManagementDevice.this, calendar.getTimeInMillis()- timeRefresh_Device +"", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    swipeRefreshLayout.setRefreshing(false);
//                    noInternet(ManagementDevice.this);
//                }
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        if (ON_BACK == 2) {
            super.onBackPressed();
        } else {
            ON_BACK++;
            Toast.makeText(this, "Hit back one more time to exit!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        MyApplication.getInstance().trackScreenView("ManagementDevice Screen");
        super.onResume();
    }
}
