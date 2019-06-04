package com.example.capstonee.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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

    // 2000 -> 2km
    private final int radius = 2000;
    private final String key = "jPNKVysk1VVndzX8Me3F%2F81FWko4M70FivLAOLRLJMA5uNIlYHqdTCw9Qtvj1feC6qRmjy3ifWxGMKv3kcEi1w%3D%3D";
    View v;
    private RecyclerView recyclerView;
    private GPSAdapter adapter;

    public FragmentLocation() {
    }

    Geocoder geocoder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.location_fragment, container, false);

        geocoder = new Geocoder(getContext());

        initAdapter(v);
        getData();

        return v;
    }

    // 주변 정보
    private void getNearPlaceInformation(final String address) throws InterruptedException {

        double[] area = convertAddrToXY(address);

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
                    Element doc = Jsoup.connect(apiURL.toString()).get();
//                    Log.e("? ", doc.toString());
                    Elements list = doc.select("item");
                    for (Element e : list) {
                        String title = e.select("title").text().split("\\(")[1].split("\\)")[0];
                        Log.e("title" , title);
                        String img = e.select("firstimage").text();
                        if (e.select("title").text() != null)
                            Log.d(" title -> ", e.select("title").text());
                        if (e.select("firstimage").text() != null)
                            Log.d(" firstimage -> ", e.select("firstimage").text());
                        if (e.select("firstimage2").text() != null)
                            Log.d(" firstimage2 -> ", e.select("firstimage2").text());
                        if (e.select("addr1").text() != null)
                            Log.d(" addr1 -> ", e.select("addr1").text());

                        if(/*title != null &&*/ img != null) {
                            adapter.addItem(address, img);
                            break;
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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

                                // 주소 넣으면 주변 정보 얻음.
                                try {
                                    getNearPlaceInformation(address);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }



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
