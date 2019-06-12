package com.example.capstonee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.Util.ImageResizeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 *  첫 회원가입 후 로그인 시 뜨는 팝업창
 */
public class PopupActivity extends Activity {

    EditText relView;
    Button setInitButton;
    ImageView setInitPhoto;
    Button stopButton, goButton;
    private Uri imgUri;
    private String downloadUrl;
    private String family;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ProgressDialog dialog;
    public static final String FB_STORAGE_PATH = "Main/";
    public static final int POP_RESULT = 9876;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        relView = findViewById(R.id.relView);
        setInitButton = findViewById(R.id.setInitButton);
        setInitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PopupActivity.this, PopupInitSetActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        setInitPhoto = findViewById(R.id.setInitPhoto);
        stopButton = findViewById(R.id.stopButton);
        goButton = findViewById(R.id.goButton);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(PopupActivity.this);
                alertdialog.setTitle("종료하시겠습니까?");
                alertdialog.setMessage("계정설정의 '분류사진 변경'에서\n 사진을 추가할 수 있습니다.");
                // 게시할 때
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("keep", false);
                        setResult(RESULT_FIRST_USER, intent);
                        finish();
                    }
                }).setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertdialog.show();
            }
        });

        //선택 완료할건지 묻기
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                family = relView.getText().toString();
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(PopupActivity.this);
                alertdialog.setTitle("설정");
                alertdialog.setMessage("'" + family + "'(으)로 하시겠습니까?");
                // 게시할 때
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("keep", true);
                        setResult(POP_RESULT, intent);
                        //파이어베이스에 사진 업로드
                        UploadPhotoinFB();
                        finish();
                    }
                }).setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertdialog.show();
            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Family");
    }

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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void UploadPhotoinFB() {
        if (imgUri != null) {
            dialog = new ProgressDialog(this);
            dialog.setTitle("사진을 업로드 중입니다...");
            dialog.show();

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = sdf.format(date)+ ".png";

            //Get the storage reference
            final StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + Login.getUserFamilyID() + "/" + filename);

            //Add file to reference
            ref.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();
                                    ImageUpload imageUpload = new ImageUpload(filename, downloadUrl, family);
                                    int fcount = Login.getUserFamilyCount() + 1;

                                    mDatabaseRef.child(Login.getUserFamilyID()).child(Integer.toString(fcount)).setValue(imageUpload);
                                    Log.v("된거야?", imageUpload.getUrl() + " " + imageUpload.getName() + " " + imageUpload.getFamily());

                                    String url = "http://104.155.130.175/makedir.py";
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("filename", filename);
                                    contentValues.put("ui", Login.getUserFamilyID());
                                    contentValues.put("fr", fcount);

                                    NetworkTask networkTask = new NetworkTask(url, contentValues);
                                    networkTask.execute();

                                    Infomation.getDatabase("User").child(Login.getUserFamilyID()).child("familyCount").setValue(fcount);
                                    Login.setFamilyCount(fcount);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(PopupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    try {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded " + (int) progress + "%");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        }
    }

    //PopupInitSet으로 부터 받아오는 정보
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imgUri = data.getParcelableExtra("photoUri");
            setInitPhoto.setImageURI(imgUri);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    @Override
    public void onBackPressed() { //뒤로가기 못하게
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            if (dialog.isShowing())
                dialog.dismiss();
    }
}
