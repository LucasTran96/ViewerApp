/**
 * ClassName: Table.java
 * AppName: CP9
 * Created by Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Table implements Serializable {

    private String ID;
    private String Login_Name;
    private String Device_Identifier;
    private String Device_Name;
    private String Device_Token;
    private String Phone_Number;
    private String OS_Device;
    private String App_Version_Number;
    private String ICCID;
    private String IMSI;
    private String IMEI;
    private String Country;
    private String SimCard_Info;
    private String Status_Message;
    private String Is_Rooted_Or_Jailbroken;
    private String GPS_Option_Turned;
    private String Modified_By;
    private String Modified_Date;
    private String Created_By;
    private String Created_Date;
    private String Brand_ID;
    private boolean Wifi_Enabled;
    private String Battery;
    private String Last_Online;

    public Table(String ID, String login_Name, String device_ID, String device_Name, String device_Token, String phone_Number, String OS_Device, String app_Version_Number, String ICCID, String IMSI, String IMEI, String country, String simCard_Info, String status_Message, String is_Rooted_Or_Jailbroken, String GPS_Option_Turned, String modified_By, String modified_Date, String created_By, String created_Date, String brand_ID, boolean wifi_Enabled, String battery, String last_Online) {
        this.ID = ID;
        Login_Name = login_Name;
        Device_Identifier = device_ID;
        Device_Name = device_Name;
        Device_Token = device_Token;
        Phone_Number = phone_Number;
        this.OS_Device = OS_Device;
        App_Version_Number = app_Version_Number;
        this.ICCID = ICCID;
        this.IMSI = IMSI;
        this.IMEI = IMEI;
        Country = country;
        SimCard_Info = simCard_Info;
        Status_Message = status_Message;
        Is_Rooted_Or_Jailbroken = is_Rooted_Or_Jailbroken;
        this.GPS_Option_Turned = GPS_Option_Turned;
        Modified_By = modified_By;
        Modified_Date = modified_Date;
        Created_By = created_By;
        Created_Date = created_Date;
        Brand_ID = brand_ID;
        Wifi_Enabled = wifi_Enabled;
        Battery = battery;
        Last_Online = last_Online;
    }

    public Table() {
    }

    public String getBrand_ID() {
        return Brand_ID;
    }

    public void setBrand_ID(String brand_ID) {
        Brand_ID = brand_ID;
    }

    public boolean getWifi_Enabled() {
        return Wifi_Enabled;
    }

    public void setWifi_Enabled(boolean wifi_Enabled) {
        Wifi_Enabled = wifi_Enabled;
    }

    public String getBattery() {
        return Battery;
    }

    public void setBattery(String battery) {
        Battery = battery;
    }

    public String getLast_Online() {
        return Last_Online;
    }

    public void setLast_Online(String last_Online) {
        Last_Online = last_Online;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLogin_Name() {
        return Login_Name;
    }

    public void setLogin_Name(String login_Name) {
        Login_Name = login_Name;
    }

    public String getDevice_Identifier() {
        return Device_Identifier;
    }

    public void setDevice_Identifier(String device_Identifier) {
        Device_Identifier = device_Identifier;
    }

    public String getDevice_Name() {
        return Device_Name;
    }

    public void setDevice_Name(String device_Name) {
        Device_Name = device_Name;
    }

    public String getDevice_Token() {
        return Device_Token;
    }

    public void setDevice_Token(String device_Token) {
        Device_Token = device_Token;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }

    public String getOS_Device() {
        return OS_Device;
    }

    public void setOS_Device(String OS_Device) {
        this.OS_Device = OS_Device;
    }

    public String getApp_Version_Number() {
        return App_Version_Number;
    }

    public void setApp_Version_Number(String app_Version_Number) {
        App_Version_Number = app_Version_Number;
    }

    public String getICCID() {
        return ICCID;
    }

    public void setICCID(String ICCID) {
        this.ICCID = ICCID;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getSimCard_Info() {
        return SimCard_Info;
    }

    public void setSimCard_Info(String simCard_Info) {
        SimCard_Info = simCard_Info;
    }

    public String getStatus_Message() {
        return Status_Message;
    }

    public void setStatus_Message(String status_Message) {
        Status_Message = status_Message;
    }

    public String getIs_Rooted_Or_Jailbroken() {
        return Is_Rooted_Or_Jailbroken;
    }

    public void setIs_Rooted_Or_Jailbroken(String is_Rooted_Or_Jailbroken) {
        Is_Rooted_Or_Jailbroken = is_Rooted_Or_Jailbroken;
    }

    public String getGPS_Option_Turned() {
        return GPS_Option_Turned;
    }

    public void setGPS_Option_Turned(String GPS_Option_Turned) {
        this.GPS_Option_Turned = GPS_Option_Turned;
    }

    public String getModified_By() {
        return Modified_By;
    }

    public void setModified_By(String modified_By) {
        Modified_By = modified_By;
    }

    public String getModified_Date() {
        return Modified_Date;
    }

    public void setModified_Date(String modified_Date) {
        Modified_Date = modified_Date;
    }

    public String getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(String created_By) {
        Created_By = created_By;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }
}
