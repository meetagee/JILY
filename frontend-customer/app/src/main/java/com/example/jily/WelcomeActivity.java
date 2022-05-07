package com.example.jily;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity that represents the Welcome screen when a user first opens up the app. From here, they
 * can either create an account or log in. Creating an account can alternatively be accomplished
 * via the Google Sign In button.
 */
public class WelcomeActivity extends AppCompatActivity {

    private final static String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initButtons();
    }

    //----------------------------------------------------------------------------------------------
    // BUTTON HANDLERS
    //----------------------------------------------------------------------------------------------
    private void initButtons() {
        Button buttonAccount;
        TextView buttonTextLogin;

        buttonAccount = findViewById(R.id.button_account);
        buttonAccount.setOnClickListener(v -> {
            Intent accountIntent =
                    new Intent(getApplicationContext(), CreateAccountActivity.class);
            startActivity(accountIntent);
        });

        buttonTextLogin = findViewById(R.id.button_text_login);
        buttonTextLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
        });
    }
}
