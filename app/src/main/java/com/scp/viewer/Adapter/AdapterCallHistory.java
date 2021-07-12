/*
  ClassName: AdapterCallHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterCallHistory used to customize the adapter for the RecyclerView of the "CallHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scp.viewer.Model.Call;
import com.scp.viewer.R;
import com.scp.viewer.View.CallHistoryDetail;
import com.scp.viewer.View.MyApplication;

import java.text.ParseException;
import java.util.ArrayList;

import static com.scp.viewer.API.APIDatabase.checkValueStringT;
import static com.scp.viewer.API.APIDatabase.formatDate;
import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIMethod.setDateForArrayList;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT_MMM;
import static com.scp.viewer.API.Global.DEFAULT_TIME_FORMAT_AM;

public class AdapterCallHistory extends RecyclerView.Adapter<AdapterCallHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<Call> mDataSet;
    public static SparseBooleanArray itemStateArrayCall = new SparseBooleanArray();

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_Name_Call_History,txt_Number_Call_History, txt_Date_Call_History, txt_Date_Call;
        View mView;
        LinearLayout cv_Call_History, lnl_Call_History;
        ImageView img_Status_Call_History, img_Name_Call_History;

        ViewHolder(View v) {
            super(v);
            txt_Name_Call_History = v.findViewById(R.id.txt_Name_Call_History);
            txt_Number_Call_History = v.findViewById(R.id.txt_Number_Call_History);
            txt_Date_Call_History = v.findViewById(R.id.txt_Date_Call_History);
            txt_Date_Call = v.findViewById(R.id.txt_Date_Call);
            img_Status_Call_History = v.findViewById(R.id.img_Status_Call_History);
            img_Name_Call_History = v.findViewById(R.id.img_Name_Call_History);
            cv_Call_History = v.findViewById(R.id.cv_Call_History);
            lnl_Call_History = v.findViewById(R.id.lnl_Call_History);
            mView = v;
            v.setOnLongClickListener(this);
            lnl_Call_History.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((com.scp.viewer.View.CallHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // Lightning click event.
            if (com.scp.viewer.View.CallHistory.isInActionMode) {

                ((com.scp.viewer.View.CallHistory) mActivity).prepareSelection(getAdapterPosition());

                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                Call call = mDataSet.get(position);
                MyApplication.getInstance().trackEvent("CallHistory", "Call phone number: " + call.getContact_Name(), "" + call.getContact_Name());
                // Path through new activity.
                Intent intent = new Intent(mActivity, CallHistoryDetail.class);
                intent.putExtra("Call_Detail", call);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    public AdapterCallHistory(Activity activity, ArrayList<Call> myDataSet) {
        mActivity = activity;
        mDataSet = myDataSet;
    }

    @NonNull
    @Override
    public AdapterCallHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_call_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Call call = mDataSet.get(position);
        if(call != null)
        {
            holder.cv_Call_History.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
            holder.txt_Name_Call_History.setText(call.getContact_Name());
            String date = getTimeItem(checkValueStringT( call.getClient_Call_Time()), DEFAULT_DATE_FORMAT_MMM);
            try {
                holder.txt_Date_Call_History.setText(formatDate(call.getClient_Call_Time(), DEFAULT_TIME_FORMAT_AM));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.txt_Date_Call_History.setText(date);
            }
            if (call.getDirection() == 1) {
                // 1 = Incoming
                // holder.txt_Number_Call_History.setText(call.getPhone_Number());
                holder.txt_Number_Call_History.setText(call.getPhone_Number());
            } else if (call.getDirection() == 0) {
                //0 = Outcoming
                //holder.txt_Number_Call_History.setText(call.getPhone_Number_SIM());
                holder.txt_Number_Call_History.setText(call.getPhone_Number_SIM());
            }
            if (call.getDuration() == 0) {
                holder.img_Status_Call_History.setImageResource(R.drawable.miss_call);
            } else {
                //Incoming call or outgoing call.
                if (call.getDirection() == 1) {
                    // holder.txt_Number_Call_History.setText(call.getPhone_Number());
                    holder.img_Status_Call_History.setImageResource(R.drawable.incoming_call);
                } else if (call.getDirection() == 0) {
                    //holder.txt_Number_Call_History.setText(call.getPhone_Number_SIM());
                    holder.img_Status_Call_History.setImageResource(R.drawable.outcoming_call);
                }
            }
            if (com.scp.viewer.View.CallHistory.isInActionMode) {
                if (com.scp.viewer.View.CallHistory.selectionList.contains(mDataSet.get(position))) {
                    holder.cv_Call_History.setBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
                    holder.cv_Call_History.setBackground(mActivity.getResources().getDrawable(R.drawable.background_url_custorm));
                    holder.img_Name_Call_History.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.selected_icon));
                }
                else {
                    holder.cv_Call_History.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                    holder.cv_Call_History.setBackground(null);
                    holder.img_Name_Call_History.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_person));
                }
            }
            else {
                holder.cv_Call_History.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                holder.cv_Call_History.setBackground(null);
                holder.img_Name_Call_History.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_person));
            }

            if(position > 0)
            {
                setDateForArrayList(position, holder.txt_Date_Call, mDataSet.get(position-1).getClient_Call_Time(), mDataSet.get(position).getClient_Call_Time());
            }else {
                holder.txt_Date_Call.setText(date);
                holder.txt_Date_Call.setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void removeData(ArrayList<Call> list) {
        for (Call call : list) {
            mDataSet.remove(call);
        }
        notifyDataSetChanged();
    }

}