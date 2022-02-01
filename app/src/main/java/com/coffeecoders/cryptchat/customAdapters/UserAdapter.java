package com.coffeecoders.cryptchat.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.coffeecoders.cryptchat.ChatActivity;
import com.coffeecoders.cryptchat.ChatListActivity;
import com.coffeecoders.cryptchat.R;
import com.coffeecoders.cryptchat.User;
import com.coffeecoders.cryptchat.databinding.ListConversationBinding;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final static String TAG = "UserAdapter";
    Context context;
    ArrayList<User> users;
    private ChatListActivity chatListActivity;

    public UserAdapter(Context context, ArrayList<User> users , ChatListActivity chatListActivity) {
        this.context = context;
        this.users = users;
        this.chatListActivity = chatListActivity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_conversation, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.userName.setText(user.getName());
        Glide.with(context)
                .load(user.getProfileImage())
                .placeholder(R.drawable.grey_box)
                .into(holder.binding.userImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent
                        (view.getContext(), ChatActivity.class );
                chatIntent.putExtra("name" , user.getName());
                chatIntent.putExtra("uid" , user.getUid());
                chatIntent.putExtra("pKey" , user.getPersonalKey());
                chatIntent.putExtra("cuKey" , chatListActivity.currentUser.getPersonalKey());
                view.getContext().startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ListConversationBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListConversationBinding.bind(itemView);
        }
    }
}
