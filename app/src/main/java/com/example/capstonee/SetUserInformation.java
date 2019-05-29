package com.example.capstonee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.google.firebase.database.DatabaseReference;

public class SetUserInformation extends AppCompatActivity {
    private EditText setInfoPhone, setInfoPassword, setInfoCheckPassword;
    private TextView setInfoID, setInfoName, setInfoBirth;
    private Button setInfoButton, setInfoCancel;
    private ImageView setInfoIcon;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_set);

        setInfoPhone = findViewById(R.id.setInfoPhone);
        setInfoPassword = findViewById(R.id.setInfoPassword);
        setInfoCheckPassword = findViewById(R.id.setInfoCheckPassword);
        setInfoID = findViewById(R.id.setInfoID);
        setInfoName = findViewById(R.id.setInfoName);
        setInfoBirth = findViewById(R.id.setInfoBirth);
        setInfoButton = findViewById(R.id.setInfoChange);
        setInfoCancel = findViewById(R.id.setInfoCancel);
        setInfoIcon = findViewById(R.id.setInfoIcon);

        setInfoPhone.setText(Login.getUserPhone());
        setInfoID.setText(Login.getUserID());
        setInfoBirth.setText(Login.getUserBirth());
        setInfoName.setText(Login.getUserName());

        setInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!setInfoPassword.getText().toString().equals(setInfoCheckPassword.getText().toString())) {
                    Toast.makeText(SetUserInformation.this, "비밀번호가 서로 같지 않습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(SetUserInformation.this);
                    alertdialog.setTitle("변경");
                    alertdialog.setMessage("이대로 변경하시겠습니까?");
                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference db = Infomation.getDatabase("User");
                            String phone = setInfoPhone.getText().toString();
                            String password = setInfoPassword.getText().toString();
                            db.child(Login.getUserID()).child("phone").setValue(phone);
                            db.child(Login.getUserID()).child("password").setValue(password);
                            Login.setPhone(phone);
                            Login.setPassword(password);
                            finish();
                        }
                    }).setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
                    alertdialog.show();
                }
            }
        });
        setInfoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(SetUserInformation.this);
                alertdialog.setTitle("취소");
                alertdialog.setMessage("개인정보 변경을 취소하시겠습니까?");
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertdialog.show();
            }
        });

        setInfoCheckPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (setInfoPassword.getText().toString().equals(setInfoCheckPassword.getText().toString())) {
                    setInfoIcon.setImageResource(R.drawable.ic_check_black_24dp);
                } else {
                    setInfoIcon.setImageResource(R.drawable.ic_close_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
