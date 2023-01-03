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

        boolean isExpanded = faqList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, descriptionTextView;
        ConstraintLayout expandableLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textView);
            descriptionTextView = itemView.findViewById(R.id.textView4);
            expandableLayout = itemView.findViewById(R.id.ExpandableLayout);

            titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Getting the position of the selected question title
                    Question question = faqList.get(getAdapterPosition());
                    //Inverting the current expanded property
                    question.setExpanded(!question.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

    }

}
