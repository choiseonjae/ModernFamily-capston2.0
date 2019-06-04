package com.example.capstonee.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.capstonee.R;
import com.example.capstonee.ShowPictureAtDate;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.InputStream;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentHome extends Fragment {
    View v;
    ImageButton changeBG;
    RelativeLayout viewBG;
    Boolean isPermission = false;

    CalendarView calendarView;
    TextView myDate;

    private static final int PICK_FROM_ALBUM = 567;


    public FragmentHome() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_fragment, container, false);
        tedPermission();

        changeBG = v.findViewById(R.id.changeBG);
        changeBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });

        viewBG = v.findViewById(R.id.home_bg);
        return v;
    }
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){
            try{
                InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();

                // bitmap 타입을 drawable로 변경
                Drawable drawable = new BitmapDrawable(getContext().getResources(), img);

                viewBG.setBackground(drawable);

//                viewBG.setB
//                viewBG.setImageBitmap(img);
//                viewBG.setAdjustViewBounds(true);
//                viewBG.setLayoutParams(new RelativeLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void tedPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                isPermission = false;
            }
        };

        TedPermission.with(getActivity())
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
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
                intent.putExtra("date", intentDate);

                // 첫 로그인 화면처럼 배경 보이고 싶은데 안되네요;; ㅠㅠ
                startActivity(intent);

            }

        });

    }

}
