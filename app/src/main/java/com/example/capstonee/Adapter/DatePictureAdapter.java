package com.example.capstonee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.capstonee.DetailPicture;
import com.example.capstonee.Model.Picture;
import com.example.capstonee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DatePictureAdapter extends RecyclerView.Adapter<DatePictureAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    ArrayList<Picture> list = new ArrayList<>();
    Context context;
    View view;

    public DatePictureAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public DatePictureAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        return new DatePictureAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DatePictureAdapter.ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return list.size();
    }

    public void addItem(Picture picture) {
        // 외부에서 item을 추가시킬 함수입니다.

        list.add(picture);
    }

    public void remove(Picture picture) {
        list.remove(list.indexOf(picture));
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private Picture picture;
        private ImageView picture_imageView;

        ItemViewHolder(View view) {
            super(view);
            picture_imageView = view.findViewById(R.id.picture_image_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailPicture.class);
                    intent.putExtra("picture", picture);
                    // 지우고 간다. 안가면 에러
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            });

        }

        void onBind(Picture picture) {

            this.picture = picture;
            Picasso.with(context).load(picture.getUri()).fit().into(picture_imageView);
        }

    }

}
