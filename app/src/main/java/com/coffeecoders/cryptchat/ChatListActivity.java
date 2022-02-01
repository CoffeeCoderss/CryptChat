package com.coffeecoders.cryptchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.coffeecoders.cryptchat.customAdapters.UserAdapter;
import com.coffeecoders.cryptchat.databinding.ActivityChatListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private final static String TAG = "ChatListActivity";
    private ActivityChatListBinding chatListBinding;
    private Toolbar chatLToolbar;
    private RecyclerView chatListRecycleView;
    private UserAdapter userAdapter;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<User> users;
    public User currentUser;

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
        userAdapter = new UserAdapter(this , users , this);
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
                        if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            users.add(user);
                        }else{
                            currentUser = user;
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}