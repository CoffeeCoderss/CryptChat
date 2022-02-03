package com.coffeecoders.cryptchat.customAdapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.cryptchat.ChatActivity;
import com.coffeecoders.cryptchat.DataChangedListener;
import com.coffeecoders.cryptchat.MessageModel;
import com.coffeecoders.cryptchat.OnClickDecrypt;
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
    private ActivityChatBinding chatBinding;
    private OnClickDecrypt onClickDecrypt;
    private ChatActivity chatActivity;
    private static final int SENT_CONST = 1;
    private static final int RECEIVE_CONST = 2;

    public ChatAdapter() {

    }

    public ChatAdapter(Context context, ArrayList<MessageModel> messagesList ,
                       OnClickDecrypt onClickDecrypt , ChatActivity chatActivity ,
                       ActivityChatBinding chatBinding) {
        this.context = context;
        this.messagesList = messagesList;
        this.onClickDecrypt = onClickDecrypt;
        this.chatActivity = chatActivity;
        this.chatBinding = chatBinding;
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
            if (!newMessage.isProtected()) {
                viewHolder.binding.sendMessage.setText(newMessage.getMessage());
            }else{
                viewHolder.binding.sendMessage.setVisibility(View.GONE);
                viewHolder.binding.sentKeyEdtxt.setVisibility(View.VISIBLE);
                viewHolder.binding.sentKeyEdtxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b){
                            Toast.makeText(context , "touched" , Toast.LENGTH_SHORT).show();
                            chatBinding.sendImgView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String senderPassKey = viewHolder.binding.sentKeyEdtxt.getText().toString();
                                    onClickDecrypt.showDecryptMsg(newMessage , true ,senderPassKey);
                                    viewHolder.binding.sentKeyEdtxt.setVisibility(View.GONE);
                                    viewHolder.binding.sendMessage.setVisibility(View.VISIBLE);
                                    viewHolder.binding.sendMessage.setText(chatActivity.getProtectedMsg());
                                }
                            });

                        }
                    }
                });
            }
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            if (!newMessage.isProtected()) {
                viewHolder.binding.receivedMessage.setText(newMessage.getMessage());
            }else{
                viewHolder.binding.receivedMessage.setVisibility(View.GONE);
                viewHolder.binding.receivedKeyEdtxt.setVisibility(View.VISIBLE);
            }
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