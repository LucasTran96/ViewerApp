/*
  ClassName: AdapterApplicationHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterApplicationHistory used to customize the adapter for the RecyclerView of the "ApplicationUsageHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.ApplicationUsage;
import com.scp.viewer.R;
import com.scp.viewer.View.ApplicationUsageHistory;
import com.scp.viewer.View.MyApplication;
import java.util.ArrayList;

import static com.scp.viewer.Adapter.AdapterAppInstallationHistory.setIconApp;

public class AdapterApplicationUsageHistory extends RecyclerView.Adapter<AdapterApplicationUsageHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<ApplicationUsage> listData;
    private static final String LINK_GOOGLE_PLAY = "https://play.google.com/store/search?q=";

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_name_App_History, txt_Date_App_History, txt_Status_App;
        View mView;
        ImageView img_icon_AppUsage;
        CardView card_view_Application;

        ViewHolder(View v) {
            super(v);
            txt_name_App_History = v.findViewById(R.id.txt_name_App_History);
            txt_Date_App_History = v.findViewById(R.id.txt_Date_App_History);
            txt_Status_App = v.findViewById(R.id.txt_Status_App);
            card_view_Application = v.findViewById(R.id.card_view_Application);
            img_icon_AppUsage = v.findViewById(R.id.img_icon_AppUsage);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Application.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ApplicationUsageHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (ApplicationUsageHistory.isInActionMode) {
                ((ApplicationUsageHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                //MyApplication.getInstance().trackEvent("ApplicationHistory", "View App detail: " + listData.get(position).getApp_Name(), "" + listData.get(position).getApp_Name());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(LINK_GOOGLE_PLAY + listData.get(position).getApp_Name()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    public AdapterApplicationUsageHistory(Activity activity, ArrayList<ApplicationUsage> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }

    @NonNull
    @Override
    public AdapterApplicationUsageHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                        int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_application_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationUsage application_usage = listData.get(position);
        holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        String time_Location;
        if(setIconApp(listData.get(position).getApp_ID(), mActivity) == null)
        {
            holder.img_icon_AppUsage.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.android_icon));
        }else {
            holder.img_icon_AppUsage.setImageDrawable(setIconApp(listData.get(position).getApp_ID(), mActivity));
        }
        //setIconApp(holder.img_icon_AppUsage, application_usage.getApp_ID());
        time_Location = application_usage.getClient_App_Time().replace("T", " ");
        holder.txt_name_App_History.setText(application_usage.getApp_Name());
        holder.txt_Date_App_History.setText(APIDatabase.getTimeItem(time_Location, null));

        if(application_usage.getApp_Type() == 0)
        {
            holder.txt_Status_App.setText(mActivity.getResources().getString(R.string.Close));
            holder.txt_Status_App.setTextColor(mActivity.getResources().getColor(R.color.red));
        }
        else
        {
            holder.txt_Status_App.setText(mActivity.getResources().getString(R.string.Open));
            holder.txt_Status_App.setTextColor(mActivity.getResources().getColor(R.color.green));
        }

        if (ApplicationUsageHistory.isInActionMode)
        {
            if (ApplicationUsageHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<ApplicationUsage> list) {
        for (ApplicationUsage application_usage : list) {
            listData.remove(application_usage);
        }
        notifyDataSetChanged();
    }

}