package com.example.jily;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.User;
import com.example.jily.utility.CryptoHandler;
import com.example.jily.utility.KeysManager;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Activity that represents where the user can log in to their account via an email and password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextUsername;
    private EditText mEditTextPassword;

    private ServerInterface mServerIf;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditTextUsername = findViewById(R.id.edit_text_username_login);
        mEditTextPassword = findViewById(R.id.edit_text_password_login);

        initButtons();

        mServerIf = ServerInterface.getInstance();
        mHandler = new LoginHandler();
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
            String username = mEditTextUsername.getText().toString().trim();
            String password = mEditTextPassword.getText().toString().trim();

            if (username.isEmpty()) {
                mEditTextUsername.setHint(R.string.text_field_required);
                mEditTextUsername.setHintTextColor(getColor(R.color.primary_dark));
            } else if (password.isEmpty()) {
                mEditTextPassword.setHint(R.string.text_field_required);
                mEditTextPassword.setHintTextColor(getColor(R.color.primary_dark));
            } else {
                KeyPair loginKeyPair = KeysManager.getInstance().getKeyPair(username);
                PublicKey loginPubKey = loginKeyPair.getPublic();
                PrivateKey loginPrivateKey = loginKeyPair.getPrivate();

                User loginUser = null;
                try {
                    loginUser = new User(username,
                            CryptoHandler.getInstance().sha256Hash(password),
                            loginPubKey,
                            loginPrivateKey,
                            User.DUMMY_USER_TYPE,
                            User.DUMMY_FIREBASE_TOKEN,
                            User.DUMMY_ACCESS_TOKEN,
                            User.DUMMY_USER_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RuntimeManager.getInstance().setCurrentUser(loginUser);
                mServerIf.setHandler(mHandler);
                mServerIf.login(loginUser);
            }
        });
    }

    private class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;

                case MessageConstants.OPERATION_FAILURE_NOT_FOUND:
                    Toast.makeText(getApplicationContext(),
                            MessageConstants.USERNAME_NOT_REGISTERED, Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_UNAUTHORIZED:
                    Toast.makeText(getApplicationContext(),
                            MessageConstants.PASSWORD_INCORRECT, Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_INCOMPATIBLE_UI:
                    Toast.makeText(getApplicationContext(),
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/LoginActivity.java
                            "You're logging in as a Merchant. Please login using the " +
                                    "Merchant interface", Toast.LENGTH_LONG).show();
=======
                            "You're logging in as a Customer. Please login using the " +
                                    "Customer interface", Toast.LENGTH_LONG).show();
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/LoginActivity.java
                    break;

                default:
                    Toast.makeText(getApplicationContext(), "There's an issue logging you in. Please " +
                            "try again.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}