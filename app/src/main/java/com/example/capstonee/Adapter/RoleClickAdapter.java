package com.example.capstonee.Adapter;

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
import com.example.capstonee.ClickRoleActivity;
import com.example.capstonee.Model.Photo;
import com.example.capstonee.R;
import com.example.capstonee.ShowPhotoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RoleClickAdapter extends RecyclerView.Adapter<RoleClickAdapter.MyViewHolder> {
    private Context mContext;
    private List<Photo> mData = new ArrayList<>();

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
    public void addItem(String uri) {
        // 외부에서 item을 추가시킬 함수입니다.
        mData.add(new Photo(uri));
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView photoImg;
        private String photoUri;
        CardView cardView;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImg = itemView.findViewById(R.id.role_photo);
            cardView = itemView.findViewById(R.id.rolecardview_id);
            photoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowPhotoActivity.class);
                    intent.putExtra("imageUrl", photoUri);
                    Log.e("imageUrl", photoUri);
                    v.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }

        void onBind(Photo photo){
            Log.e("getUri = ", photo.getUri());
            Picasso.with(mContext).load(photo.getUri()).fit().centerInside().into(photoImg);
            photoUri = photo.getUri();
        }
    }
}
