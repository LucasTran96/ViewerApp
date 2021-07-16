/*
  ClassName: Clipboard.java
  Project: ViewerApp
  Author: Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-13
  Description:
  History:2021-07-13
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Clipboard implements Serializable {

    private String Device_ID, Client_Clipboard_Time, Clipboard_Content, Created_Date, From_App;
    private int RowIndex, ID;

    public Clipboard() {
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Clipboard_Time() {
        return Client_Clipboard_Time;
    }

    public void setClient_Clipboard_Time(String client_Clipboard_Time) {
        Client_Clipboard_Time = client_Clipboard_Time;
    }

    public String getClipboard_Content() {
        return Clipboard_Content;
    }

    public void setClipboard_Content(String clipboard_Content) {
        Clipboard_Content = clipboard_Content;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }

    public String getFrom_App() {
        return From_App;
    }

    public void setFrom_App(String from_App) {
        From_App = from_App;
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
