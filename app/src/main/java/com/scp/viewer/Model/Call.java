/*
  ClassName: Call.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description:
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Call implements Serializable {

    private String Device_ID,
            Client_Call_Time,
            Phone_Number_SIM,
            Phone_Number;
    private String Contact_Name,
            Created_Date;
    private int ID, RowIndex,
            Direction, Duration;


    public Call() {

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Call_Time() {
        return Client_Call_Time;
    }

    public void setClient_Call_Time(String client_Call_Time) {
        Client_Call_Time = client_Call_Time;
    }

    public String getPhone_Number_SIM() {
        return Phone_Number_SIM;
    }

    public void setPhone_Number_SIM(String phone_Number_SIM) {
        Phone_Number_SIM = phone_Number_SIM;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getContact_Name() {
        return Contact_Name;
    }

    public void setContact_Name(String contact_Name) {
        Contact_Name = contact_Name;
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
}
