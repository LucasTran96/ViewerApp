/*
  ClassName: ApplicationUsageEntity.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Database.Entity;

public class ClipboardEntity {

    /**
     * Table name: ClipboardHistory
     */
    public static final String TABLE_CLIPBOARD_HISTORY = "ClipboardHistory";
    //public static final String COLUMN_ROW_INDEX_CLIPBOARD = "Row_Index";
    //public static final String COLUMN_ID_CLIPBOARD = "ID";
    //public static final String COLUMN_DEVICE_ID_CLIPBOARD = "Device_ID";
    public static final String COLUMN_CONTENT_CLIPBOARD = "Clipboard_Content";
    public static final String COLUMN_CLIENT_CLIPBOARD_TIME = "Client_Clipboard_Time";
    public static final String COLUMN_FROM_APP_CLIPBOARD = "From_App";
    public static final String COLUMN_CREATED_DATE_CLIPBOARD = "Created_Date";

/*    private String Device_ID, Client_Clipboard_Time, Clipboard_Content, Created_Date, From_App;
    private int RowIndex, ID;*/

}
