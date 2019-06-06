package com.example.capstonee;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstonee.Model.Picture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ShowPhotoActivity extends AppCompatActivity {
    //private ImageView picture_imageView;
    static String TAG = "tw";
    Context context;

    //이미지 버튼들(다운로드, 삭제, 공유)과 이미지 뷰
    ImageButton downloadButton;
    ImageButton removeButton;
    ImageButton shareButton;
    ImageView imageView;

    //카카오톡 외부 전송시 사용하는 변수들
    File imageFile;
    Uri uri;
    //URL url;
    Picture picture;

    //이미지 로컬 다운로드 할 때 사용하는 변수들
    Bitmap mSaveBm;
    String sdCardFilename;
    Date now;
    File file = null;

    //삭제시 사용할 id
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //초기화
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        //String ImageUrl = intent.getStringExtra("imageUrl"); 종한씨가 받아온 부분
        //id = intent.getStringExtra("id"); 종한씨가 받아온 부분

        //객체로 받아오는 걸로 바꿈.
        picture = (Picture) getIntent().getSerializableExtra("picture");

        //이미지 뷰와 버튼 세 개
        downloadButton = (ImageButton) findViewById(R.id.downloadButton);
        removeButton = (ImageButton) findViewById(R.id.removeButton);
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        imageView = (ImageView)findViewById(R.id.detailPhoto);

        //잠시 피카소는 태우가 주석처리! 왜냐? 저장할 때 SD카드 내부 저장을 위한 비트맵 변환을 위해.
        //Picasso.with(getApplicationContext()).load(ImageUrl).fit().centerInside().into(imageView);
        OpenHttpConnection opHttpCon = new OpenHttpConnection();  //비트맵으로 받아오기 위해서 정의
        opHttpCon.execute(imageView, picture.getUri());               //execute메소드 호출하면서 비트맵 얻어옮.

        //삭제 부분. 내 코드 다 지움.
        removeButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowPhotoActivity.this, "삭제는 구현X", Toast.LENGTH_LONG).show();
            }
        });

        //저장 부분
        downloadButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                makeImageFile(1); //1은 로컬에 이미지 저장 의미
            }
        });

        //공유 부분
        shareButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                makeImageFile(2);  //2는 카카오톡으로 공유를 의미
                sendKaKao(uri);
            }
        });
    }

    private void sendKaKao(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setPackage("com.kakao.talk");
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Uri uriMarket = Uri.parse("market://deatils?id=com.kakao.talk");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriMarket);
            startActivity(intent);
        }
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
                in = new java.net.URL(url).openStream();
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

    void makeImageFile(int num){
        if(num==2){
            Toast.makeText(ShowPhotoActivity.this, "공유는 다소 시간이 걸릴 수 있습니다.", Toast.LENGTH_LONG).show();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
        now = new Date();
        sdCardFilename = formatter.format(now) + ".png";

        OutputStream outStream = null;
        File dir = new File(Environment.getExternalStorageDirectory() + "/modernFam");
        if(!dir.exists())
            dir.mkdirs();
        //String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        //Log.d("디렉토리:", extStorageDirectory);
        //file = new File(extStorageDirectory, sdCardFilename);
        file = new File(dir, sdCardFilename);
        imageFile = new File(dir, sdCardFilename);
        try {
            outStream = new FileOutputStream(file);
            Log.d("파일경로", file.toString());
            mSaveBm.compress(
                    Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            uri = Uri.parse(Environment.getExternalStorageDirectory() + "/modernFam/"+ sdCardFilename);   //sdcardfilename때문에 파일 공유 여기 삽입
            if(num==1){
                Toast.makeText(ShowPhotoActivity.this, "저장완료", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ShowPhotoActivity.this, "카톡으로 이동합니다!", Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ShowPhotoActivity.this,
                    e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ShowPhotoActivity.this,
                    e.toString(), Toast.LENGTH_LONG).show();
        }
        //앨범에 이미지 저장 후 바로 최신화 시켜주기 위해 이미지 스캐닝(파일스캐닝)을 해줘야함.
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)) );
    }
}
