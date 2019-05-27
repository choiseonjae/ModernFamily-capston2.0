package com.example.capstonee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignActivity extends AppCompatActivity {
    private onBackPressedDouble obpd;
    Button join, login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        obpd = new onBackPressedDouble(this);

        join = findViewById(R.id.join); // 회원가입 창으로 이동
        login = findViewById(R.id.login); // 로그인 창으로 이동

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this, SignIn.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        obpd.onBackPressed(); // 두번 누를 경우 앱 종료
    }
}
