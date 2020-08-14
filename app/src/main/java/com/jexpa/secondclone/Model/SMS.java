/**
 * ClassName: SMS.java
 * AppName: SecondClone
 * Created by Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Model;

import java.io.Serializable;

public class SMS implements Serializable {


    private int ID;
    private String Device_ID;
    private String Client_Message_Time;
    private String Phone_Number_SIM;
    private String Phone_Number;
    private int Direction;
    private String Text_Message;
    private String Contact_Name;
    private String Created_Date;

    public SMS(int ID, String device_ID, String client_Message_Time, String phone_Number_SIM
            , String phone_Number, int direction, String text_Message, String contact_Name
            , String created_Date) {
        this.ID = ID;
        Device_ID = device_ID;
        Client_Message_Time = client_Message_Time;
        Phone_Number_SIM = phone_Number_SIM;
        Phone_Number = phone_Number;
        Direction = direction;
        Text_Message = text_Message;
        Contact_Name = contact_Name;
        Created_Date = created_Date;
    }

    public SMS() {
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

    public String getClient_Message_Time() {
        return Client_Message_Time;
    }

    public void setClient_Message_Time(String client_Message_Time) {
        Client_Message_Time = client_Message_Time;
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

    public String getText_Message() {
        return Text_Message;
    }

    public void setText_Message(String text_Message) {
        Text_Message = text_Message;
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
}


