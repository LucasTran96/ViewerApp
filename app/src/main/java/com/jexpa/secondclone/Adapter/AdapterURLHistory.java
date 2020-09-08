/*
  ClassName: AdapterURLHistory.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterURLHistory used to customize the adapter for the RecyclerView of the "URLHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jexpa.secondclone.Model.URL;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.MyApplication;
import com.jexpa.secondclone.View.URLHistory;

import java.util.ArrayList;

import static com.jexpa.secondclone.API.APIDatabase.checkValueStringT;
import static com.jexpa.secondclone.API.APIDatabase.getTimeItem;
import static com.jexpa.secondclone.API.APIMethod.formatURL;
import static com.jexpa.secondclone.API.APIMethod.setDateForArrayList;
import static com.jexpa.secondclone.API.Global.DEFAULT_DATE_FORMAT_MMM;

public class AdapterURLHistory extends RecyclerView.Adapter<AdapterURLHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<URL> mDataSet;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_Date_URL_History, txt_name_URL_History, txt_Text_URL, txt_Date_URL;
        View mView;
        ImageView img_URL_History;
        LinearLayout card_view_URL, lnl_URL_Selected;

        ViewHolder(View v) {
            super(v);

            txt_Date_URL_History = v.findViewById(R.id.txt_Date_URL_History);
            txt_name_URL_History = v.findViewById(R.id.txt_name_URL_History);
            txt_Text_URL = v.findViewById(R.id.img_Text_URL);
            txt_Date_URL = v.findViewById(R.id.txt_Date_URL);
            img_URL_History = v.findViewById(R.id.img_URL_History);
            card_view_URL = v.findViewById(R.id.card_view_URL);
            lnl_URL_Selected = v.findViewById(R.id.lnl_URL_Selected);
            mView = v;
            card_view_URL.setOnLongClickListener(this);
            card_view_URL.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {

            ((URLHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view)
        {
            int position = getAdapterPosition();
            if (URLHistory.isInActionMode) {
                ((URLHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                URL url = mDataSet.get(position);
                MyApplication.getInstance().trackEvent("URLHistory", "View URL detail: " + url.getURL_Link(), "" + url.getURL_Link());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(startOpenWebPage(url.getURL_Link())));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    /**
     * startOpenWebPage this is a method to check the path and check if no http then add them
     * and open history url on google chrome.
     */
    public static String startOpenWebPage(String url)
    {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    public AdapterURLHistory(Activity activity, ArrayList<URL> myDataSet)
    {
        mActivity = activity;
        mDataSet = myDataSet;
    }

    @NonNull
    @Override
    public AdapterURLHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_url_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        URL url = mDataSet.get(position);
        if(url != null)
        {

            holder.card_view_URL.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
            String time_URL = getTimeItem(checkValueStringT( url.getClient_URL_Time()), DEFAULT_DATE_FORMAT_MMM);
            String domain = formatURL(url.getURL_Link());
            Log.d("domain","domain = "+domain);
            holder.txt_name_URL_History.setText(domain);
            //holder.txt_Date_URL_History.setText(formatDate(url.getClient_URL_Time(), DEFAULT_TIME_FORMAT_AM));
            String checkSTR = url.getURL_Link().replace("http://", "").replace("https://","");
            if(checkSTR.contains("/"))
            {
              int lastIndex = checkSTR.indexOf("/");
              holder.txt_Date_URL_History.setText(checkSTR.substring(lastIndex+1).replace(".html",""));
            }else {
                holder.txt_Date_URL_History.setText(checkSTR);
            }

            String textIcon = (url.getURL_Link().length()>0) ? url.getURL_Link().charAt(0)+"" : "";
            holder.txt_Text_URL.setText(textIcon.toUpperCase());

            if (URLHistory.isInActionMode) {
                if (URLHistory.selectionList.contains(mDataSet.get(position)))
                {
                    // background_url_custom
                    holder.txt_Text_URL.setVisibility(View.GONE);
                    holder.lnl_URL_Selected.setBackground(mActivity.getResources().getDrawable(R.drawable.background_url_custorm));
                    holder.img_URL_History.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.selected_icon));
                }
                else {
                    holder.lnl_URL_Selected.setBackground(null);
                    holder.txt_Text_URL.setVisibility(View.VISIBLE);
                    holder.img_URL_History.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.url_background_icon));
                }
            }
            else {
                holder.lnl_URL_Selected.setBackground(null);
                holder.txt_Text_URL.setVisibility(View.VISIBLE);
                holder.img_URL_History.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.url_background_icon));
            }

            if(position > 0)
            {
                setDateForArrayList(position, holder.txt_Date_URL, mDataSet.get(position-1).getClient_URL_Time(), mDataSet.get(position).getClient_URL_Time());
            }else {
                holder.txt_Date_URL.setText(time_URL);
                holder.txt_Date_URL.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }

    public void removeData(ArrayList<URL> list)
    {
        for (URL url : list) {
            mDataSet.remove(url);
        }
        notifyDataSetChanged();
    }
}