/*
  ClassName: LaunchScreen.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class LaunchScreen use to check whether logged in or not logged in before
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import com.scp.viewer.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scp.viewer.API.APIURL;
import com.scp.viewer.Database.DatabaseUser;
import com.scp.viewer.Model.User;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.Global.DEFAULT_LINK_REGISTER;
import static com.scp.viewer.API.Global.DEFAULT_LINK_RENEW;
import static com.scp.viewer.API.Global.DEFAULT_PROTOCOL_LINK;
import static com.scp.viewer.API.Global.DEFAULT_LINK_FORGETPASSWORD;
import static com.scp.viewer.API.Global.DEFAULT_LINK_ABOUTUS;
import static com.scp.viewer.API.Global.DEFAULT_COPYRIGHT;
import static com.scp.viewer.API.Global.DEFAULT_LOGO_IMAGE_PATH;
import static com.scp.viewer.API.Global.DEFAULT_PRODUCT_NAME;
import static com.scp.viewer.API.Global.DEFAULT_VERSION_NAME;
import static com.scp.viewer.API.Global.SETTINGS;

public class LaunchScreen extends AppCompatActivity {
    private String email, password;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        DatabaseUser databaseUser = new DatabaseUser(this);
        SharedPreferences prefs = getSharedPreferences(SETTINGS, Activity.MODE_PRIVATE);
        String language = prefs.getString("MyLang", "");
        setLocale(language);
        //The method of checking the file distributing.xml exists or not.
        checkFileDistributingExist(getApplicationContext());

        ImageView img_Logo_Launch = findViewById(R.id.img_Logo_Launch);
        TextView txt_AppName_Launch = findViewById(R.id.txt_AppName_Launch);
        TextView txt_VersionName_Launch = findViewById(R.id.txt_VersionName_Launch);
        Log.d("test", "DEFAULT_LOGO_IMAGE_PATH = "+ DEFAULT_LOGO_IMAGE_PATH );
        Picasso.with(getApplicationContext()).load(DEFAULT_LOGO_IMAGE_PATH).error(R.drawable.no_image).into(img_Logo_Launch);
        txt_AppName_Launch.setText(DEFAULT_PRODUCT_NAME);
        txt_VersionName_Launch.setText(DEFAULT_VERSION_NAME);
        Log.d("test", DEFAULT_VERSION_NAME);
        // mLog = Log4jHelper.getLogger("LaunchScreen.class");
        int i = databaseUser.getNotesCount();
        Log.d("data_User", i+"");
        Intent intent1;
        User user;
        if (APIURL.isConnected(this)) {
            if (i == 0) {
                databaseUser.addUser(new User(1, "123", "123"));
                Intent intent = new Intent(getApplicationContext(), Authentication.class);
                startActivity(intent);
                finish();
            } else {
                user = databaseUser.getNote(1);
                email = user.getEmail();
                password = user.getPassword();
                if (user.getEmail().equals("123") || user.getEmail().equals("")
                        || user.getPassword().equals("123")) {
                    intent1 = new Intent(getApplicationContext(), Authentication.class);
                    startActivity(intent1);
                    finish();
                } else {
                    new checkLoginAsyncTask().execute();
                }
            }
        }

        if (!APIURL.isConnected(this)) {
            if (databaseUser.getNotesCount() != 0) {
                user = databaseUser.getNote(1);
                email = user.getEmail();
                password = user.getPassword();
                if (user.getEmail().equals("123")
                        || user.getPassword().equals("123")) {
                    intent1 = new Intent(getApplicationContext(), Authentication.class);
                    startActivity(intent1);
                    finish();
                } else {
                    Intent intentMain = new Intent(getApplicationContext(), ManagementDevice.class);
                    startActivity(intentMain);
                    finish();
                }
            } else {
                databaseUser.addUser(new User(1, "123", "123"));
                Intent intent = new Intent(getApplicationContext(), Authentication.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // save data to Preferences
        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS, MODE_PRIVATE).edit();
        editor.putString("MyLang", lang);
        editor.apply();
    }

    public static void checkFileDistributingExist(Context context) {
        int resId = context.getResources().getIdentifier(
                "PRODUCT_NAME",
                "string",
                context.getPackageName()
        );
        try {
            Log.d("resId", resId + "");
            if (resId != 0) {

                DEFAULT_PROTOCOL_LINK = getStringResourceByName("DEFAULT_LINK_PROTOCOL_URL",context) + "/api/apiv2";
                DEFAULT_LINK_FORGETPASSWORD = getStringResourceByName("LINK_FORGET_PASSWORD",context);
                DEFAULT_PRODUCT_NAME = getStringResourceByName("PRODUCT_NAME",context);
                DEFAULT_VERSION_NAME = getStringResourceByName("VERSION_NAME",context);
                DEFAULT_COPYRIGHT = getStringResourceByName("COPYRIGHT",context);
                DEFAULT_LOGO_IMAGE_PATH = getStringResourceByName("LOGO_IMAGE_PATH",context);
                DEFAULT_LINK_ABOUTUS = getStringResourceByName("LINK_ABOUT_US",context);
                DEFAULT_LINK_REGISTER = getStringResourceByName("LINK_REGISTER",context);
                DEFAULT_LINK_RENEW = getStringResourceByName("LINK_RENEW",context);
            }
        } catch (Exception e) {
            MyApplication.getInstance().trackException(e);
            e.getMessage();
        }
    }

    public static String getStringResourceByName(String aString, Context context) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        Log.d("testPtcl", context.getString(resId) );
        return context.getString(resId);
    }

    @SuppressLint("StaticFieldLeak")
    private class checkLoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String value = "<RequestParams  Password=\"" + password + "\" Email=\"" + email + "\"/>";
            String function = "AuthenticateWithoutDevice";
            return APIURL.POST(value, function);

        }

        @Override
        protected void onPostExecute(String result) {

            deviceObject(result);
            try {
                if (bodyLogin.getResultId().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                    Intent intentMain = new Intent(getApplicationContext(), ManagementDevice.class);
                    //intentMain.putExtra("userName1",user);
                    startActivity(intentMain);
                    finish();
                    // mLog.debug("Login Success");
                } else {
                    Toast.makeText(LaunchScreen.this, bodyLogin.getDescription() + "", Toast.LENGTH_SHORT).show();
                    Intent intentMain = new Intent(getApplicationContext(), Authentication.class);
                    startActivity(intentMain);
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(LaunchScreen.this, "Has an error with the link protocol", Toast.LENGTH_SHORT).show();
                MyApplication.getInstance().trackException(e);
                Intent intentMain = new Intent(getApplicationContext(), Authentication.class);
                startActivity(intentMain);
                finish();
                Log.e("ExceptionLogin", e.getMessage());
                Toast.makeText(LaunchScreen.this, "Has an error with the link protocol", Toast.LENGTH_SHORT).show();
                e.getMessage();
            }
        }
    }

    @Override
    protected void onResume() {
        MyApplication.getInstance().trackScreenView("LaunchScreen Screen");
        super.onResume();
    }
}
