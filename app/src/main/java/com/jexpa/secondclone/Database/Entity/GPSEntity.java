/*
  ClassName: GPSEntity.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Database.Entity;

public class GPSEntity {

    /**
     * The column name values of the GetLocation table.
     */
    public static final String TABLE_GETLOCATION = "Location";
    public static final String DATABASE_NAME_GETLOCATION = "LocationManager";
    public static final int DATABASE_VERSION_GETLOCATION = 1;
    public static final String COLUMN_GETLOCATION_ROWINDEX = "RowIndex";
    public static final String COLUMN_GETLOCATION_ID = "ID";
    public static final String COLUMN_GETLOCATION_DEVICE_ID = "Device_ID";
    public static final String COLUMN_GETLOCATION_CLIENT_GPS_TIME = "Client_GPS_Time";
    public static final String COLUMN_GETLOCATION_LATITUDE = "Latitude";
    public static final String COLUMN_GETLOCATION_LONGITUDE = "Longitude";
    public static final String COLUMN_GETLOCATION_ACCURACY = "Accuracy";
    public static final String COLUMN_GETLOCATION_CREATED_DATE = "Created_Date";

}
