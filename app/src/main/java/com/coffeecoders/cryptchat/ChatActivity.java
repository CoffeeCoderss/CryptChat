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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.coffeecoders.cryptchat.customAdapters.ChatAdapter;
import com.coffeecoders.cryptchat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
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
    String senderMessage, receiverMessage;
    private User curUser;
    private String privateKey="";
    private String key = "fielnviwfjvnkeeythfkladfkkf";

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


        String publicKey = chatIntent.getExtras().getString("pKey");
        String privateKey = chatIntent.getExtras().getString("cuKey");
        Log.e(TAG, "onCreate: privateKey " + privateKey );
        setTitle(title);
        senderMessage = senderUid + receiverUid;
        receiverMessage = receiverUid + senderUid;
        Log.e(TAG, "onCreate: senderId" + senderMessage);
        Log.e(TAG, "onCreate: senderId" + receiverMessage);


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
                        try {
                            messageModel.setMessage(decrypt(messageModel.getMessage()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onEvent: decrypt falied" );
                        }
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
                // String typedMsg = encryption(chatBinding.msgEditTxt.getText().toString());
                String typedMsg = null;
                try {
                    typedMsg = encrypt(chatBinding.msgEditTxt.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


    private SecretKeySpec generateKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = key.getBytes("UTF-8");
        digest.update(bytes , 0 , bytes.length);
        byte[] pass = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(pass , "AES");
        return secretKeySpec;
    }

    private String encrypt(String msg)throws Exception{
        SecretKeySpec secretKeySpec = generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE , secretKeySpec);
        byte[] encVal = cipher.doFinal(msg.getBytes());
        String enCryptedMsg = Base64.encodeToString(encVal , Base64.DEFAULT);
        return enCryptedMsg;
    }

    private String decrypt(String msg)throws Exception{
        SecretKeySpec secretKeySpec = generateKey();
        Cipher deCipher = Cipher.getInstance("AES");
        deCipher.init(Cipher.DECRYPT_MODE , secretKeySpec);
        byte[] decodedString = Base64.decode(msg , Base64.DEFAULT);
        byte[] decVal = deCipher.doFinal(decodedString);
        String deCryptedMsg = new String(decVal);
        return deCryptedMsg;
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