package com.example.jily;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Activity that represents where the user can log in to their account via an email and password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.edit_text_email_login);
        editTextPassword = findViewById(R.id.edit_text_password_login);

        initButtons();

        // TODO: Set up a class that extends Handler and override handleMessage()
    }

    //----------------------------------------------------------------------------------------------
    // BUTTON HANDLERS
    //----------------------------------------------------------------------------------------------
    private void initButtons() {
        ImageButton buttonBackAccount;
        Button buttonLogin;

        buttonBackAccount = findViewById(R.id.button_back_login);
        buttonBackAccount.setOnClickListener(v -> onBackPressed());

        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty()) {
                editTextEmail.setHint(R.string.text_field_required);
                editTextEmail.setHintTextColor(getColor(R.color.primary_dark));
            }
            else if (password.isEmpty()) {
                editTextPassword.setHint(R.string.text_field_required);
                editTextPassword.setHintTextColor(getColor(R.color.primary_dark));
            }
            else {
                // TODO: Verify credentials with ServerInterface's login()
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
