package com.jexpa.secondclone.Model;

import java.io.Serializable;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: AmbientRecord
 * History: 8/18/2020
 * Project: SecondClone
 */
public class AmbientRecord implements Serializable {

    private String date, fileName, duration, AmbientMediaLink, deviceID;
    private int isSaved;
    private long size;

    public AmbientRecord() {
    }


    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(int isSaved) {
        this.isSaved = isSaved;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getAmbientMediaLink() {
        return AmbientMediaLink;
    }

    public void setAmbientMediaLink(String ambientMediaLink) {
        AmbientMediaLink = ambientMediaLink;
    }

}
