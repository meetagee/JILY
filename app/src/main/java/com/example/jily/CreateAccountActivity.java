package com.example.jily;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.User;
import com.example.jily.model.User.UserType;
import com.example.jily.utility.KeysManager;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Activity that represents where the user can create an account via an email and password.
 */
public class CreateAccountActivity extends AppCompatActivity {

    private final static String DEFAULT_USER_TYPES_PROMPT = "Select user type...";
    private final int PASSWORD_MIN_LENGTH = 8;
    private final List<String> AVAILABLE_USER_TYPES = initAvailableUserTypesList();

    private EditText mEditTextUsername;
    private EditText mEditTextPassword;
    private UserType mUserType;

    private ServerInterface mServerIf;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mEditTextUsername = findViewById(R.id.edit_text_username_account);
        mEditTextPassword = findViewById(R.id.edit_text_password_account);

        initSpinners();
        initButtons();

        mServerIf = ServerInterface.getInstance();
        mHandler = new CreateAccountHandler();
    }

    private List<String> initAvailableUserTypesList() {
        List<String> retList = new ArrayList<String>() {{
            add(DEFAULT_USER_TYPES_PROMPT);
        }};
        Arrays.stream(UserType.values()).map(Enum::toString).forEach(retList::add);

        return retList;
    }

    private void initSpinners() {
        final Spinner spinner = (Spinner) findViewById(R.id.edit_spinner_user_type);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.item_spinner, AVAILABLE_USER_TYPES) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUserType = UserType.fromInt(position - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mUserType = UserType.fromInt(-1);
            }
        });
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
            String username = mEditTextUsername.getText().toString().trim();
            String password = mEditTextPassword.getText().toString().trim();

            if (username.isEmpty()) {
                mEditTextUsername.setHint(R.string.text_field_required);
                mEditTextUsername.setHintTextColor(getColor(R.color.primary_dark));
            } else if (password.isEmpty()) {
                mEditTextPassword.setHint(R.string.text_field_required);
                mEditTextPassword.setHintTextColor(getColor(R.color.primary_dark));
            } else if (password.length() < PASSWORD_MIN_LENGTH) {
                Toast.makeText(this, "Password must be at least 8 characters",
                        Toast.LENGTH_SHORT).show();
            } else if (mUserType == null) {
                Toast.makeText(this, "Please select an user type",
                        Toast.LENGTH_SHORT).show();
            } else {
                KeyPair signupKeyPair = KeysManager.getInstance().getKeyPair(username);
                PublicKey signUpPubKey = signupKeyPair.getPublic();
                PrivateKey signUpPrivateKey = signupKeyPair.getPrivate();

                User newUser = new User(username,
                        password,
                        Base64.getEncoder().encodeToString(signUpPubKey.getEncoded()),
                        mUserType.toString(),
                        User.DUMMY_FIREBASE_TOKEN,
                        User.DUMMY_ACCESS_TOKEN,
                        User.DUMMY_USER_ID);
                newUser.setPrivateKey(signUpPrivateKey);

                RuntimeManager.getInstance().setCurrentUser(newUser);
                mServerIf.setHandler(mHandler);
                mServerIf.createUser(newUser);
            }
        });
    }

    private class CreateAccountHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;

                case MessageConstants.OPERATION_FAILURE_UNAUTHORIZED:
                    Toast.makeText(getApplicationContext(),
                            MessageConstants.DUPLICATE_USER_ERR_STRING, Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getApplicationContext(), "There's an issue signing up. Please " +
                            "try again.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
