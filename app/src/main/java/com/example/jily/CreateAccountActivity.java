package com.example.jily;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.jily.connectivity.MessageConstants;

/**
 * Activity that represents where the user can create an account via an email and password.
 */
public class CreateAccountActivity extends AppCompatActivity {

    private final int PASSWORD_MIN_LENGTH = 8;

    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editTextEmail = findViewById(R.id.edit_text_email_account);
        editTextPassword = findViewById(R.id.edit_text_password_account);

        initButtons();

        // TODO: [RAN OUT OF TIME] Set up a class that extends Handler and override handleMessage()
    }

    //----------------------------------------------------------------------------------------------
    // BUTTON HANDLERS
    //----------------------------------------------------------------------------------------------
    private void initButtons() {
        ImageButton buttonBackAccount;
        Button buttonSignup;

        buttonBackAccount = findViewById(R.id.button_back_account);
        buttonBackAccount.setOnClickListener(v -> onBackPressed());

        buttonSignup = findViewById(R.id.button_signup);
        buttonSignup.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty()) {
                editTextEmail.setHint(R.string.text_field_required);
                editTextEmail.setHintTextColor(getColor(R.color.primary_dark));
            }
            else if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email address entered",
                        Toast.LENGTH_SHORT).show();
            }
            else if (password.isEmpty()) {
                editTextPassword.setHint(R.string.text_field_required);
                editTextPassword.setHintTextColor(getColor(R.color.primary_dark));
            }
            else if (password.length() < PASSWORD_MIN_LENGTH) {
                Toast.makeText(this, "Password must be at least 8 characters",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                // TODO: [RAN OUT OF TIME] Create a new user via ServerInterface's createAccount()
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putExtra(MessageConstants.EMAIL, email);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
