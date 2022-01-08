package com.coffeecoders.cryptchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.coffeecoders.cryptchat.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        // if the user has already registered
        /*
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
            startActivity(intent);
            finish();
        }
        */
        binding.continueButton.setOnClickListener(view -> {
            Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
            intent.putExtra("phoneNumber", binding.phoneNumberTextView.getText().toString());
            startActivity(intent);
        });
    }
}