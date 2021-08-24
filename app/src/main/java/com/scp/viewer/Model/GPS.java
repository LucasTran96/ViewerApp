/**
 * ClassName: GPS.java
 * @Project: CP9
 * @author  Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class GPS implements Serializable {

    private int RowIndex;
    private long ID;
    private String Device_ID;
    private String Client_GPS_Time;
    private Double Latitude;
    private Double Longitude;
    private int Accuracy;
    private String Created_Date;


    public GPS(int rowIndex, long ID, String device_ID, String client_GPS_Time, Double latitude, Double longitude, int accuracy, String created_Date) {
        RowIndex = rowIndex;
        this.ID = ID;
        Device_ID = device_ID;
        Client_GPS_Time = client_GPS_Time;
        Latitude = latitude;
        Longitude = longitude;
        Accuracy = accuracy;
        Created_Date = created_Date;
    }

    public GPS() {
    }

    public int getRowIndex() {
        return RowIndex;
    }

    public void setRowIndex(int rowIndex) {
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

    public String getClient_GPS_Time() {
        return Client_GPS_Time;
    }

    public void setClient_GPS_Time(String client_GPS_Time) {
        Client_GPS_Time = client_GPS_Time;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public int getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(int accuracy) {
        Accuracy = accuracy;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }
    /**
     * "RowIndex":3,
     "ID":239305087,
     "Device_ID":"D22228E7-9697-4BCC-A87B-382BF57624D2",
     "Client_GPS_Time":"2018-06-25T15:32:02",
     "Location_Address": "Newyork"
     "Latitude":10.1123323,
     "Longitude":106.1234444,
     "Accuracy":0,
     "Created_Date":"2018-06-25T01:32:03"
     */
}
