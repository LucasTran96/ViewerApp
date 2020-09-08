/**
 * ClassName: Photo.java
 * AppName: SecondClone
 * Created by Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-11-16
 * Description:
 * History:2018-11-19
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {

    private int IsLoaded;
    private long rowIndex, ID;
    private String device_ID, client_Captured_Date, caption, file_Name, ext, media_URL, created_Date, CDN_URL;

    public Photo(Parcel in) {
        rowIndex = in.readInt();
        ID = in.readInt();
        IsLoaded = in.readInt();
        device_ID = in.readString();
        client_Captured_Date = in.readString();
        caption = in.readString();
        file_Name = in.readString();
        ext = in.readString();
        media_URL = in.readString();
        created_Date = in.readString();
        CDN_URL = in.readString();
    }

    public Photo() {

    }

    public long getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(long rowIndex) {
        this.rowIndex = rowIndex;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public int getIsLoaded() {
        return IsLoaded;
    }

    public void setIsLoaded(int isLoaded) {
        IsLoaded = isLoaded;
    }

    public String getDevice_ID() {
        return device_ID;
    }

    public void setDevice_ID(String device_ID) {
        this.device_ID = device_ID;
    }

    public String getClient_Captured_Date() {
        return client_Captured_Date;
    }

    public void setClient_Captured_Date(String client_Captured_Date) {
        this.client_Captured_Date = client_Captured_Date;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFile_Name() {
        return file_Name;
    }

    public void setFile_Name(String file_Name) {
        this.file_Name = file_Name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMedia_URL() {
        return media_URL;
    }

    public void setMedia_URL(String media_URL) {
        this.media_URL = media_URL;
    }

    public String getCreated_Date() {
        return created_Date;
    }

    public void setCreated_Date(String created_Date) {
        this.created_Date = created_Date;
    }

    public String getCDN_URL() {
        return CDN_URL;
    }

    public void setCDN_URL(String CDN_URL) {
        this.CDN_URL = CDN_URL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(rowIndex);
        parcel.writeLong(ID);
        parcel.writeInt(IsLoaded);
        parcel.writeString(device_ID);
        parcel.writeString(client_Captured_Date);
        parcel.writeString(caption);
        parcel.writeString(file_Name);
        parcel.writeString(ext);
        parcel.writeString(media_URL);
        parcel.writeString(created_Date);
        parcel.writeString(CDN_URL);
    }
}
