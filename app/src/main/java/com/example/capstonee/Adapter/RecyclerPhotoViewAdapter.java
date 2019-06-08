package com.example.capstonee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.capstonee.ClickRoleActivity;
import com.example.capstonee.Model.ImageUpload;
import com.example.capstonee.Model.Photo;
import com.example.capstonee.R;
import com.example.capstonee.ShowPhotoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/****
 *  FragmentAlbum에서 사진을 보여주는데 쓰일 RecyclerViewAdapter
 */
public class RecyclerPhotoViewAdapter extends RecyclerView.Adapter<RecyclerPhotoViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<ImageUpload> mData = new ArrayList<>();
    //PJH
    //setMode가 1이다 -> AlbumFragment에서 호출한 것. 이 경우는 사진 클릭이 가능
    //2다 -> FamilyModifyActivity에서 호출한 것. 이 경우는 사진 클릭 불가.
    public static int setMode = 1;

    public RecyclerPhotoViewAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.cardview_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
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
    public void removeItem(String name){
        int index;
        for(int i=0; i<mData.size(); i++){
            if(mData.get(i).getName().equals(name)) {
                index = i;
                mData.remove(index);
                break;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView photoImg;
        private TextView cardText;
        CardView cardView;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImg = itemView.findViewById(R.id.album_image_id);
            cardText = itemView.findViewById(R.id.cardtext_id);
            cardView = itemView.findViewById(R.id.cardview_id);
            if(setMode == 1) {
                photoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ClickRoleActivity.class);
                        intent.putExtra("role", cardText.getText().toString());
                        Log.e("role!!!", cardText.getText().toString());
                        v.getContext().startActivity(intent);
                    }
                });
            }
        }

        void onBind(final ImageUpload imageUpload){
            Picasso.with(mContext).load(imageUpload.getUrl()).fit().centerInside().into(photoImg);
            cardText.setText(imageUpload.getFamily());
            if(setMode == 2){
                photoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ShowPhotoActivity.class);
                        intent.putExtra("imageUrl", imageUpload.getUrl());
                        intent.putExtra("fileName", imageUpload.getName());
                        intent.putExtra("From", "PhotoView");
                        v.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }
        }
    }
}
