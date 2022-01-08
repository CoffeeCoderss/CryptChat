package com.coffeecoders.cryptchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coffeecoders.cryptchat.databinding.ActivityOtpactivityBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    ActivityOtpactivityBinding binding;
    FirebaseAuth firebaseAuth;
    String verifyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        firebaseAuth = FirebaseAuth.getInstance();
        // otp options
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verifyID = s;
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
        // verify if the otp is successful
        // auto verifies without the need to click confirm
        binding.otpView.setOtpCompletionListener(otp -> {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifyID, otp);
            firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
                else
                    Toast.makeText(OTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            });
        });
    }
}