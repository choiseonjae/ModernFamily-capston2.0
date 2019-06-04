package com.example.capstonee.Album;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.capstonee.Adapter.RecyclerPhotoViewAdapter;
import com.example.capstonee.Adapter.RoleAdapter;
import com.example.capstonee.GPS;
import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.Model.Picture;
import com.example.capstonee.R;
import com.example.capstonee.RequestHttpURLConnection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// 현재 가지고 있는 Role 들을 보여줌.
public class FragmentAlbum extends Fragment {
    //Album
    View view;
    RecyclerView recyclerView;
    RecyclerPhotoViewAdapter recyclerViewAdapter;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private boolean isPermission = true;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private Uri photoUri;
    private DatabaseReference gpsRef;
    private DatabaseReference pictureRef;
    private File tempFile;
    private String role;
    private String distUri;
    private String filename;
    public FragmentAlbum() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 해당 view 설정 : 자바 파일 <---바인딩---> fragment
        view = inflater.inflate(R.layout.album_fragment, container, false);

        recyclerView = view.findViewById(R.id.album_recyclerview);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewAdapter = new RecyclerPhotoViewAdapter(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(recyclerViewAdapter);
        getData();

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        fab = view.findViewById(R.id.fab_main);
        fab1 = view.findViewById(R.id.fab_sub1);
        fab2 = view.findViewById(R.id.fab_sub2);

        tedPermission();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                Toast.makeText(getContext(), "Main", Toast.LENGTH_SHORT).show();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                Log.v("알림", "사진촬영 선택");
                if (isPermission) openCamera();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                Log.v("알림", "갤러리 선택");
                if (isPermission) goToAlbum();
            }
        });
        return view;
    }

    // 사진 업로드 후 VM에 URL 보내는 곳
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        private NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);

            return result;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            int idx = s.indexOf(".");
            //Toast.makeText(getActivity(), s+ " " +idx+"", Toast.LENGTH_SHORT).show();

            String dist = s.substring(0, idx);
            DatabaseReference mDataref = FirebaseDatabase.getInstance().getReference("Family").child(Login.getUserFamilyID()).child(dist);
            mDataref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("dataSnapshot1.getKey", dataSnapshot.getKey());
                    ImageUpload imageUpload = dataSnapshot.getValue(ImageUpload.class);
                    role = imageUpload.getFamily();
                    setRoleFamily(role);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //이미지 확장자
    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // 사진 업로드
    private void uploadPicture() {
        // 진행상황 보여줌.
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("업로드중...");
        progressDialog.show();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        filename = sdf.format(date)+ "." +getImageExt(photoUri);

        // 사용자 폴더에 사진 파일 저장을 위한 서버 저장 공간 참조 가져옴.
        final StorageReference storageRef = Infomation.getAlbum(Login.getUserFamilyID() + "/" + filename);

        // 서버에 사진 업로드
        storageRef.putFile(photoUri)
                //성공시
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                try {
                                    progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기

                                    pictureRef = Infomation.getAlbumData(Login.getUserFamilyID()).push();

                                    final Picture picture = new Picture();
                                    picture.setFileName(filename);
                                    String gps[] = new GPS().currentLocation(getContext(), getActivity());

                                    picture.setGpsProvider(gps[0]);
                                    picture.setLongitude(Double.parseDouble(gps[1]));
                                    picture.setLatitude(Double.parseDouble(gps[2]));
                                    picture.setAltitude(Double.parseDouble(gps[3]));
                                    picture.setUploadID(Login.getUserFamilyID());
                                    picture.setPictureID(pictureRef.getKey());
                                    picture.setUri(uri.toString());
                                    picture.setDeleted(false);
                                    distUri = uri.toString();
                                    Log.v("간닷", Login.getUserID());
                                    pictureRef.setValue(picture);

                                    if (picture.getLatitude() != -1) {
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Log.e("Thread 시작", "");
                                                    String location = GPS.getAdrress(picture.getLatitude(), picture.getLongitude());
                                                    Log.e("location", location);
                                                    picture.setLocation(location);

                                                    // 위치 쪼개기
                                                    String[] gpsDivide = location.split(" ");
                                                    gpsRef = Infomation.getDatabase("GPS");
                                                    for (int i = 0; i < gpsDivide.length; i++) {
                                                        gpsRef = gpsRef.child(gpsDivide[i]);
                                                    }

                                                    // 위치 추가
                                                    gpsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) gpsRef.setValue(Integer.parseInt(dataSnapshot.getValue().toString()) + 1);
                                                            else gpsRef.setValue(1);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                    });
                                                    pictureRef.setValue(picture);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        thread.start();
                                    }
                                    Toast.makeText(getContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                                    String url = "http://34.97.246.11/recognition.py";

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("filename", filename);
                                    contentValues.put("ui", Login.getUserFamilyID());

                                    NetworkTask networkTask = new NetworkTask(url, contentValues);
                                    networkTask.execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                })
                //실패시
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                    }
                })
                //진행중
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //dialog에 진행률을 퍼센트로 출력해 준다
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                    }
                });
    }

    //카메라나 갤러리에서 사진 가져온 거 처리해주는 부분
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("TAG", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                photoUri = data.getData();
                Log.d("CAMERA", "PICK_FROM_ALBUM photoUri : " + photoUri);
                cropImage(photoUri);
                break;
            }
            case PICK_FROM_CAMERA: {
                photoUri = Uri.fromFile(tempFile);
                Log.d("ALBUM", "takePhoto photoUri : " + photoUri);
                cropImage(photoUri);
                break;
            }
            case Crop.REQUEST_CROP: {
                UploadPicture_alert();
            }
        }
    }

    //갤러리 열어줘
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 디바이스 사용자로 부터 얻어야할 권한 설정
    private void tedPermission() {
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
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    //카메라 실행
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "이미지 처리 오류", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(getActivity(), "com.example.capstonee.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } else {
                photoUri = Uri.fromFile(tempFile);
                Log.d("NOT OVER NOUGAR", "takePhoto photoUri : " + photoUri);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }

    // 이미지 크롭
    private void cropImage(Uri photoUri) {
        Log.d("Tag", "TEMPFILE : " + tempFile);
        if (tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "이미지 처리 오류!", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                e.printStackTrace();
            }
        }
        Uri savingUri = Uri.fromFile(tempFile);
        Crop.of(photoUri, savingUri).asSquare().start(getContext(), this);
    }

    public void setRoleFamily(final String role){
        DatabaseReference mDataref = FirebaseDatabase.getInstance().getReference("Album").child(Login.getUserFamilyID());
        mDataref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("dataSnapshot.getKey", dataSnapshot.getKey());
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot.getkey", snapshot.getKey());
                    Picture picture = snapshot.getValue(Picture.class);
                    Log.e("picture.getName = ", picture.getFileName());
                    if(filename.equals(picture.getFileName())){
                        Log.d("role = ", role);
                        Log.d("distUri = ", distUri);
                        ImageUpload imageUpload = new ImageUpload(distUri, filename);
                        DatabaseReference newDataref = FirebaseDatabase.getInstance().getReference("role").child(Login.getUserFamilyID());
                        newDataref.child(role).push().setValue(imageUpload);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    // 선재 코드
    // adapter 초기화
//    private void initAdapter() {
//        // xml 에 존재하는 recycler 와 연결
//        recyclerView = view.findViewById(R.id.album_recyclerview);
//        // 이거는 꽉찬 그리디 화면 인가?
////        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        // 내가 데이터를 넣어줄 어뎁터 생성
//        adapter = new RoleAdapter(getContext());
//        // 현재 xml 에 보여질 recycler 설정
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        // recycler 에 보여질 객체 하나를 뜻하는 어뎁터 연결
//        recyclerView.setAdapter(adapter);
//    }

    // DB의 변경을 바로 바로 업데이트 한 뒤 xml 에 뿌려주기 위한 Listener
    private void getData() {
        Log.d("LOG - FAMILY ID : ", Login.getUserFamilyID());
        if(Login.getUserFamilyCount() > 0) {
            // 현재 사용자의 Family DB 에서 역할가져온다.
            final DatabaseReference roleRef = Infomation.getDatabase("Family").child(Login.getUserFamilyID());
            Log.e("roleRef = ", roleRef.getKey());
            // family - id - 이후 key : value
            roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ImageUpload imageUpload = snapshot.getValue(ImageUpload.class);
                        // 현재 역할 한 분
                        String role = imageUpload.getFamily();
                        String uri = imageUpload.getUrl();

                        recyclerViewAdapter.addItem(role, uri);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    // 이미지 파일 생성
    private File createImageFile() throws IOException {
        long now = System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(now));
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Album/");
        if (!storageDir.exists()) storageDir.mkdirs();

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("createImage", "createIMAGEFILE : " + image.getAbsolutePath());

        return image;
    }


    // 사진 업로드 할 지 물어보는 알람
    public void UploadPicture_alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("사진 업로드");
        builder.setMessage("사진을 Cloud에 업로드 하시겠습니까?\n'아니오' 선택 시 사진은 삭제됩니다.");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        uploadPicture();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "취소 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });

        builder.show();
    }



    // 내가 모르는 메소드 ㅋㅋ..
    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}
