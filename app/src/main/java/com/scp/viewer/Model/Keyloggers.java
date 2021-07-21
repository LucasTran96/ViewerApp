/*
  ClassName: KeyLoggers.java
  Project: ViewerApp
  Author: Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-19
  Description:
  History:2021-07-19
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Keyloggers implements Serializable {

    private String Device_ID, Client_KeyLogger_Time, KeyLogger_Name, Content, Created_Date;
    private int RowIndex, ID;

    public Keyloggers() {
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_KeyLogger_Time() {
        return Client_KeyLogger_Time;
    }

    public void setClient_KeyLogger_Time(String client_KeyLogger_Time) {
        Client_KeyLogger_Time = client_KeyLogger_Time;
    }

    public String getKeyLogger_Name() {
        return KeyLogger_Name;
    }

    public void setKeyLogger_Name(String keyLogger_Name) {
        KeyLogger_Name = keyLogger_Name;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
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
