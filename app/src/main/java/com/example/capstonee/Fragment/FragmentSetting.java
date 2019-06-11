package com.example.capstonee.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstonee.FamilyInformation;
import com.example.capstonee.FamilyModifyActivity;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.R;
import com.example.capstonee.SetUserInformation;
import com.example.capstonee.SignActivity;
import com.example.capstonee.TranskmitKeyKakao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentSetting extends Fragment {
    View v;
    private ImageButton setInfo;
    TextView Username, Id;
    private Uri photoUri;
    private CircleImageView profile_imageView;
    private static final int PICK_FROM_ALBUM = 567;

    public FragmentSetting() { }

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
        LinearLayout familyModify = v.findViewById(R.id.familyModify);
        LinearLayout transmitKey = v.findViewById(R.id.transmitkeykakao);
        profile_imageView = v.findViewById(R.id.profile_image_setting);

        if (Login.getProfileUri().equals("")) {
            profile_imageView.setImageResource(R.drawable.default_profile);
        } else {
            Picasso.with(getContext()).load(Login.getProfileUri()).fit().into(profile_imageView);
        }

        profile_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfile();
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
        // 패밀리 정보
        LinearLayout familyInfo = v.findViewById(R.id.familyInfo);
        familyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FamilyInformation.class);
                startActivity(intent);
            }
        });
        // 유저 정보 변경
        setInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetUserInformation.class);
                startActivity(intent);
            }
        });
        familyModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FamilyModifyActivity.class));
            }
        });

        transmitKey.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TranskmitKeyKakao.class));
            }
        });


        return v;
    }

    // 프로필 변경
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
                            // 앨범에서 사진 삭제

                            // 캐쉬(?) 도 변경
                            Login.setProfileUri("");
                            profile_imageView.setImageResource(R.drawable.default_profile);
                        }
                        dialog.dismiss();
                    }
                });
        ad.show();
    }

    // 갤러리 접근
    private void goToAlbum() {
        //isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra("type", "profile_change");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 사진 선택 후 결과 반환
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){
            try{
                photoUri = data.getData();
                cropImage(photoUri);
//                InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
//                Bitmap img = BitmapFactory.decodeStream(in);

//                // uri 얻어서 스토리지 + DB 에 저장
//                Uri uri = getImageUri(getContext(), img);
//                Infomation.getStorageRef("Profile").child(Login.getUserID()).putFile(uri);
//                Infomation.getDatabase("User").child(Login.getUserID()).child("profileUri").setValue(uri.toString());
//
//                // 캐쉬(?) 도 변경
//                Login.setProfileUri(uri.toString());
//
//                in.close();
//                // 이미지 표시
//                profile_imageView.setImageBitmap(img);
//                profile_imageView.setAdjustViewBounds(true);
//                profile_imageView.setLayoutParams(new RelativeLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            photoUri = result.getUri();
            Picasso.with(getContext()).load(photoUri).fit().into(profile_imageView);

            // 사용자 폴더에 사진 파일 저장을 위한 서버 저장 공간 참조 가져옴.
            final StorageReference storageRef = Infomation.getStorageRef("Profile").child(Login.getUserID());

            storageRef.putFile(photoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseDatabase.getInstance().getReference("User").child(Login.getUserID()).child("profileUri").setValue(uri.toString());
                                    Login.setProfileUri(photoUri.toString());
                                }
                            });
                        }
                    });
        }
    }

    // 이미지 크롭
    private void cropImage(Uri photoUri) {
        CropImage.activity(photoUri).start(getContext(), this);
    }
    //bitmap 으로 uri 얻기
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
