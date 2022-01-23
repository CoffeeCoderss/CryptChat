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
    private static final String SECRET_KEY = "SECRET_KEY_PASS";
    private static final String SALT = "ANOTHER_SECRET_KEY_PASS";
    private Cipher cipher, decipher;
    private final byte[] encryptionKey = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private final SecretKeySpec secretKeySpec = new SecretKeySpec(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, "AES/CFB/NoPadding");
    String sender;
    String receive;

    public ChatAdapter() {

    }

    public ChatAdapter(Context context, ArrayList<MessageModel> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
//        this.sender = sender;
//        this.receive = receive;
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


    private String decryption(String string) {
        byte[] encryptedByte = new byte[0];
        encryptedByte = string.getBytes(StandardCharsets.ISO_8859_1);
        String decryptedString = string;
        byte[] decryption;
        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(encryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Log.e("error", "invalid key");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Log.e("error", "bad padding");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.e("error", "illegal block size");
        }
        return decryptedString;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String testD(String string) {
        try {
            IvParameterSpec ivspec = new IvParameterSpec(encryptionKey);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(string)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel newMessage = messagesList.get(position);
        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.binding.sendMessage.setText(testD(newMessage.getMessage()));
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.binding.receivedMessage.setText(testD(newMessage.getMessage()));
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