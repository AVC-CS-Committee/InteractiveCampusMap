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

    private void addQuestionItem(){
       // Resources res = getResources();
        faqList.add(new Question("Where are the open labs and what are their hours?", "Link to website"));
    }
}