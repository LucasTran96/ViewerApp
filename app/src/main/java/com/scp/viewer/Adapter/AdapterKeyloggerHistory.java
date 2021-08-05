/*
  ClassName: AdapterKeyloggerHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-23
  Description: class AdapterKeyloggerHistory used to customize the adapter for the RecyclerView of the "KeyloggerHistory.class"
  History: 2021-07-23
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.Keyloggers;
import com.scp.viewer.R;
import com.scp.viewer.View.KeyloggerHistory;

import java.util.ArrayList;

public class AdapterKeyloggerHistory extends RecyclerView.Adapter<AdapterKeyloggerHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<Keyloggers> listData;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_Keylogger_Name, txt_Content_Keylogger_History, txt_Date_Keylogger_History;
        View mView;
        CardView card_view_Keylogger;

        ViewHolder(View v) {
            super(v);

            txt_Keylogger_Name = v.findViewById(R.id.txt_Keylogger_Name);
            txt_Content_Keylogger_History = v.findViewById(R.id.txt_Content_Keylogger_History);
            txt_Date_Keylogger_History = v.findViewById(R.id.txt_Date_Keylogger_History);
            card_view_Keylogger = v.findViewById(R.id.card_view_Keylogger);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Keylogger.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((KeyloggerHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (KeyloggerHistory.isInActionMode) {
                ((KeyloggerHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                try {
                    Keyloggers keyloggers = listData.get(position);
                    Toast.makeText(mActivity, keyloggers.getContent(), Toast.LENGTH_LONG).show();
                }catch (Exception e)
                {
                    e.getMessage();
                }
            }
        }
    }

    public AdapterKeyloggerHistory(Activity activity, ArrayList<Keyloggers> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }


    @NonNull
    @Override
    public AdapterKeyloggerHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_keylogger_history, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Keyloggers keyloggers = listData.get(position);
        holder.card_view_Keylogger.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        holder.txt_Keylogger_Name.setText(keyloggers.getKeyLogger_Name());
        holder.txt_Content_Keylogger_History.setText("\"" + keyloggers.getContent()+ "\"");
        holder.txt_Date_Keylogger_History.setText(APIDatabase.getTimeItem(keyloggers.getClient_KeyLogger_Time(), null));

        if (KeyloggerHistory.isInActionMode)
        {
            if (KeyloggerHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_Keylogger.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_Keylogger.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<Keyloggers> list) {
        for (Keyloggers keyloggers : list) {
            listData.remove(keyloggers);
        }
        notifyDataSetChanged();
    }

}