/*
  ClassName: AdapterSMSHistory.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterSMSHistory used to customize the adapter for the RecyclerView of the "SMSHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.jexpa.secondclone.Model.SMS;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.MyApplication;
import com.jexpa.secondclone.View.SMSHistory;
import com.jexpa.secondclone.View.SMSHistoryDetail;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIDatabase.checkValueStringT;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;

public class AdapterSMSHistory extends RecyclerView.Adapter<AdapterSMSHistory.ViewHolder> {
    private Activity mActivity;
    private static List<SMS> mDataSet;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_User_Name_SMS, txt_Message__SMS, txt_time_SMS, txt_minutes__SMS;
        View mView;
        ImageView imgUser_SMS;
        CardView card_view_SMS;

        ViewHolder(View v) {
            super(v);
            txt_User_Name_SMS = v.findViewById(R.id.txt_User_Name_SMS);
            txt_Message__SMS = v.findViewById(R.id.txt_Message__SMS);
            txt_time_SMS = v.findViewById(R.id.txt_time_SMS);
            txt_minutes__SMS = v.findViewById(R.id.txt_minutes__SMS);
            imgUser_SMS = v.findViewById(R.id.imgUser_SMS);
            card_view_SMS = v.findViewById(R.id.card_view_SMS);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_SMS.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((SMSHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // Lightning click event.
            if (SMSHistory.isInActionMode_SMS) {
                ((SMSHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                SMS sms = mDataSet.get(position);
                Log.d("dssdsd", sms.getClient_Message_Time());
                MyApplication.getInstance().trackEvent("SMSHistory", "View SMS detail: " + sms.getContact_Name(), "" + sms.getContact_Name());
                // Path through new activity.
                Log.d("nameTable", SMSHistory.name_Table_SMSHistory);
                Intent intent = new Intent(mActivity, SMSHistoryDetail.class);
                intent.putExtra("sms_History", sms.getContact_Name());
                intent.putExtra("nameTable", SMSHistory.name_Table_SMSHistory);
                intent.putExtra("nameDevice", sms.getDevice_ID());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_bottom,  R.anim.slide_out_bottom);
            }
        }
    }

    public AdapterSMSHistory(Activity activity, List<SMS> myDataSet) {
        mActivity = activity;
        mDataSet = myDataSet;
    }

    @NonNull
    @Override
    public AdapterSMSHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_sms_location, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SMS sms = mDataSet.get(position);
        Log.d("dssdsd", sms.getClient_Message_Time());
        holder.txt_User_Name_SMS.setText(sms.getContact_Name());
        holder.txt_Message__SMS.setText(sms.getText_Message());
        String time_SMS;
        time_SMS = checkValueStringT(sms.getClient_Message_Time());
        String date = getTimeItem(time_SMS,null);
        Log.d("dssdsd", "date = "+date);
        String dateFinal [] = date.split(" ");
        holder.txt_time_SMS.setText(dateFinal[0]);
        if(dateFinal.length>1)
        {
            holder.txt_minutes__SMS.setText((dateFinal.length>2)?(dateFinal[1]+" "+dateFinal[2]):dateFinal[1]);
        }
        else {
            holder.txt_minutes__SMS.setText(dateFinal[0]);
        }

        if (SMSHistory.isInActionMode_SMS) {
            if (SMSHistory.selectionList.contains(mDataSet.get(position))) {
                holder.card_view_SMS.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
                holder.imgUser_SMS.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.selected_icon));
            }
            else {
                holder.card_view_SMS.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
                holder.imgUser_SMS.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.user));
            }
        }
        else {
            holder.card_view_SMS.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
            holder.imgUser_SMS.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.user));
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void removeData(ArrayList<SMS> list) {
        for (SMS sms : list) {
            mDataSet.remove(sms);
        }
        notifyDataSetChanged();
    }
}
