package com.coffeecoders.cryptchat.customAdapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.cryptchat.ChatActivity;
import com.coffeecoders.cryptchat.DataChangedListener;
import com.coffeecoders.cryptchat.MessageModel;
import com.coffeecoders.cryptchat.R;
import com.coffeecoders.cryptchat.databinding.ActivityChatBinding;
import com.coffeecoders.cryptchat.databinding.ItemReceivedBinding;
import com.coffeecoders.cryptchat.databinding.ItemSentBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ChatAdapter extends RecyclerView.Adapter {
    private final static String TAG = "ChatAdapter";
    private Context context;
    private ArrayList<MessageModel> messagesList;
    ActivityChatBinding chatBinding;
    private static final int SENT_CONST = 1;
    private static final int RECEIVE_CONST = 2;

    public ChatAdapter() {

    }

    public ChatAdapter(Context context, ArrayList<MessageModel> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENT_CONST) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_received, parent, false);
            return new ReceiverViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel newMessage = messagesList.get(position);
        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.binding.sendMessage.setText(newMessage.getMessage());
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.binding.receivedMessage.setText(newMessage.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel newMessage = messagesList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(newMessage.getSenderId())) {
            return SENT_CONST;
        } else {
            return RECEIVE_CONST;
        }
    }


    public class SentViewHolder extends RecyclerView.ViewHolder {
        ItemSentBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        ItemReceivedBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceivedBinding.bind(itemView);
        }
    }
}