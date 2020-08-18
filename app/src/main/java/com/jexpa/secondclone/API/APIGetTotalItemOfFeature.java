package com.jexpa.secondclone.API;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import static com.jexpa.secondclone.API.APIDatabase.getThread;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.setToTalLog;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.getDateNowInMaxDate;
import static com.jexpa.secondclone.API.APIURL.getTimeNow;
import static com.jexpa.secondclone.API.Global.CONTACT_TOTAL;
import static com.jexpa.secondclone.API.Global.MIN_TIME;
import static com.jexpa.secondclone.API.Global.NumberLoad;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CONTACT;
import static com.jexpa.secondclone.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

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

        public contactAsyncTask(String function, String deviceID, Context context) {
            this.functionName = function;
            this.deviceID = deviceID;
            this.context = context;
        }

        public contactAsyncTask(String functionName, String deviceID, Context context, String smsType) {
            this.functionName = functionName;
            this.deviceID = deviceID;
            this.context = context;
            this.smsType = smsType;
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String value;
            if(functionName.equals("GetSMSByDateTime"))
            {

                value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\"100\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\" Type=\"" + smsType + "\" />"; //  Type=\"" + style + "\"
            }
            else {
                value = "<RequestParams Device_ID=\"" + deviceID + "\" Start=\"0\" Length=\"100\" Min_Date=\"" + MIN_TIME + "\" Max_Date=\"" +  getDateNowInMaxDate() + "\"  />"; //  Type=\"" + style + "\"
            }
            String function = functionName;
            return APIURL.POST(value, function);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try {
                deviceObject(s);
                JSONObject jsonObj = new JSONObject(bodyLogin.getData());
                JSONArray GPSJsonTable1 = jsonObj.getJSONArray("Table1");
                String totalRow = "0";
                if(GPSJsonTable1.toString().isEmpty())
                {
                    totalRow = jsonObj.getString("TotalRows");
                }
                else {
                    totalRow = GPSJsonTable1.getJSONObject(0).getString("TotalRow");

                }
                Log.d("aassa", totalRow + " == "+ functionName);

            } catch (JSONException e) {
                MyApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

}
