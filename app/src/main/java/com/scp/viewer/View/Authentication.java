/*
  ClassName: Authentication.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class Authentication used to check and confirm user login account to use.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.scp.viewer.API.APIURL;
import com.scp.viewer.Database.DatabaseUser;
import com.scp.viewer.Model.User;
import com.scp.viewer.R;
import com.scp.viewer.API.APIDatabase;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.Locale;
import java.util.Objects;

import static com.scp.viewer.API.APIMethod.startAnim;
import static com.scp.viewer.API.APIMethod.stopAnim;
import static com.scp.viewer.API.APIURL.bodyLogin;
import static com.scp.viewer.API.APIURL.deviceObject;
import static com.scp.viewer.API.Global.DEFAULT_LINK_FORGETPASSWORD;
import static com.scp.viewer.API.Global.DEFAULT_LINK_REGISTER;
import static com.scp.viewer.API.Global.DEFAULT_LOGO_IMAGE_PATH;
import static com.scp.viewer.API.Global.DEFAULT_PRODUCT_NAME;
import static com.scp.viewer.API.Global.DEFAULT_VERSION_NAME;
import static com.scp.viewer.API.Global.SETTINGS;
import static com.scp.viewer.Adapter.AdapterURLHistory.startOpenWebPage;
import static com.scp.viewer.View.LaunchScreen.checkFileDistributingExist;

public class Authentication extends AppCompatActivity {

    EditText edt_Email, edt_Password;
    TextView txt_AppName_Authentication, txt_AppVersion_Authentication, txt_ForgetPassword,btn_Register;
    ImageView img_Logo_Authentication,img_HidePassword;
    Button btn_SignIn;
    public String pw, email;
    DatabaseUser databaseUser;
    private boolean checkHidePassword = true;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    // public static Body bodyLogin =new Body();

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        checkFileDistributingExist(getApplicationContext());
        loadLocale();
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(Authentication.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 12;
                ActivityCompat.requestPermissions(Authentication.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }
        }

        setID();
        setEvent();
        databaseUser = new DatabaseUser(this);
    }

    @SuppressLint("ResourceType")
    private void setID() {
        btn_Register = findViewById(R.id.btn_Register);
        btn_SignIn = findViewById(R.id.btn_SignIn);
        edt_Email = findViewById(R.id.edt_Email);
        avLoadingIndicatorView = findViewById(R.id.aviLogin);
        edt_Password = findViewById(R.id.edt_Password);
        txt_AppName_Authentication = findViewById(R.id.txt_AppName_Authentication);
        txt_AppVersion_Authentication = findViewById(R.id.txt_AppVersion_Authentication);
        txt_ForgetPassword = findViewById(R.id.txt_ForgetPassword);
        img_Logo_Authentication = findViewById(R.id.img_Logo_Authentication);
        img_HidePassword = findViewById(R.id.img_HidePassword);
        txt_AppName_Authentication.setText(DEFAULT_PRODUCT_NAME);
        txt_AppVersion_Authentication.setText(DEFAULT_VERSION_NAME);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Log.d("DEFAULT_LOGO_IMAGE_PATH", DEFAULT_LOGO_IMAGE_PATH);
        Picasso.with(getApplicationContext()).load(DEFAULT_LOGO_IMAGE_PATH).error(R.drawable.no_image).into(img_Logo_Authentication);
    }

    private void setEvent() {

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(startOpenWebPage(DEFAULT_LINK_REGISTER)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }
        });
        txt_ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(startOpenWebPage(DEFAULT_LINK_FORGETPASSWORD)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MyApplication.getInstance().trackEvent("SignIn", "SignIn", "Login Event");

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SignIn");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SignIn");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Login Event");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                } catch (Exception e) {
                    e.getMessage();
                }

                if (APIURL.isConnected(getApplicationContext())) {
                    if (edt_Email.getText().toString().length() < 1 || edt_Password.getText().toString().length() < 1) {
                        Toast.makeText(Authentication.this, "Username or password is empty", Toast.LENGTH_SHORT).show();
                    } else {
                        avLoadingIndicatorView.setVisibility(View.VISIBLE);
                        startAnim(avLoadingIndicatorView);
                        new loginAsyncTask(edt_Password.getText().toString(), edt_Email.getText().toString()).execute();
                    }
                } else {
                    User user = databaseUser.getNote(1);
                    String email = user.getEmail();
                    if (!email.equals("123")) {
                        Intent intentMain = new Intent(getApplicationContext(), ManagementDevice.class);
                        intentMain.putExtra("user", user.getEmail());
                        startActivity(intentMain);
                        finish();
                    } else {
                        APIURL.noInternet(Authentication.this);
                    }
                }
            }
        });

        img_HidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkHidePassword)
                {
                    edt_Password.setTransformationMethod(null);
                    edt_Password.setSelection(edt_Password.length());
                    img_HidePassword.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_visibility_black_24dp));
                    checkHidePassword = false;
                }
                else {
                    edt_Password.setTransformationMethod(new PasswordTransformationMethod());
                    edt_Password.setSelection(edt_Password.length());
                    img_HidePassword.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_visibility_off_black_24dp));
                    checkHidePassword = true;
                }
            }
        });
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

    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS, Activity.MODE_PRIVATE);
        String language = prefs.getString("MyLang", "");
        setLocale(language);

    }

    @SuppressLint("StaticFieldLeak")
    private class loginAsyncTask extends AsyncTask<String, Void, String> {
        String password, emailName;

        public loginAsyncTask(String password, String emailName) {
            this.password = password;
            this.emailName = emailName;
        }

        @Override
        protected String doInBackground(String... urls) {

//            pw = edt_Password.getText().toString();
//            email = edt_Email.getText().toString();
            pw = password;
            email = emailName;
            String value = "<RequestParams  Password=\"" + pw + "\" Email=\"" + email + "\"/>";
            String function = "AuthenticateWithoutDevice";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(Authentication.this, result, Toast.LENGTH_SHORT).show();
            if(result != null && !result.isEmpty())
            {
                deviceObject(result);
                try {
                    Log.d("bodyLogin: ", bodyLogin.getDescription());
                    Log.d("resultLogin: ", result + "");
                    if (bodyLogin.getResultId().equals("1") && bodyLogin.getIsSuccess().equals("1")) {
                        databaseUser.updateUser(new User(1, email, pw));
                        Intent intentMain = new Intent(getApplicationContext(), ManagementDevice.class);
                        //intentMain.putExtra("userName",email);
                        startActivity(intentMain);
                        finish();
                    } else {
                        edt_Password.setText("");
                        APIDatabase.getToast(Authentication.this, bodyLogin.getDescription());
                    }

                } catch (Exception e) {
                    edt_Password.setText("");
                    Log.e("ExceptionLogin", e.getMessage() + "");
                    Toast.makeText(Authentication.this, "Has an error with the link protocol", Toast.LENGTH_SHORT).show();
                    e.getMessage();
                }
            }

            stopAnim(avLoadingIndicatorView);
            avLoadingIndicatorView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
