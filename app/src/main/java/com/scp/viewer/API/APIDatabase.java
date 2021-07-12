/*
  ClassName: APIDatabase.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class APIDatabase used to create template methods for other reusable classes
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.scp.viewer.API;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scp.viewer.R;
import com.scp.viewer.View.MyApplication;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import static com.scp.viewer.API.APIMethod.subDate;
import static com.scp.viewer.API.APIURL.formatStringToDate;
import static com.scp.viewer.API.APIURL.getDateNow;
import static com.scp.viewer.API.Global.DATE;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_FORMAT_AM;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_TIME_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_TIME_FORMAT_AM;
import static com.scp.viewer.API.Global.HOUR_MILLIS;
import static com.scp.viewer.Database.Entity.ManagementDeviceEntity.COLUMN_WIFI_ENABLED;

public class APIDatabase {

    /*
        These are parameter that require an underscore to be saved in SQLite without error
         because the server returns the variable name when there is an underscore when there is no underscore.
     */
    private static final String ROWINDEX = "RowIndex";
    private static final String ROW_INDEX = "Row_Index";
    private static final String ISLOADED = "IsLoaded";
    private static final String IS_LOADED = "Is_Loaded";
    private static final String EXPORTCSV = "Exportcsv";
    private static final String EXPORT_CSV = "Export_CSV";
    private static final String SERVERTIME = "ServerTime";
    private static final String SERVER_TIME = "Server_Time";

    /*
        These are the values that are required to check that if the server returns a null value,
        it will be converted to a String instead of an Int like any other parameter.
     */
    private static final String DELIVERY_TO_EMAIL = "Deliverry_To_Email";
    private static final String MONITOR_NUMBER = "Monitor_Number";
    private static final String URL_SERVER = "URL_Server";


    /**
     * The method converts the object to a map and adds it to the ContentValues
     * Method API_Add_Database used by the database classes such as:
     * 1. DatabaseApplicationUsage.class   7. DatabaseGetSMS.class
     * 2. DatabaseCall.class               8. DatabaseLastUpdate.class
     * 3. DatabaseContact.class            9. DatabaseNotes.class
     * 4. DatabaseDevice.class             10.DatabaseURL.class
     * 5. DatabaseGetLocation.class        11.DatabaseUser.class
     * 6. DatabaseGetSetting.class         12.DatabaseUserInfo.class
     */
    public static ContentValues API_Add_Database(Object object, Boolean intORString) {
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> map = oMapper.convertValue(object, Map.class);
        ContentValues contentValues1 = new ContentValues();
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            Log.d("entry.getValue()","entry.getValue() = "+ entry.getValue() + "   entry.getKey() = "+ entry.getKey());
            String entryKey = "";


            if(entry.getKey().length()>1)
            {
                // capitalize first letter String output = str.substring (0, 1).toUpperCase () + str.substring (1); //
                entryKey = addUnderline(entry.getKey().substring(0,1).toUpperCase()+ entry.getKey().substring(1));
            }

            Log.d("entry.getValue()","entry.getValue() = "+ entry.getValue() + "   entryKey = "+ entryKey);
            if (entry.getValue() instanceof Integer) {
                contentValues1.put(entryKey, (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                contentValues1.put(entryKey, (Long) entry.getValue());

            } else if (entry.getValue() instanceof Boolean) {
                if(entryKey.equals(COLUMN_WIFI_ENABLED))
                {
                    contentValues1.put(entryKey, ((Boolean)entry.getValue())?1:0);
                }
                else
                    contentValues1.put(entryKey, (Boolean) entry.getValue());

            } else if (entry.getValue() instanceof Double) {
                contentValues1.put(entryKey, (Double) entry.getValue());

            } else {

                // intORString = true is Integer else is String
                if(intORString)
                {
                    if(entryKey.equals(DELIVERY_TO_EMAIL)|| entryKey.equals(MONITOR_NUMBER)|| entryKey.equals(URL_SERVER) )
                    {
                        contentValues1.put(entryKey, ((entry.getValue() == null) ? "" : (String) entry.getValue()));
                    }else {
                        contentValues1.put(entryKey, ((entry.getValue() == null) ? 0 : 0));
                    }
                }
                else
                    contentValues1.put(entryKey, ((entry.getValue() == null) ? "" : (String) entry.getValue()));
            }
        }
        return contentValues1;
    }

    /**
     * addUnderline is a method of testing the parameters without underscores between 2 words, it will add an underscore.
     * @param entryKey
     */
    private static String addUnderline(String entryKey) {
        String entryKeyTemp = entryKey;
        if(entryKey.equals(ROWINDEX))
            entryKeyTemp = entryKey.replace(ROWINDEX,ROW_INDEX);
        else if(entryKey.equals(ISLOADED))
            entryKeyTemp = entryKey.replace(ISLOADED,IS_LOADED);
        else if(entryKey.equals(EXPORTCSV))
            entryKeyTemp = entryKey.replace(EXPORTCSV,EXPORT_CSV);
        else if(entryKey.equals(SERVERTIME))
            entryKeyTemp = entryKey.replace(SERVERTIME,SERVER_TIME);

        return entryKeyTemp;
    }

    /**
     * The method compares two String values​when converting the Date type.
     */
    private static String formatStringDate(String d1, String d2) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(Global.DEFAULT_DATE_FORMAT);
        String result = "";
        Date endDay;
        Date currentDay;
        try {
            endDay = formatter.parse(d1);
            currentDay = formatter.parse(d2);
            if (endDay.equals(currentDay)) {
                result = "Equals";
            } else if (endDay.after(currentDay)) {
                result = "After";
            } else result = "Before";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Format date conversion method.
     * @param date is the type of date you put in.
     * @param endDateFormat is the type of date you want to receive back.
     * @return endDateFormat
     */
    public static String formatDate(String date, String endDateFormat) throws ParseException {
        @SuppressLint("SimpleDateFormat") Date initDate = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT).parse(date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        return formatter.format(initDate);
    }

    /**
     * Format date conversion method.
     * @param date is a String date value you entered
     * @return date_Format is a date type of DEFAULT_DATETIME_FORMAT_AM
     */
    public static String getFormatDateAM(String date)
    {
        String date_Format = APIDatabase.checkValueStringT(date);
        try {
            date_Format = formatDate(date_Format,DEFAULT_DATETIME_FORMAT_AM);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("getFormatDateAM", e.getMessage() + "");
        }
        return date_Format;
    }

    /**
     * getThread is a generic method to dismiss progressDialog
     * @param progressDialog This is progressDialog there is no need to dismiss
     */
    public static void getThread(final ProgressDialog progressDialog) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                } finally {
                    if(progressDialog!=null)
                    {
                        progressDialog.dismiss();
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * get Toast is the method of displaying Toast messages to the end user.
     */
    public static void getToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * The method retrieves the final synchronization time of each device.
     * The method getTimeLastSync used in the class "Dashboard.class"
     */
    @SuppressLint("SetTextI18n")
    public static void getTimeLastSync(TextView textView, Context context, String timeModified)
    {
        String time = timeModified;
        String timeDate = timeModified;
        // Two-time comparison method
        String timeEnd = APIURL.getTimeNow();
        try {
            if (time != null) {

                time = formatDate(time, DEFAULT_DATE_FORMAT);
                timeEnd = formatDate(timeEnd, DEFAULT_DATE_FORMAT);
                String timeEqual = formatStringDate(time, timeEnd);
                String timeIfBefore = checkValueStringT(timeModified);
                int timeHoursBefore = Integer.parseInt(formatDate(timeModified, "HH"));
                int timeHoursAfter = Integer.parseInt(formatDate(APIURL.getTimeNow(), "HH"));
                int timeBefore = Integer.parseInt(formatDate(timeModified, "mm"));
                int timeAfter = Integer.parseInt(formatDate(APIURL.getTimeNow(), "mm"));
                String timeDefault = formatDate(timeModified, DEFAULT_TIME_FORMAT);
                switch (timeEqual) {
                    case "Equals":
                        if (timeHoursAfter-timeHoursBefore == 0 || timeHoursAfter-timeHoursBefore == 1){
                            // subTime variable to store the time value subtracted.
                            int subTime = timeAfter - timeBefore;
                            Log.d("subTime", subTime + ":"+timeAfter+"="+timeBefore);
                            if (subTime == 0) {
                                textView.setText(MyApplication.getResourcses().getString(R.string.JustAMoment));
                            }else if (subTime > 5 ) {
                                textView.setText("Today "+timeDefault);
                            }
                            else if (subTime < 5 && subTime > 0) {
                                textView.setText(MyApplication.getResourcses().getString(R.string.JustMinutesAgo,subTime));
                            }
                            else if (subTime < 0) {
                                    int timeMin = (timeAfter + 60) - timeBefore;
                                    Log.d("timeMin", timeMin + "");
                                    if (timeMin <= 5 && timeMin > 0) {
                                        textView.setText(MyApplication.getResourcses().getString(R.string.JustMinutesAgo,timeMin));
                                    } else {
                                        textView.setText("Today "+timeDefault );
                                    }
                            }
                        }
                        else {
                            textView.setText("Today "+timeDefault);
                        }
                        break;
                    case "Before":
                        long sub_Date = subDate(timeDate);
                        if (sub_Date / (DATE) == 1) {
                            textView.setText("Yesterday  "+timeDefault );

                        } else {
                            textView.setText(timeDate);
                        }
                        break;
                    default:
                        textView.setText(timeIfBefore + "");
                        break;
                }
            }
        } catch (ParseException e) {

            e.printStackTrace();
        }
    }

    /**
     * getTimeItem The method of checking whether a date is today or yesterday or earlier.
     */
    public static String getTimeItem(String timeModified, String typeFormatDate)
    {

        String dateChange = "";
        String time = timeModified;
        String timeDate = timeModified;
        // Two-time comparison method
        String timeEnd = APIURL.getTimeNow();
        try {
            if (time != null) {

                time = formatDate(time, DEFAULT_DATE_FORMAT);
                timeEnd = formatDate(timeEnd, DEFAULT_DATE_FORMAT);
                String timeEqual = formatStringDate(time, timeEnd);
                String timeDefault;
                if(typeFormatDate == null)
                {
                    timeDefault = formatDate(timeModified, DEFAULT_TIME_FORMAT_AM);
                }else {
                    timeDefault = formatDate(timeModified, typeFormatDate);
                }
                Log.d("zdate",timeEqual);

                switch (timeEqual) {
                    case "Equals":
                        dateChange = "Today "+timeDefault;
                        break;
                    case "Before":
                        long sub_Date = subDate(timeDate);
                        dateChange = checkDateYesterday(sub_Date, timeModified, typeFormatDate, timeDefault);
                        break;
                    default:
                        dateChange = formatDateE(timeModified,typeFormatDate);
                        break;
                }
            }
        } catch (ParseException e) {
            Log.d("dateGPS", e.getMessage());
            e.printStackTrace();
        }
        return dateChange;
    }

    /**
     * This is method of checking whether this is Yesterday or not.
     * @return dateChange
     */
    private static String checkDateYesterday(long sub_Date, String dateString, String typeFormatDate, String timeDefault)
    {
        String dateChange ="";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate date1 = LocalDate.parse(getDateNow(formatStringToDate(dateString)));
                LocalDate date2 = LocalDate.parse(getDateNow(null));
                long subDate = date1.until(date2, ChronoUnit.DAYS);
                if(subDate == 1)
                {
                    dateChange = "Yesterday "+timeDefault ;
                }
                else {
                    dateChange = formatDateE(dateString,typeFormatDate);
                }
            }
            else
            {
                if(sub_Date < 24 * HOUR_MILLIS)
                {
                    dateChange = "Yesterday "+timeDefault ;
                }
                else {
                    dateChange = formatDateE(dateString,typeFormatDate);
                }
            }
            return dateChange;
    }


    /**
     * formatDateE is the method that converts the default date type to a style "yyyy-MM-dd hh:mm aa"
     */
    private static String formatDateE(String date, String dateType)
    {
        String timeDefault;

            try {
                if(dateType == null)
                {
                    timeDefault = formatDate(date, DEFAULT_DATETIME_FORMAT_AM);
                }else {
                    timeDefault = formatDate(date, dateType);
                }
            } catch (ParseException e)
            {
                timeDefault = date;
                e.printStackTrace();
            }
        return timeDefault;
    }

    /**
     * time_SMS_Format "Today "+ "HH:mm:ss"
     */
    public static String time_SMS_Format(String time) {
        String time_One = time;
        String time_End = APIURL.getTimeNow();
        try {
            String time3 = formatDate(time_One, DEFAULT_TIME_FORMAT);
            time_One = formatDate(time_One, DEFAULT_DATETIME_FORMAT);
            time_End = formatDate(time_End, DEFAULT_DATETIME_FORMAT);
            String time1 = formatStringDate(time_One, time_End);
            switch (time1) {
                case "Equals":
                    time_One = "Today "+time3;
                    break;
                case "Before":
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time_One;
    }

    /**
     * checkValueStringT If there is a T-type date, then remove the letter T.
     */
    public static String checkValueStringT(String value) {
        String time;
        if (value.indexOf("T") == 1) {
            time = value.replace("T", " ");
        } else {
            time = value.replace(" ", " ");
        }
        return time;
    }
}
