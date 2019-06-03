package com.example.capstonee.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstonee.FamilyInformation;
import com.example.capstonee.MainActivity;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.R;
import com.example.capstonee.SetUserInformation;
import com.example.capstonee.SignActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentSetting extends Fragment {
    View v;
    private ImageButton setInfo;
    TextView Username, Id;
    private CircleImageView profile_imageView;
    private static final int PICK_FROM_ALBUM = 567;

    public FragmentSetting() {
    }

    private void changeProfile() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());

        ad.setTitle("프로필 사진 설정");       // 제목 설정

        ad.setItems(new String[]{"이미지 변경", "기본 이미지로 변경"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        if (id == 0)
                            goToAlbum();
                        else if (id == 1) {
                            // 초기화
                            Infomation.getDatabase("User").child(Login.getUserID()).child("profileUri").setValue("");
                            profile_imageView.setImageResource(R.drawable.default_profile);
                        }
                        dialog.dismiss();
                    }
                });

        ad.show();
    }

    private void goToAlbum() {
        //isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra("type", "profile_change");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.setting_fragment, container, false);
        Username = v.findViewById(R.id.username);
        Username.setText(Login.getUserName());
        Id = v.findViewById(R.id.userid);
        setInfo = v.findViewById(R.id.setInfo);
        Id.setText(Login.getUserID());
        LinearLayout logout = v.findViewById(R.id.logout);

        profile_imageView = v.findViewById(R.id.profile_image_setting);

        if (Login.getProfileUri().equals("")) {
            profile_imageView.setImageResource(R.drawable.default_profile);
        } else {
            Picasso.with(getContext()).load(Login.getProfileUri()).fit().into(profile_imageView);
        }

        profile_imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeProfile();
                return false;
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());
                alertdialog.setTitle("로그아웃");
                alertdialog.setMessage("로그아웃 하시겠습니까?");
                // 게시할 때
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        Log.d("LOGOUT : ", pref.getString("userID", null) + " " + pref.getString("userPassword", null));
                        editor.remove("userID");
                        editor.remove("userPassword");
                        editor.apply();
                        Toast.makeText(getContext(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), SignActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                alertdialog.show();
            }
        });
        LinearLayout familyInfo = v.findViewById(R.id.familyInfo);
        familyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FamilyInformation.class);
                startActivity(intent);
            }
        });
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetUserInformation.class);
                startActivity(intent);
            }
        });
        return v;
    }
}
