package com.example.avcinteractivemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {


    private RecyclerView recyclerView;

    private ArrayList<ContactItems> contactItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactItems = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        //Stores questions in contactItems ArrayList
        addContactItem();

        ContactRecyclerAdapter recyclerAdapter = new ContactRecyclerAdapter(contactItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(ContactActivity.this, R.drawable.back_icon));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    // Adds items to the emergency contact activity
    private void addContactItem(){
        contactItems.add(new ContactItems("AVC Sheriff's Office", getString(R.string.sherrifs_office_description), "661.722.6399"));
        contactItems.add(new ContactItems("Campus Safety Escort Program", getString(R.string.campus_safety_escort_description), "661.722.6399"));
        contactItems.add(new ContactItems("Criminal Prevention and Detection", getString(R.string.criminal_prevention_detection_description), "661.722.6399"));

        // Adds dividers between contact items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}