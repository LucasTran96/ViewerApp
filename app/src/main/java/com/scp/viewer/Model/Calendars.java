/*
  ClassName: Calendar.java
  Project: ViewerApp
  Author: Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2021-07-15
  Description: this is a calendar object that gets data from the json of the returned protocols.
  History:2021-07-15
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Calendars implements Serializable {

    private String Device_ID, Client_Calendar_Time, From_Date,
            To_Date, Location, Title, Repetition, Created_Date;
    private int RowIndex, ID;


    public Calendars() {
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Calendar_Time() {
        return Client_Calendar_Time;
    }

    public void setClient_Calendar_Time(String client_Calendar_Time) {
        Client_Calendar_Time = client_Calendar_Time;
    }

    public String getFrom_Date() {
        return From_Date;
    }

    public void setFrom_Date(String from_Date) {
        From_Date = from_Date;
    }

    public String getTo_Date() {
        return To_Date;
    }

    public void setTo_Date(String to_Date) {
        To_Date = to_Date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getRepetition() {
        return Repetition;
    }

    public void setRepetition(String repetition) {
        Repetition = repetition;
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
