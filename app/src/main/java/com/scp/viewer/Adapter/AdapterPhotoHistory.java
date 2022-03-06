/*
  ClassName: AdapterPhotoHistory.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-11-16
  Description: class AdapterPhotoHistory used to customize the adapter for the RecyclerView of the "PhotoHistory.class"
  History:2018-11-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.scp.viewer.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kodmap.app.library.PopopDialogBuilder;
import com.kodmap.app.library.model.BaseItem;
import com.scp.viewer.Model.Photo;
import com.scp.viewer.R;
import com.scp.viewer.View.MyApplication;
import com.scp.viewer.View.PhotoHistoryDetail;
import com.scp.viewer.View.PhotoHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.scp.viewer.API.APIURL.isConnected;
import static com.scp.viewer.API.Global.File_PATH_SAVE_IMAGE;


public class AdapterPhotoHistory extends RecyclerView.Adapter<AdapterPhotoHistory.ViewHolder> {

    //public static SparseBooleanArray itemStateArrayPhoto = new SparseBooleanArray();
    private PhotoHistory photoHistory;
    public  static List<Photo> photoList;
    private Activity mActivity;
    public static List<Integer> positionLastSelected = new ArrayList<>();
    public AdapterPhotoHistory(Activity context, List<Photo> photoList) {
        AdapterPhotoHistory.photoList = photoList;
        photoHistory = (PhotoHistory) context;
        mActivity =context;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @NonNull
    @Override
    public AdapterPhotoHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_photo_history, parent, false);
        return new ViewHolder(view, photoHistory);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        if(photo != null)
        {
            //((PhotoHolder) holder).bind(photo, position);
            String filepath = File_PATH_SAVE_IMAGE;
            if (isConnected(photoHistory)) {
                if (photoList.get(position).getIsLoaded() == 1)
                {
                    // Picasso.with(photoHistory).load(new File(filepath, photo.getFile_Name())).error(R.drawable.no_image).placeholder(R.drawable.spinner).into(img_photo_History);
                    Glide.with(photoHistory)
                            .load(new File(filepath, photo.getFile_Name())) //Edit
                            .placeholder(R.drawable.spinner)
                            .error(R.drawable.no_image)
                            .into(holder.img_photo_History);
                    holder.img_photo_isSave.setVisibility(View.VISIBLE);
                    Log.d("photo.getFile_Name()",photo.getFile_Name());
                }
                else {

                    String url = photo.getCDN_URL() + photo.getMedia_URL() + "/thumb/l/" + photo.getFile_Name();
                            //photo.getCDN_URL() + photo.getMedia_URL() + "/thumb/l" + "/" + photo.getFile_Name();

                    Glide.with(photoHistory)
                            .load(url) //Edit
                            .placeholder(R.drawable.spinner)
                            .error(R.drawable.no_image)
                            .timeout(30000)
                            .into(holder.img_photo_History);
                    // .thumbnail(0.5f).override(200, 200)
                    // .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                    holder.img_photo_isSave.setVisibility(View.GONE);
                    Log.d("photo.getFile_Name()", photo.getCDN_URL() + photo.getMedia_URL() + "/thumb/l" + "/" + photo.getFile_Name());
                }
            } else {

                //Picasso.with(photoHistory).load(new File(filepath, photo.getFile_Name())).error(R.drawable.no_image).placeholder(R.drawable.spinner).into(img_photo_History);
                Glide.with(photoHistory)
                        .load(new File(filepath, photo.getFile_Name())) //Edit
                        .placeholder(R.drawable.spinner)
                        .error(R.drawable.no_image)
                        .into(holder.img_photo_History);
                holder.img_photo_isSave.setVisibility(View.VISIBLE);

                Log.d("photo.getFile_Name()", filepath + photo.getFile_Name());
            }

            //            if (!isInActionMode) {
            //
            //                checkBox_photo.setVisibility(View.GONE);
            //
            //            } else {
            //                checkBox_photo.setVisibility(View.VISIBLE);
            //            }

            //            if (!itemStateArrayPhoto.get(position, false)) {
            //                checkBox_photo.setChecked(false);
            //                view_Show.setVisibility(View.GONE);
            //            } else {
            //                checkBox_photo.setChecked(true);
            //                view_Show.setVisibility(View.VISIBLE);
            //
            //

            if (com.scp.viewer.View.PhotoHistory.isInActionMode)
            {
                //holder.checkBox_photo.setVisibility(View.VISIBLE);
                if (com.scp.viewer.View.PhotoHistory.selectionList.contains(photoList.get(position))) {
                    //holder.checkBox_photo.setChecked(true);
                    holder.img_photo_Selected.setBackgroundColor(mActivity.getResources().getColor(R.color.stranparent));
                    //holder.view_Show.setVisibility(View.VISIBLE);
                    holder.img_photo_Selected.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.selected_icon));
                    Log.d("URLD",positionLastSelected.size() + " ADD0  ");

                    if(positionLastSelected != null && positionLastSelected.size() > 0)
                    {
                        boolean checkAdd = false;
                        for (int i=0; i<positionLastSelected.size(); i++)
                        {
                            if(positionLastSelected.get(i) == position)
                            {
                                checkAdd = true;
                            }
                        }

                        if(!checkAdd)
                        {
                            positionLastSelected.add(position);
                            Log.d("URLD",positionLastSelected.size() + " ADĐ");
                        }
                    }
                    else {
                        positionLastSelected.add(position);
                    }

                    Log.d("URLD",positionLastSelected.size() + " ADD ");
                }
                else
                {
                    //holder.checkBox_photo.setChecked(false);
                    holder.img_photo_Selected.setImageDrawable(null);
                    holder.img_photo_Selected.setBackgroundColor(mActivity.getResources().getColor(R.color.stranparent));
                    //holder.view_Show.setVisibility(View.GONE);
                    if(positionLastSelected != null && positionLastSelected.size()>0)
                    {
                        for (int i=0; i<positionLastSelected.size(); i++)
                        {
                            if(positionLastSelected.get(i) == position)
                            {
                                positionLastSelected.remove(i);
                                Log.d("URLD",positionLastSelected.size() + " remove");
                            }
                        }
                    }
                }
            }
            else {
                holder.img_photo_Selected.setImageDrawable(null);
                //holder.checkBox_photo.setChecked(false);
                holder.img_photo_Selected.setBackgroundColor(mActivity.getResources().getColor(R.color.stranparent));
                //holder.view_Show.setVisibility(View.GONE);
            }

            if(photo.getIsLoaded() == 1)
            {
                holder.img_photo_isSave.setVisibility(View.VISIBLE);
            }
            else {
                holder.img_photo_isSave.setVisibility(View.GONE);
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        //CheckBox checkBox_photo;
        View mmView;//view_Show
        ImageView img_photo_History, img_photo_isSave, img_photo_Selected;
        FrameLayout ln_Photo;
        PhotoHistory photoHistorySmall;

        ViewHolder(View itemView, PhotoHistory photoHistory) {

            super(itemView);
            this.setIsRecyclable(false);
            this.photoHistorySmall = photoHistory;
            ln_Photo = itemView.findViewById(R.id.ln_Photo);
            img_photo_History = itemView.findViewById(R.id.img_photo_History);
            img_photo_isSave = itemView.findViewById(R.id.img_photo_isSave);
            img_photo_Selected = itemView.findViewById(R.id.img_photo_Selected);
            mmView = itemView;
            ln_Photo.setOnClickListener(this);
            mmView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            if (com.scp.viewer.View.PhotoHistory.isInActionMode)
            {
                ((com.scp.viewer.View.PhotoHistory)mActivity).prepareSelection(getAdapterPosition());
                notifyDataSetChanged();
                //Toast.makeText(photoHistorySmall, "Position = "+ adapterPosition, Toast.LENGTH_SHORT).show();

            } else if (adapterPosition != RecyclerView.NO_POSITION) {

                List<String> url_list = new ArrayList<>();
                for (Photo photo:photoList)
                {
                    //data.get(position).getCDN_URL() + data.get(position).getMedia_URL() + "/" + data.get(position).getFile_Name()
                    url_list.add(photo.getCDN_URL() + photo.getMedia_URL() + "/" + photo.getFile_Name());
                }
                Photo photo = photoList.get(adapterPosition);
                //MyApplication.getInstance().trackEvent("PhotoHistory", "View Photo detail ", "View PhotoHistory");
                /*Intent intent = new Intent(photoHistory, PhotoHistoryDetail.class);
                intent.putExtra("position", adapterPosition);
                photoHistory.startActivity(intent);*/
                ////////////////////////////////////////////////
               /* List<String> url_list = new ArrayList<>();
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_25_17_20_28_1629886828_370381.jpg");
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_25_17_03_37_1629885817_318689.jpg");
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_25_16_58_23_1629885503_331657.jpg");
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_25_16_48_39_1629884919_280146.jpg");
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_25_16_47_45_1629884865_370547.jpg");
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_25_16_43_07_1629884587_389594.jpg");
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_21_10_08_38_screenshot_2021-03-24-15-25-48-40_63657.jpg");
                url_list.add("http://media.secondclone.com/media/picture/76cad4a2ecea5e59/thumb/l/76cad4a2ecea5e59_2021_08_25_16_43_07_1629884587_389594.jpg");
*/
                Dialog dialog = new PopopDialogBuilder(mActivity)
                        // Set list like as option1 or option2 or option3
                        .setList(url_list, adapterPosition)
                        // or setList with initial position that like .setList(list,position)
                        // Set dialog header color
                        .setHeaderBackgroundColor(android.R.color.holo_blue_light)
                        // Set dialog background color
                        .setDialogBackgroundColor(R.color.color_dialog_bg)
                        // Set close icon drawable
                        .setCloseDrawable(R.drawable.ic_close_white_24dp)
                        // Set loading view for pager image and preview image
                        .setLoadingView(R.layout.loading_view)
                        // Set dialog style
                        .setDialogStyle(R.style.DialogStyle)
                        // Choose selector type, indicator or thumbnail
                        .showThumbSlider(true)
                        // Set image scale type for slider image
                        .setSliderImageScaleType(ImageView.ScaleType.FIT_XY)
                        // Set indicator drawable
                        .setSelectorIndicator(R.drawable.sample_indicator_selector)
                        // Enable or disable zoomable
                        .setIsZoomable(true)
                        // Build Km Slider Popup Dialog
                        .build();

                dialog.show();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            ((com.scp.viewer.View.PhotoHistory)mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }
    }

    public void removeData(ArrayList<Photo> list) {
        for (Photo photo : list) {
            photoList.remove(photo);
        }
        notifyDataSetChanged();
    }
}