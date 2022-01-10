package com.coffeecoders.cryptchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.coffeecoders.cryptchat.databinding.FragmentPhoneNoBinding;

public class PhoneNoFragment extends Fragment {
    FragmentPhoneNoBinding phoneNoBinding;
    private Toolbar PhoneNoToolbar;
    public PhoneNoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        phoneNoBinding =  FragmentPhoneNoBinding.inflate(inflater, container, false);
        PhoneNoToolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("CryptChat");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        phoneNoBinding.continueButton.setOnClickListener(view -> {
//            Intent intent = new Intent(getContext(), OtpFragment.class);
//            intent.putExtra("phoneNumber", phoneNoBinding.phoneNumberTextView.getText().toString());
//            startActivity(intent);
            OtpFragment otpFragment = new OtpFragment();
            Bundle args = new Bundle();
            args.putString("phoneNumber", phoneNoBinding.phoneNumberTextView.getText().toString());
            otpFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container , otpFragment).commit();
        });
        return phoneNoBinding.getRoot();
    }
}