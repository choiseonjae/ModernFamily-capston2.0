package com.example.capstonee;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.capstonee.Adapter.DatePictureAdapter;
import com.example.capstonee.Adapter.RoleAdapter;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.Picture;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ShowPictureAtDate extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatePictureAdapter adapter;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture_at_date);

        date = getIntent().getStringExtra("date");

        initAdapter();
        getData();

    }

    // 선재 코드
    // adapter 초기화
    private void initAdapter() {
        // xml 에 존재하는 recycler 와 연결
        recyclerView = findViewById(R.id.show_picture_recycler_view);
        // 이거는 꽉찬 그리디 화면 인가?
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        // 내가 데이터를 넣어줄 어뎁터 생성
        adapter = new DatePictureAdapter(getApplicationContext());
        // 현재 xml 에 보여질 recycler 설정
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        // recycler 에 보여질 객체 하나를 뜻하는 어뎁터 연결
        recyclerView.setAdapter(adapter);
    }

    // DB의 변경을 바로 바로 업데이트 한 뒤 xml 에 뿌려주기 위한 Listener
    private void getData() {
        Log.d("LOG - FAMILY ID : ",
                Login.getUserFamilyID());

        // 현재 사용자의 Family DB 에서 역할가져온다.
        final DatabaseReference dateRef = Infomation.getDatabase("Album").child(Login.getUserFamilyID());

        // family - id - 이후 key : value
        // 새끼가 빠져서 들어왔으면 짹깍짹깍 안들어오냐?
        dateRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



                String searchDate = dataSnapshot.child("uploadTime").getValue().toString().split("_")[0];


                // 잠시 로그 확인
                Log.e("date ", date);
                Log.e("searchDate ", searchDate);

                if(searchDate.equals(date)) {

                    Picture picture = dataSnapshot.getValue(Picture.class);

                    // 역할 + 사진 주소
                    adapter.addItem(picture);

                    // F5
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // F5
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // F5
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // F5
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
