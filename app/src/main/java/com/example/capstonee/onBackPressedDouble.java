package com.example.capstonee;

import android.app.Activity;
import android.widget.Toast;

/**
 * 뒤로가기 두 번 누를시 프로그램 종료시킴
 */
public class onBackPressedDouble {
    private long backKeyPressedTime = 0;

    private Toast toast;
    private Activity activity;
    public onBackPressedDouble(Activity context){
        this.activity = context;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
            toast.cancel();
            activity.finishAffinity();
        }
    }
    public void showGuide(){
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
