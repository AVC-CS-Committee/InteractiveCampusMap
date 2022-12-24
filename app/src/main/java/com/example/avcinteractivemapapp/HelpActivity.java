package com.example.avcinteractivemapapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

public class HelpActivity extends AppCompatActivity {

    //TODO: Add back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(HelpActivity.this, R.drawable.back_icon));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}