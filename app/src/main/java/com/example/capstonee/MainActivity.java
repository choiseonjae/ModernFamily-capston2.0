package com.example.capstonee;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private onBackPressedDouble obpd;
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
        adapter.AddFragment(new FragmentChat(), "");
        adapter.AddFragment(new FragmentLocation(), "");
        adapter.AddFragment(new FragmentSetting(), "");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_album_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_place_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_settings_black_24dp);
    }

    @Override
    public void onBackPressed() {
        obpd.onBackPressed();
    }
}
