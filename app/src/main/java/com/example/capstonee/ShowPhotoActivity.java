package com.example.capstonee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ShowPhotoActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        Intent intent = getIntent();
        String ImageUrl = intent.getExtras().getString("ImageUrl");
        imageView = findViewById(R.id.detailPhoto);
        Picasso.with(getApplicationContext()).load(ImageUrl).fit().centerInside().into(imageView);
    }
}
