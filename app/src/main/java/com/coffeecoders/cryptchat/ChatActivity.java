package com.coffeecoders.cryptchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = "ChatActivity";
    private ActivityChatBinding chatBinding;
    private Intent chatIntent;
    private ChatAdapter chatAdapter;
    private ArrayList<MessageModel> messagesList;
    private FirebaseStorage storage;
    private FirebaseFirestore firebaseFirestore;
    private final byte[] encryptionKey = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private static final String SECRET_KEY = "SECRET_KEY_PASS";
    private static final String SALT = "ANOTHER_SECRET_KEY_PASS";
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
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
        Log.e(TAG, "onCreate: senderId" + senderMessage);
        Log.e(TAG, "onCreate: senderId" + receiverMessage);
        // encryption and decryption
        try {
            cipher = Cipher.getInstance("AES/CFB/NoPadding");
            decipher = Cipher.getInstance("AES/CFB/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey, "AES/CFB/NoPadding");

        firebaseFirestore.collection("chats")
                .document(senderMessage)
                .collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    Log.e(TAG, "ERROR : ", error);

                else {
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                // String typedMsg = encryption(chatBinding.msgEditTxt.getText().toString());
                String typedMsg = testE(chatBinding.msgEditTxt.getText().toString());
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

    // encryption of message
    private String encryption(String string) {
        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];
        int ctLength = 0;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String finalString = null;
        finalString = new String(encryptedByte, StandardCharsets.ISO_8859_1);
        return finalString;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String testE(String string) {
        try {
            IvParameterSpec ivspec = new IvParameterSpec(encryptionKey);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(string.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    // decryption of message
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