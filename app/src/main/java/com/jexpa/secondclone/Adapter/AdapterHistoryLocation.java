/*
 ClassName: AdapterHistoryLocation.java
 @Project: SecondClone
 @author  Lucas Walker (lucas.walker@jexpa.com)
 Created Date: 2018-06-05
 Description: class AdapterHistoryLocation used to customize the adapter for the RecyclerView of the "HistoryLocation.class"
 History:2018-10-08
 Copyright © 2018 Jexpa LLC. All rights reserved.
*/

package com.jexpa.secondclone.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jexpa.secondclone.API.APIDatabase;
import com.jexpa.secondclone.Model.GPS;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.HistoryLocation;
import com.jexpa.secondclone.View.MapLocation;
import com.jexpa.secondclone.View.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterHistoryLocation extends RecyclerView.Adapter<AdapterHistoryLocation.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<GPS> gpsArrayList;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_time_history_location, txt_name_location;
        View mView;
        CardView card_view;

        ViewHolder(View v) {
            super(v);
            txt_time_history_location = v.findViewById(R.id.txt_time_history_location);
            txt_name_location = v.findViewById(R.id.txt_name_location);
            card_view = v.findViewById(R.id.card_view);
            mView = v;
            v.setOnLongClickListener(this);
            card_view.setOnClickListener(this);
            v.setOnClickListener(this);
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
                    intent.putExtra("Addresses", getAddress(gps.getLatitude(), gps.getLongitude()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                }

            }
        }
    }

    public String getAddress(Double latitude, Double longitude) {
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        String addressName = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() != 0 ) {
                String address = addresses.get(0).getAddressLine(0);
                String area = addresses.get(0).getLocality();
                String city = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                addressName = address + ", " + area + ", " + city + ", " + country + ".";
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
            String time_Location = APIDatabase.checkValueStringT(gps.getClient_GPS_Time());
            holder.txt_time_history_location.setText(APIDatabase.getTimeItem(time_Location,null));
            if (gps.getLongitude() == 0 || gps.getLatitude() == 0) {
                holder.txt_name_location.setText("Unknown location.");
            } else {
                holder.txt_name_location.setText(getAddress(gps.getLatitude(), gps.getLongitude()) + "");
            }
            if (HistoryLocation.isInActionMode) {
                if (HistoryLocation.selectionList.contains(gpsArrayList.get(position))) {
                    holder.card_view.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_300));
                    //holder.txt_name_location.setTextColor(Color.parseColor("#000000"));
                }
                else {
                    holder.card_view.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
                }
            }
            else {
                holder.card_view.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
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