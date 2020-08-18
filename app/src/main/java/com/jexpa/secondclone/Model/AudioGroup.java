package com.jexpa.secondclone.Model;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: AudioGroup
 * History: 8/18/2020
 * Project: SecondClone
 */
public class AudioGroup {
    /*
    duration
    name
    date
    deviceID
    isSave
    Url
     */
    private String duration, contactName, date, deviceID, URL_Audio, audioName;
    private int isSave, isAmbient, ID;

    public AudioGroup() {
    }

    public String getAudioName() {
        return audioName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getIsAmbient() {
        return isAmbient;
    }

    public void setIsAmbient(int isAmbient) {
        this.isAmbient = isAmbient;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getURL_Audio() {
        return URL_Audio;
    }

    public void setURL_Audio(String URL_Audio) {
        this.URL_Audio = URL_Audio;
    }

    public int getIsSave() {
        return isSave;
    }

    public void setIsSave(int isSave) {
        this.isSave = isSave;
    }
}
