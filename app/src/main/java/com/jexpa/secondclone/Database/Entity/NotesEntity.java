/*
  ClassName: NotesEntity.java
  Project: SecondClone
  author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Database.Entity;

public class NotesEntity {

    /**
     * Table name: Notes_History
     */
    public static final String TABLE_NOTE_HISTORY = "NoteHistory";
    public static final String DATABASE_NAME_NOTE_HISTORY = "NoteManager";
    public static final int DATABASE_VERSION_NOTE_HISTORY = 1;
    public static final String COLUMN_ROWINDEX_NOTE = "RowIndex";
    public static final String COLUMN_ID_NOTE = "ID";
    public static final String COLUMN_DEVICE_ID_NOTE = "Device_ID";
    public static final String COLUMN_CLIENT_NOTE_TIME = "Client_Note_Time";
    public static final String COLUMN_CONTENT_NOTE = "Content";
    public static final String COLUMN_CREATED_DATE_NOTE = "Created_Date";
}
