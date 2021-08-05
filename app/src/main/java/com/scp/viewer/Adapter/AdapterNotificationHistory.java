/*
  ClassName: AdapterNotificationHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-20
  Description: class AdapterNotificationHistory used to customize the adapter for the RecyclerView of the "NotificationHistory.class"
  History: 2021-07-20
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.Notifications;
import com.scp.viewer.R;
import com.scp.viewer.View.NotificationHistory;
import java.util.ArrayList;

public class AdapterNotificationHistory extends RecyclerView.Adapter<AdapterNotificationHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<Notifications> listData;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_App_Name_Notification, txt_Title_Notification, txt_Date_Notification, txt_Content_Notification;
        View mView;
        //LinearLayout ln_Info_Notification;
        CardView card_view_Notification;

        ViewHolder(View v) {
            super(v);
            txt_Content_Notification = v.findViewById(R.id.txt_Content_Notification);
            txt_App_Name_Notification = v.findViewById(R.id.txt_App_Name_Notification);
            txt_Title_Notification = v.findViewById(R.id.txt_Title_Notification);
            txt_Date_Notification = v.findViewById(R.id.txt_Date_Notification);
            card_view_Notification = v.findViewById(R.id.card_view_Notification);
            //ln_Info_Notification = v.findViewById(R.id.ln_Info_Notification);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Notification.setOnClickListener(this);
            //ln_Info_Notification.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((NotificationHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (NotificationHistory.isInActionMode) {
                ((NotificationHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {

                try {
                    Notifications notifications = listData.get(position);
                    Toast.makeText(mActivity, notifications.getNotification_Content(), Toast.LENGTH_LONG).show();
                }catch (Exception e)
                {
                    e.getMessage();
                }

            }
        }
    }

    public AdapterNotificationHistory(Activity activity, ArrayList<Notifications> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }

    @NonNull
    @Override
    public AdapterNotificationHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_notification_history, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notifications notifications = listData.get(position);
        Log.d("NotificationHistory"," get Notification = "+  notifications.getNotification_Title());
        holder.card_view_Notification.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        holder.txt_App_Name_Notification.setText(notifications.getApp_Name());
        holder.txt_Title_Notification.setText(notifications.getNotification_Title());
        holder.txt_Content_Notification.setText("\"" + notifications.getNotification_Content() + "\"");
        holder.txt_Date_Notification.setText(APIDatabase.getTimeItem(notifications.getCreated_Date(), null));

        if (NotificationHistory.isInActionMode)
        {
            if (NotificationHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_Notification.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_Notification.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<Notifications> list) {
        for (Notifications notifications : list) {
            listData.remove(notifications);
        }
        notifyDataSetChanged();
    }

}