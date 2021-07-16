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
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.ApplicationUsage;
import com.scp.viewer.Model.Networks;
import com.scp.viewer.R;
import com.scp.viewer.View.ApplicationUsageHistory;
import com.scp.viewer.View.MyApplication;
import com.scp.viewer.View.NetworkHistory;

import java.util.ArrayList;

import static com.scp.viewer.Adapter.AdapterAppInstallationHistory.setIconApp;

public class AdapterNetworkHistory extends RecyclerView.Adapter<AdapterNetworkHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<Networks> listData;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_name_Network_History, txt_Date_Network_History, txt_Status_Network;
        View mView;
        CardView card_view_Network;

        ViewHolder(View v) {
            super(v);
            txt_name_Network_History = v.findViewById(R.id.txt_name_Network_History);
            txt_Date_Network_History = v.findViewById(R.id.txt_Date_Network_History);
            txt_Status_Network = v.findViewById(R.id.txt_Status_Network);
            card_view_Network = v.findViewById(R.id.card_view_Network);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Network.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((NetworkHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (NetworkHistory.isInActionMode) {
                ((NetworkHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                /*MyApplication.getInstance().trackEvent("ApplicationHistory", "View App detail: " + listData.get(position).getApp_Name(), "" + listData.get(position).getApp_Name());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(LINK_GOOGLE_PLAY + listData.get(position).getApp_Name()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);*/
            }
        }
    }

    public AdapterNetworkHistory(Activity activity, ArrayList<Networks> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }

    @NonNull
    @Override
    public AdapterNetworkHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_network_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Networks networks = listData.get(position);
        holder.card_view_Network.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));

        holder.txt_name_Network_History.setText(networks.getNetwork_Connection_Name());
        holder.txt_Date_Network_History.setText(APIDatabase.getTimeItem(networks.getClient_Network_Connection_Time(), null));

        if(networks.getStatus() == 0)
        {
            holder.txt_Status_Network.setText(mActivity.getResources().getString(R.string.Disconnect));
            holder.txt_Status_Network.setTextColor(mActivity.getResources().getColor(R.color.red));
        }
        else
        {
            holder.txt_Status_Network.setText(mActivity.getResources().getString(R.string.Connect));
            holder.txt_Status_Network.setTextColor(mActivity.getResources().getColor(R.color.green));
        }

        if (NetworkHistory.isInActionMode)
        {
            if (NetworkHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_Network.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_Network.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<Networks> list) {
        for (Networks networks : list) {
            listData.remove(networks);
        }
        notifyDataSetChanged();
    }

}