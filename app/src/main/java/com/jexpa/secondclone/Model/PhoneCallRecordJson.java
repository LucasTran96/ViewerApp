package com.jexpa.secondclone.Model;

import java.io.Serializable;

public class PhoneCallRecordJson implements Serializable {

    private int RowIndex, ID, Duration, Direction, Audio_Size;
    private String Device_ID, Client_Recorded_Date, Audio_Name, Content_Type, Phone_Number, Contact_Name, Ext, Media_URL, Created_Date;

    public PhoneCallRecordJson() {
    }

    public int getRowIndex() {
        return RowIndex;
    }

    public void setRowIndex(int rowIndex) {
        RowIndex = rowIndex;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public int getAudio_Size() {
        return Audio_Size;
    }

    public void setAudio_Size(int audio_Size) {
        Audio_Size = audio_Size;
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Recorded_Date() {
        return Client_Recorded_Date;
    }

    public void setClient_Recorded_Date(String client_Recorded_Date) {
        Client_Recorded_Date = client_Recorded_Date;
    }

    public String getAudio_Name() {
        return Audio_Name;
    }

    public void setAudio_Name(String audio_Name) {
        Audio_Name = audio_Name;
    }

    public String getContent_Type() {
        return Content_Type;
    }

    public void setContent_Type(String content_Type) {
        Content_Type = content_Type;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }

    public String getContact_Name() {
        return Contact_Name;
    }

    public void setContact_Name(String contact_Name) {
        Contact_Name = contact_Name;
    }

    public String getExt() {
        return Ext;
    }

    public void setExt(String ext) {
        Ext = ext;
    }

    public String getMedia_URL() {
        return Media_URL;
    }

    public void setMedia_URL(String media_URL) {
        Media_URL = media_URL;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }
}
