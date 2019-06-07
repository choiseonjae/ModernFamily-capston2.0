package com.example.capstonee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.capstonee.NearHotPlace;
import com.example.capstonee.R;

import java.util.ArrayList;

public class GPSAdapter extends RecyclerView.Adapter<GPSAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<String> addr = new ArrayList<>();
    private ArrayList<String> titleList = new ArrayList<>();

    private Context context;
    private View view;

    public GPSAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public GPSAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_area_item, parent, false);
        return new GPSAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GPSAdapter.ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(titleList.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return titleList.size();
    }

    public void addItem(String title) {
        // 외부에서 item을 추가시킬 함수입니다.

        titleList.add(title);

    }

//    public void remove(String userID) {
//        familyMemberIDList.remove(familyMemberIDList.indexOf(userID));
//    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private String address;
        private LinearLayout linearLayout;
        private TextView popular_textView;
        private ImageView popular_imageView;

        ItemViewHolder(View view) {
            super(view);

            popular_textView = view.findViewById(R.id.popular_text_view);
            popular_imageView = view.findViewById(R.id.popular_image_view);
            linearLayout = view.findViewById(R.id.popular_linearlayout);
            // 짧게 누를 시
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NearHotPlace.class);
                    intent.putExtra("address", address);
                    context.startActivity(intent);
                }
            });



        }

        void onBind(String address) {
            //잠시 로그 확인
//            Log.e("role, uri", Arrays.toString(value));

            // 값 저장
            this.address = address;


            // 화면에 보여주기
            popular_textView.setText(address);

//            // 반 쪼개야해용
//            ViewGroup.LayoutParams lp = popular_imageView.getLayoutParams();
//            lp.width = context.getResources().getDisplayMetrics().widthPixels;
//            lp.height = lp.width / 3;
//
//            pictureUri = pictureUri.replace("http://","https://");

//            Log.d("? ", pictureUri);

//            Picasso.with(context).load(pictureUri).fit().into(popular_imageView);

        }

    }


}
