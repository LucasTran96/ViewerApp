/**
 * ClassName: PhotoJson.java
 * AppName: CP9
 * Created by Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-11-16
 * Description:
 * History:2018-11-19
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.cp9.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhotoJson implements Serializable {

    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("Device_Id")
    @Expose
    private String deviceId;
    @SerializedName("Client_Captured_Date")
    @Expose
    private String clientCapturedDate;
    @SerializedName("Caption")
    @Expose
    private String caption;
    @SerializedName("Ext")
    @Expose
    private String ext;
    @SerializedName("Location_Info")
    @Expose
    private Object locationInfo;
    @SerializedName("Latitude")
    @Expose
    private Integer latitude;
    @SerializedName("Longitude")
    @Expose
    private Integer longitude;
    @SerializedName("Media_URL")
    @Expose
    private String mediaURL;
    @SerializedName("File_Name")
    @Expose
    private String fileName;
    @SerializedName("Created_Date")
    @Expose
    private String createdDate;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getClientCapturedDate() {
        return clientCapturedDate;
    }

    public void setClientCapturedDate(String clientCapturedDate) {
        this.clientCapturedDate = clientCapturedDate;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Object getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(Object locationInfo) {
        this.locationInfo = locationInfo;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

}
