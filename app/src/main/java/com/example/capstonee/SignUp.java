package com.example.capstonee;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstonee.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    private MaterialEditText edtId, edtPhone, edtName, edtPassword, edtPassword2;
    private String id, name, birth, password, phone;
    private MaterialEditText edtBirth;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private ImageView setImage;
    private Button btnBirthChoose, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtId = (MaterialEditText) findViewById(R.id.edtId);
        edtName = (MaterialEditText) findViewById(R.id.edtName);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPhone.setText(getIntent().getStringExtra("edtPhone"));

        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPassword2 = (MaterialEditText) findViewById(R.id.edtPassword2);

        btnBirthChoose = findViewById(R.id.btnBirthChoose);

        edtBirth = (MaterialEditText) findViewById(R.id.edtBirth);
        edtBirth.setEnabled(false);

        setImage = findViewById(R.id.setImage);
        edtPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtPassword.getText().toString().equals(edtPassword2.getText().toString())) {
                    setImage.setImageResource(R.drawable.ic_check_black_24dp);
                } else {
                    setImage.setImageResource(R.drawable.ic_close_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnBirthChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String birth;
                        if (month < 10)
                            birth = year + "0" + (month + 1);
                        else birth = year + "" +(month + 1);
                        if (day < 10) birth += "0" + day;
                        else birth += Integer.toString(day);
                        edtBirth.setText(birth);
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(
                        SignUp.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDataSetListener,
                        year, month, day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }

        });
        btnSignUp = findViewById(R.id.btnSignUp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("잠시만 기다리세요....");
                mDialog.show();
                id = edtId.getText().toString();
                name = edtName.getText().toString();
                birth = edtBirth.getText().toString();
                password = edtPassword.getText().toString();
                phone = edtPhone.getText().toString();

                Log.d("LOG", "ID = " +id+ " NAME = " +name+ " BIRTH = " +birth+ " PASSWORD = " +password+ " PASSWORD2 = " +edtPassword2.getText().toString()+ " PHONE = " +phone);
                if (id.equals("") || name.equals("") || birth.equals("") || password.equals("") || edtPassword2.getText().equals("") || phone.equals("")) {
                    Toast.makeText(SignUp.this, "모든 정보를 다 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
                else if(!password.equals(edtPassword2.getText().toString())){
                    Toast.makeText(SignUp.this, "비밀번호가 서로 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
                else {
                    // 아이디, 이름, 번호 다 입력했는지 체크
                    table_user.child(edtId.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Check if already user phone
                            Log.d("LOG", dataSnapshot.toString());
                            Log.d("LOG", Boolean.toString(dataSnapshot.exists()));
                            if (dataSnapshot.exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "중복된 ID가 있습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(phone, name, password, birth, 0, "", "", "", "", 1);

                                table_user.child(id).setValue(user);
                                Toast.makeText(SignUp.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUp.this, SignActivity.class);
                                startActivity(intent);
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
}
