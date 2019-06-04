package com.example.capstonee;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.capstonee.Adapter.ViewPagerAdapter;
import com.example.capstonee.Album.FragmentAlbum;
import com.example.capstonee.Chat.Chatting;
import com.example.capstonee.Fragment.FragmentChat;
import com.example.capstonee.Fragment.FragmentHome;
import com.example.capstonee.Fragment.FragmentLocation;
import com.example.capstonee.Fragment.FragmentSetting;
import com.example.capstonee.Model.Login;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private onBackPressedDouble obpd;
    private static final int POP_RESULT = 9876;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obpd = new onBackPressedDouble(this);
        tabLayout = findViewById(R.id.tablayout_id);
        viewPager = findViewById(R.id.viewpager_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //프래그먼트를 여기에 추가
        adapter.AddFragment(new FragmentHome(), "");
        adapter.AddFragment(new FragmentAlbum(), "");
        adapter.AddFragment(new Chatting(), "");
        adapter.AddFragment(new FragmentLocation(), "");
        adapter.AddFragment(new FragmentSetting(), "");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_album_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_place_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_settings_black_24dp);

        if(isNeverVisited()){
            Intent intent = new Intent(this, FindMyFamilyActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_CANCELED) {
            Intent intent = new Intent(this, PopupActivity.class);
            startActivityForResult(intent, 1);
        }else if(requestCode == 1 && resultCode == POP_RESULT){
            Boolean keep = (Boolean) data.getBooleanExtra("keep", false);
            if(keep){
                Intent intent = new Intent(this, PopupActivity.class);
                startActivityForResult(intent, 1);
            }
        }
        Log.e("앨범 코드 ; ", requestCode + "");
        Log.e("앨범 코드2 ; ", resultCode+ "");
    }

    public boolean isNeverVisited(){
        //첫 로그인 시 UserFamilyID = ""
        return Login.getUserFamilyID().equals("");
    }

    @Override
    public void onBackPressed() {
        obpd.onBackPressed();
    }
}
