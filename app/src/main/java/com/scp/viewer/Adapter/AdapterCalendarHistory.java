/*
  ClassName: AdapterCalendarHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-14
  Description: class AdapterCalendarHistory used to customize the adapter for the RecyclerView of the "CalendarHistory.class"
  History:2021-07-14
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.Calendars;
import com.scp.viewer.R;
import com.scp.viewer.View.CalendarHistory;

import java.text.ParseException;
import java.util.ArrayList;

import static com.scp.viewer.API.APIDatabase.formatDate;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_DATETIME_FORMAT_AM;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT;
import static com.scp.viewer.API.Global.DEFAULT_TIME_FORMAT_AM;

public class AdapterCalendarHistory extends RecyclerView.Adapter<AdapterCalendarHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<Calendars> listData;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_From_Date_History, txt_Title_Calendar_History, txt_To_Location_Calendar_History;
        View mView;
        CardView card_view_Calendar;

        ViewHolder(View v) {
            super(v);
            txt_From_Date_History = v.findViewById(R.id.txt_Date_Start);
            txt_Title_Calendar_History = v.findViewById(R.id.txt_Calendar_Title);
            txt_To_Location_Calendar_History = v.findViewById(R.id.txt_Location_Calendar);
            card_view_Calendar = v.findViewById(R.id.card_view_Calendar);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Calendar.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((CalendarHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (CalendarHistory.isInActionMode) {
                ((CalendarHistory) mActivity).prepareSelection(getAdapterPosition());
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

    public AdapterCalendarHistory(Activity activity, ArrayList<Calendars> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }

    @NonNull
    @Override
    public AdapterCalendarHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_calendar_history, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Calendars calendars = listData.get(position);
            holder.card_view_Calendar.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
            if(calendars.getFrom_Date() != null && calendars.getTo_Date() != null)
            {
                holder.txt_From_Date_History.setText(getEventDateCalendar(calendars.getFrom_Date(), calendars.getTo_Date()));
            }
            else {
                holder.txt_From_Date_History.setText(APIDatabase.getTimeItem(calendars.getFrom_Date(), null) + "  -  " + APIDatabase.getTimeItem(calendars.getTo_Date(), null));
            }
           /* String fromDate = getEventDateCalendar(calendars.getFrom_Date(), calendars.getTo_Date()); // 1: from date; 2: to date.
            holder.txt_From_Date_History.setText(calendars.getFrom_Date());*/
            holder.txt_Title_Calendar_History.setText(calendars.getTitle());
            //holder.txt_To_Date_History.setText(calendars.getTo_Date());
            if(calendars.getLocation()!= null && !calendars.getLocation().isEmpty())
            {
                holder.txt_To_Location_Calendar_History.setVisibility(View.VISIBLE);
                holder.txt_To_Location_Calendar_History.setText(calendars.getLocation());
            }
            else {
                holder.txt_To_Location_Calendar_History.setVisibility(View.GONE);
            }


            if (CalendarHistory.isInActionMode)
            {
                if (CalendarHistory.selectionList.contains(listData.get(position))) {
                    holder.card_view_Calendar.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
                }
            }else {
                holder.card_view_Calendar.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
            }
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    /**
     * getEventDateCalendar: this is the method to convert the default date to a date with AM and PM, or if the date is the same, only display the time of the event.
     * @param from_date "2021-07-14 10:00:00"
     * @param to_date "2021-07-14 12:23:12"
     * @return
     */
    private String getEventDateCalendar(String from_date, String to_date)
    {
        try {
            Log.d("from_date", "from_date = "+ from_date);
            String from_Dates = formatDate(from_date, DEFAULT_DATE_FORMAT);
            String end_Dates = formatDate(to_date, DEFAULT_DATE_FORMAT);
            if(from_Dates.equals(end_Dates))
            {
                return APIDatabase.getTimeItem(formatDate(from_date, DEFAULT_DATETIME_FORMAT), null) + "  -  "+ formatDate(to_date, DEFAULT_TIME_FORMAT_AM);
            }else {
                return APIDatabase.getTimeItem(formatDate(from_date, DEFAULT_DATETIME_FORMAT), null) + "  -  " + APIDatabase.getTimeItem(formatDate(to_date, DEFAULT_DATETIME_FORMAT), null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Unknown";
        }


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<Calendars> list) {
        for (Calendars calendars : list) {
            listData.remove(calendars);
        }
        notifyDataSetChanged();
    }

}