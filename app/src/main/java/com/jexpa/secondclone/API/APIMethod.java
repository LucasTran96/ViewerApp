/*
  ClassName: APIMethod.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class APIMethod used to create template methods for other reusable classes
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.jexpa.secondclone.API;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.HistoryLocation;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Method;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jexpa.secondclone.API.APIDatabase.checkValueStringT;
import static com.jexpa.secondclone.API.APIDatabase.formatDate;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.APIURL.noInternet;
import static com.jexpa.secondclone.API.Global.CONTACT_TOTAL;
import static com.jexpa.secondclone.API.Global.DEFAULT_DATETIME_FORMAT;
import static com.jexpa.secondclone.API.Global.DEFAULT_DATE_FORMAT;
import static com.jexpa.secondclone.API.Global.DEFAULT_DATE_FORMAT_MMM;
import static com.jexpa.secondclone.API.Global.LIMIT_REFRESH;
import static com.jexpa.secondclone.API.Global.SETTINGS;
import static com.jexpa.secondclone.API.Global.time_Refresh_Device;


public class APIMethod {

    public  static ProgressDialog  progressDialog;
    /**
     * method minus two days
     * @param stringExpiry is the expiration date of the device
     * @return sub_Date
     */
    @SuppressLint("SimpleDateFormat")
    public static long subDate(String stringExpiry) {

        Date d1 = null;
        try {
            d1 = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT).parse(stringExpiry);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /* Today's date */
        Date today = new Date();
        return today.getTime() - d1.getTime();
    }

    /**
     * getProgressDialog()
     * @param title is the title from the other class passed into.
     * @param context is the Context of the class that uses it.
     */
    public static void getProgressDialog(String title, Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    public static void setDateForArrayList(int position, TextView textView, String dateBefore, String dateCurrent)
    {
        String time_URL = getTimeItem(checkValueStringT(dateCurrent), DEFAULT_DATE_FORMAT_MMM);
        if(position > 0)
        {
            String dateNext;
            String dateHere;

            try {
                dateNext = formatDate(dateBefore, DEFAULT_DATE_FORMAT);
                dateHere = formatDate(dateCurrent, DEFAULT_DATE_FORMAT);

                if(!dateNext.equals(dateHere))
                {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(time_URL);
                }
                else {
                    textView.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                textView.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }else {
            textView.setText(time_URL);
            textView.setVisibility(View.VISIBLE);
        }
    }

    // format URL
    public static String formatURL(String str) {
        try {
            if (!(str.startsWith("http://") || str.startsWith("https://"))) {
                str =  str.replace("http://","").replace("https://","");
                str = "http://"+ str.trim();
                Log.d("txhost", str+" 16");

            }
            String host = new URI(str).getHost();
            Log.d("txhost", host+" 18");
            if(host != null && (!host.isEmpty()))
            {
                Log.d("txhost", host+" 20");
                if (host.contains("www.")) {
                    host = host.replace("www.","");
                }
            }
            else {
                String checkSTR = str.replace("http://", "").replace("https://","");
                try {
                    if(checkSTR.contains("/"))
                    {
                        int indexOf = checkSTR.indexOf("/");
                        host = checkSTR.substring(0,indexOf-1);
                    }else {
                        host = checkSTR;
                    }

                }catch (Exception e)
                {
                    host = checkSTR;
                    e.getMessage();
                }

            }
            Log.d("txhost", host+"");
            return host.trim();
        } catch (Throwable th) {
            th.printStackTrace();
            return str;
        }
    }

    public static void setToTalLog(JSONArray jsonArray, String name_Log, Context context)
    {
        if(jsonArray.length() !=0)
        {
            String totalRow = "0";
            try {
                totalRow = jsonArray.getJSONObject(0).getString("TotalRow");
                Log.d("totalRow"," totalRow = "+ totalRow);
                if(totalRow != null)
                {
                    SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.apply();
                    editor.putLong(name_Log,Long.parseLong(totalRow));
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
