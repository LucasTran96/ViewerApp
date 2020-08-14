/*
  ClassName: AdapterDevice.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterDevice used to customize the adapter for the RecyclerView of the "ManagementDevice.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jexpa.secondclone.Model.ModelDevice;
import com.jexpa.secondclone.Model.Table;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.DashBoard;
import com.jexpa.secondclone.View.ManagementDevice;
import com.jexpa.secondclone.View.MyApplication;
import java.util.ArrayList;

import static com.jexpa.secondclone.API.APIDatabase.getFormatDateAM;
import static com.jexpa.secondclone.API.Global.ON_BACK;

public class AdapterDevice extends RecyclerView.Adapter<AdapterDevice.ViewHolder> {
    private ArrayList<Table> deviceList;
    private int linkItem;
    private Activity context;

    //  constructor three parameters
    public AdapterDevice(ArrayList<Table> deviceList, int linkItem, Activity context) {
        this.deviceList = deviceList;
        this.linkItem = linkItem;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(linkItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Table device = deviceList.get(position);
        holder.txtDevice.setText(device.getDevice_Name());
        holder.txt_CreateDate.append(": " + getFormatDateAM(device.getCreated_Date()));

        try {
            if (device.getOS_Device().contains("iOS")) {
                holder.imgDevice.setImageResource(R.drawable.iphone_logo);
            } else {
                holder.imgDevice.setImageResource(R.drawable.android_icon);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // Count the number of elements in deviceList
    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtDevice, txt_CreateDate;
        ImageView imgDevice;
        CardView btn_Device;
        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtDevice = itemView.findViewById(R.id.txtDevice);
            txt_CreateDate = itemView.findViewById(R.id.txt_CreateDate);
            //txt_GPS_Status = itemView.findViewById(R.id.txt_GPS_Status);
            imgDevice = itemView.findViewById(R.id.imgDevice);
            btn_Device = itemView.findViewById(R.id.btn_Device);
            itemView.setOnClickListener(this);
            btn_Device.setOnClickListener(this);
        }

        //  The click event method of items Click in the RecyclerView
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                //  intent will open the activity Dashboard
                ModelDevice device = new ModelDevice(deviceList.get(position).getDevice_Name(), deviceList.get(position).getID());
                Intent intent = new Intent(context, DashBoard.class);
                //  intent sends the device object to the class Dashboard
                intent.putExtra("device", device.getId());
                intent.putExtra("packageID",ManagementDevice.packageID);
                //intent.putExtra("tablePhone",table);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ON_BACK = 1;
                MyApplication.getInstance().trackEvent("Device", "ViewDevice: " + device.getName(), "" + device.getName());
            }
        }
    }
}
