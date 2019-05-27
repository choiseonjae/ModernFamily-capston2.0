package com.example.capstonee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InitActivity extends AppCompatActivity {
    private onBackPressedDouble obpd;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        obpd = new onBackPressedDouble(this);

        if(isLogined()){ //자동로그인 시
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference saveUserInfo = database.getReference("User");
            Login.setID(userID);
            saveUserInfo.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("LOGGOG", user.getName()+ " " +user.getPassword()+ " " +user.getPhone());
                    Login.setName(user.getName());
                    Login.setPassword(user.getPassword());
                    Login.setPhone(user.getPhone());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.d("Login1", Login.getUserID());
            Intent intent = new Intent(InitActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(InitActivity.this, SignActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        obpd.onBackPressed();
    }
    public boolean isLogined(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        userID = pref.getString("userID", null);
        String userPassword = pref.getString("userPassword", null);
        Log.d("LOGIN IN INIT ", userID + " " + userPassword);
        return userID != null && userPassword != null;
    }
}
