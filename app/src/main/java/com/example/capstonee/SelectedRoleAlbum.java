package com.example.capstonee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;
import android.widget.TextView;

public class SelectedRoleAlbum extends AppCompatActivity {

    TextView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_role_album);

        String role = getIntent().getStringExtra("role");
        recyclerView = findViewById(R.id.selected_role_text_view);
        recyclerView.setText(role);


    }
}
