package com.coffeecoders.cryptchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.coffeecoders.cryptchat.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        toolbar = mainBinding.toolbar;
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container , new PhoneNoFragment()).commit();
    }
}