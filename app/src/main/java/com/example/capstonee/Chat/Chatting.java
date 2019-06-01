package com.example.capstonee.Chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.capstonee.Adapter.RoleAdapter;
import com.example.capstonee.Model.Chat;
import com.example.capstonee.Model.Infomation;
import com.example.capstonee.Model.Login;
import com.example.capstonee.R;
import com.example.capstonee.Adapter.MessageAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class Chatting extends Fragment {

    private MessageAdapter adapter;
    private List<Chat> chatList = new ArrayList<>();
    private RecyclerView recyclerView;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 해당 view 설정 : 자바 파일 <---바인딩---> fragment
        view = inflater.inflate(R.layout.chat_fragment, container, false);

        initAdapter();
        getData();

        // 메세지 보내기
        view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText writeEdit = (EditText) view.findViewById(R.id.write_edit);

                Log.e("채팅 창 존재 : ", (writeEdit == null) + "");

                // 사용자가 쓴 거 찾음.
                String message = writeEdit.getText().toString();

                // 사용자가 타이핑 함.
                if(!message.equals("")){
                    // 초기화
                    writeEdit.setText("");
                    // 현재 시간
                    String time = Infomation.currentTime();

                    // 전송
                    Chat chat = new Chat();
                    chat.setSender(Login.getUserID());
                    chat.setMessage(message);
                    // 하드 코딩
                    chat.setNoRead(4);
                    chat.setTime(time);

                    Log.e(" Family ID : ",Login.getUserFamilyID());

                    // 해당 User 의 패밀리 ID
                    Infomation.getDatabase("Chat").child(Login.getUserFamilyID()).push().setValue(chat);
                }



            }
        });


        return view;
    }

    // 선재 코드
    // adapter 초기화
    private void initAdapter() {
        // xml 에 존재하는 recycler 와 연결
        recyclerView = view.findViewById(R.id.chat_recycler_view);

        // 내가 데이터를 넣어줄 어뎁터 생성
        adapter = new MessageAdapter(view.getContext());

        // 현재 xml 에 보여질 recycler 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // recycler 에 보여질 객체 하나를 뜻하는 어뎁터 연결
        recyclerView.setAdapter(adapter);
    }

    private void getData(){

        // 로그 확인
        Log.e("Log Family ID : ", Login.getUserFamilyID());

        // Chat DB -> family ID
        final DatabaseReference chatRef = Infomation.getDatabase("Chat").child(Login.getUserFamilyID());

        // key : 시간, value : chat
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // 채팅 하나 객체
                Chat chat = dataSnapshot.getValue(Chat.class);

                // 어뎁터에 추가
                adapter.add(chat);

                // F5
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chatting);
//
//        // 상대바
//        final String opponentID = getIntent().getExtras().getString("USER_ID");
//        final String myID = Common.getMyId();
//        Toolbar toolbar = (Toolbar)findViewById(R.id.opponent_view_toolbar);
//
//        toolbar.setTitle(opponentID);
//        final String chatName = Common.integrate(myID, opponentID);
//
//        final DatabaseReference contentsRef = Common.getDatabase(Common.CHAT_INFOMAION).child(chatName);
//
//        recyclerView = findViewById(R.id.listView2);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        linearLayoutManager.setStackFromEnd(true);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        // 전송 버튼 클릭 시 메세지 전송
//        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                EditText write = (EditText) findViewById(R.id.writeEdit);
//
//                ChatData chatData = new ChatData();
//                chatData.setSender(myID); // 내 아이디
//                chatData.setReceiver(opponentID); // 상대방 아이디
//                chatData.setMessage(write.getText().toString()); // 메세지
//                chatData.setTime(Common.chatTimeStamp()); // 전송 시간
//
//                contentsRef.push().setValue(chatData); // DB에 저장
//                write.setText(""); // 전송 후 초기화
//            }
//        });
//
//        // 메세지 도착 시 업데이트
//        // addValueEventListener 변경 및 추가 시 Child DB 전부 돌려줌.
//        // addChildEventListener 하위에 추가된 DB 넘겨준다.
//        contentsRef.addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                ChatData chat = dataSnapshot.getValue(ChatData.class);  // chatData를 가져오고
//                // 받아온 내용의 Sender, Receiver 과 채팅방 id 들과 같아야 한다.
//                if (chat.isRelation(myID, opponentID)) {
//                    chatList.add(chat);
//                    messageAdapter = new MessageAdapter(getApplicationContext(), chatList);
//                    recyclerView.setAdapter(messageAdapter);
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }

}
