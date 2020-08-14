/**
 * ClassName: DeviceFeature.java
 * @Project: SecondClone
 * @author  Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Model;

import java.io.Serializable;

public class DeviceFeature implements Serializable {

    private int ID;
    private String Device_ID;
    private String Calendar;
    private int SMS;
    private int Call;
    private int GPS;
    private String GPS_Interval;
    private int Report_Interval;
    private int URL;
    private String Email;
    private int Contact;
    private int Photo;
    private int App;
    private int Recorded_Call;
    private String Note;
    private String Video;
    private String Voice_Memos;
    private String Ambient_Record;
    private String Ambient_Record_Duration;
    private String WhatApp;
    private String Yahoo;
    private String KeyLogger;
    private String Secret_Key;
    private String Viber;
    private String Tango;
    private String Wechat;
    private String Facebook;
    private String Ola;
    private String Skype;
    private String Hangouts;
    private String Bbm;
    private String Line;
    private String Kik;
    private String Twitter;
    private String Instagram;
    private String Snapchat;
    private int Horizontal;
    private int Vertical;
    private String Monitor_Number;
    private String Admin_Number;
    private int Auto_Upgrade;
    private int Uninstall;
    private String URL_Server;
    private int Flush_Data_Even_Expired;
    private int Delivery_Logs_By_Email;
    private int Report_Problem;
    private int Level_Log_File;
    private String Save_Battery;
    private String Connection_Type;
    private String ServerTime;
    private String Client_Date;
    private int Silent_Call;
    private int Run_Mode;
    private String Exportcsv;
    private String Hide_Cydia;
    private String Modified_Date;
    private int Modified_By;
    private String Created_Date;
    private int Created_By;

    public DeviceFeature() {
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

    public String getCalendar() {
        return Calendar;
    }

    public void setCalendar(String calendar) {
        Calendar = calendar;
    }

    public int getSMS() {
        return SMS;
    }

    public void setSMS(int SMS) {
        this.SMS = SMS;
    }

    public int getCall() {
        return Call;
    }

    public void setCall(int call) {
        Call = call;
    }

    public int getGPS() {
        return GPS;
    }

    public void setGPS(int GPS) {
        this.GPS = GPS;
    }

    public String getGPS_Interval() {
        return GPS_Interval;
    }

    public void setGPS_Interval(String GPS_Interval) {
        this.GPS_Interval = GPS_Interval;
    }

    public int getReport_Interval() {
        return Report_Interval;
    }

    public void setReport_Interval(int report_Interval) {
        Report_Interval = report_Interval;
    }

    public int getURL() {
        return URL;
    }

    public void setURL(int URL) {
        this.URL = URL;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getContact() {
        return Contact;
    }

    public void setContact(int contact) {
        Contact = contact;
    }

    public int getPhoto() {
        return Photo;
    }

    public void setPhoto(int photo) {
        Photo = photo;
    }

    public int getApp() {
        return App;
    }

    public void setApp(int app) {
        App = app;
    }

    public int getRecorded_Call() {
        return Recorded_Call;
    }

    public void setRecorded_Call(int recorded_Call) {
        Recorded_Call = recorded_Call;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public String getVoice_Memos() {
        return Voice_Memos;
    }

    public void setVoice_Memos(String voice_Memos) {
        Voice_Memos = voice_Memos;
    }

    public String getAmbient_Record() {
        return Ambient_Record;
    }

    public void setAmbient_Record(String ambient_Record) {
        Ambient_Record = ambient_Record;
    }

    public String getAmbient_Record_Duration() {
        return Ambient_Record_Duration;
    }

    public void setAmbient_Record_Duration(String ambient_Record_Duration) {
        Ambient_Record_Duration = ambient_Record_Duration;
    }

    public String getWhatApp() {
        return WhatApp;
    }

    public void setWhatApp(String whatApp) {
        WhatApp = whatApp;
    }

    public String getYahoo() {
        return Yahoo;
    }

    public void setYahoo(String yahoo) {
        Yahoo = yahoo;
    }

    public String getKeyLogger() {
        return KeyLogger;
    }

    public void setKeyLogger(String keyLogger) {
        KeyLogger = keyLogger;
    }

    public String getSecret_Key() {
        return Secret_Key;
    }

    public void setSecret_Key(String secret_Key) {
        Secret_Key = secret_Key;
    }

    public String getViber() {
        return Viber;
    }

    public void setViber(String viber) {
        Viber = viber;
    }

    public String getTango() {
        return Tango;
    }

    public void setTango(String tango) {
        Tango = tango;
    }

    public String getWechat() {
        return Wechat;
    }

    public void setWechat(String wechat) {
        Wechat = wechat;
    }

    public String getFacebook() {
        return Facebook;
    }

    public void setFacebook(String facebook) {
        Facebook = facebook;
    }

    public String getOla() {
        return Ola;
    }

    public void setOla(String ola) {
        Ola = ola;
    }

    public String getSkype() {
        return Skype;
    }

    public void setSkype(String skype) {
        Skype = skype;
    }

    public String getHangouts() {
        return Hangouts;
    }

    public void setHangouts(String hangouts) {
        Hangouts = hangouts;
    }

    public String getBbm() {
        return Bbm;
    }

    public void setBbm(String bbm) {
        Bbm = bbm;
    }

    public String getLine() {
        return Line;
    }

    public void setLine(String line) {
        Line = line;
    }

    public String getKik() {
        return Kik;
    }

    public void setKik(String kik) {
        Kik = kik;
    }

    public String getTwitter() {
        return Twitter;
    }

    public void setTwitter(String twitter) {
        Twitter = twitter;
    }

    public String getInstagram() {
        return Instagram;
    }

    public void setInstagram(String instagram) {
        Instagram = instagram;
    }

    public String getSnapchat() {
        return Snapchat;
    }

    public void setSnapchat(String snapchat) {
        Snapchat = snapchat;
    }

    public int getHorizontal() {
        return Horizontal;
    }

    public void setHorizontal(int horizontal) {
        Horizontal = horizontal;
    }

    public int getVertical() {
        return Vertical;
    }

    public void setVertical(int vertical) {
        Vertical = vertical;
    }

    public String getMonitor_Number() {
        return Monitor_Number;
    }

    public void setMonitor_Number(String monitor_Number) {
        Monitor_Number = monitor_Number;
    }

    public String getAdmin_Number() {
        return Admin_Number;
    }

    public void setAdmin_Number(String admin_Number) {
        Admin_Number = admin_Number;
    }

    public int getAuto_Upgrade() {
        return Auto_Upgrade;
    }

    public void setAuto_Upgrade(int auto_Upgrade) {
        Auto_Upgrade = auto_Upgrade;
    }

    public int getUninstall() {
        return Uninstall;
    }

    public void setUninstall(int uninstall) {
        Uninstall = uninstall;
    }

    public String getURL_Server() {
        return URL_Server;
    }

    public void setURL_Server(String URL_Server) {
        this.URL_Server = URL_Server;
    }

    public int getFlush_Data_Even_Expired() {
        return Flush_Data_Even_Expired;
    }

    public void setFlush_Data_Even_Expired(int flush_Data_Even_Expired) {
        Flush_Data_Even_Expired = flush_Data_Even_Expired;
    }

    public int getDelivery_Logs_By_Email() {
        return Delivery_Logs_By_Email;
    }

    public void setDelivery_Logs_By_Email(int delivery_Logs_By_Email) {
        Delivery_Logs_By_Email = delivery_Logs_By_Email;
    }

    public int getReport_Problem() {
        return Report_Problem;
    }

    public void setReport_Problem(int report_Problem) {
        Report_Problem = report_Problem;
    }

    public int getLevel_Log_File() {
        return Level_Log_File;
    }

    public void setLevel_Log_File(int level_Log_File) {
        Level_Log_File = level_Log_File;
    }

    public String getSave_Battery() {
        return Save_Battery;
    }

    public void setSave_Battery(String save_Battery) {
        Save_Battery = save_Battery;
    }

    public String getConnection_Type() {
        return Connection_Type;
    }

    public void setConnection_Type(String connection_Type) {
        Connection_Type = connection_Type;
    }

    public String getServerTime() {
        return ServerTime;
    }

    public void setServerTime(String serverTime) {
        ServerTime = serverTime;
    }

    public String getClient_Date() {
        return Client_Date;
    }

    public void setClient_Date(String client_Date) {
        Client_Date = client_Date;
    }

    public int getSilent_Call() {
        return Silent_Call;
    }

    public void setSilent_Call(int silent_Call) {
        Silent_Call = silent_Call;
    }

    public int getRun_Mode() {
        return Run_Mode;
    }

    public void setRun_Mode(int run_Mode) {
        Run_Mode = run_Mode;
    }

    public String getExportcsv() {
        return Exportcsv;
    }

    public void setExportcsv(String exportcsv) {
        Exportcsv = exportcsv;
    }

    public String getHide_Cydia() {
        return Hide_Cydia;
    }

    public void setHide_Cydia(String hide_Cydia) {
        Hide_Cydia = hide_Cydia;
    }

    public String getModified_Date() {
        return Modified_Date;
    }

    public void setModified_Date(String modified_Date) {
        Modified_Date = modified_Date;
    }

    public int getModified_By() {
        return Modified_By;
    }

    public void setModified_By(int modified_By) {
        Modified_By = modified_By;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public void setCreated_Date(String created_Date) {
        Created_Date = created_Date;
    }

    public int getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(int created_By) {
        Created_By = created_By;
    }
}
