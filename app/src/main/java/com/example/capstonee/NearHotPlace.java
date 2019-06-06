package com.example.capstonee;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.capstonee.Adapter.GPSAdapter;
import com.example.capstonee.Adapter.HotPlaceAdapter;
import com.example.capstonee.Model.Infomation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearHotPlace extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HotPlaceAdapter adapter;
    private Geocoder geocoder;
    // 2000 -> 2km
    private final int radius = 2000;
    private final String APIkey = "jPNKVysk1VVndzX8Me3F%2F81FWko4M70FivLAOLRLJMA5uNIlYHqdTCw9Qtvj1feC6qRmjy3ifWxGMKv3kcEi1w%3D%3D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_near_hot_place);

            String address = getIntent().getExtras().getString("address");

            geocoder = new Geocoder(getApplicationContext());
            initAdapter();
            getData(address);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 선재 코드
    // adapter 초기화
    private void initAdapter() {
        // xml 에 존재하는 recycler 와 연결
        recyclerView = findViewById(R.id.hot_place);
        // 이거는 꽉찬 그리디 화면 인가?
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        // 내가 데이터를 넣어줄 어뎁터 생성
        adapter = new HotPlaceAdapter(getApplicationContext());
        // 현재 xml 에 보여질 recycler 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // recycler 에 보여질 객체 하나를 뜻하는 어뎁터 연결
        recyclerView.setAdapter(adapter);
    }

    // DB의 변경을 바로 바로 업데이트 한 뒤 xml 에 뿌려주기 위한 Listener
    private void getData(final String address) throws InterruptedException {

        double[] area = convertAddrToXY(address);

        Log.e("area", area[0] + "");

        // 위도 - 경도
        final double longitude = area[0];
        final double latitude = area[1];

        ////////////////////////////////////////////////////////////////////

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ////////////////////////////////////////////
                String key = "jPNKVysk1VVndzX8Me3F%2F81FWko4M70FivLAOLRLJMA5uNIlYHqdTCw9Qtvj1feC6qRmjy3ifWxGMKv3kcEi1w%3D%3D";

                StringBuilder apiURL = new StringBuilder(
                        "https://api.visitkorea.or.kr/openapi/service/rest/RusService/locationBasedList");
                // 인증키
                apiURL.append("?ServiceKey=" + key);
                // 위치 반경
                apiURL.append("&mapX=" + longitude + "&mapY=" + latitude + "&radius=" + radius);
                // 나머지 설정
                apiURL.append("&pageNo=1&numOfRows=10&listYN=Y&arrange=A&MobileOS=AND&MobileApp=모던패밀리");

                try {
                    // 전체
                    Element doc = Jsoup.connect(apiURL.toString()).get();

                    // item 하나 하나 추출
                    Elements list = doc.select("item");
                    for (Element e : list) {
                        String title = e.select("title").text().split("\\(")[1].split("\\)")[0];
                        String image = e.select("firstimage").text();
                        String image2 = e.select("firstimage2").text();
                        String address = e.select("addr1").text();

                        Map<String, String> map = new HashMap<>();
                        map.put("title", title);
                        map.put("address", address);
                        map.put("image", image);
                        map.put("image2", image2);

                        Log.e("title", title);
                        Log.e("address", address);
                        Log.e("image", image);
                        Log.e("image2", image2);


                        if (!map.get("title").equals("") && !map.get("image").equals("")) {
                            adapter.addItem(map);
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        t.start();
        t.join();
        adapter.notifyDataSetChanged();

        ////////////////////////////////////////////////////////////////////
    }

    // 주소를 위도 경도로
    private double[] convertAddrToXY(String addr) {
        List<Address> list = null;

        String str = addr;
        try {
            list = geocoder.getFromLocationName(
                    str, // 지역 이름
                    10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.e("해당되는 주소 정보는 없습니다", "??");
            } else {
//                Log.e(" 결과는 ", list.get(0).toString());
                //          list.get(0).getCountryName();  // 국가명
                Log.e(" 위도 : ", list.get(0).getLatitude() + "");        // 위도
                Log.e(" 경도 : ", list.get(0).getLongitude() + "");    // 경도

                return new double[]{list.get(0).getLongitude(), list.get(0).getLatitude()};
            }

        }
        return null;
    }
}
