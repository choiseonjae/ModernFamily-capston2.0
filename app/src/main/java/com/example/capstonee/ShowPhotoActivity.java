package com.example.capstonee;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.Picture;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.Query;
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
    private ImageView imageView;
    private Toolbar toolbar;
    private String ImageUrl;
    private String fileName;
    private String role;
    private String From;
    private String Kind;

    // PJH
    // kind는 뭐냐면 어댑터에서 사진을 클릭해서 이 액티비티로 와서 삭제를 누르면,
    // DB에서 얘가 어디에 속해있는지 파악할 필요가 있다.
    // 그때 쓰는게 바로 Kind
    Context context;


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

        Intent intent = getIntent();
        ImageUrl = intent.getStringExtra("imageUrl");
        fileName = intent.getStringExtra("fileName");
        role = intent.getStringExtra("role");
        From = intent.getStringExtra("From");

        Log.e("urlfilefrom", ImageUrl + " " + fileName + " " + role + " " + From);
        imageView = findViewById(R.id.detailPhoto);
        //Picasso.with(getApplicationContext()).load(ImageUrl).fit().centerInside().into(imageView);

        //잠시 피카소는 태우가 주석처리! 왜냐? 저장할 때 SD카드 내부 저장을 위한 비트맵 변환을 위해.
        //Picasso.with(getApplicationContext()).load(ImageUrl).fit().centerInside().into(imageView);
        OpenHttpConnection opHttpCon = new OpenHttpConnection();  //비트맵으로 받아오기 위해서 정의
        opHttpCon.execute(imageView, ImageUrl);               //execute메소드 호출하면서 비트맵 얻어옮.

        toolbar = findViewById(R.id.show_photo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // 사진 업로드 후 VM에 URL 보내는 곳
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        private NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);

            return result;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder alertdialog;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete_button:
                alertdialog = new AlertDialog.Builder(this);
                alertdialog.setTitle("삭제하시겠습니까?");
                alertdialog.setMessage("한번 삭제하면 돌이킬 수 없게 됩니다!");
                // 게시할 때
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (From.equals("PhotoView")) {
                            // 초기 사진 설정하는 부분임!!!!!! 나중에 분류할 사진 삭제는 밑에 있음!!
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference("Main");
                            StorageReference pictureRef = storageRef.child(Login.getUserFamilyID()).child(fileName);
                            pictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //File 삭제 완료
                                    //PhotoViewAdapter에서 사진 클릭시
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Family").child(Login.getUserFamilyID());
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                ImageUpload imageUpload = snapshot.getValue(ImageUpload.class);
                                                if (imageUpload.getName().equals(fileName)) {
                                                    Kind = snapshot.getKey();
                                                    RemoveFamilyData(Kind);
                                                    Move();
                                                    // VM에서 해당 사진 삭제해주는 url
                                                    String url = "http://104.155.130.175/removefile.py";

                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("filename", Kind+".png");
                                                    contentValues.put("ui", Login.getUserFamilyID());

                                                    NetworkTask networkTask = new NetworkTask(url, contentValues);
                                                    networkTask.execute();

//                                                    final int count = Login.getUserFamilyCount2()-1;
//                                                    Infomation.getDatabase("User").child(Login.getUserFamilyID()).child("familyCount2").setValue(count)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//                                                            Login.setFamilyCount2(count);
//                                                        }
//                                                    });


                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) { }
                            });
                        } else {
                            //RoleClickAdapter에서 클릭시, 즉 분류된 사진 삭제시
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference("Album");
                            StorageReference pictureRef = storageRef.child(Login.getUserFamilyID()).child(fileName);
                            pictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Album");
                                    databaseReference = databaseReference.child(Login.getUserFamilyID());
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Picture picture = snapshot.getValue(Picture.class);
                                                if (picture.getFileName().equals(fileName)) {
                                                    Kind = snapshot.getKey();
                                                    RemoveAlbumData(Kind);
                                                    RoleData(role, fileName);
                                                    Move();
                                                    break;
                                                }
                                            }
                                            Log.e("KIND = ", Kind);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    });
                                }
                            });
                        }
                    }
                });
                alertdialog.setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertdialog.show();
                break;
            case R.id.download_button:
                alertdialog = new AlertDialog.Builder(this);
                alertdialog.setTitle("다운로드");
                alertdialog.setMessage("사진을 다운로드 하시겠습니까?");
                // 게시할 때
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeImageFile(1);
                    }
                });
                alertdialog.setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertdialog.show();
                break;
            case R.id.move_button:
                //TODO :: 사진 이동
                break;
            case R.id.kakao_share:
                alertdialog = new AlertDialog.Builder(this);
                alertdialog.setTitle("카카오톡으로 공유");
                alertdialog.setMessage("사진을 카카오톡에 공유 하시겠습니까?");
                // 게시할 때
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeImageFile(2);  //2는 카카오톡으로 공유를 의미
                        sendKaKao(uri);
                    }
                });
                alertdialog.setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertdialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void RemoveAlbumData(String Kind){
        FirebaseDatabase.getInstance().getReference("Album").child(Login.getUserFamilyID()).child(Kind).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShowPhotoActivity.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowPhotoActivity.this, "삭제 실패..", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void RemoveFamilyData(String Kind){
        FirebaseDatabase.getInstance().getReference("Family").child(Login.getUserFamilyID()).child(Kind).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShowPhotoActivity.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowPhotoActivity.this, "삭제 실패..", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void RoleData(final String role, final String fileName){
        DatabaseReference another = FirebaseDatabase.getInstance().getReference("role").child(Login.getUserFamilyID()).child(role);
        another.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageUpload imageUpload = snapshot.getValue(ImageUpload.class);
                    if (imageUpload.getName().equals(fileName)) {
                        String Kind = snapshot.getKey();
                        RemoveRoleData(role, Kind);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void RemoveRoleData(String role, String Kind){
        FirebaseDatabase.getInstance().getReference("role").child(Login.getUserFamilyID()).child(role).child(Kind).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShowPhotoActivity.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowPhotoActivity.this, "삭제 실패..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void Move(){
        Intent intent = new Intent();
        intent.putExtra("name", fileName);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onBackPressed() { }

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

            mSaveBm.compress(Bitmap.CompressFormat.PNG, 100, outStream);

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
