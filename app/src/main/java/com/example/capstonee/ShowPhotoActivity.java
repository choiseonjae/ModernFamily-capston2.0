package com.example.capstonee;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstonee.Model.ImageUpload;
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
import com.squareup.picasso.Picasso;


public class ShowPhotoActivity extends AppCompatActivity {
    private ImageView imageView;
    private Toolbar toolbar;
    private String ImageUrl;
    private String fileName;
    private String role;
    private String From;
    private String Kind;
    private static final int GO_BACK = 6754;

    // PJH
    // kind는 뭐냐면 어댑터에서 사진을 클릭해서 이 액티비티로 와서 삭제를 누르면,
    // DB에서 얘가 어디에 속해있는지 파악할 필요가 있다.
    // 그때 쓰는게 바로 Kind
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        Intent intent = getIntent();
        ImageUrl = intent.getStringExtra("imageUrl");
        fileName = intent.getStringExtra("fileName");
        role = intent.getStringExtra("role");
        From = intent.getStringExtra("From");

        Log.e("urlfilefrom", ImageUrl + " " + fileName + " " + role + " " + From);
        imageView = findViewById(R.id.detailPhoto);
        Picasso.with(getApplicationContext()).load(ImageUrl).fit().centerInside().into(imageView);

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
            if(s.equals("finish"))
                Toast.makeText(ShowPhotoActivity.this, "서버에서도 사진 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete_button:
                android.app.AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
                alertdialog.setTitle("삭제하시겠습니까?");
                alertdialog.setMessage("한번 삭제하면 돌이킬 수 없게 됩니다!");
                // 게시할 때
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (From.equals("PhotoView")) {
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
                                                    Log.e("시발", Kind);
                                                    Move();
                                                    String url = "http://34.97.246.11/removefile.py";

                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("filename", Kind+".png");
                                                    contentValues.put("ui", Login.getUserFamilyID());

                                                    NetworkTask networkTask = new NetworkTask(url, contentValues);
                                                    networkTask.execute();

                                                    int count = Login.getUserFamilyCount();
                                                    Login.setFamilyCount(count-1);
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
                            //RoleClickAdapter에서 클릭시
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
            case R.id.kakao_share:
                //TODO :: 카카오 공유넣기
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
    public void onBackPressed() {
    }
}
