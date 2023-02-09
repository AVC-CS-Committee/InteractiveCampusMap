package com.example.avcinteractivemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TemplateMarkerDescriptions extends AppCompatActivity {
    ArrayList<String> imagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_marker_descriptions);

        // Adds the back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(TemplateMarkerDescriptions.this, R.drawable.icon_back));

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        Bundle bundle = getIntent().getExtras();

        // Parse the image paths
        parseJson(bundle.getString("images"));

        // Set the text and image on the activity
        setTitle(bundle.getString("title"));
        setDescription(bundle.getString("description"));
        setImage();
    }

    private void setTitle(String title) {
        TextView titleText = findViewById(R.id.location_title_textView);
        titleText.setText(title);
    }

    private void setDescription(String description) {
        TextView textView = findViewById(R.id.description_textView);
        textView.setText(description);
    }

    private void setImage() {
        // Get image id
        try {
            int imageId = getResources().getIdentifier(imagePaths.get(0), "drawable", getPackageName());

            // Set image
            ImageView image = findViewById(R.id.location_imageView);
            image.setImageResource(imageId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseJson(String imageJson) {
        try {
            JSONArray paths = new JSONArray(imageJson);

            for (int i = 0; i < paths.length(); i++) {
                imagePaths.add("image_" + paths.getString(i));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}