package com.coffeecoders.cryptchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.coffeecoders.cryptchat.databinding.ActivityChatListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private ActivityChatListBinding chatListBinding;
    private Toolbar chatLToolbar;
    private RecyclerView chatListRecycleView;
    private UserAdapter userAdapter;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatListBinding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(chatListBinding.getRoot());
        chatLToolbar = chatListBinding.ChatListToolbar;
        chatLToolbar.setTitle("CryptChat");
        firebaseFirestore = FirebaseFirestore.getInstance();
        users = new ArrayList<>();
        chatListRecycleView = chatListBinding.chatList;
        userAdapter = new UserAdapter(this , users);
        chatListRecycleView.setLayoutManager(new LinearLayoutManager(this));
        chatListRecycleView.setAdapter(userAdapter);

        firebaseFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    users.clear();
                    for (DocumentSnapshot documentSnapshot : list) {
                        User user = documentSnapshot.toObject(User.class);
                        users.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}