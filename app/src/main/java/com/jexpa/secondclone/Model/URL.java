/**
 * ClassName: URL.java
 * AppName: SecondClone
 * Created by Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Model;

import java.io.Serializable;

public class URL implements Serializable {

    private long RowIndex, ID;
    private String Device_ID, Client_URL_Time, URL_Link, Created_Date;

    public URL() {
    }

    public URL(long rowIndex, long ID, String device_ID, String Client_URL_Time, String URL_Link, String created_Date) {
        RowIndex = rowIndex;
        this.ID = ID;
        Device_ID = device_ID;
        this.Client_URL_Time = Client_URL_Time;
        this.URL_Link = URL_Link;
        Created_Date = created_Date;
    }

    public long getRowIndex() {
        return RowIndex;
    }

    public void setRowIndex(long rowIndex) {
        RowIndex = rowIndex;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_URL_Time() {
        return Client_URL_Time;
    }

    public void setClient_URL_Time(String client_URL_Time) {
        Client_URL_Time = client_URL_Time;
    }

    public String getURL_Link() {
        return URL_Link;
    }

    public void setURL_Link(String URL_Link) {
        this.URL_Link = URL_Link;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }
}
