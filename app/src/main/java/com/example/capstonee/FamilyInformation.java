package com.example.capstonee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstonee.Adapter.FamilyAdapter;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FamilyInformation extends AppCompatActivity {
    FamilyAdapter adapter;
    User user;
    private static final int POP_RESULT = 9876;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_information);


        // 내 정보 가져오기 : user <-  내 정보 넣음
        final DatabaseReference myInfo = Infomation.getDatabase("User").child(Login.getUserID());
        myInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                init();
                getData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // 우리 가족 클릭
        TextView ourFamily = findViewById(R.id.family1);
        ourFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "우리 가족 선택", Toast.LENGTH_LONG).show();
                // 디폴트가 우리 가족이면
                if (Login.getUserDefaultFamily() == 1) return;

                // 존재하는 우리가족 FamilyID가 없으면 설정
                if (Login.getUserFamilyID1().equals("")) {
                    setNewFamily_alert(1);
                }
                // 이미 FamilyID가 존재하면 해당 FamilyID가 Default로 설정
                else {
                    Login.setFamilyID(Login.getUserFamilyID1());
                    Login.setDefaultFamily(1);
                }

            }
        });
        // 친가 클릭
        // 지금 디폴트가 친가가 아닐때
        TextView chin_ga = findViewById(R.id.family2);
        chin_ga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "친가 선택", Toast.LENGTH_LONG).show();

                // 디폴트가 친가면
                if (Login.getUserDefaultFamily() == 2)
                    return;


                // 존재하는 친가 FamilyID가 없으면 설정
                if (Login.getUserFamilyID2().equals("")) {
                    setNewFamily_alert(2);
                }
                // 이미 FamilyID가 존재하면 해당 FamilyID가 Default로 설정
                else {
                    Login.setFamilyID(Login.getUserFamilyID2());
                    Login.setDefaultFamily(2);
                }

            }
        });


        // 외가 클릭
        TextView why_ga = findViewById(R.id.family3);
        why_ga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "외가 선택", Toast.LENGTH_LONG).show();

                // 디폴트가 친가면
                if (Login.getUserDefaultFamily() == 3)
                    return;

                // 존재하는 친가 FamilyID가 없으면 설정
                if (Login.getUserFamilyID3().equals("")) {
                    setNewFamily_alert(3);
                }
                // 이미 FamilyID가 존재하면 해당 FamilyID가 Default로 설정
                else {
                    Login.setFamilyID(Login.getUserFamilyID3());
                    Login.setDefaultFamily(3);
                }

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                familyAdd_alert();
            }
        });


    }

    private void setNewFamily_alert(final int default_family) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(FamilyInformation.this);

        ad.setTitle("관계 없음");       // 제목 설정
        ad.setMessage("폴더에 사진이 없습니다.\n 설정으로 넘어갑니다.");   // 내용 설정

        // 확인 버튼 설정
        ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Login.setDefaultFamily(default_family);
                setNewFamily();
            }
        });

        ad.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.show();

    }

    private void setNewFamily() {
        Intent intent = new Intent(this, FindMyFamilyActivity.class);
        startActivityForResult(intent, 1);
    }

    private void init() {  //리사이클러뷰 초기화 및 동작
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 3 개 나옴 한 줄에 리사이클러뷰가 네모 형태로 나중에 써보셈
//      recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FamilyAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void familyAdd_alert() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(FamilyInformation.this);

        ad.setTitle("가족 추가");       // 제목 설정
        ad.setMessage("추가할 가족 ID 입력해주세요.");   // 내용 설정

        // EditText 삽입하기
        final EditText et = new EditText(FamilyInformation.this);
        ad.setView(et);

        // 확인 버튼 설정
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Text 값
                final String addID = et.getText().toString();

                // 추가할 가족의 ID를 User DB 에서 확인하기 위해 참조를 가져온다.
                final DatabaseReference userRef = Infomation.getDatabase("User").child(addID);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // User DB 에서 존재하지 않는 사용자이면 종료
                        if (!dataSnapshot.exists()) {
                            Log.d("없는 ", "사용자 입니다.");
                            return;
                        }

                        // 이미 맺어진 가족이 있으면 종료
                        // 현재는 처음부터 값을 안 넣어서 null 로 비교함.
                        String opponentFamilyID = dataSnapshot.getValue(User.class).getFamilyID();
                        if (!opponentFamilyID.equals(""))
//                        if (opponentFamilyID != null)
                            return;

                        // 맺어진 가족이 없는 사용자이기 때문에 내 가족으로 추가
                        // 1. 현재 사용자가 가족관계가 있는지 본다.
                        // 2. 현재 사용자에게 가족관계가 없으면 생성 및 추가, 있으면 가져온다.
                        // 3. 상대방 User DB 에 familyID를 추가한다.
                        // 4. 가지고 있는 가족 ID로 내 이름과, 사용자 이름을 넣는다.

                        String familyID = "";
                        DatabaseReference familyRef;
                        // family DB 와 family ID를 가져온다.
                        // 현재는 값 입력을 아예 안해서 null 로 비교
                        if (user.getFamilyID().equals("")) {
//                        if(user.getFamilyID() == null){
                            familyRef = Infomation.getDatabase("Family").push();
                            familyID = familyRef.getKey();

                            // 사용자 User DB에 family ID 추가
                            user.setFamilyID(familyID);
                            Infomation.getDatabase("User").child(Login.getUserID()).setValue(user);

                            // family DB에 사용자 추가
                            familyRef.push().setValue(Login.getUserID());
                        } else {
                            familyID = user.getFamilyID();
                            familyRef = Infomation.getDatabase("Family").child(familyID);
                        }

                        Map<String, Object> update = new HashMap<>();
                        update.put("familyID", familyID);

                        // user DB에 family ID 추가
                        Infomation.getDatabase("User").child(addID).updateChildren(update);

                        // family DB에 추가
                        familyRef.push().setValue(addID);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        // 취소 버튼 설정
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            private DialogInterface dialog;
            private int which;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                this.dialog = dialog;
                this.which = which;
            }
        });

        // 창 띄우기
        ad.show();

    }

    private void getData() {

        String familyID = user.getFamilyID();
        final DatabaseReference familyRef = Infomation.getDatabase("Family").child(familyID);
        familyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("dataSnapshot : ", dataSnapshot.toString());

                // 현재 사용자랑 같은 팀을 보여줌.
                if (!dataSnapshot.getValue().toString().equals(Login.getUserID()))
                    adapter.addItem(dataSnapshot.getValue().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("change", dataSnapshot.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e("remove", dataSnapshot.toString());
                adapter.remove(dataSnapshot.getValue().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 결과 해결
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            Intent intent = new Intent(this, PopupActivity.class);
            startActivityForResult(intent, 1);
        } else if (requestCode == 1 && resultCode == POP_RESULT) {
            Boolean keep = (Boolean) data.getBooleanExtra("keep", false);
            if (keep) {
                Intent intent = new Intent(this, PopupActivity.class);
                startActivityForResult(intent, 1);
            }
        }
        Log.e("앨범 코드 ; ", requestCode + "");
        Log.e("앨범 코드2 ; ", resultCode + "");
    }

}
