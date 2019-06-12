package com.example.capstonee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FamilyInformation extends AppCompatActivity {
    User user;
    private static final int CHINGA = 9876;
    private static final int WOEGA = 8765;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_information);


        // 우리 가족 클릭
        findViewById(R.id.family1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 디폴트가 우리 가족이면
                if (Login.getUserDefaultFamily() == 1)
                    Toast.makeText(FamilyInformation.this, "이미 우리 가족을 선택한 상태입니다!", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(FamilyInformation.this, "보여줄 앨범을 우리 가족으로 변경합니다!", Toast.LENGTH_LONG).show();
                    Login.setFamilyID(Login.getUserFamilyID1());
                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("User").child(Login.getUserFamilyID());
                    dr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            int fc, fc2;
                            fc = user.getFamilyCount();
                            fc2 = user.getFamilyCount2();
                            Login.setFamilyCount(fc);
                            Login.setFamilyCount2(fc2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Login.setDefaultFamily(1);
                }
            }
        });
        // 친가 클릭
        // 지금 디폴트가 친가가 아닐때
        findViewById(R.id.family2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 디폴트가 친가면
                if (Login.getUserDefaultFamily() == 2) {
                    Toast.makeText(FamilyInformation.this, "이미 친가를 선택하셨습니다!", Toast.LENGTH_LONG).show();
                }
                else {
                    // 존재하는 친가 FamilyID가 없으면 설정
                    if (Login.getUserFamilyID2().equals("")) {
                        Intent intent = new Intent(FamilyInformation.this, FindMyCousinActivity.class);
                        intent.putExtra("COUSIN", "CHINGA");
                        startActivityForResult(intent, CHINGA);
                    } else {
                        Toast.makeText(FamilyInformation.this, "보여줄 앨범을 친가로 변경합니다!", Toast.LENGTH_LONG).show();
                        Login.setFamilyID(Login.getUserFamilyID2());
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("User").child(Login.getUserFamilyID2());
                        dr.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                int fc, fc2;
                                fc = user.getFamilyCount();
                                fc2 = user.getFamilyCount2();
                                Login.setFamilyCount(fc);
                                Login.setFamilyCount2(fc2);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Login.setDefaultFamily(2);
                    }
                }

            }
        });


        // 외가 클릭
        findViewById(R.id.family3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 디폴트가 외가면
                if (Login.getUserDefaultFamily() == 3){
                    Toast.makeText(FamilyInformation.this, "이미 외가를 선택하셨습니다!", Toast.LENGTH_LONG).show();
                }

                else {
                    // 존재하는 외가 FamilyID가 없으면 설정
                    if (Login.getUserFamilyID3().equals("")) {
                        Intent intent = new Intent(FamilyInformation.this, FindMyCousinActivity.class);
                        intent.putExtra("COUSIN", "WOEGA");
                        startActivityForResult(intent, WOEGA);
                    } else {
                        Toast.makeText(FamilyInformation.this, "보여줄 앨범을 외가로 변경합니다!", Toast.LENGTH_LONG).show();
                        Login.setFamilyID(Login.getUserFamilyID3());
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("User").child(Login.getUserFamilyID3());
                        dr.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                int fc, fc2;
                                fc = user.getFamilyCount();
                                fc2 = user.getFamilyCount2();
                                Login.setFamilyCount(fc);
                                Login.setFamilyCount2(fc2);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Login.setDefaultFamily(3);
                    }
                }
            }
        });
    }

    // 결과 해결
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHINGA && resultCode == RESULT_OK) {
            Login.setFamilyID(Login.getUserFamilyID2());
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("User").child(Login.getUserFamilyID2());
            dr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    int fc, fc2;
                    fc = user.getFamilyCount();
                    fc2 = user.getFamilyCount2();
                    Login.setFamilyCount(fc);
                    Login.setFamilyCount2(fc2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Login.setDefaultFamily(2);
        }
        else if(requestCode == WOEGA && resultCode == RESULT_OK){
            Login.setFamilyID(Login.getUserFamilyID3());
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("User").child(Login.getUserFamilyID3());
            dr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    int fc, fc2;
                    fc = user.getFamilyCount();
                    fc2 = user.getFamilyCount2();
                    Login.setFamilyCount(fc);
                    Login.setFamilyCount2(fc2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Login.setDefaultFamily(3);
        }
    }
}
