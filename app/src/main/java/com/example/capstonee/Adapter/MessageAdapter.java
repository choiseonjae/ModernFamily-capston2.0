package com.example.capstonee.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.capstonee.Model.Chat;
import com.example.capstonee.Model.Login;
import com.example.capstonee.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    // chat 모음
    ArrayList<Chat> chatList = new ArrayList<>();
    Context context;
    View view;

    public MessageAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        if (i == MSG_TYPE_RIGHT)
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, viewGroup, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Chat chat = chatList.get(position);
//        viewHolder.show_message.setText(chat.getMessage());

        viewHolder.onBind(chat);
    }

    public void add(Chat chat) {
        chatList.add(chat);
    }


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final String myID = Login.getUserID();

    FirebaseUser firebaseUser;


    @Override
    public int getItemCount() {
        return chatList.size();
    }


    @Override
    public int getItemViewType(int position) {
        // message sender
        String sender = chatList.get(position).getSender();
        Log.e("sender : ", sender);
        Log.e("my id : ", myID);
        Log.e("sender == id : ", myID.equals(chatList.get(position).getSender()) + "");
        if (sender.equals(myID))
            return MSG_TYPE_RIGHT;
        return MSG_TYPE_LEFT;


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile_image;
        private Chat chat;
        private TextView message_textView;

        public ViewHolder(View itemView) {
            super(itemView);

            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            message_textView = itemView.findViewById(R.id.show_message_text_view);
        }


        void onBind(Chat chat) {
            this.chat = chat;
            Log.e("메세지 ?", chat.getMessage());
            if (chat.getUri().equals(""))
                message_textView.setText(chat.getMessage());
        }

    }


}
