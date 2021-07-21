package com.scp.viewer.API;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.scp.viewer.View.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.scp.viewer.API.APIMethod.getToTalLog;
import static com.scp.viewer.API.APIMethod.setToTalLog;
import static com.scp.viewer.API.APIMethod.setTotalLongForSMS;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.APIURL.getDateNowInMaxDate;
import static com.scp.viewer.API.Global.AMBIENT_RECORDING_TOTAL;
import static com.scp.viewer.API.Global.APP_INSTALLATION_TOTAL;
import static com.scp.viewer.API.Global.APP_USAGE_TOTAL;
import static com.scp.viewer.API.Global.CALENDAR_TOTAL;
import static com.scp.viewer.API.Global.CALL_TOTAL;
import static com.scp.viewer.API.Global.CLIPBOARD_TOTAL;
import static com.scp.viewer.API.Global.CONTACT_TOTAL;
import static com.scp.viewer.API.Global.FACEBOOK_TOTAL;
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
import static com.scp.viewer.API.Global.URL_TOTAL;
import static com.scp.viewer.API.Global.VIBER_TOTAL;
import static com.scp.viewer.API.Global.WHATSAPP_TOTAL;
import static com.scp.viewer.API.Global.YOUTUBE_TOTAL;
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

        public contactAsyncTask(String functionName, String deviceID, Context context, String smsType, TextView txt_total_number) {
            this.functionName = functionName;
            this.deviceID = deviceID;
            this.context = context;
            this.smsType = smsType;
            this.txt_total_number = txt_total_number;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String value;
            if(functionName.equals(GET_SMS_HISTORY))
            {
                value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\""+ LENGHT +"\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\" Type=\"" + smsType + "\" />";
            }
            else {
                value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\""+ LENGHT +"\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\"  />";
            }
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

                if(!functionName.equals(GET_SMS_HISTORY))
                {
                    setNewTotalItemOfFeature(functionName, totalRow, txt_total_number);
                }
                else {
                    if(style != null && !style.equals("50") && style.equals(smsType))
                    {
                        setTotalLongForSMS(totalRow, style, context, deviceID);
                    }
                    setNewTotalItemOfFeature(functionName, totalRow, txt_total_number, smsType);
                }
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

            if(functionName.equals(GET_CALL_HISTORY))
            {
                setTotalNumberTextView(CALL_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_CONTACT_HISTORY))
            {

                setTotalNumberTextView(CONTACT_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_URL_HISTORY))
            {

                setTotalNumberTextView(URL_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_LOCATION_HISTORY))
            {

                setTotalNumberTextView(GPS_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_PHOTO_HISTORY))
            {

                setTotalNumberTextView(PHOTO_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_PHONE_CALL_RECORDING))
            {

                setTotalNumberTextView(PHONE_CALL_RECORDING_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_AMBIENT_VOICE_RECORDING))
            {

                setTotalNumberTextView(AMBIENT_RECORDING_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_APPLICATION_USAGE))
            {

                setTotalNumberTextView(APP_USAGE_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_APP_INSTALLATION_HISTORY)) // 2021-07-12
            {

                setTotalNumberTextView(APP_INSTALLATION_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_CLIPBOARD_HISTORY)) // 2021-07-14
            {

                setTotalNumberTextView(CLIPBOARD_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_NOTES_HISTORY))
            {

                txt_total_number.setVisibility(View.GONE);
            }
            else if(functionName.equals(GET_CALENDAR_HISTORY)) // 2021-07-15
            {

                setTotalNumberTextView(CALENDAR_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_NETWORK_HISTORY)) // 2021-07-15
            {

                setTotalNumberTextView(NETWORK_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_YOUTUBE_HISTORY)) // 2021-07-15
            {

                setTotalNumberTextView(YOUTUBE_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_NOTIFICATION_HISTORY)) // 2021-07-19
            {

                setTotalNumberTextView(NOTIFICATION_TOTAL + deviceID, totalRow, false);
            }
            else if(functionName.equals(GET_KEYLOGGER_HISTORY)) // 2021-07-19
            {

                setTotalNumberTextView(KEYLOGGER_TOTAL + deviceID, totalRow, false);
            }
        }

        /**
         * setNewTotalItemOfFeature This is the method of checking which feature is the total item in SMS: SMS, WhatsApp, Viber ...
         */
        @SuppressLint("SetTextI18n")
        private void setNewTotalItemOfFeature(String functionName, String totalRow, TextView txt_total_number, String smsType)
        {
            if(functionName.equals(GET_SMS_HISTORY))
            {
                if(smsType.equals(SMS_DEFAULT_TYPE))
                {
                    setTotalNumberTextView(SMS_TOTAL + deviceID, totalRow, true);
                }
                else if(smsType.equals(SMS_WHATSAPP_TYPE))
                {
                    setTotalNumberTextView(WHATSAPP_TOTAL + deviceID, totalRow, true);
                }
                else if(smsType.equals(SMS_VIBER_TYPE))
                {
                    setTotalNumberTextView(VIBER_TOTAL + deviceID, totalRow, true);
                }
                else if(smsType.equals(SMS_FACEBOOK_TYPE))
                {
                    setTotalNumberTextView(FACEBOOK_TOTAL + deviceID, totalRow, true);
                }
                else if(smsType.equals(SMS_SKYPE_TYPE))
                {
                    setTotalNumberTextView(SKYPE_TOTAL + deviceID, totalRow, true);
                }
                else if(smsType.equals(SMS_HANGOUTS_TYPE))
                {
                    setTotalNumberTextView(HANGOUTS_TOTAL + deviceID, totalRow, true);
                }
                else if(smsType.equals(SMS_INSTAGRAM_TYPE))
                {
                    setTotalNumberTextView(INSTAGRAM_TOTAL + deviceID, totalRow, true);
                }
            }
        }

        /**
         * setTotalNumberTextView This is a method to assign a new value to textview to notify you of new data.
         */
        @SuppressLint("SetTextI18n")
        private void setTotalNumberTextView(String FunctionName, String totalRow, boolean checkIfSMS)
        {
            SharedPreferences preferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
            long totalOld = preferences.getLong(FunctionName, -1);
            boolean checkOldTotal;
            if(checkIfSMS)
            {
                if(totalOld == -1)
                {
                    setToTalLog(totalRow, FunctionName, context);
                    checkOldTotal = false;
                }
                else {
                    checkOldTotal = true;
                }
            }
            else {
                checkOldTotal = true;
            }
            if(checkOldTotal)
            {
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
    }

}
