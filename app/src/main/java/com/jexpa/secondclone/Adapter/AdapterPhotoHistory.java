/*
  ClassName: AdapterPhotoHistory.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-11-16
  Description: class AdapterPhotoHistory used to customize the adapter for the RecyclerView of the "PhotoHistory.class"
  History:2018-11-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.jexpa.secondclone.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jexpa.secondclone.Model.Photo;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.MyApplication;
import com.jexpa.secondclone.View.PhotoHistoryDetail;
import com.jexpa.secondclone.View.PhotoHistory;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.Global.File_PATH_SAVE_IMAGE;


public class AdapterPhotoHistory extends RecyclerView.Adapter {

    public static SparseBooleanArray itemStateArrayPhoto = new SparseBooleanArray();
    private PhotoHistory photoHistory;
    private static List<Photo> photoList;

    public AdapterPhotoHistory(Activity context, List<Photo> photoList) {
        AdapterPhotoHistory.photoList = photoList;
        photoHistory = (PhotoHistory) context;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_photo_history, parent, false);
        return new PhotoHolder(view, photoHistory);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        if(photo != null)
        {
            ((PhotoHolder) holder).bind(photo, position);
        }

    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox checkBox_photo;
        View mmView, view_Show;
        ImageView img_photo_History;
        FrameLayout ln_Photo;
        PhotoHistory photoHistorySmall;

        PhotoHolder(View itemView, PhotoHistory photoHistory) {

            super(itemView);
            this.setIsRecyclable(false);
            this.photoHistorySmall = photoHistory;
            ln_Photo = itemView.findViewById(R.id.ln_Photo);
            img_photo_History = itemView.findViewById(R.id.img_photo_History);
            checkBox_photo = itemView.findViewById(R.id.cb_Check_PhotoListDelete);
            checkBox_photo.setEnabled(false);
            mmView = itemView;
            view_Show = itemView.findViewById(R.id.view_Show);
            view_Show.setVisibility(View.GONE);
            ln_Photo.setOnLongClickListener(photoHistory);
            ln_Photo.setOnClickListener(this);

        }

        @SuppressLint("ResourceAsColor")
        void bind(Photo photo, int position) {
            String filepath = File_PATH_SAVE_IMAGE;
            if (isConnected(photoHistory)) {
                if (photoList.get(position).getIsLoaded() == 1) {
                    Picasso.with(photoHistory).load(new File(filepath, photo.getFile_Name())).error(R.drawable.no_image).placeholder(R.drawable.spinner).into(img_photo_History);

                } else {
                    Glide.with(photoHistory).load(photo.getCDN_URL() + photo.getMedia_URL() + "/thumb/l" + "/" + photo.getFile_Name())
                            .thumbnail(0.5f).override(200, 200).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.spinner).error(R.drawable.no_image)
                            .into(img_photo_History);
                }
            } else {

                Picasso.with(photoHistory).load(new File(filepath, photo.getFile_Name())).error(R.drawable.no_image).placeholder(R.drawable.spinner).into(img_photo_History);
            }

            if (!PhotoHistory.isInActionMode) {

                checkBox_photo.setVisibility(View.GONE);

            } else {
                checkBox_photo.setVisibility(View.VISIBLE);
            }

            if (!itemStateArrayPhoto.get(position, false)) {
                checkBox_photo.setChecked(false);
                view_Show.setVisibility(View.GONE);
            } else {
                checkBox_photo.setChecked(true);
                view_Show.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            if (PhotoHistory.isInActionMode) {
                photoHistory.prepareSelection(getAdapterPosition());

                if (!itemStateArrayPhoto.get(adapterPosition, false)) {
                    checkBox_photo.setChecked(true);
                    view_Show.setVisibility(View.VISIBLE);
                    itemStateArrayPhoto.put(adapterPosition, true);
                } else {
                    checkBox_photo.setChecked(false);
                    view_Show.setVisibility(View.GONE);
                    itemStateArrayPhoto.put(adapterPosition, false);
                }
            } else if (adapterPosition != RecyclerView.NO_POSITION) {
                MyApplication.getInstance().trackEvent("PhotoHistory", "View Photo detail ", "View PhotoHistory");
                Intent intent = new Intent(photoHistory, PhotoHistoryDetail.class);
                intent.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) photoList);
                intent.putExtra("position", adapterPosition);
                photoHistory.startActivity(intent);
            }
        }
    }

    public void removeData(ArrayList<Photo> list) {
        for (Photo photo : list) {
            photoList.remove(photo);
        }
        notifyDataSetChanged();
    }
}