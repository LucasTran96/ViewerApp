/*
  ClassName: Networks.java
  Project: ViewerApp
  Author: Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-17
  Description:
  History:2021-07-17
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Networks implements Serializable {

    private String Device_ID, Client_Network_Connection_Time, Network_Connection_Name, Created_Date;
    private int RowIndex, ID, Network_Type, Status;

    public Networks() {
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Network_Connection_Time() {
        return Client_Network_Connection_Time;
    }

    public void setClient_Network_Connection_Time(String client_Network_Connection_Time) {
        Client_Network_Connection_Time = client_Network_Connection_Time;
    }

    public String getNetwork_Connection_Name() {
        return Network_Connection_Name;
    }

    public void setNetwork_Connection_Name(String network_Connection_Name) {
        Network_Connection_Name = network_Connection_Name;
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

    public int getNetwork_Type() {
        return Network_Type;
    }

    public void setNetwork_Type(int network_Type) {
        Network_Type = network_Type;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
