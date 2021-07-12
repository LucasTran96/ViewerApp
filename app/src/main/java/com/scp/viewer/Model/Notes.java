/*
  ClassName: Notes.java
 * @author  Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.scp.viewer.Model;

import java.io.Serializable;

public class Notes implements Serializable {

    private long RowIndex, ID;
    private String Device_ID, Client_Note_Time, Content, Created_Date;

    public Notes() {
    }

    public Notes(long rowIndex, long ID, String device_ID, String client_Note_Time, String content, String created_Date) {
        RowIndex = rowIndex;
        this.ID = ID;
        Device_ID = device_ID;
        Client_Note_Time = client_Note_Time;
        Content = content;
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

    public String getClient_Note_Time() {
        return Client_Note_Time;
    }

    public void setClient_Note_Time(String client_Note_Time) {
        Client_Note_Time = client_Note_Time;
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
}
