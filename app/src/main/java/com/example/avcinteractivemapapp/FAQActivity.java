package com.example.avcinteractivemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;

import java.util.ArrayList;

//Tutorial links: https://youtu.be/9rcrYFO1ogc , https://youtu.be/hbMqd0XRN34

public class FAQActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FAQRecyclerAdapter recyclerAdapter;

    ArrayList<Question> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqactivity);

        faqList = new ArrayList<>();

        //Stores questions in faqList ArrayList
        addQuestionItem();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new FAQRecyclerAdapter(faqList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);


        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(FAQActivity.this, R.drawable.back_icon));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // Adds questions with titles and descriptions.
    private void addQuestionItem(){
        // Use the following format when adding hyperlinks
        String openLabsDescriptionText = "See the " + String.format("<a href=\"%s\">AVC website</a>", "https://www.avc.edu/administration/its/labs") + ".";

        // Provide a title, description, and true or false depending on whether or not you need a link
        faqList.add(new Question("Where are the open labs and what are their hours?", openLabsDescriptionText, true));
        faqList.add(new Question("test", "Test", false));
    }
}