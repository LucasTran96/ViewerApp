/*
  ClassName: AccountInFo.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description:
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class AccountInFo implements Serializable {

    private String ID;
    private String Login_Code;
    private String Password;
    private String Expiry_Date;
    private String Status;
    private String Created_Date;
    private String Modified_Date;
    private String User_Type;
    private String Nick_Name;
    private String Max_Device;
    private String Package_Name;
    private String Tracking_Level;
    private String Last_IP_Access;
    private String Last_Time_Access;
    private String Time_Zone_ID;

    public AccountInFo() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLogin_Code() {
        return Login_Code;
    }

    public void setLogin_Code(String login_Code) {
        Login_Code = login_Code;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getExpiry_Date() {
        return Expiry_Date;
    }

    public void setExpiry_Date(String expiry_Date) {
        Expiry_Date = expiry_Date;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }

    public void setModified_Date(String modified_Date) {
        Modified_Date = modified_Date;
    }

    public void setUser_Type(String user_Type) {
        User_Type = user_Type;
    }

    public void setNick_Name(String nick_Name) {
        Nick_Name = nick_Name;
    }

    public void setMax_Device(String max_Device) {
        Max_Device = max_Device;
    }

    public String getPackage_Name() {
        return Package_Name;
    }

    public void setPackage_Name(String package_Name) {
        Package_Name = package_Name;
    }

    public void setTracking_Level(String tracking_Level) {
        Tracking_Level = tracking_Level;
    }

    public void setLast_IP_Access(String last_IP_Access) {
        Last_IP_Access = last_IP_Access;
    }

    public void setLast_Time_Access(String last_Time_Access) {
        Last_Time_Access = last_Time_Access;
    }

    public void setTime_Zone_ID(String time_Zone_ID) {
        Time_Zone_ID = time_Zone_ID;
    }

    public String getStatus() {
        return Status;
    }

    public String getModified_Date() {
        return Modified_Date;
    }

    public String getUser_Type() {
        return User_Type;
    }

    public String getNick_Name() {
        return Nick_Name;
    }

    public String getMax_Device() {
        return Max_Device;
    }

    public String getTracking_Level() {
        return Tracking_Level;
    }

    public String getLast_IP_Access() {
        return Last_IP_Access;
    }

    public String getLast_Time_Access() {
        return Last_Time_Access;
    }

    public String getTime_Zone_ID() {
        return Time_Zone_ID;
    }
}
