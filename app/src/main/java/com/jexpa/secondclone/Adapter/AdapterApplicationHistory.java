/*
  ClassName: AdapterApplicationHistory.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterApplicationHistory used to customize the adapter for the RecyclerView of the "ApplicationUsageHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jexpa.secondclone.Model.ApplicationUsage;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.ApplicationUsageHistory;
import com.jexpa.secondclone.View.MyApplication;
import java.util.ArrayList;

public class AdapterApplicationHistory extends RecyclerView.Adapter<AdapterApplicationHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<ApplicationUsage> listData;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_name_App_History, txt_Date_App_History;
        View mView;
        ImageView img_icon_AppUsage;
        CardView card_view_Application;

        ViewHolder(View v) {
            super(v);
            txt_name_App_History = v.findViewById(R.id.txt_name_App_History);
            txt_Date_App_History = v.findViewById(R.id.txt_Date_App_History);
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
                MyApplication.getInstance().trackEvent("ApplicationHistory", "View App detail: " + listData.get(position).getApp_Name(), "" + listData.get(position).getApp_Name());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/search?q=" + listData.get(position).getApp_Name()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    public AdapterApplicationHistory(Activity activity, ArrayList<ApplicationUsage> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }

    @NonNull
    @Override
    public AdapterApplicationHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
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
        setIconApp(holder.img_icon_AppUsage, application_usage.getApp_ID());
        time_Location = application_usage.getClient_App_Time().replace("T", " ");
        holder.txt_name_App_History.setText(application_usage.getApp_Name());
        holder.txt_Date_App_History.setText(time_Location.substring(0, 16));
        if (ApplicationUsageHistory.isInActionMode) {
            if (ApplicationUsageHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    private void setIconApp(ImageView imageView, String packageName)
    {
        try
        {
            Drawable drawable = mActivity.getPackageManager()
                    .getApplicationIcon(packageName);
            imageView.setImageDrawable(drawable);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
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