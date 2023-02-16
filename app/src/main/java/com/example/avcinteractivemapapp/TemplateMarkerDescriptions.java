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
    ViewFlipper  viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_marker_descriptions);

        viewFlipper = (ViewFlipper) findViewById(R.id.imageScroller); // get the reference of ViewFlipper

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

            if (imageId == 0) return;

            // Sets how quickly the images are flipped (in milliseconds)
            viewFlipper.setFlipInterval(1000);

            // The 1st image is always set
            ImageView image = findViewById(R.id.image1);
            image.setImageResource(imageId);

            // Set image(s)
            // True if location has 3 images
            if(imagePaths.size() == 3) {

                // Starts flipping through images
                viewFlipper.startFlipping();

                // Set image 2
                int imageId2 = getResources().getIdentifier(imagePaths.get(1), "drawable", getPackageName());
                ImageView image2 = findViewById(R.id.image2);
                image2.setImageResource(imageId2);

                // Set image 3
                int imageId3 = getResources().getIdentifier(imagePaths.get(2), "drawable", getPackageName());
                ImageView image3 = findViewById(R.id.image3);
                image3.setImageResource(imageId3);

            }
            // True if location has 2 images
            else if(imagePaths.size() == 2) {

                // Removes the last template image since only 2 images are being flipped through
                viewFlipper.removeView(findViewById(R.id.image3));

                // Starts flipping through images
                viewFlipper.startFlipping();

                // Set image 2
                int imageId2 = getResources().getIdentifier(imagePaths.get(1), "drawable", getPackageName());
                ImageView image2 = findViewById(R.id.image2);
                image2.setImageResource(imageId2);

            }
            // Default case is just 1 image
            else {

                // Stop flipping if only 1 image (otherwise would flip through template images)
                viewFlipper.stopFlipping();
            }

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