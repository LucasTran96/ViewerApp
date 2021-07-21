/*
  ClassName: AdapterClipboardHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-12
  Description: class AdapterClipboardHistory used to customize the adapter for the RecyclerView of the "ClipboardHistory.class"
  History: 2021-07-12
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.AppInstallation;
import com.scp.viewer.Model.Clipboard;
import com.scp.viewer.Model.Notifications;
import com.scp.viewer.R;
import com.scp.viewer.View.AppInstallationHistory;
import com.scp.viewer.View.ClipboardHistory;
import com.scp.viewer.View.MyApplication;

import java.util.ArrayList;

import static com.scp.viewer.API.APIDatabase.formatDate;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_FORMAT_AM;

public class AdapterClipboardHistory extends RecyclerView.Adapter<AdapterClipboardHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<Clipboard> listData;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_From_App_History, txt_Date_Clipboard_History, txt_Content_Clipboard_History;
        View mView;
        ImageView img_icon_Clipboard;
        CardView card_view_Clipboard;

        ViewHolder(View v) {
            super(v);

            txt_From_App_History = v.findViewById(R.id.txt_From_App_History);
            txt_Date_Clipboard_History = v.findViewById(R.id.txt_Date_Clipboard_History);
            txt_Content_Clipboard_History = v.findViewById(R.id.txt_Content_Clipboard_History);
            card_view_Clipboard = v.findViewById(R.id.card_view_Clipboard);
            img_icon_Clipboard = v.findViewById(R.id.img_icon_AppUsage);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Clipboard.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((ClipboardHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (ClipboardHistory.isInActionMode) {
                ((ClipboardHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {

                /*MyApplication.getInstance().trackEvent("Clipboard", "View App detail: " + listData.get(position).getApp_Name(), "" + listData.get(position).getApp_Name());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(LINK_GOOGLE_PLAY + listData.get(position).getApp_Name()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);*/
            }
        }
    }

    public AdapterClipboardHistory(Activity activity, ArrayList<Clipboard> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }


    @NonNull
    @Override
    public AdapterClipboardHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_clipboard_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clipboard clipboard = listData.get(position);
        holder.card_view_Clipboard.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        holder.txt_From_App_History.setText(clipboard.getFrom_App());
        holder.txt_Content_Clipboard_History.setText("\"" + clipboard.getClipboard_Content()+ "\"");
        holder.txt_Date_Clipboard_History.setText(APIDatabase.getTimeItem(clipboard.getClient_Clipboard_Time(), null));

        if (ClipboardHistory.isInActionMode)
        {
            if (ClipboardHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_Clipboard.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_Clipboard.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<Clipboard> list) {
        for (Clipboard clipboard : list) {
            listData.remove(clipboard);
        }
        notifyDataSetChanged();
    }

}