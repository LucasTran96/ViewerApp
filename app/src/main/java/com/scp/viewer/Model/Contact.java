/**
 * ClassName: Contact.java
 * @Project: CP9
 * @author  Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Model;

import java.io.Serializable;

public class Contact implements Serializable {

    private String Device_ID, Client_Contact_Time,
            Contact_Name, Phone, Email, Organization, Address, Created_Date;
    private int color;
    private long RowIndex, ID;

    public Contact() {
    }

    public Contact(String device_ID, String client_Contact_Time, String contact_Name, String phone, String email, String organization, String address, String created_Date, long rowIndex, long ID) {
        Device_ID = device_ID;
        Client_Contact_Time = client_Contact_Time;
        Contact_Name = contact_Name;
        Phone = phone;
        Email = email;
        Organization = organization;
        Address = address;
        Created_Date = created_Date;
        RowIndex = rowIndex;
        this.ID = ID;

    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getClient_Contact_Time() {
        return Client_Contact_Time;
    }

    public void setClient_Contact_Time(String client_Contact_Time) {
        Client_Contact_Time = client_Contact_Time;
    }

    public String getContact_Name() {
        return Contact_Name;
    }

    public void setContact_Name(String contact_Name) {
        Contact_Name = contact_Name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getOrganization() {
        return Organization;
    }

    public void setOrganization(String organization) {
        Organization = organization;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
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
}
