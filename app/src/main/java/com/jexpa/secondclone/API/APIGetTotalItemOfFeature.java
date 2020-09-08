package com.jexpa.secondclone.API;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jexpa.secondclone.Adapter.AdapterContactHistory;
import com.jexpa.secondclone.Model.Contact;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.ContactHistory;
import com.jexpa.secondclone.View.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.getSharedPreferString;
import static com.jexpa.secondclone.API.APIMethod.getToTalLog;
import static com.jexpa.secondclone.API.APIMethod.setToTalLog;
import static com.jexpa.secondclone.API.APIMethod.setTotalLongForSMS;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.getDateNowInMaxDate;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.Global.AMBIENT_RECORDING_TOTAL;
import static com.jexpa.secondclone.API.Global.APP_USAGE_TOTAL;
import static com.jexpa.secondclone.API.Global.CALL_TOTAL;
import static com.jexpa.secondclone.API.Global.CONTACT_TOTAL;
import static com.jexpa.secondclone.API.Global.FACEBOOK_TOTAL;
import static com.jexpa.secondclone.API.Global.GET_AMBIENT_VOICE_RECORDING;
import static com.jexpa.secondclone.API.Global.GET_APPLICATION_USAGE;
import static com.jexpa.secondclone.API.Global.GET_CALL_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_CONTACT_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_LOCATION_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_NOTES_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_PHONE_CALL_RECORDING;
import static com.jexpa.secondclone.API.Global.GET_PHOTO_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_SMS_HISTORY;
import static com.jexpa.secondclone.API.Global.GET_URL_HISTORY;
import static com.jexpa.secondclone.API.Global.GPS_TOTAL;
import static com.jexpa.secondclone.API.Global.HANGOUTS_TOTAL;
import static com.jexpa.secondclone.API.Global.MIN_TIME;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.API.Global.PHONE_CALL_RECORDING_TOTAL;
import static com.jexpa.secondclone.API.Global.PHOTO_TOTAL;
import static com.jexpa.secondclone.API.Global.SETTINGS;
import static com.jexpa.secondclone.API.Global.SKYPE_TOTAL;
import static com.jexpa.secondclone.API.Global.SMS_DEFAULT_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_FACEBOOK_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_HANGOUTS_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_SKYPE_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_TOTAL;
import static com.jexpa.secondclone.API.Global.SMS_VIBER_TYPE;
import static com.jexpa.secondclone.API.Global.SMS_WHATSAPP_TYPE;
import static com.jexpa.secondclone.API.Global.URL_TOTAL;
import static com.jexpa.secondclone.API.Global.VIBER_TOTAL;
import static com.jexpa.secondclone.API.Global.WHATSAPP_TOTAL;
import static com.jexpa.secondclone.Adapter.AdapterFeatureDashboard.getSMSType;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CONTACT;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;
import static com.jexpa.secondclone.View.SMSHistory.style;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: APIGetTotalItemOfFearture
 * History: 8/14/2020
 * Project: SecondClone
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
            if(functionName.equals("GetSMSByDateTime"))
            {
                value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\"100\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\" Type=\"" + smsType + "\" />";
            }
            else {
                value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\"100\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\"  />";
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
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());

                if(functionName.equals("GetPhoneRecording") )
                {
                    JSONObject jsonObjData = jsonObj.getJSONObject("Data");
                    JSONArray GPSJson = jsonObjData.getJSONArray("Table1");
                    totalRow = getToTalLog(null,GPSJson,"TotalRow");

                }else if(functionName.equals("GetAmbients"))
                {
                    totalRow = getToTalLog(jsonObj,null,"TotalRows");
                }
                else if(functionName.equals("GetURL"))
                {
                    totalRow = getToTalLog(jsonObj,null,"TotalRecord");
                }
                else if(functionName.equals("GetLocations"))
                {

                    JSONArray GPSJsonPaging = jsonObj.getJSONArray("Paging");
                    totalRow = getToTalLog(null,GPSJsonPaging,"TotalRow");
                }
                else if(functionName.equals("GetPhotos"))
                {
                    JSONObject jsonObjListImg = jsonObj.getJSONObject("ListImg");
                    totalRow = getToTalLog(jsonObjListImg,null,"TotalRecord");
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

                Log.d("totalRow", "totalRow = "+ totalRow + " functionName = "+ functionName);

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
                        setTotalLongForSMS(totalRow, style, context);
                    }
                    setNewTotalItemOfFeature(functionName, totalRow, txt_total_number, smsType);
                }
            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }

        @SuppressLint("SetTextI18n")
        private void setNewTotalItemOfFeature(String functionName, String totalRow, TextView txt_total_number)
        {

            if(functionName.equals(GET_CALL_HISTORY))
            {
                setTotalNumberTextView(CALL_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_CONTACT_HISTORY))
            {

                setTotalNumberTextView(CONTACT_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_URL_HISTORY))
            {

                setTotalNumberTextView(URL_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_LOCATION_HISTORY))
            {

                setTotalNumberTextView(GPS_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_PHOTO_HISTORY))
            {

                setTotalNumberTextView(PHOTO_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_PHONE_CALL_RECORDING))
            {

                setTotalNumberTextView(PHONE_CALL_RECORDING_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_AMBIENT_VOICE_RECORDING))
            {

                setTotalNumberTextView(AMBIENT_RECORDING_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_APPLICATION_USAGE))
            {

                setTotalNumberTextView(APP_USAGE_TOTAL, totalRow, false);
            }
            else if(functionName.equals(GET_NOTES_HISTORY))
            {

                txt_total_number.setVisibility(View.GONE);
            }

        }

        @SuppressLint("SetTextI18n")
        private void setNewTotalItemOfFeature(String functionName, String totalRow, TextView txt_total_number, String smsType)
        {
            if(functionName.equals(GET_SMS_HISTORY))
            {
                if(smsType.equals(SMS_DEFAULT_TYPE))
                {
                    setTotalNumberTextView(SMS_TOTAL, totalRow, true);
                }
                else if(smsType.equals(SMS_WHATSAPP_TYPE))
                {
                    setTotalNumberTextView(WHATSAPP_TOTAL, totalRow, true);
                }
                else if(smsType.equals(SMS_VIBER_TYPE))
                {
                    setTotalNumberTextView(VIBER_TOTAL, totalRow, true);
                }
                else if(smsType.equals(SMS_FACEBOOK_TYPE))
                {
                    setTotalNumberTextView(FACEBOOK_TOTAL, totalRow, true);
                }
                else if(smsType.equals(SMS_SKYPE_TYPE))
                {
                    setTotalNumberTextView(SKYPE_TOTAL, totalRow, true);
                }
                else if(smsType.equals(SMS_HANGOUTS_TYPE))
                {
                    setTotalNumberTextView(HANGOUTS_TOTAL, totalRow, true);
                }
            }
        }

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
