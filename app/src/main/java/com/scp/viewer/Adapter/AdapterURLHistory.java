/*
  ClassName: AdapterURLHistory.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterURLHistory used to customize the adapter for the RecyclerView of the "URLHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scp.viewer.Model.URL;
import com.scp.viewer.R;
import com.scp.viewer.View.MyApplication;
import com.scp.viewer.View.URLHistory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.scp.viewer.API.APIDatabase.checkValueStringT;
import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIMethod.formatURL;
import static com.scp.viewer.API.APIMethod.setDateForArrayList;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT_MMM;

public class AdapterURLHistory extends RecyclerView.Adapter<AdapterURLHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<URL> mDataSet;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_Date_URL_History, txt_name_URL_History, txt_Text_URL, txt_Date_URL;
        View mView;
        ImageView img_URL_History_NoInternet, img_URL_Background_History_Internet, img_URL_History_Internet;
        LinearLayout card_view_URL, lnl_URL_Selected;

        ViewHolder(View v) {
            super(v);

            txt_Date_URL_History = v.findViewById(R.id.txt_Date_URL_History);
            txt_name_URL_History = v.findViewById(R.id.txt_name_URL_History);
            txt_Text_URL = v.findViewById(R.id.img_Text_URL_NoInternet);
            txt_Date_URL = v.findViewById(R.id.txt_Date_URL);
            img_URL_History_NoInternet = v.findViewById(R.id.img_URL_History_NoInternet);
            img_URL_Background_History_Internet = v.findViewById(R.id.img_URL_Background_History_Internet);
            img_URL_History_Internet = v.findViewById(R.id.img_URL_History_Internet);
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
                //MyApplication.getInstance().trackEvent("URLHistory", "View URL detail: " + url.getURL_Link(), "" + url.getURL_Link());
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


            String domain;

            if(url.getURL_Link().contains("google.com/search?"))
                domain ="google.com";
            else
                domain = formatURL(url.getURL_Link());

            Log.d("domain","domain = "+domain);
            holder.txt_name_URL_History.setText(domain);

            String checkSTR = url.getURL_Link().replace("http://", "").replace("https://","");
            if(checkSTR.contains("/"))
            {
              int lastIndex = checkSTR.indexOf("/");
              holder.txt_Date_URL_History.setText(checkSTR.substring(lastIndex+1).replace(".html",""));
            }else {
                holder.txt_Date_URL_History.setText(checkSTR);
            }

            String textIcon = (url.getURL_Link().length()>0) ? url.getURL_Link().replace(" ","").charAt(0) +"" : "a";
            Log.d("checktextIcon", " url.getURL_Link().length() = " + url.getURL_Link().length() +" url.getURL_Link() = " + url.getURL_Link().trim() + " textIcon = " + textIcon);

            if(getSpecialCharacterCount(textIcon))
            {
                textIcon = (url.getURL_Link().length()>1) ? url.getURL_Link().replace(" ","").charAt(1) +"" : "a";
            }

            holder.txt_Text_URL.setText(textIcon.toUpperCase());

            if (URLHistory.isInActionMode) {
                if (URLHistory.selectionList.contains(mDataSet.get(position)))
                {
                    // background_url_custom
                    holder.txt_Text_URL.setVisibility(View.GONE);
                    holder.lnl_URL_Selected.setBackground(mActivity.getResources().getDrawable(R.drawable.background_url_custorm));
                    holder.img_URL_History_Internet.setVisibility(View.GONE);
                    holder.img_URL_Background_History_Internet.setVisibility(View.GONE);
                    holder.img_URL_History_NoInternet.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.selected_icon));
                }
                else {
                    holder.lnl_URL_Selected.setBackground(null);
                    holder.txt_Text_URL.setVisibility(View.VISIBLE);
                    holder.img_URL_History_NoInternet.setVisibility(View.VISIBLE);
                    holder.img_URL_History_NoInternet.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.url_background_icon));
                    getGlideImage(holder.img_URL_History_Internet, holder.img_URL_Background_History_Internet, domain);
                }
            }
            else {
                holder.lnl_URL_Selected.setBackground(null);
                holder.txt_Text_URL.setVisibility(View.VISIBLE);
                holder.img_URL_History_NoInternet.setVisibility(View.VISIBLE);
                holder.img_URL_History_NoInternet.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.url_background_icon));
                getGlideImage(holder.img_URL_History_Internet, holder.img_URL_Background_History_Internet, domain);
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

    private void getGlideImage(ImageView imageView, ImageView imageViewBackground, String doMain)
    {

        // "https://"+doMain+"/favicon.ico"
        if (isConnected(mActivity))
        {
            imageView.setVisibility(View.VISIBLE);
            imageViewBackground.setVisibility(View.VISIBLE);
            Glide.with(mActivity)
                    .load("https://www.google.com/s2/favicons?sz=64&domain_url="+doMain) //Edit
                    .placeholder(R.drawable.url_background_icon_load)
                    .error(R.drawable.url_background_icon)
                    .into(imageView);
        }
        else {
            imageView.setVisibility(View.GONE);
            imageViewBackground.setVisibility(View.GONE);
        }

    }

    /**
     * getSpecialCharacterCount: This is a method that checks whether the character is a special character or not.
     * @param s
     */
    private boolean getSpecialCharacterCount(String s)
    {
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(s);
        // boolean b = m.matches();
        boolean b = m.find();
        if (b)
        {
            Log.d("zSpecial", "There is a special character in my string ");
            return true;
        }
        else
        {
            Log.d("zSpecial", "There is no special char.");
            return false;
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