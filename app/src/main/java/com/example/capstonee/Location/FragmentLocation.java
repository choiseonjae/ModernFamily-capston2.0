package com.example.capstonee.Location;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.capstonee.Adapter.DatePictureAdapter;
import com.example.capstonee.Adapter.GPSAdapter;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.Picture;
import com.example.capstonee.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FragmentLocation extends Fragment {


    View v;
    private RecyclerView recyclerView;
    private GPSAdapter adapter;

    public FragmentLocation() {
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.location_fragment, container, false);



        initAdapter(v);
        getData();

        return v;
    }

    // 선재 코드
    // adapter 초기화
    private void initAdapter(View view) {
        // xml 에 존재하는 recycler 와 연결
        recyclerView = view.findViewById(R.id.popular_recycler_view);
        // 이거는 꽉찬 그리디 화면 인가?
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        // 내가 데이터를 넣어줄 어뎁터 생성
        adapter = new GPSAdapter(getContext());
        // 현재 xml 에 보여질 recycler 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // recycler 에 보여질 객체 하나를 뜻하는 어뎁터 연결
        recyclerView.setAdapter(adapter);
    }

    // DB의 변경을 바로 바로 업데이트 한 뒤 xml 에 뿌려주기 위한 Listener
    private void getData() {
        Infomation.getDatabase("GPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // key 대한민국 - 경기도 - 수원시 - 권선구 - --동
                ArrayList<String> addrList = new ArrayList<>();

                // 국가
                Iterable<DataSnapshot> countryList = dataSnapshot.getChildren();
                for (DataSnapshot country : countryList) {

                    // 도
                    Iterable<DataSnapshot> stateList = country.getChildren();

                    for (DataSnapshot state : stateList) {

                        // 시
                        Iterable<DataSnapshot> cityList = state.getChildren();

                        for (DataSnapshot city : cityList) {

                            // 구
                            Iterable<DataSnapshot> districtList = city.getChildren();

                            for (DataSnapshot district : districtList) {

                                Log.e("key (구) : ", district.getKey());
                                Log.e("value (동) : ", district.getValue().toString());

                                String si = city.getKey();
                                String gu = district.getKey();
                                final String dong = district.getChildren().iterator().next().getKey();

                                String address = si + " " + gu + " " + dong;
                                adapter.addItem(address);
                                adapter.notifyDataSetChanged();

                                // 주소 넣으면 주변 정보 얻음.
//                                try {
////                                    getNearPlaceInformation(address);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }



                            }

                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
