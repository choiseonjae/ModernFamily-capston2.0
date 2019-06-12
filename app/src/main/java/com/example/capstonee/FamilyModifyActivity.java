package com.example.capstonee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.capstonee.Adapter.RecyclerPhotoViewAdapter;
import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class FamilyModifyActivity extends AppCompatActivity {
    private static final int POP_RESULT = 9876;
    private static final int REQUEST_FMODIFY = 789;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    RecyclerPhotoViewAdapter recyclerViewAdapter;
    private Toolbar modifyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_modify);

        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setMessage("동일인물의 사진을 바꾸려면\n삭제 후 다시 저장해주세요.\n동일인이라면 꼭 같은 이름으로 저장하세요.\n(그래야 같은 앨범이 됩니다!)");
        confirm.setPositiveButton("확인", null);
        confirm.show();

        recyclerView = findViewById(R.id.modifyRecycler);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewAdapter = new RecyclerPhotoViewAdapter(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(recyclerViewAdapter);
        RecyclerPhotoViewAdapter.setMode = 2;

        getData();

        floatingActionButton = findViewById(R.id.fab_modify);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FamilyModifyActivity.this, PopupActivity.class);
                startActivityForResult(intent, REQUEST_FMODIFY);
            }
        });

        modifyToolbar = findViewById(R.id.modify_toolbar);
        setSupportActionBar(modifyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //toolbar의 back키 눌렀을 때 동작
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FMODIFY && resultCode == POP_RESULT) {
            Boolean keep = (Boolean) data.getBooleanExtra("keep", false);
            if (keep) {
                Intent intent = new Intent(this, PopupActivity.class);
                startActivityForResult(intent, REQUEST_FMODIFY);
            }
        }
    }

    // DB의 변경을 바로 바로 업데이트 한 뒤 xml 에 뿌려주기 위한 Listener
    private void getData() {
        Log.d("LOG - FAMILY ID : ", Login.getUserFamilyID());

        // 현재 사용자의 Family DB 에서 역할가져온다.
        final DatabaseReference roleRef = Infomation.getDatabase("Family").child(Login.getUserFamilyID());
        Log.e("roleRef = ", roleRef.getKey());
        // family - id - 이후 key : value
        roleRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ImageUpload imageUpload = dataSnapshot.getValue(ImageUpload.class);
                // 현재 역할 한 분
                String name = imageUpload.getName();
                String role = imageUpload.getFamily();
                String uri = imageUpload.getUrl();

                if (!role.equals("미분류")) {
                    recyclerViewAdapter.addItem(name, uri, role);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ImageUpload imageUpload = dataSnapshot.getValue(ImageUpload.class);

                String name = imageUpload.getName();
                recyclerViewAdapter.removeItem(name);
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
