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
import com.example.jily.utility.KeysManager;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

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
                KeyPair signupKeyPair = KeysManager.getInstance().getKeyPair(username);
                PublicKey signUpPubKey = signupKeyPair.getPublic();
                PrivateKey signUpPrivateKey = signupKeyPair.getPrivate();

                User loginUser = new User(username,
                        password,
                        Base64.getEncoder().encodeToString(signUpPubKey.getEncoded()),
                        User.DUMMY_USER_TYPE,
                        User.DUMMY_FIREBASE_TOKEN,
                        User.DUMMY_ACCESS_TOKEN,
                        User.DUMMY_USER_ID);
                loginUser.setPrivateKey(signUpPrivateKey);

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

                default:
                    Toast.makeText(getApplicationContext(), "There's an issue logging you in. Please " +
                            "try again.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
