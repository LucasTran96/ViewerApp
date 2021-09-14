/*
  ClassName: AdapterApplicationHistory.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterApplicationHistory used to customize the adapter for the RecyclerView of the "ApplicationUsageHistory.class"
  History:2018-10-08
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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import com.scp.viewer.Model.ApplicationUsage;
import com.scp.viewer.R;
import com.scp.viewer.View.AppInstallationHistory;
import com.scp.viewer.View.ApplicationUsageHistory;
import com.scp.viewer.View.MyApplication;

import java.util.ArrayList;

public class AdapterAppInstallationHistory extends RecyclerView.Adapter<AdapterAppInstallationHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<AppInstallation> listData;
    private static final String LINK_GOOGLE_PLAY = "https://play.google.com/store/search?q=";

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_name_App_History, txt_Date_App_History, txt_Status_App;
        View mView;
        ImageView img_icon_AppUsage;
        CardView card_view_Application;

        ViewHolder(View v) {
            super(v);
            txt_name_App_History = v.findViewById(R.id.txt_name_App_History);
            txt_Date_App_History = v.findViewById(R.id.txt_Date_App_History);
            txt_Status_App = v.findViewById(R.id.txt_Status_App);
            card_view_Application = v.findViewById(R.id.card_view_Application);
            img_icon_AppUsage = v.findViewById(R.id.img_icon_AppUsage);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Application.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((AppInstallationHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (AppInstallationHistory.isInActionMode) {
                ((AppInstallationHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                //MyApplication.getInstance().trackEvent("ApplicationHistory", "View App detail: " + listData.get(position).getApp_Name(), "" + listData.get(position).getApp_Name());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(LINK_GOOGLE_PLAY + listData.get(position).getApp_Name()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    public AdapterAppInstallationHistory(Activity activity, ArrayList<AppInstallation> myDataSet) {
        mActivity = activity;
        listData = myDataSet;
    }

    @NonNull
    @Override
    public AdapterAppInstallationHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                       int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_application_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInstallation application_Install = listData.get(position);
        holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));

        if(setIconApp(listData.get(position).getApp_ID(), mActivity) == null)
        {
            holder.img_icon_AppUsage.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.android_icon));
        }else {
            holder.img_icon_AppUsage.setImageDrawable(setIconApp(listData.get(position).getApp_ID(), mActivity));
        }

        holder.txt_name_App_History.setText(application_Install.getApp_Name());
        holder.txt_Date_App_History.setText(APIDatabase.getTimeItem(application_Install.getClient_App_Time(), null));

        if (AppInstallationHistory.isInActionMode)
        {
            if (AppInstallationHistory.selectionList.contains(listData.get(position))) {
                holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
            }
        }else {
            holder.card_view_Application.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    public static Drawable setIconApp( String packageName, Context context)
    {
        try
        {
           /* PackageManager packageManager = mActivity.getPackageManager();
            Bitmap mBitmap = packageManager.getApplicationIcon(packageName);*/

           /* return mActivity.getPackageManager()
                    .getApplicationIcon(packageName);*/
            return getIconFromPackageName(packageName, context);

        }
        catch (Exception e)
        {
            e.getMessage();
            return null;
        }
    }

    public static Drawable getIconFromPackageName(String packageName, Context context)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            Context otherAppCtx = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);

            int displayMetrics[] = {DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH, DisplayMetrics.DENSITY_TV};

            for (int displayMetric : displayMetrics)
            {
                try
                {
                    Drawable d = otherAppCtx.getResources().getDrawableForDensity(pi.applicationInfo.icon, displayMetric);
                    if (d != null)
                    {
                        return d;
                    }
                }
                catch (Resources.NotFoundException e)
                {
//                      Log.d(TAG, "NameNotFound for" + packageName + " @ density: " + displayMetric);
                    continue;
                }
            }

        }
        catch (Exception e)
        {
            // Handle Error here
        }

        ApplicationInfo appInfo = null;
        try
        {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return null;
        }

        return appInfo.loadIcon(pm);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeData(ArrayList<AppInstallation> list) {
        for (AppInstallation appInstallation : list) {
            listData.remove(appInstallation);
        }
        notifyDataSetChanged();
    }

}