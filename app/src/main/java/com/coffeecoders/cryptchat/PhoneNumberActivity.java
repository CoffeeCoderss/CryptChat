package com.coffeecoders.cryptchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coffeecoders.cryptchat.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.continueButton.setOnClickListener(view -> {
            Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
            intent.putExtra("phoneNumber", binding.phoneNumberTextView.getText().toString());
            startActivity(intent);
        });
    }
}