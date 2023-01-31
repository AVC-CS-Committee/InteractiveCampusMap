package com.example.avcinteractivemapapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.MyViewHolder> {

    ArrayList<ContactItems> contactItems;

    public ContactRecyclerAdapter(ArrayList<ContactItems> contactItems) { this.contactItems = contactItems; }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView contactName;
        private TextView contactDescription;
        private TextView contactNumber;

        public MyViewHolder(final View view){
            super(view);
            contactName = view.findViewById(R.id.contact_name);
            contactDescription = view.findViewById(R.id.contact_description);
            contactNumber = view.findViewById(R.id.contact_number);
        }

    }

    @NonNull
    @Override
    public ContactRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactRecyclerAdapter.MyViewHolder holder, int position) {
        String contact = contactItems.get(position).getContactTitle();
        String description = contactItems.get(position).getContactDescription();
        String number = contactItems.get(position).getContactNumber();

        holder.contactName.setText(contact);
        holder.contactDescription.setText(description);
        holder.contactNumber.setText(number);
    }

    @Override
    public int getItemCount() {
        return contactItems.size();
    }

}
