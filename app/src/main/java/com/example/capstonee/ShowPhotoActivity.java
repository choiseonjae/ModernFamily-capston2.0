package com.example.capstonee;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;


public class ShowPhotoActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        Intent intent = getIntent();
        String ImageUrl = intent.getExtras().getString("ImageUrl");
        imageView = findViewById(R.id.detailPhoto);
        try{
            Uri uri = Uri.parse(ImageUrl);
            Log.d("URL", ImageUrl);
            imageView.setImageURI(uri);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
