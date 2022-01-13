package com.coffeecoders.cryptchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.coffeecoders.cryptchat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        toolbar = mainBinding.toolbar;
        setSupportActionBar(toolbar);

         firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(intent);
        }else 
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PhoneNoFragment()).commit();
    }
}