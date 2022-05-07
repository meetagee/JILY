package com.example.jily;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that represents the splash screen launched immediately after the user opens up the app.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent welcomeIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }
}
