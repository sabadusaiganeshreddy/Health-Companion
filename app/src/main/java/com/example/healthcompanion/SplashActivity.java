package com.example.healthcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Splash screen activity that checks if user is logged in
 * and redirects accordingly.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_TIME = 2000; // 2 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Delayed navigation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkCurrentUser();
            }
        }, SPLASH_DISPLAY_TIME);
    }

    private void checkCurrentUser() {
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            // User is signed in, go to main activity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // No user is signed in, go to login activity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close this activity
    }
}