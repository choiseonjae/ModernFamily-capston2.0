package com.example.capstonee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Calendar extends Fragment {
    CalendarView calendarView;
    TextView myDate;


    public Calendar() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.calendar_fragment, container, false);
    }
    public void onStart(){
        super.onStart();
        calendarView = (CalendarView) getView().findViewById(R.id.calendarView);
        myDate = (TextView)getView().findViewById(R.id.myDate);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                month += 1;
                String date = (month) + "/" + dayOfMonth  + "/" + year;
                myDate.setText(date);
                Intent intent = new Intent(getContext(), ShowPictureAtDate.class);
                String intentDate = (year + "").substring(2) + (month < 10 ? "0" + month : month) + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);

                intent.putExtra("date",intentDate);

                // 첫 로그인 화면처럼 배경 보이고 싶은데 안되네요;; ㅠㅠ
                startActivity(intent);


            }

        });

    }

}