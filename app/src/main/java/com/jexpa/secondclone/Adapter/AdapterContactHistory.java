 /*
 ClassName: AdapterContactHistory.java
 Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
 Created Date: 2018-06-05
 Description: class AdapterContactHistory used to customize the adapter for the RecyclerView of the "ContactHistory.class"
 History:2018-10-08
 Copyright © 2018 Jexpa LLC. All rights reserved.
 */


package com.jexpa.secondclone.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jexpa.secondclone.Model.Contact;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.ContactHistory;
import com.jexpa.secondclone.View.ContactHistoryDetail;
import com.jexpa.secondclone.View.MyApplication;
import java.util.ArrayList;

 public class AdapterContactHistory extends RecyclerView.Adapter<AdapterContactHistory.ViewHolder>
        implements Filterable {

    private Activity mActivity;
    private static ArrayList<Contact> mDataSet;
    private ArrayList<Contact> contactListFiltered;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_name_Contact, img_Text_Icon;
        View mView;
        ImageView img_User_Contact;
        LinearLayout Card_View_Contact;

        ViewHolder(View v) {
            super(v);

            txt_name_Contact = v.findViewById(R.id.txt_name_Contact);
            img_Text_Icon = v.findViewById(R.id.img_Text_Icon);
            Card_View_Contact = v.findViewById(R.id.Card_View_Contact);
            img_User_Contact = v.findViewById(R.id.img_User_Contact);
            mView = v;
            v.setOnLongClickListener(this);
            Card_View_Contact.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            Contact contact = contactListFiltered.get(getAdapterPosition());
            ((ContactHistory) mActivity).prepareToolbar(contact,getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // lightning click event.
            if (ContactHistory.isInActionMode) {
                Contact contact = contactListFiltered.get(position);
                ((ContactHistory) mActivity).prepareSelection(contact,position);
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION) {
                Contact contact = contactListFiltered.get(position);
                MyApplication.getInstance().trackEvent("ContactHistory", "View contact detail: " + contact.getContact_Name(), "" + contact.getContact_Name());
                // Path through new activity.
                Intent intent = new Intent(mActivity, ContactHistoryDetail.class);
                intent.putExtra("contact_Detail", contact);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        }
    }

    public AdapterContactHistory(Activity activity, ArrayList<Contact> myDataSet) {
        mActivity = activity;
        mDataSet = myDataSet;
        contactListFiltered = myDataSet;
    }

    @NonNull
    @Override
    public AdapterContactHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_contact_historys, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contactListFiltered.get(position);
        if(contact != null)
        {
            holder.txt_name_Contact.setText(contact.getContact_Name());
            String textIcon = (contact.getContact_Name().length()>0) ? contact.getContact_Name().charAt(0)+"" : "";
            holder.img_Text_Icon.setText(textIcon.toUpperCase());
            //holder.img_User_Contact.setBackgroundColor(contact.getColor());
            if (ContactHistory.isInActionMode) {
                if (ContactHistory.selectionList.contains(contactListFiltered.get(position)))
                {
                    holder.img_User_Contact.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.selected_icon));
                    holder.img_Text_Icon.setVisibility(View.GONE);
                }
                else {
                    actionModeFalse(holder, contact.getColor(), contact);
                }
            }
            else {
                actionModeFalse(holder, contact.getColor(), contact);
            }
        }
    }

    private void actionModeFalse(ViewHolder holder, int currentColor, Contact contact)
    {
        holder.img_User_Contact.setImageDrawable(null);
        holder.img_Text_Icon.setVisibility(View.VISIBLE);
        if(CharacterIsNumberOrDigitTest(contact.getContact_Name()))
        {
            holder.img_User_Contact.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contact_icon));
            holder.img_Text_Icon.setVisibility(View.GONE);
        }
        else
        {
            holder.img_User_Contact.setBackgroundColor(currentColor);
            holder.img_Text_Icon.setVisibility(View.VISIBLE);
        }

    }
    private void setIMG(ImageView img)
    {
        img.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = mDataSet;
                } else {
                    ArrayList<Contact> filteredList = new ArrayList<>();
                    for (Contact row : mDataSet) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getContact_Name().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Contact>) filterResults.values;
                //notifyDataSetChanged();

            }
        };
    }

    public void removeData(ArrayList<Contact> list) {
        for (Contact contact : list) {
            contactListFiltered.remove(contact);
        }
        notifyDataSetChanged();
    }

     /**
      * This is a method of checking whether the first letter of the contact's name is a number or is a letter.
      */
     private boolean CharacterIsNumberOrDigitTest (String contactName)
     {
         boolean flag = false;
         if(contactName.length() > 0)
         {
             flag = Character.isDigit(contactName.charAt(0));
         }
         return flag;
     }
}