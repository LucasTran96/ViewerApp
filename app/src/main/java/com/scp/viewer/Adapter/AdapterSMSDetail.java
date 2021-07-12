/*
  ClassName: AdapterSMSDetail.java
  @Project: ViewerApp
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterSMSDetail used to customize the adapter for the RecyclerView of the "SMSHistoryDetail.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.scp.viewer.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static com.scp.viewer.API.APIDatabase.checkValueStringT;
import static com.scp.viewer.API.APIDatabase.formatDate;
import static com.scp.viewer.API.APIDatabase.getTimeItem;
import static com.scp.viewer.API.Global.DEFAULT_DATE_FORMAT;

import com.scp.viewer.API.APIDatabase;
import com.scp.viewer.Model.SMS;
import com.scp.viewer.R;
import com.scp.viewer.View.SMSHistoryDetail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AdapterSMSDetail extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    public static SparseBooleanArray itemStateArray = new SparseBooleanArray();
    private SMSHistoryDetail sms_history_detail;
    private static List<SMS> mMessageList;
    Context context;

    public AdapterSMSDetail(Activity context, List<SMS> messageList)
    {
        mMessageList = messageList;
        sms_history_detail = (SMSHistoryDetail) context;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        SMS sms = mMessageList.get(position);

        if (sms.getDirection() == 0) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sms_sent, parent, false);
            return new SentMessageHolder(view, sms_history_detail);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sms_received, parent, false);
            return new ReceivedMessageHolder(view, sms_history_detail);
        }
        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SMS sms = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(sms, position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(sms, position);
                break;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageText, timeText, txt_Date_Unique;
        CheckBox checkBox_SMS;
        View mmView;
        RelativeLayout rll_Sent_SMS;
        LinearLayout ln_Date_Sent;
        SMSHistoryDetail sms_history_detail;

        SentMessageHolder(View itemView, SMSHistoryDetail sms_history_detail) {
            super(itemView);
            this.setIsRecyclable(false);
            this.sms_history_detail = sms_history_detail;
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            txt_Date_Unique = itemView.findViewById(R.id.txt_Date_Unique);
            rll_Sent_SMS = itemView.findViewById(R.id.rll_Sent_SMS);
            ln_Date_Sent = itemView.findViewById(R.id.ln_Date_Sent);
            ln_Date_Sent.setVisibility(View.GONE);
            checkBox_SMS = itemView.findViewById(R.id.checkbox_SMS);
            mmView = itemView;
            rll_Sent_SMS.setOnLongClickListener(sms_history_detail);
            rll_Sent_SMS.setOnClickListener(this);
        }

        @SuppressLint("ResourceAsColor")
        void bind(SMS sms, int position) {
            messageText.setText(sms.getText_Message());
            String time_Format = getTimeItem(checkValueStringT(sms.getClient_Message_Time()),null);
            timeText.setText(time_Format );
            timeText.setVisibility(View.GONE);
            if(position > 0)
            {
                String dateNext;
                String dateHere;
                try {
                    dateNext = formatDate(sms_history_detail.list_SMS_Detail.get(position-1).getClient_Message_Time(), DEFAULT_DATE_FORMAT);
                    dateHere = formatDate(sms_history_detail.list_SMS_Detail.get(position).getClient_Message_Time(), DEFAULT_DATE_FORMAT);

                    if(!dateNext.equals(dateHere))
                    {
                        ln_Date_Sent.setVisibility(View.VISIBLE);
                        txt_Date_Unique.setVisibility(View.VISIBLE);
                        txt_Date_Unique.setText(time_Format);
                    }
                    else {
                        ln_Date_Sent.setVisibility(View.GONE);
                        txt_Date_Unique.setVisibility(View.GONE);
                    }
                } catch (ParseException e) {
                    txt_Date_Unique.setVisibility(View.GONE);
                    ln_Date_Sent.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                if(sms_history_detail.list_SMS_Detail.get(position-1).getDirection() == 0)
                {
                    if(position < sms_history_detail.list_SMS_Detail.size()-1)
                    {
                        if(sms_history_detail.list_SMS_Detail.get(position+1).getDirection() == 1)
                        {
                            messageText.setBackground(context.getResources().getDrawable(R.drawable.my_message_final));
                        }
                        else {
                            messageText.setBackground(context.getResources().getDrawable(R.drawable.my_message_next));
                        }

                    }else {
                        messageText.setBackground(context.getResources().getDrawable(R.drawable.my_message));
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0,5,0,0);
                    rll_Sent_SMS.setLayoutParams(params);
                }
                else if(sms_history_detail.list_SMS_Detail.get(position-1).getDirection() == 1)
                {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0,10,0,0);
                    rll_Sent_SMS.setLayoutParams(params);
                    if(position < sms_history_detail.list_SMS_Detail.size()-1)
                    {
                        if(sms_history_detail.list_SMS_Detail.get(position+1).getDirection() == 1)
                        {
                            messageText.setBackground(context.getResources().getDrawable(R.drawable.my_message));
                        }
                        else {
                            messageText.setBackground(context.getResources().getDrawable(R.drawable.my_message_previous));
                        }
                    }else {
                        messageText.setBackground(context.getResources().getDrawable(R.drawable.my_message));
                    }
                }
                else {

                    LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0,10,0,0);
                    rll_Sent_SMS.setLayoutParams(params);
                    messageText.setBackground(context.getResources().getDrawable(R.drawable.my_message));
                }
            }else {
                ln_Date_Sent.setVisibility(View.VISIBLE);
                txt_Date_Unique.setVisibility(View.VISIBLE);
                txt_Date_Unique.setText(time_Format);
            }

            if (!SMSHistoryDetail.isInActionMode_SMS_Detail) {

                checkBox_SMS.setVisibility(View.GONE);

            } else {
                checkBox_SMS.setVisibility(View.VISIBLE);
            }

            if (!itemStateArray.get(position, false)) {
                checkBox_SMS.setChecked(false);
            } else {
                checkBox_SMS.setChecked(true);
            }
        }


        @Override
        public void onClick(View view) {
            //if (photoHistory.isInActionMode_SMS_Detail) {
            if (SMSHistoryDetail.isInActionLong) {
                sms_history_detail.prepareSelection(getAdapterPosition());
                int adapterPosition = getAdapterPosition();
                if (!itemStateArray.get(adapterPosition, false)) {
                    checkBox_SMS.setChecked(true);
                    itemStateArray.put(adapterPosition, true);
                } else {
                    checkBox_SMS.setChecked(false);
                    itemStateArray.put(adapterPosition, false);
                }
            }
            else {
                if(timeText.getVisibility() == View.GONE)
                    timeText.setVisibility(View.VISIBLE);
                else
                    timeText.setVisibility(View.GONE);
            }
        }
        //}
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageText, timeText, txt_Date_Unique;//nameText
        ImageView profileImage;
        CheckBox checkBox_sms;
        View vie;
        LinearLayout lnl_date, ln_Receiver;
        RelativeLayout rll_Received_SMS;
        SMSHistoryDetail sms_history_detail;

        ReceivedMessageHolder(View itemView, SMSHistoryDetail sms_history_detail) {
            super(itemView);
            this.setIsRecyclable(false);
            this.sms_history_detail = sms_history_detail;
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            txt_Date_Unique = itemView.findViewById(R.id.txt_Date_Unique);
            txt_Date_Unique.setVisibility(View.GONE);
            rll_Received_SMS = itemView.findViewById(R.id.rll_Received_SMS);
            lnl_date = itemView.findViewById(R.id.lnl_date);
            ln_Receiver = itemView.findViewById(R.id.ln_Receiver);
            lnl_date.setVisibility(View.GONE);
            profileImage = itemView.findViewById(R.id.image_message_profile);
            checkBox_sms = itemView.findViewById(R.id.checkbox_SS);
            vie = itemView;
            rll_Received_SMS.setOnLongClickListener(sms_history_detail);
            rll_Received_SMS.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bind(SMS sms, int position) {
            messageText.setText(sms.getText_Message());
            // Format the stored timestamp into a readable String using method.
            String time_Format = getTimeItem(APIDatabase.checkValueStringT(sms.getClient_Message_Time()),null);
            timeText.setText(time_Format + "");
            timeText.setVisibility(View.GONE);

                if(position>0)
                {
                    String dateNext;
                    String dateHere;
                    try {
                        dateNext = formatDate(sms_history_detail.list_SMS_Detail.get(position-1).getClient_Message_Time(), DEFAULT_DATE_FORMAT);
                        dateHere = formatDate(sms_history_detail.list_SMS_Detail.get(position).getClient_Message_Time(), DEFAULT_DATE_FORMAT);

                        if(!dateNext.equals(dateHere))
                        {
                            lnl_date.setVisibility(View.VISIBLE);
                            txt_Date_Unique.setVisibility(View.VISIBLE);
                            txt_Date_Unique.setText(time_Format);
                        }
                        else {
                            txt_Date_Unique.setVisibility(View.GONE);
                            lnl_date.setVisibility(View.GONE);
                        }
                    } catch (ParseException e) {
                        lnl_date.setVisibility(View.GONE);
                        txt_Date_Unique.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    // Here is how to handle display corners of messages.
                    if(sms_history_detail.list_SMS_Detail.get(position-1).getDirection() == 1)
                    {

                        profileImage.setImageDrawable(null);
                        if(position < sms_history_detail.list_SMS_Detail.size()-1)
                        {
                            if(sms_history_detail.list_SMS_Detail.get(position+1).getDirection() == 0)
                            {
                                messageText.setBackground(context.getResources().getDrawable(R.drawable.their_message_final));
                            }
                            else {
                                messageText.setBackground(context.getResources().getDrawable(R.drawable.their_message_next));
                            }

                        }else {
                            messageText.setBackground(context.getResources().getDrawable(R.drawable.their_message));
                        }
                    }
                    else if(sms_history_detail.list_SMS_Detail.get(position-1).getDirection() == 0)
                    {
                        profileImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                        if(position < sms_history_detail.list_SMS_Detail.size()-1)
                        {
                            if(sms_history_detail.list_SMS_Detail.get(position+1).getDirection() == 0)
                            {
                                messageText.setBackground(context.getResources().getDrawable(R.drawable.their_message));
                            }
                            else {
                                messageText.setBackground(context.getResources().getDrawable(R.drawable.their_message_previous));
                            }
                        }else {
                            messageText.setBackground(context.getResources().getDrawable(R.drawable.their_message));
                        }
                    }
                    else
                    {
                        profileImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                        messageText.setBackground(context.getResources().getDrawable(R.drawable.their_message));
                    }
                }
                else {
                    profileImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                    lnl_date.setVisibility(View.VISIBLE);
                    txt_Date_Unique.setVisibility(View.VISIBLE);
                    txt_Date_Unique.setText(time_Format);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                }

            if (!SMSHistoryDetail.isInActionMode_SMS_Detail)
            {
                checkBox_sms.setVisibility(View.GONE);

            } else {
                checkBox_sms.setVisibility(View.VISIBLE);
            }

            if (!itemStateArray.get(position, false)) {
                checkBox_sms.setChecked(false);
            } else {
                checkBox_sms.setChecked(true);
            }
        }

        @Override
        public void onClick(View view) {
            ///if (photoHistory.isInActionMode_SMS_Detail) {
            if (SMSHistoryDetail.isInActionLong) {
                sms_history_detail.prepareSelection(getAdapterPosition());
                int adapterPosition = getAdapterPosition();
                if (!itemStateArray.get(adapterPosition, false)) {
                    checkBox_sms.setChecked(true);
                    itemStateArray.put(adapterPosition, true);
                } else {
                    checkBox_sms.setChecked(false);
                    itemStateArray.put(adapterPosition, false);
                }

            } else {
                if(timeText.getVisibility() == View.GONE)
                    timeText.setVisibility(View.VISIBLE);
                else
                    timeText.setVisibility(View.GONE);
            }
        }
    }

    public void removeData(ArrayList<SMS> list) {
        for (SMS sms : list) {
            mMessageList.remove(sms);
        }
        notifyDataSetChanged();
    }
}
