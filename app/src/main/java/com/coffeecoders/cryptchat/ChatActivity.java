package com.coffeecoders.cryptchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.coffeecoders.cryptchat.customAdapters.ChatAdapter;
import com.coffeecoders.cryptchat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = "ChatActivity";
    private ActivityChatBinding chatBinding;
    private Intent chatIntent;
    private ChatAdapter chatAdapter;
    private ArrayList<MessageModel> messagesList;
    private FirebaseStorage storage;
    private FirebaseFirestore firebaseFirestore;
    String senderMessage, receiverMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());
        messagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messagesList);
        chatBinding.chatRecycleView.setLayoutManager(new LinearLayoutManager(this));
        chatBinding.chatRecycleView.setAdapter(chatAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        chatIntent = getIntent();
        String title = chatIntent.getExtras().getString("name");
        String receiverUid = chatIntent.getExtras().getString("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();
        setTitle(title);
        senderMessage = senderUid + receiverUid;
        receiverMessage = receiverUid + senderUid;
        Log.e(TAG, "onCreate: senderId"+senderMessage );
        Log.e(TAG, "onCreate: senderId"+ receiverMessage );

        firebaseFirestore.collection("chats")
                .document(senderMessage)
                .collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    Log.e(TAG, "ERROR : ", error);

                else{
                    List<DocumentSnapshot> list = value.getDocuments();
                        messagesList.clear();
                        for (DocumentSnapshot documentSnapshot : list) {
                            MessageModel messageModel = documentSnapshot.toObject(MessageModel.class);
                            messagesList.add(messageModel);
                        }
                        Collections.sort(messagesList, new Comparator<MessageModel>() {
                            @Override
                            public int compare(MessageModel messageModel, MessageModel t1) {
                                return (int) (messageModel.getTimestamp() - t1.getTimestamp());
                            }
                        });
                        chatAdapter.notifyDataSetChanged();
                }
            }
        });


        chatBinding.sendImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedMsg = chatBinding.msgEditTxt.getText().toString();
                Date date = new Date();
                MessageModel message = new MessageModel(typedMsg, senderUid, date.getTime());
                chatBinding.msgEditTxt.setText("");
                firebaseFirestore.collection("chats")
                        .document(senderMessage)
                        .collection("messages")
                        .add(message)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                firebaseFirestore.collection("chats")
                                        .document(receiverMessage)
                                        .collection("messages")
                                        .add(message)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                            }
                                        });
                            }
                        });
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