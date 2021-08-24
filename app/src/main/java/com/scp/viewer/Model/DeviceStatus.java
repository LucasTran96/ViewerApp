package com.scp.viewer.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceStatus {

    @SerializedName("ID")
    @Expose
    private long id;
    @SerializedName("Device_ID")
    @Expose
    private long deviceID;
    @SerializedName("Is_Rooted_Or_Jailbroken")
    @Expose
    private Boolean isRootedOrJailbroken;
    @SerializedName("GPS_Option_Turned")
    @Expose
    private Boolean gPSOptionTurned;
    @SerializedName("Status_Messages")
    @Expose
    private String statusMessages;
    @SerializedName("Battery")
    @Expose
    private String battery;
    @SerializedName("Battery_Status")
    @Expose
    private Boolean batteryStatus;
    @SerializedName("Last_Online")
    @Expose
    private String lastOnline;
    @SerializedName("Wifi_Enabled")
    @Expose
    private Boolean wifiEnabled;
    @SerializedName("Modified_Date")
    @Expose
    private String modifiedDate;
    @SerializedName("Created_Date")
    @Expose
    private String createdDate;

    public DeviceStatus() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(long deviceID) {
        this.deviceID = deviceID;
    }

    public Boolean getRootedOrJailbroken() {
        return isRootedOrJailbroken;
    }

    public void setRootedOrJailbroken(Boolean rootedOrJailbroken) {
        isRootedOrJailbroken = rootedOrJailbroken;
    }

    public Boolean getgPSOptionTurned() {
        return gPSOptionTurned;
    }

    public void setgPSOptionTurned(Boolean gPSOptionTurned) {
        this.gPSOptionTurned = gPSOptionTurned;
    }

    public String getStatusMessages() {
        return statusMessages;
    }

    public void setStatusMessages(String statusMessages) {
        this.statusMessages = statusMessages;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public Boolean getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(Boolean batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public Boolean getWifiEnabled() {
        return wifiEnabled;
    }

    public void setWifiEnabled(Boolean wifiEnabled) {
        this.wifiEnabled = wifiEnabled;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
