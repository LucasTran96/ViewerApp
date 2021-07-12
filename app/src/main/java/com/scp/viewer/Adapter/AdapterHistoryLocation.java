/*
 ClassName: AdapterHistoryLocation.java
 @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
 Created Date: 2018-06-05
 Description: class AdapterHistoryLocation used to customize the adapter for the RecyclerView of the "HistoryLocation.class"
 History:2018-10-08
 Copyright © 2018 Jexpa LLC. All rights reserved.
*/

package com.scp.viewer.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.scp.viewer.Model.GPS;
import com.scp.viewer.R;
import com.scp.viewer.View.HistoryLocation;
import com.scp.viewer.View.MapLocation;
import com.scp.viewer.View.MyApplication;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static com.scp.viewer.API.APIDatabase.checkValueStringT;
import static com.scp.viewer.API.APIDatabase.formatDate;
import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.APIMethod.setDateForArrayList;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT_MMM;
import static com.scp.viewer.API.Global.DEFAULT_TIME_FORMAT_AM;

public class AdapterHistoryLocation extends RecyclerView.Adapter<AdapterHistoryLocation.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<GPS> gpsArrayList;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_time_history_location, txt_name_location,txt_date_location;
        ImageView img_Selected_Location;
        CardView card_view;

        ViewHolder(View v) {
            super(v);
            txt_time_history_location = v.findViewById(R.id.txt_time_history_location);
            txt_date_location = v.findViewById(R.id.txt_date_location);
            txt_name_location = v.findViewById(R.id.txt_name_location);
            card_view = v.findViewById(R.id.card_view);
            img_Selected_Location = v.findViewById(R.id.img_Selected_Location);
            card_view.setOnClickListener(this);
            card_view.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            ((HistoryLocation) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // Lightning click event.
            if (HistoryLocation.isInActionMode) {
                ((HistoryLocation) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                GPS gps = gpsArrayList.get(position);
                if(gps != null)
                {
                    MyApplication.getInstance().trackEvent("LocationHistory", "View location detail ", "" + gps.getID());
                    // Path through new activity.
                    Intent intent = new Intent(mActivity, MapLocation.class);
                    intent.putExtra("GPS", gps);
                    intent.putExtra("Addresses", getAddress(gps.getLatitude(), gps.getLongitude(),mActivity));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                }
            }
        }
    }

    public static String getAddress(Double latitude, Double longitude, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressName = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() != 0 ) {
                addressName = addresses.get(0).getAddressLine(0) ;
            }
        } catch (IOException e) {
            Log.d("address", e.getMessage() + "");
            e.printStackTrace();
        }
        if (addressName == null) {
            addressName = "Location not found!";
        }
        return addressName;
    }
    public AdapterHistoryLocation(Activity activity, ArrayList<GPS> myDataSet) {
        mActivity = activity;
        gpsArrayList = myDataSet;
    }

    @NonNull
    @Override
    public AdapterHistoryLocation.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_history_location, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GPS gps = gpsArrayList.get(position);
        if(gps != null)
        {
            //String time_Location = APIDatabase.checkValueStringT(gps.getClient_GPS_Time());
            String time_URL = getTimeItem(checkValueStringT( gps.getClient_GPS_Time()), DEFAULT_DATE_FORMAT_MMM);
            // APIDatabase.getTimeItem(time_Location,null)
            try {
                holder.txt_time_history_location.setText(formatDate(checkValueStringT( gps.getClient_GPS_Time()), DEFAULT_TIME_FORMAT_AM));
            } catch (ParseException e) {
                holder.txt_time_history_location.setText(time_URL);
                e.printStackTrace();
            }
            if (gps.getLongitude() == 0 || gps.getLatitude() == 0) {
                holder.txt_name_location.setText("Unknown location.");
            } else {
                holder.txt_name_location.setText(getAddress(gps.getLatitude(), gps.getLongitude(),mActivity) + "");
            }
            if (HistoryLocation.isInActionMode) {
                if (HistoryLocation.selectionList.contains(gpsArrayList.get(position))) {
                    holder.card_view.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
                    holder.img_Selected_Location.setVisibility(View.VISIBLE);
                }
                else {
                    holder.card_view.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
                    holder.img_Selected_Location.setVisibility(View.GONE);
                    //holder.card_view.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                }
            }
            else {
                holder.card_view.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
                holder.img_Selected_Location.setVisibility(View.GONE);
                //holder.card_view.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
            }

            if(position > 0)
            {
                setDateForArrayList(position, holder.txt_date_location, gpsArrayList.get(position-1).getClient_GPS_Time(), gpsArrayList.get(position).getClient_GPS_Time());
            }else {
                holder.txt_date_location.setText(time_URL);
                holder.txt_date_location.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return gpsArrayList.size();
    }

    public void removeData(ArrayList<GPS> list) {
        for (GPS gps : list) {
            gpsArrayList.remove(gps);
        }
        notifyDataSetChanged();
    }

}