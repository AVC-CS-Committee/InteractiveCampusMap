package com.example.avcinteractivemapapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    //Holds all help items
    private final ArrayList<HelpItems> helpItems;

    //Listener for when a help item is clicked
    private final RecyclerViewClickListener listener;

    //When we want to create an instance of the recycler adapter, we need to pass it a list of users
    public RecyclerAdapter(ArrayList<HelpItems> helpItems, RecyclerViewClickListener listener){
        this.helpItems = helpItems;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView nameTxt;

        public MyViewHolder(final View view){
            super(view);
            nameTxt = view.findViewById(R.id.helpItem);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        String helpItemName = helpItems.get(position).getHelpItem();
        holder.nameTxt.setText(helpItemName);
    }

    @Override
    public int getItemCount() {
        return helpItems.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
