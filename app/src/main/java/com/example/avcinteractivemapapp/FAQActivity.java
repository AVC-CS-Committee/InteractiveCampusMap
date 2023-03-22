package com.example.avcinteractivemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

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
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(FAQActivity.this, R.drawable.icon_back));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    // Adds questions with titles and descriptions.
    private void addQuestionItem(){
        // Use the following format when adding hyperlinks
        // String openLabsDescriptionText = "See the " + String.format("<a href=\"%s\">AVC website</a>", "https://www.avc.edu/administration/its/labs") + ".";

        // Open questions json file
        InputStream jsonData = getResources().openRawResource(R.raw.faq_questions);
        Scanner scnr = new Scanner(jsonData);
        StringBuilder builder = new StringBuilder();

        // Store the json file into a string using StringBuilder
        while (scnr.hasNextLine()) {
            builder.append(scnr.nextLine());
        }

        scnr.close();
        parseJson(builder.toString());
    }

    // Parses the json string into Question objects, then adds the object to the
    // faqList ArrayList
    // Code from: https://youtu.be/gnj-Df7QQHU
    private void parseJson(String toString) {
        try {
            JSONArray root = new JSONArray(toString);

            for (int i = 0; i < root.length(); i++) {
                JSONObject question = root.getJSONObject(i);

                faqList.add(new Question(question.getString("question"),
                                         question.getString("answer"),
                                         question.getBoolean("hasLink")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}