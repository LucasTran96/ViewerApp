/*
  ClassName: APIURL.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class APIURL use this to create a method to associate the form to other classes
  and use other methods such as checking internet connection, converting json to object or String, or object array.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.scp.viewer.API;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import com.scp.viewer.Model.Body;
import com.scp.viewer.Model.Data;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.scp.viewer.View.MyApplication;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static com.scp.viewer.API.Global.ACCEPT;
import static com.scp.viewer.API.Global.CONTENT_TYPE;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_MAX_DATE;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_PROTOCOL_LINK;
import static com.scp.viewer.API.Global.FUNCTION;
import static com.scp.viewer.API.Global.INPUTVALUE;
import static com.scp.viewer.API.Global.ISPUBLIC;
import static com.scp.viewer.API.Global.SETTINGS;
import static com.scp.viewer.API.Global.TOKENKEY;
import static com.scp.viewer.API.Global.VALUE_ACCEPT;
import static com.scp.viewer.API.Global.VALUE_CONTENT_TYPE;
import static com.scp.viewer.API.Global.VALUE_ISPUBLIC;
import static com.scp.viewer.API.Global.VALUE_TOKENKEY;

public class APIURL {

    public static Body bodyLogin = new Body();
    private static Data dataJson = new Data();
    public static Table table = new Table();

    /**
     * The POST method sends the protocol to the server sending and receiving data.
     * Method POST used by the classes such as:
     * 1. ApplicationUsageHistory.class      7.LaunchScreen.class
     * 2. Authentication.class               8.ManagementDevice.class
     * 3. CallHistory.class                  9.NotesHistory.class
     * 4. ContactHistory.class              10.PhotoHistory.class
     * 5. Dashboard.class                    11.Register.class
     * 6. HistoryLocation.class             12.SMSHistory.class
     * 13.URLHistory.class
     */
    public static String POST(String value, String function) {
        // logger = Log4jHelper.getLogger("APIURL.class");
        InputStream inputStream;
        String result = "";
        try {
           /* SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https",
                    SSLSocketFactory.getSocketFactory(), 443));

            HttpParams params = new BasicHttpParams();

            SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);*/
            //HttpClient httpclient = new DefaultHttpClient(mgr, params);
            /* 1. HttpClient */
            HttpClient httpclient = new DefaultHttpClient();
            /* 2. make POST request to the given URL */
            HttpPost httpPost = new HttpPost(DEFAULT_PROTOCOL_LINK);
            Log.d("dateTest", "url = "+ DEFAULT_PROTOCOL_LINK);
            /* 3. build jsonObject */
            String json;
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(INPUTVALUE, value);
            jsonObject.accumulate(TOKENKEY, VALUE_TOKENKEY);
            jsonObject.accumulate(FUNCTION, function);
            jsonObject.accumulate(ISPUBLIC, VALUE_ISPUBLIC);
            json = jsonObject.toString();
            Log.d("dateTest", "json = "+ json);
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader(ACCEPT, VALUE_ACCEPT);
            httpPost.setHeader(CONTENT_TYPE, VALUE_CONTENT_TYPE);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
            Log.d("GetPhoneRecording", ""+ "\nfunctionName = " + function + "\n JSON = "+ json + "\n  result = "+ result);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    /**
     * The convertInputStreamToString method is used to convert from json to string.
     */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            result.append(line);
        inputStream.close();
        return result.toString();
    }

    /**
     * The fromJson method is used to convert from json to Object "Body".
     */
    public static Body fromJson(JSONObject jsonObject) {
        // Deserialize json into object fields
        try {
            /*
                "Id": 40900,
                "IsSuccess": 1,
                "ResultId": 1,
                "Data": {},
                "Code": "S_GETAMBIENT_001",
                "Description": "Get ambient has been successfully.",
                "DateTimeFormatPattern": "yyyy-MM-dd HH:mm:ss",
                "DebugInfo": null,
                "FormatData": nul
             */
            bodyLogin.setIsSuccess(jsonObject.getString("IsSuccess"));
            bodyLogin.setResultId(jsonObject.getString("ResultId"));
            bodyLogin.setData(jsonObject.getString("Data"));
            bodyLogin.setCode(jsonObject.getString("Code"));
            bodyLogin.setDescription(jsonObject.getString("Description"));
            bodyLogin.setDebugInfo(jsonObject.getString("DebugInfo"));
            Log.d("GetPhoneRecording", ""+ "functionName = " + "  bodyLogin.getData() = "+ jsonObject.getString("Data"));
        } catch (JSONException e) {
            e.printStackTrace();
            // logger.error("\n\n\n\n\n====METHOD fromJson ===: \n"+e);
            return null;
        }
        return bodyLogin;
    }

    /**
     * The dataJson method is used to convert from json to Object "Data".
     */
    public static Data dataJson(JSONObject jsonObject) {
        // Deserialize json into object fields
        try {
            dataJson.setDeviceInfo(jsonObject.getString("DeviveInfo"));
            dataJson.setExtend(jsonObject.getString("Extend"));
            dataJson.setDeviceFeature(jsonObject.getString("DeviceFeature"));
            dataJson.setDeviceLive(jsonObject.getString("DeviceLive"));
        } catch (JSONException e) {
            e.printStackTrace();
            //logger.error("\n\n\n\n\n====METHOD dataJson ===: \n"+e);
            return null;
        }
        return dataJson;
    }

    /**
     * The dataJson method is used to convert from json to Object.
     */
    public static void deviceObject(String jsonParse) {
        try {
            JSONObject obj = new JSONObject(jsonParse);
            fromJson(obj);
            //logger.debug("==== METHOD deviceObject ===: \n"+ obj.toString());
            Log.d("DeviceObject", obj.toString());
        } catch (Throwable t) {
            //logger.error("\n\n\n\n\n==== METHOD deviceObject catch ===: \n"+"Could not parse malformed JSON: \"" + jsonPare + "\"");
            Log.e("DeviceObject", "Could not parse malformed JSON: \"" + jsonParse + "\"");
        }
    }
    /**
     * The isConnected method is used to check the internet.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * The getTimeNow method is used to retrieve the current time.
     */
    public static String getTimeNow() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        return format.format(date);
    }

    /**
     * The getTimeNow method is used to retrieve the current time.
     */
    public static String getDateNowInMaxDate() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATETIME_MAX_DATE);
        return format.format(date);
    }

    /**
     * The getTimeNow method is used to retrieve the Day.
     */
    public static String getDateNow(Date dateString) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        if(dateString == null)
        {
            Date date = Calendar.getInstance().getTime();
            return formatter.format(date);
        }
        else {
            return formatter.format(dateString);
        }
    }

    /**
     * formatStringToDate Here is the method to convert a String date value to a DEFAULT_DATE_FORMAT "yyyy-MM-dd"
     * @return "yyyy-MM-dd"
     */
    public static Date formatStringToDate(String dateString)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
            e.printStackTrace();
        }
        return date;
    }

    /**
     * The noInternet method is used to display the AlertDialog when no internet connection is available.
     */
    public static void noInternet(Activity activity) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setMessage(MyApplication.getResourcses().getString(R.string.TurnOn));
            builder.setPositiveButton(MyApplication.getResourcses().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
       catch (Exception e)
       {
           e.getMessage();
       }
    }

    /**
     * The alertDialog method is used to show sample AlertDialog has 3 parameter values ​​for other classes to reuse
     */
    public static void alertDialog(final Activity context, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(MyApplication.getResourcses().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                dialogInterface.dismiss();
                //context.finish();
            }
        });


        builder.show();

    }

    public static void alertDialogAll(final Activity context, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(MyApplication.getResourcses().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }


    public static void clearSharedPreferences(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
