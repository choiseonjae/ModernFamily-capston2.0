package com.example.capstonee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.capstonee.Model.Login;

public class FakeActivity extends AppCompatActivity {
    private static final int POP_RESULT = 9876;
    private static final int FAKE_REQUEST = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake);
        if(isNeverVisited()){
            Intent intent = new Intent(this, FindMyFamilyActivity.class);
            startActivityForResult(intent, FAKE_REQUEST);
        }else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super = 우선 프래그먼트를 가라
        if(resultCode == RESULT_CANCELED && requestCode == FAKE_REQUEST) {
            Toast.makeText(FakeActivity.this, "이 ID를 가족ID로 생성합니다.", Toast.LENGTH_SHORT).show();
            //가족 ID가 없다면 여기로 오거라
            Intent intent = new Intent(this, PopupActivity.class);
            startActivityForResult(intent, FAKE_REQUEST);
        }else if(resultCode == POP_RESULT && requestCode == FAKE_REQUEST){
            //POP_RESULT에 keep이 true면, 계속해서 초기사진을 집어넣어라
            Boolean keep = (Boolean) data.getBooleanExtra("keep", false);
            if(keep){
                Intent intent = new Intent(this, PopupActivity.class);
                startActivityForResult(intent, FAKE_REQUEST);
            }
        }else{
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public boolean isNeverVisited(){
        //첫 로그인 시 UserFamilyID = ""
        return Login.getUserFamilyID().equals("");
    }
}
