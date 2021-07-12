/*
  ClassName: AdapterNoteHistory.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterNoteHistory used to customize the adapter for the RecyclerView of the "NotesHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scp.viewer.Model.Notes;
import com.scp.viewer.R;
import com.scp.viewer.View.MyApplication;
import com.scp.viewer.View.NotesHistory;
import com.scp.viewer.View.NotesHistoryDetail;

import java.util.ArrayList;

public class AdapterNoteHistory extends RecyclerView.Adapter<AdapterNoteHistory.ViewHolder> {

    private Activity mActivity;
    private static ArrayList<Notes> mDataSet;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_Date_Note_History, txt_Content_Note_History;
        View mView;
        CardView card_view_Note;

        ViewHolder(View v) {
            super(v);
            txt_Date_Note_History = v.findViewById(R.id.txt_Date_Note_History);
            txt_Content_Note_History = v.findViewById(R.id.txt_Content_Note_History);
            card_view_Note = v.findViewById(R.id.card_view_Note);
            mView = v;
            v.setOnLongClickListener(this);
            card_view_Note.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((NotesHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // lightning click event.
            if (NotesHistory.isInActionMode) {
                ((NotesHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                Notes notes = mDataSet.get(position);
                MyApplication.getInstance().trackEvent("NoteHistory", "View note detail", "" + notes.getID());
                // Path through new activity.
                Intent intent = new Intent(mActivity, NotesHistoryDetail.class);
                intent.putExtra("notes_Detail", notes);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    public AdapterNoteHistory(Activity activity, ArrayList<Notes> myDataSet) {
        mActivity = activity;
        mDataSet = myDataSet;
    }

    @NonNull
    @Override
    public AdapterNoteHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_note_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notes notes = mDataSet.get(position);
        holder.mView.setBackgroundResource(R.color.white);
        String time_Location = notes.getClient_Note_Time().replace("T", " ");
        holder.txt_Date_Note_History.setText(time_Location.substring(0, 16));
        holder.txt_Content_Note_History.setText(notes.getContent());
        if (NotesHistory.isInActionMode) {
            if (NotesHistory.selectionList.contains(mDataSet.get(position))) {
                holder.mView.setBackgroundResource(R.color.grey_600);
                //holder.txt_name_location.setTextColor(Color.parseColor("#000000"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void removeData(ArrayList<Notes> list) {
        for (Notes notes : list) {
            mDataSet.remove(notes);
        }
        notifyDataSetChanged();
    }

}