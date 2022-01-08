package com.coffeecoders.cryptchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.coffeecoders.cryptchat.databinding.FragmentOtpBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;


public class OtpFragment extends Fragment {
    FragmentOtpBinding otpBinding;
    private Toolbar otpToolbar;
    FirebaseAuth firebaseAuth;
    String verifyID;

    public OtpFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        otpBinding = FragmentOtpBinding.inflate(inflater, container, false);
        otpToolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("otp");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        String phoneNumber = getArguments().getString("phoneNumber");
        firebaseAuth = FirebaseAuth.getInstance();
        // otp options
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(getActivity())
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
        otpBinding.fragmentOtpView.setOtpCompletionListener(otp -> {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifyID, otp);
            firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    getActivity().getSupportFragmentManager().beginTransaction().
                            add(R.id.fragment_container , new ProfileFragment()).commit();
                }
                else
                    Toast.makeText(getContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
            });
        });

        return otpBinding.getRoot();
    }
}