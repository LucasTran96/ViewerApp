/*
  ClassName: NotesHistoryDetail.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: Class NotesHistoryDetail used to display the contents of a Notes.
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.cp9.View;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jexpa.cp9.API.APIURL;
import com.jexpa.cp9.Database.DatabaseNotes;
import com.jexpa.cp9.Model.Notes;
import com.jexpa.cp9.R;

import static com.jexpa.cp9.API.APIURL.deviceObject;
import static com.jexpa.cp9.API.APIURL.bodyLogin;
import static com.jexpa.cp9.API.APIURL.isConnected;

public class NotesHistoryDetail extends AppCompatActivity implements View.OnClickListener {
    TextView txt_Time_Notes_Detail, txt_Content_Notes_History;
    Button btn_Delete_Notes_History;
    private DatabaseNotes database_notes;
    private Notes notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_rcv_notes_history_detail);
        database_notes = new DatabaseNotes(this);
        setID();
        notes = (Notes) getIntent().getSerializableExtra("notes_Detail");
        setEvent();
    }

    private void setEvent() {
        txt_Time_Notes_Detail.setText(notes.getClient_Note_Time().replace("T", " "));
        txt_Content_Notes_History.setText(notes.getContent());
        btn_Delete_Notes_History.setOnClickListener(this);
    }

    private void setID() {
        txt_Time_Notes_Detail = findViewById(R.id.txt_Time_Notes_Detail);
        txt_Content_Notes_History = findViewById(R.id.txt_Content_Notes_History);
        btn_Delete_Notes_History = findViewById(R.id.btn_Delete_Notes_History);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_Delete_Notes_History: {
                if (isConnected(NotesHistoryDetail.this)) {
                    new clear_Note().execute();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class clear_Note extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String value = "<RequestParams Device_ID=\"" + notes.getDevice_ID() + "\" List_ID=\"" + notes.getID() + "\" />";
            String function = "ClearMultiNote";
            return APIURL.POST(value, function);
        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);

            if (bodyLogin.getCode().startsWith("S")) {
                database_notes.delete_Contact_History(notes);
                NotesHistoryDetail.this.finish();
            } else {
                Toast.makeText(NotesHistoryDetail.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();

            }
        }
    }
}
