package com.example.capstonee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.capstonee.Model.Picture;
import com.squareup.picasso.Picasso;

public class DetailPicture extends AppCompatActivity {

    private ImageView picture_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Picture picture = (Picture) getIntent().getSerializableExtra("picture");

        picture_imageView = findViewById(R.id.picture);
        Picasso.with(this).load(picture.getUri()).fit().into(picture_imageView);

    }
}
