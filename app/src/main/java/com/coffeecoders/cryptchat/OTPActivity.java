package com.coffeecoders.cryptchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.coffeecoders.cryptchat.databinding.ActivityOtpactivityBinding;
import com.coffeecoders.cryptchat.databinding.ActivityPhoneNumberBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

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
                if (task.isSuccessful())
                    Toast.makeText(OTPActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(OTPActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            });
        });
    }
}