/*
  ClassName: Notifications.java
  Project: ViewerApp
  Author: Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-19
  Description:
  History:2021-07-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Notifications implements Serializable {

    private String Device_ID, App_Name, Notification_Title, Notification_Content, Client_Notification_Time, Created_Date;
    private int RowIndex, ID;

    public Notifications() {
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Notification_Time() {
        return Client_Notification_Time;
    }

    public void setClient_Notification_Time(String client_Notification_Time) {
        Client_Notification_Time = client_Notification_Time;
    }

    public String getApp_Name() {
        return App_Name;
    }

    public void setApp_Name(String app_Name) {
        App_Name = app_Name;
    }

    public String getNotification_Title() {
        return Notification_Title;
    }

    public void setNotification_Title(String notification_Title) {
        Notification_Title = notification_Title;
    }

    public String getNotification_Content() {
        return Notification_Content;
    }

    public void setNotification_Content(String notification_Content) {
        Notification_Content = notification_Content;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
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
}
