package com.example.capstonee.Location;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstonee.Model.Picture;
import com.example.capstonee.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ShowLocationImage extends AppCompatActivity {

    Context context;
    String imageUrl;
    ImageView imageView;
    Bitmap mSaveBm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //초기화
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location_image);

        imageUrl = getIntent().getExtras().getString("imageUrl");
        //이미지 뷰와 버튼 세 개
        imageView = (ImageView)findViewById(R.id.location_image_view);

        //Picasso.with(getApplicationContext()).load(ImageUrl).fit().centerInside().into(imageView); //잠시 피카소는 태우가 주석처리! 왜냐? SD카드 내부 저장을 위한 비트맵 변환을 위해.
        OpenHttpConnection opHttpCon = new OpenHttpConnection();  //비트맵으로 받아오기 위해서 정의
        opHttpCon.execute(imageView, imageUrl);               //execute메소드 호출하면서 비트맵 얻어옮.

    }

    private class OpenHttpConnection extends AsyncTask<Object, Void, Bitmap> {
        private ImageView bmImage;
        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            bmImage = (ImageView) params[0];
            String url = (String) params[1];
            InputStream in = null;
            try {
                in = new URL(url).openStream();
                mBitmap = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return mBitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
            mSaveBm = bm;
            bmImage.setImageBitmap(bm);
        }
    }
}