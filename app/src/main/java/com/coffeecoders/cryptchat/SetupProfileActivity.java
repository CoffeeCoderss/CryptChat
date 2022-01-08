package com.coffeecoders.cryptchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coffeecoders.cryptchat.databinding.ActivitySetupProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetupProfileActivity extends AppCompatActivity {
    ActivitySetupProfileBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    ActivityResultLauncher<String> getImage;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
//        // get the image data and set it
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                binding.profileImage.setImageURI(result);
                selectedImage = result;
        });
        // all types of images will be available to select
        binding.profileImage.setOnClickListener(view -> getImage.launch("image/*"));
        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.nameTextView.getText().toString();
                if (name.isEmpty()) {
                    binding.nameTextView.setError("Please enter a name to continue");
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
                                        String uid = firebaseAuth.getUid();
                                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                                        String name = binding.nameTextView.getText().toString();
                                        User user = new User(uid, name, phone, imageURL);
                                        CollectionReference collectionReference = firebaseFirestore.collection("users");
                                        collectionReference.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}