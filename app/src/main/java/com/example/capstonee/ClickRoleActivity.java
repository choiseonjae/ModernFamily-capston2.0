package com.example.capstonee;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.capstonee.Adapter.RecyclerPhotoViewAdapter;
import com.example.capstonee.Adapter.RoleClickAdapter;
import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class ClickRoleActivity extends AppCompatActivity {
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Boolean isPermission = true;
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private Uri photoUri;
    private Toolbar albumToolbar;
    RecyclerView recyclerView;
    RoleClickAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_role);

        recyclerView = findViewById(R.id.role_recyclerview);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewAdapter = new RoleClickAdapter(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(recyclerViewAdapter);

        getData();

        albumToolbar = findViewById(R.id.album_toolbar);
        setSupportActionBar(albumToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        fab = findViewById(R.id.role_fab_main);
        fab1 = findViewById(R.id.role_fab_sub1);
        fab2 = findViewById(R.id.role_fab_sub2);

        tedPermission();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                Log.v("알림", "사진촬영 선택");
                //if(isPermission) openCamera();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //toolbar의 back키 눌렀을 때 동작
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 디바이스 사용자로 부터 얻어야할 권한 설정
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                isPermission = false;
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void getData() {
        String role = getIntent().getStringExtra("role");
        // 현재 사용자의 Family DB 에서 역할가져온다.
        final DatabaseReference roleRef = Infomation.getDatabase("role").child(Login.getUserFamilyID()).child(role);
        if(roleRef.getKey() != null) {
            Log.e("roleRef!! = ", roleRef.getKey());
            // family - id - 이후 key : value
            // 새끼가 빠져서 들어왔으면 짹깍짹깍 안들어오냐?
            roleRef.addListenerForSingleValueEvent(new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.e("sg", snapshot.getChildren().toString());
                        ImageUpload imageUpload = snapshot.getValue(ImageUpload.class);
//                        // 현재 역할 한 분
                        String uri = imageUpload.getUrl();
                        Log.e("ref uri = ", imageUpload.getUrl());
                        recyclerViewAdapter.addItem(uri);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // 내가 모르는 메소드 ㅋㅋ..
    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}
