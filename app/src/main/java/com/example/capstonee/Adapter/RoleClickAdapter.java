package com.example.capstonee.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.R;
import com.example.capstonee.ShowPhotoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RoleClickAdapter extends RecyclerView.Adapter<RoleClickAdapter.MyViewHolder> {
    private Context mContext;
    private List<ImageUpload> mData = new ArrayList<>();
    private int SHOW_PHOTO_FINISH = 9487;

    public RoleClickAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.cardview2_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleClickAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.onBind(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public void addItem(String name, String uri, String role) {
        // 외부에서 item을 추가시킬 함수입니다.
        mData.add(new ImageUpload(name, uri, role));
    }
    public void removeItem(String name)
    {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getName().equals(name)) {
                mData.remove(i);
                break;
            }
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView photoImg;
        private String photoUri;
        private String fileName;
        private String role;
        CardView cardView;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImg = itemView.findViewById(R.id.role_photo);
            cardView = itemView.findViewById(R.id.rolecardview_id);
            photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShowPhotoActivity.class);
                    intent.putExtra("imageUrl", photoUri);
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("role", role);
                    intent.putExtra("From", "RoleClick");
                    Log.e("imageUrl", photoUri);
                    ((Activity)mContext).startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), SHOW_PHOTO_FINISH);
                }
            });
        }

        void onBind(ImageUpload imageUpload){
            Log.e("getUri = ", imageUpload.getUrl());
            Picasso.with(mContext).load(imageUpload.getUrl()).fit().centerInside().into(photoImg);
            photoUri = imageUpload.getUrl();
            fileName = imageUpload.getName();
            role = imageUpload.getFamily();
        }
    }
}
