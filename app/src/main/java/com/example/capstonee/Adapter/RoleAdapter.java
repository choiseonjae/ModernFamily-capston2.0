package com.example.capstonee.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.User;
import com.example.capstonee.R;
import com.example.capstonee.SelectedRoleAlbum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<String[]> familyMemberIDList = new ArrayList<>();
    private Context context;
    private View view;

    public RoleAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(familyMemberIDList.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return familyMemberIDList.size();
    }

    public void addItem(String role, String pictureUri) {
        // 외부에서 item을 추가시킬 함수입니다.

        familyMemberIDList.add(new String[]{role, pictureUri});
    }

    public void remove(String userID) {
        familyMemberIDList.remove(familyMemberIDList.indexOf(userID));
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private String role, pictureUri;
        private LinearLayout linearLayout;
        private TextView role_textView;
        private ImageView role_imageView;

        ItemViewHolder(View view) {
            super(view);

            role_textView = view.findViewById(R.id.role_text_view);
            role_imageView = view.findViewById(R.id.role_image_view);
            linearLayout = view.findViewById(R.id.role_linearlayout);
            // 짧게 누를 시
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SelectedRoleAlbum.class);
                    intent.putExtra("role", role);
                    context.startActivity(intent);
                }
            });


            // 오래 누르면
//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    familyAdd_alert(familyMemberID);
//                    return false;
//                }
//            });

        }

        void onBind(String[] value) {
            //잠시 로그 확인
            Log.e("role, uri", Arrays.toString(value));

            // 값 저장
            role = value[0];
            pictureUri = value[1];

            // 화면에 보여주기
            role_textView.setText(role);

            ViewGroup.LayoutParams lp = role_imageView.getLayoutParams();
            lp.width = context.getResources().getDisplayMetrics().widthPixels;
            lp.height = lp.width / 2;

            Picasso.with(context).load(pictureUri).fit().into(role_imageView);
        }

    }

    private void familyAdd_alert(final String familyMemberID) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context);

        ad.setTitle("가족 삭제");       // 제목 설정
        ad.setMessage("선택한 가족을 퇴출시키겠습니까?\n한번 퇴출된 가족은 되돌릴 수 없습니다.");   // 내용 설정

        // 확인 버튼 설정
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DatabaseReference userRef = Infomation.getDatabase("User").child(familyMemberID);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String familyID = user.getFamilyID();
                        user.setFamilyID("");
                        userRef.setValue(user);

                        // family DB 삭제
                        final DatabaseReference familyRef = Infomation.getDatabase("Family").child(familyID);
                        familyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren())
                                    if (d.getValue().toString().equals(familyMemberID))
                                        familyRef.child(d.getKey()).removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // 취소 버튼 설정
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            private DialogInterface dialog;
            private int which;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                this.dialog = dialog;
                this.which = which;
            }
        });

        // 창 띄우기
        ad.show();

    }

}
