package com.example.avcinteractivemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

//Tutorial Link: https://youtu.be/vBxNDtyE_Co

public class HelpActivity extends AppCompatActivity {

    private ArrayList<HelpItems> helpItems;
    private RecyclerView recyclerView;
    private RecyclerAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        recyclerView = findViewById(R.id.recyclerView);
        helpItems = new ArrayList<>();
        addHelpItem();
        setAdapter();

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(HelpActivity.this, R.drawable.back_icon));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setAdapter() {
        setOnClickListener();
        RecyclerAdapter adapter = new RecyclerAdapter(helpItems, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = (v, position) -> {
            if(position == 0){
                Intent intent = new Intent(getApplicationContext(), FAQActivity.class);
                startActivity(intent);
            }
            else if(position == 1){
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                startActivity(intent);
            }
        };
    }

    // Adds items to the help activity
    // NOTE: If the position of an item in the helpItems ArrayList changes, it's position must be updated above in the setOnClickListener()
    private void addHelpItem(){
        helpItems.add(new HelpItems("Frequently Asked Questions"));
        helpItems.add(new HelpItems("Support"));

        //Adds dividers between help items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}