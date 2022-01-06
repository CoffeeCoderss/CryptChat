package com.coffeecoders.cryptchat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.coffeecoders.cryptchat.databinding.ActivitySetupProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class SetupProfileActivity extends AppCompatActivity {
    ActivitySetupProfileBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    ActivityResultLauncher<String> getImage;
//    = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Object>() {
//        @Override
//        public void onActivityResult(Object result) {
//            Intent data = result.getData();
//            if (data != null)
//                if (data.getData() != null) {
//                    binding.profileImage.setImageURI(data.getData());
//                }
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                binding.profileImage.setImageURI(result);
            }
        });
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage.launch("image/*");
            }
        });
    }
}