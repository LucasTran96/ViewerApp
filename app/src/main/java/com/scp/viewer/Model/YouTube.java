/*
  ClassName: YouTube.java
  Project: ViewerApp
  Author: Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-19
  Description:
  History:2021-07-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class YouTube implements Serializable {

    private String Device_ID, Client_Youtube_Time, Video_Name, Channel_Name, Views, Created_Date;
    private int RowIndex, ID;

    public YouTube() {
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Youtube_Time() {
        return Client_Youtube_Time;
    }

    public void setClient_Youtube_Time(String client_Youtube_Time) {
        Client_Youtube_Time = client_Youtube_Time;
    }

    public String getVideo_Name() {
        return Video_Name;
    }

    public void setVideo_Name(String video_Name) {
        Video_Name = video_Name;
    }

    public String getChannel_Name() {
        return Channel_Name;
    }

    public void setChannel_Name(String channel_Name) {
        Channel_Name = channel_Name;
    }

    public String getViews() {
        return Views;
    }

    public void setViews(String views) {
        Views = views;
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
