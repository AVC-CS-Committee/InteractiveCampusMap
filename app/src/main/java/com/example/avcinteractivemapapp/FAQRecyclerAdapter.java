package com.example.avcinteractivemapapp;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FAQRecyclerAdapter extends RecyclerView.Adapter<FAQRecyclerAdapter.ViewHolder> {

    ArrayList<Question> faqList;

    public FAQRecyclerAdapter(ArrayList<Question> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.faq_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //Sets the value of the title and description textViews to whatever values the current position in faqList holds
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Question question = faqList.get(position);
        holder.titleTextView.setText(question.getTitle());

        // If there is a hyperlink, then make the description textView a link
        if(question.hasLink()) {
            holder.descriptionTextView.setText(Html.fromHtml(question.getDescription()));
            holder.descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            holder.descriptionTextView.setText(question.getDescription());
        }

        // Animates the dropdown arrow and expands the CardView when clicked
        holder.expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle expand/collapse functionality
                if(holder.descriptionTextView.getVisibility() == View.GONE){
                    holder.descriptionTextView.setVisibility(View.VISIBLE);
                    holder.expandIcon.animate().rotation(180);
                } else {
                    holder.descriptionTextView.setVisibility(View.GONE);
                    holder.expandIcon.animate().rotation(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, descriptionTextView;
        private ImageView expandIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textView);
            descriptionTextView = itemView.findViewById(R.id.textView4);
            expandIcon = itemView.findViewById(R.id.spinner);

        }

    }

}
