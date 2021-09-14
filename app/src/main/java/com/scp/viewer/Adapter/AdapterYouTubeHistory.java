/*
  ClassName: AdapterYouTubeHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-19
  Description: class AdapterYouTubeHistory used to customize the adapter for the RecyclerView of the "YouTubeHistory.class"
  History: 2021-07-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.YouTube;
import com.scp.viewer.R;
import com.scp.viewer.View.MyApplication;
import com.scp.viewer.View.YouTubeHistory;
import java.util.ArrayList;

import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.COLUMN_LAST_YOUTUBE;
import static com.scp.viewer.Database.Entity.LastTimeGetUpdateEntity.TABLE_LAST_UPDATE;

public class AdapterYouTubeHistory extends RecyclerView.Adapter<AdapterYouTubeHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<YouTube> listData;
    private final String VIEWS = "views";

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_Chanel_Name_YouTube, txt_Views_YouTube, txt_Date_YouTube, txt_Video_Name_YouTube;
        View mView;
        CardView card_view_YouTube;

        ViewHolder(View v) {
            super(v);
            txt_Video_Name_YouTube = v.findViewById(R.id.txt_Video_Name_YouTube);
            txt_Chanel_Name_YouTube = v.findViewById(R.id.txt_Chanel_Name_YouTube);
            txt_Views_YouTube = v.findViewById(R.id.txt_Views_YouTube);
            txt_Date_YouTube = v.findViewById(R.id.txt_Date_YouTube);
            card_view_YouTube = v.findViewById(R.id.card_view_YouTube);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_YouTube.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((YouTubeHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (YouTubeHistory.isInActionMode) {
                ((YouTubeHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {

                //MyApplication.getInstance().trackEvent("YouTube", "View App detail: " + listData.get(position).getVideo_Name(), "" + listData.get(position).getChannel_Name());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String link_YouTube = "https://www.youtube.com/results?search_query=";
                intent.setData(Uri.parse(link_YouTube + listData.get(position).getVideo_Name()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    public AdapterYouTubeHistory(Activity activity, ArrayList<YouTube> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }

    @NonNull
    @Override
    public AdapterYouTubeHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_youtube_history, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YouTube youTube = listData.get(position);
        holder.card_view_YouTube.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));


        if(youTube.getViews().contains(VIEWS))
        {
            holder.txt_Views_YouTube.setText(checkRemoveAfterViews(youTube.getViews()));
            holder.txt_Chanel_Name_YouTube.setText(youTube.getChannel_Name());
            holder.txt_Video_Name_YouTube.setText(youTube.getVideo_Name());
        }else {
            if(youTube.getVideo_Name().contains(VIEWS) && !youTube.getChannel_Name().contains(VIEWS))
            {
                holder.txt_Views_YouTube.setText(checkRemoveAfterViews(youTube.getVideo_Name()));
                holder.txt_Chanel_Name_YouTube.setText(youTube.getChannel_Name());
                holder.txt_Video_Name_YouTube.setText(youTube.getViews());
            }else if(!youTube.getVideo_Name().contains(VIEWS) && youTube.getChannel_Name().contains(VIEWS)){
                holder.txt_Views_YouTube.setText(checkRemoveAfterViews(youTube.getChannel_Name()));
                holder.txt_Chanel_Name_YouTube.setText(youTube.getViews());
                holder.txt_Video_Name_YouTube.setText(youTube.getVideo_Name());
            }else {
                holder.txt_Views_YouTube.setText(checkRemoveAfterViews(youTube.getChannel_Name()));
                holder.txt_Chanel_Name_YouTube.setText(youTube.getViews());
                holder.txt_Video_Name_YouTube.setText(youTube.getVideo_Name());
            }
        }
       /* holder.txt_Chanel_Name_YouTube.setText("Chanel: " + youTube.getChannel_Name());
        holder.txt_Video_Name_YouTube.setText(youTube.getVideo_Name());
        holder.txt_Views_YouTube.setText(youTube.getViews());*/
        holder.txt_Date_YouTube.setText(APIDatabase.getTimeItem(youTube.getClient_Youtube_Time(), null));

        if (YouTubeHistory.isInActionMode)
        {
            if (YouTubeHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_YouTube.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_YouTube.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    private String checkRemoveAfterViews(String views)
    {
        if(views.contains(VIEWS))
        {
            return views.substring(0, (views.indexOf(VIEWS))) + " " +VIEWS;
        }
        else {
            return views;
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<YouTube> list) {
        for (YouTube youTube : list) {
            listData.remove(youTube);
        }
        notifyDataSetChanged();
    }

}