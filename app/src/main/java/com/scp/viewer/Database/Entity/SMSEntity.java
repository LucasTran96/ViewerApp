/*
  ClassName: SMSEntity.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database.Entity;

public class SMSEntity {

    /**
     * The column name values of the  SMSTable table.
     * APP_TYPE_ALL = -1,
    *  APP_TYPE_DEFAULT = 0,
    *  APP_TYPE_IMESSAGGE = 10000,
    *  APP_TYPE_WHATSAPP = 1,
    *  APP_TYPE_YAHOO_MESSENGER = 2,
    *  APP_TYPE_VIBER = 3,
    *  APP_TYPE_FACEBOOK = 4,
    *  APP_TYPE_SKYPE = 5,
    *  APP_TYPE_TANGO = 6,
    *  APP_TYPE_WECHAT = 7,
    *  APP_TYPE_OLA = 8,
    *  APP_TYPE_HANGOUTS = 9,
    *  APP_TYPE_BBM = 10,
    *  APP_TYPE_LINE = 11,
    *  APP_TYPE_KIK = 12,
    *  APP_TYPE_TWITTER = 13,
    *  APP_TYPE_INSTAGRAM = 14,
    *  APP_TYPE_SNAPCHAT = 15,
    *  OUTGOING = 0,
    *  INCOMING = 1
     */

    public static final String TABLE_GET_SMS = "SMS_Table";
    public static final String TABLE_GET_WHATSAPP = "WhatsApp_Table";
    public static final String TABLE_GET_VIBER = "Viber_Table";
    public static final String TABLE_GET_FACEBOOK = "Facebook_Table";
    public static final String TABLE_GET_SKYPE = "Skype_Table";
    public static final String TABLE_GET_HANGOUTS = "Hangouts_Table";
    public static final String TABLE_GET_BBM = "BBM_Table";
    public static final String TABLE_GET_LINE = "LINE_Table";
    public static final String TABLE_GET_KIK = "KIK_Table";
    public static final String TABLE_GET_INSTAGRAM = "Instagram_Table"; // 2021-07-23
    public static final String COLUMN_ID_SMS = "ID";
    public static final String COLUMN_DEVICE_ID_SMS = "Device_ID";
    public static final String COLUMN_CLIENT_MESSAGE_TIME_SMS = "Client_Message_Time";
    public static final String COLUMN_PHONE_NUMBER_SIM_SMS = "Phone_Number_SIM";
    public static final String COLUMN_PHONE_NUMBER_SMS = "Phone_Number";
    public static final String COLUMN_DIRECTION_SMS = "Direction";
    public static final String COLUMN_TEXT_MESSAGE_SMS = "Text_Message";
    public static final String COLUMN_CONTACT_NAME_SMS = "Contact_Name";
    public static final String COLUMN_CREATED_DATE_SMS = "Created_Date";
}
