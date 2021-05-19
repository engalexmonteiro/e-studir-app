package com.example.estudir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handle = new Handler();

        handle.postDelayed(() -> {
            Intent login = new Intent(Splash.this,MainActivity.class);
            startActivity(login);
            finish();
        }, 2000);
    }
}