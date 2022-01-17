package com.coffeecoders.cryptchat.customAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.cryptchat.DataChangedListener;
import com.coffeecoders.cryptchat.MessageModel;
import com.coffeecoders.cryptchat.R;
import com.coffeecoders.cryptchat.databinding.ItemReceivedBinding;
import com.coffeecoders.cryptchat.databinding.ItemSentBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

public class ChatAdapter extends RecyclerView.Adapter {
    private final static String TAG = "ChatAdapter";
    private Context context;
    private ArrayList<MessageModel> messagesList;
    private static final int SENT_CONST = 1;
    private static final int RECEIVE_CONST = 2;
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    private final byte[] encryptionKey = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    String sender;
    String receive;

    public ChatAdapter(Context context, ArrayList<MessageModel> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
//        this.sender = sender;
//        this.receive = receive;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENT_CONST) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_received, parent, false);
            return new ReceiverViewHolder(view);
        }
    }



    private String decryption(String string) {
        byte[] encryptedByte = string.getBytes(StandardCharsets.ISO_8859_1);
        String decryptedString = string;
        byte[] decryption;
        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(encryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel newMessage = messagesList.get(position);
        if(holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder)holder;
            viewHolder.binding.sendMessage.setText(newMessage.getMessage());
        }else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            String encryptedMessage = decryption(newMessage.getMessage());
            viewHolder.binding.receivedMessage.setText(encryptedMessage);
        }


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel newMessage = messagesList.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(newMessage.getSenderId())) {
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
