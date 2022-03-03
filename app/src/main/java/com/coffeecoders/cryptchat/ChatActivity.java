package com.coffeecoders.cryptchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

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

public class ChatActivity extends AppCompatActivity implements OnClickDecrypt {
    private final static String TAG = "ChatActivity";
    private ActivityChatBinding chatBinding;
    private Intent chatIntent;
    private ChatAdapter chatAdapter;
    private ArrayList<MessageModel> messagesList;
    private FirebaseStorage storage;
    private FirebaseFirestore firebaseFirestore;
    private String senderMessage, receiverMessage , protectedMsg;
    private String privateKey="";
    private String publicKey="";
    private String encryptMsg = "";
    private String encryptSenderMsg = "";
    private String encryptReceiverMsg = "";
    private boolean isProtectedMode = false;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private String key = "fielnviwfjvnkeeythfkladfkkf";

    public String getKey() {
        return key;
    }

    public String getProtectedMsg() {
        return protectedMsg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());
        messagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messagesList ,this , this
         , chatBinding);
        mLayoutManager = new LinearLayoutManager(this);
        /**start from bottom of the screen **/
        mLayoutManager.setStackFromEnd(true);
        recyclerView = chatBinding.chatRecycleView;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(chatAdapter);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);

        /**
         * when keyboard pop up recycleView will scrollDown
         */
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if( bottom < oldBottom) {
                    scrollToBottom();
                }
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        chatIntent = getIntent();
        String title = chatIntent.getExtras().getString("name");
        String receiverUid = chatIntent.getExtras().getString("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();

        /**
         * key for receiver msg
         */
        try {
            publicKey = decrypt(chatIntent.getExtras().getString("pKey") , key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * key for sender msg
         */
        try {
            privateKey = decrypt(chatIntent.getExtras().getString("cuKey") , key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "onCreate: privateKey " + privateKey );
        setTitle(title);
        senderMessage = senderUid + receiverUid;
        receiverMessage = receiverUid + senderUid;
        Log.e(TAG, "onCreate: senderId" + senderMessage);
        Log.e(TAG, "onCreate: senderId" + receiverMessage);

        /**
         * receive msg from firebase
         */
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
                            if (!messageModel.isProtected()) {
                                /**
                                 * decrypt msg
                                 */
                                messageModel.setMessage(decrypt(messageModel.getMessage(), key));
                            }
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


        /**
         * send message onclickListener
         */
        chatBinding.sendImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToBottom();
                // String typedMsg = encryption(chatBinding.msgEditTxt.getText().toString());
                String typedMsg = null;
                try {
                    typedMsg = chatBinding.msgEditTxt.getText().toString();
                    if (!isProtectedMode) {
                      encryptMsg = encrypt(typedMsg , key);
                    }else{
                        encryptSenderMsg = encrypt(typedMsg , privateKey);
                        encryptReceiverMsg = encrypt(typedMsg , publicKey);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Date date = new Date();
                MessageModel message = new MessageModel(encryptMsg,encryptSenderMsg,
                        encryptReceiverMsg, isProtectedMode ,senderUid, date.getTime());
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

    /**
     * scroll the recycleView in backGround to the end of the adapterCount
     */
    private void scrollToBottom(){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(
                        recyclerView.getAdapter().getItemCount() - 1);
            }
        },100);
    }

    /**
     * key generation
     * @param key
     * @return
     * @throws Exception
     */
    private SecretKeySpec generateKey(String key) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = key.getBytes("UTF-8");
        digest.update(bytes , 0 , bytes.length);
        byte[] pass = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(pass , "AES");
        return secretKeySpec;
    }

    /**
     * encryption
     * @param msg
     * @param key
     * @return
     * @throws Exception
     */
    public String encrypt(String msg , String key)throws Exception{
        SecretKeySpec secretKeySpec = generateKey(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE , secretKeySpec);
        byte[] encVal = cipher.doFinal(msg.getBytes());
        String enCryptedMsg = Base64.encodeToString(encVal , Base64.DEFAULT);
        return enCryptedMsg;
    }

    /**
     * decryption
     * @param msg
     * @param key
     * @return
     * @throws Exception
     */
    private String decrypt(String msg , String key)throws Exception{
        SecretKeySpec secretKeySpec = generateKey(key);
        Cipher deCipher = Cipher.getInstance("AES");
        deCipher.init(Cipher.DECRYPT_MODE , secretKeySpec);
        byte[] decodedString = Base64.decode(msg , Base64.DEFAULT);
        byte[] decVal = deCipher.doFinal(decodedString);
        String deCryptedMsg = new String(decVal);
        return deCryptedMsg;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_menu , menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            /**
             * turn on protected mode
             */
            case R.id.chatActivity_menu:
                if (!isProtectedMode) {
                    isProtectedMode = true;
                    item.setIcon(R.drawable.lock2);
                    Toast.makeText(this , "Private mode ON" , Toast.LENGTH_SHORT).show();
                }else{
                    isProtectedMode = false;
                    item.setIcon(R.drawable.lock);
                    Toast.makeText(this , "Private mode OFF" , Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDecryptMsg(MessageModel messageModel, boolean isSenderMsg , String senderKey) {
        if (isSenderMsg){
            try {
                protectedMsg = "";
                protectedMsg =  decrypt(messageModel.getEncryptSenderMsg() , senderKey);
            }catch (Exception e){}

        }else{
            try {
                protectedMsg = "";
                protectedMsg =  decrypt(messageModel.getEncryptReceiverMsg() , senderKey);
            }catch (Exception e){}
        }
    }

}