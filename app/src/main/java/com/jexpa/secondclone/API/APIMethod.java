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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.wang.avi.AVLoadingIndicatorView;

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

import static android.content.Context.MODE_PRIVATE;
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
                    setSharedPreferLong(context,name_Log,Long.parseLong(totalRow));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public static void setSharedPreferLong(Context context,String name, long value)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, MODE_PRIVATE).edit();
        editor.putLong(name, value);
        editor.commit();
    }

    public static long getSharedPreferLong(Context context,String name)
    {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
        return preferences.getLong(name, 0);
    }

    /**
     * checkItemExist This is a support method to check whether this record already exists in the database or not and add it to the database.
     */
    public static boolean checkItemExistString(SQLiteDatabase database, String tableName, String rawDeviceID, String deviceID, String rawIdContact, String nameFile){

        String query = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'", tableName, rawDeviceID, deviceID, rawIdContact, nameFile);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    /*
        startAnim this is the method for showing progress bar custom.
     */
    public static void startAnim(AVLoadingIndicatorView avLoadingIndicatorView){
        avLoadingIndicatorView.show();
        // or avi.smoothToShow();
    }

    /*
        stopAnim this is the method for hide progress bar custom.
     */
    public static void stopAnim(AVLoadingIndicatorView avLoadingIndicatorView){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        avLoadingIndicatorView.hide();


    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }
}
