package com.coffeecoders.cryptchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.coffeecoders.cryptchat.customAdapters.ChatAdapter;
import com.coffeecoders.cryptchat.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = "ChatActivity";
    private ActivityChatBinding chatBinding;
    private Intent chatIntent;
    private ChatAdapter chatAdapter;
    private ArrayList<MessageModel> messagesList;
    private FirebaseStorage storage;
    private FirebaseFirestore firebaseFirestore;
    String senderMessage, receiverMessage;
    String senderUid;
    String receiverUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        messagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this , messagesList);
        chatIntent = getIntent();
        String title = chatIntent.getExtras().getString("name");
        String ReceiverUid = chatIntent.getExtras().getString("uid");
        setTitle(title);

        String senderUid = FirebaseAuth.getInstance().getUid();

        senderMessage = senderUid+receiverUid;
        receiverMessage = receiverUid+senderUid;

        chatBinding.sendImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedMsg = chatBinding.msgEditTxt.getText().toString();
                Date date = new Date();
                MessageModel message = new MessageModel(typedMsg, senderUid, date.getTime());
                chatBinding.msgEditTxt.setText("");
                firebaseFirestore.collection("message").add(message);

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}