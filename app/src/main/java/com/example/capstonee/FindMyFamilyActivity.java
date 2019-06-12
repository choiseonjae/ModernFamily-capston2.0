package com.example.capstonee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// 가족이랑 관계 맺기
public class FindMyFamilyActivity extends Activity {
    EditText familyID, familyPW;
    Button noIDButton, enrollButton;
    String ID, PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_find_my_family);
        familyID = findViewById(R.id.familyID);
        familyPW = findViewById(R.id.familyPW);
        noIDButton = findViewById(R.id.noIDButton);
        enrollButton = findViewById(R.id.enrollButton);

        //가족 ID가 없을 때
        noIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.setFamilyID(Login.getUserID());
                Login.setFamilyID1(Login.getUserID());
                
                ImageUpload imageUpload = new ImageUpload("unknown", "https://sj0ufylf41ew0pru-zippykid.netdna-ssl.com/wp-content/uploads/2016/10/questionmark6-300x300.jpg", "미분류");
                FirebaseDatabase.getInstance().getReference("Family").child(Login.getUserFamilyID()).child("unknown").setValue(imageUpload);

                Intent intent = new Intent();
                intent.putExtra("keep", true);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        //가족 ID 등록시
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = familyID.getText().toString();
                PW = familyPW.getText().toString();

                //  사용자가 내 아이디 입력
                if (ID.equals(Login.getUserID())) {
                    Toast.makeText(FindMyFamilyActivity.this, "자신의 ID가 아닌 가족의 ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else { // 정상 입력
                    final DatabaseReference findUser = Infomation.getDatabase("User");
                    findUser.child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // 해당 ID가 존재
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                // PW가 일치하지 않음
                                if (!PW.equals(user.getPassword())) {
                                    Toast.makeText(FindMyFamilyActivity.this, "ID / PW를 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                                } else { // PW 가 일치
                                    // family ID
                                    final String familyID = user.getFamilyID1();
                                    // familyCount;
                                    final int familyCount = user.getFamilyCount();
                                    // User DB : 현재 사용자의 familyID 값을 업데이트함. - 검색한 user 와 같은 family ID 값으로
                                    Infomation.getDatabase("User").child(Login.getUserID()).child("familyID").setValue(familyID);
                                    Infomation.getDatabase("User").child(Login.getUserID()).child("familyID1").setValue(familyID);

                                    Login.setFamilyID(familyID);
                                    Login.setFamilyID1(familyID);
                                    Login.setFamilyCount(familyCount);

                                    Intent intent = new Intent();
                                    intent.putExtra("keep", false);
                                    setResult(RESULT_OK, intent);

                                    finish();

                                }

                            } else {
                                Toast.makeText(FindMyFamilyActivity.this, "ID / PW를 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    @Override
    public void onBackPressed() { //뒤로가기 못하게
    }
}
