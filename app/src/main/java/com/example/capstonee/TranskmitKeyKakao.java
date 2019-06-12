package com.example.capstonee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstonee.Adapter.MessageAdapter;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Random;

public class TranskmitKeyKakao extends AppCompatActivity {


    private Button button;
    private MaterialEditText editText;
    private int key;
    private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_key);
        button = findViewById(R.id.okaykeybutton);
        editText = (MaterialEditText) findViewById(R.id.key);

        key = createRandomnumber();
        message = Integer.toString(key);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setPackage("com.kakao.talk");
        startActivity(intent);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals(message)){
                    MessageAdapter.keyFlag = true; //복호화 가능해짐
                    Infomation.getDatabase("User").child(Login.getUserID()).child("visible").setValue(true);
                    Login.setVisible(true);
                    Toast.makeText(TranskmitKeyKakao.this, "키 값 일치. 대화 잠금장치가 풀립니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else{
                    Toast.makeText(TranskmitKeyKakao.this, "키 값 불일치", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    int createRandomnumber(){
        key = new Random().nextInt(8999) + 1000;
        return key;
    }

}
