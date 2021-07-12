/*
  ClassName: AmbientRecordEntity.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */
package com.scp.viewer.Database.Entity;

public class AmbientRecordEntity {

    public static final String TABLE_AMBIENTRECORD_HISTORY = "AmbientRecordHistory";
    public static final String COLUMN_DEVICE_ID_AMBIENTRECORD = "deviceID";
    public static final String COLUMN_AUDIO_NAME_AMBIENTRECORD = "fileName";
    public static final String COLUMN_DURATION_AMBIENTRECORD = "duration";
    public static final String COLUMN_AUDIO_SIZE_AMBIENTRECORD = "size";
    public static final String COLUMN_CREATED_DATE_AMBIENTRECORD = "date";
    public static final String COLUMN_CDN_URL_AMBIENTRECORD = "AmbientMediaLink";
    public static final String COLUMN_ISSAVED_AMBIENTRECORD = "isSaved";

    /*
    private String date, fileName, duration, AmbientMediaLink, deviceID;
    private int isSaved;
    private long size;
     */
}
