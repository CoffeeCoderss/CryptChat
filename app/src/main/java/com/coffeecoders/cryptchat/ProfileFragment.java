package com.coffeecoders.cryptchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coffeecoders.cryptchat.databinding.FragmentPhoneNoBinding;
import com.coffeecoders.cryptchat.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {

    private final static String TAG = "ProfileFragment";

    FragmentProfileBinding profileBinding;
    private Toolbar profileToolbar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    ActivityResultLauncher<String> getImage;
    Uri selectedImage;
    private ChatActivity chatActivity;
    private String encrypt_key;
    public ProfileFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        profileBinding =  FragmentProfileBinding.inflate(inflater, container, false);
        profileToolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        chatActivity = new ChatActivity();
//        // get the image data and set it
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            profileBinding.profileImage.setImageURI(result);
            selectedImage = result;
        });
        // all types of images will be available to select
        profileBinding.profileImage.setOnClickListener(view -> getImage.launch("image/*"));
        profileBinding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = profileBinding.nameTextView.getText().toString();
                String uid = firebaseAuth.getUid();
                String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                String pKey = profileBinding.personalKey.getText().toString();
                try {
                    /** personal key encrypted **/
                    encrypt_key= chatActivity.encrypt(pKey , chatActivity.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (name.isEmpty()) {
                    profileBinding.nameTextView.setError("Please enter a name to continue");
                    return;
                }
                if (selectedImage != null) {
                    StorageReference storageReference = firebaseStorage.getReference().child("Profiles").child(firebaseAuth.getUid());
                    storageReference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageURL = uri.toString();

                                        User user = new User(uid, name, phone, imageURL , encrypt_key);
                                        CollectionReference collectionReference = firebaseFirestore.collection("users");
                                        collectionReference.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Intent ChatListIntent = new Intent
                                                        (getContext(),ChatListActivity.class );
                                                startActivity(ChatListIntent);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                } else {

                    User user = new User(uid, name, phone, "No image" , encrypt_key);
                    CollectionReference collectionReference = firebaseFirestore.collection("users");
                    collectionReference.add(user).addOnSuccessListener(documentReference -> {
                        Intent ChatListIntent = new Intent
                                (getContext(),ChatListActivity.class );
                        startActivity(ChatListIntent);
                    });
                }
            }
        });
        return profileBinding.getRoot();
    }
}