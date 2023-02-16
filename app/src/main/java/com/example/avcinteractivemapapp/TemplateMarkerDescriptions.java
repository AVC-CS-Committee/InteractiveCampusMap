package com.example.avcinteractivemapapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TemplateMarkerDescriptions extends AppCompatActivity {
    ArrayList<String> imagePaths = new ArrayList<>();
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_marker_descriptions);

        // Adds the back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(TemplateMarkerDescriptions.this, R.drawable.icon_back));

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

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
        // Get the reference of ViewFlipper
        viewFlipper = findViewById(R.id.imageScroller);

        // Sets how quickly the images are flipped (in milliseconds)
        viewFlipper.setFlipInterval(1000);

        // If a popup has no images, use default image
        if (imagePaths.isEmpty()) {
            int defaultImage = getResources().getIdentifier("image_avc_logo", "drawable", getPackageName());
            if (defaultImage == 0) return;

            ImageView image = new ImageView(getApplicationContext());
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageResource(defaultImage);

            viewFlipper.addView(image);
            viewFlipper.startFlipping();
            return;
        }

        // Get image id
        try {
            // Set image(s)
            for (int i = 0; i < imagePaths.size(); i++) {
                // Get the image via path
                int imageId = getResources().getIdentifier(imagePaths.get(i), "drawable", getPackageName());
                if (imageId == 0) return;

                // Create a new ImageView object and set the image
                ImageView image = new ImageView(getApplicationContext());
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setImageResource(imageId);

                // Add the image to the ViewFlipper
                viewFlipper.addView(image);
            }

            viewFlipper.startFlipping();

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
