package com.example.jily.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jily.CreateAccountActivity;
import com.example.jily.WelcomeActivity;
import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.databinding.FragmentSettingsBinding;
import com.example.jily.model.User;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private ServerInterface mServerIf;
    private Handler mHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel homeViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        User currentUser = RuntimeManager.getInstance().getCurrentUser();

        final TextView tvUsername = binding.textUserUsername;
        CharSequence displayUsername = "Username: " + currentUser.getUsername();
        tvUsername.setText(displayUsername);
        homeViewModel.getText().observe(getViewLifecycleOwner(), tvUsername::setText);

        final TextView tvUserId = binding.textUserId;
        CharSequence displayUserId = "User ID: " + currentUser.getUserId();
        tvUserId.setText(displayUserId);
        homeViewModel.getText().observe(getViewLifecycleOwner(), tvUserId::setText);

        final TextView tvUserPubkey = binding.textUserPubkey;
        CharSequence displayPubkey = "Public key: " + currentUser.getPublicKey();
        tvUserPubkey.setText(displayPubkey);
        homeViewModel.getText().observe(getViewLifecycleOwner(), tvUserPubkey::setText);

        final TextView tvUserAccessToken = binding.textUserAccessToken;
        CharSequence displayAccessToken = "Access token: " + currentUser.getAccessToken();
        tvUserAccessToken.setText(displayAccessToken);
        homeViewModel.getText().observe(getViewLifecycleOwner(), tvUserAccessToken::setText);

        final TextView tvUserType = binding.textUserType;
        CharSequence displayUserType = "User type: " + currentUser.getUserType();
        tvUserType.setText(displayUserType);
        homeViewModel.getText().observe(getViewLifecycleOwner(), tvUserType::setText);

        initButtons();

        mServerIf = ServerInterface.getInstance();
        mHandler = new SettingsHandler();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initButtons() {
        Button buttonLogout;

        buttonLogout = binding.buttonLogout;
        buttonLogout.setOnClickListener(view -> {
            mServerIf.setHandler(mHandler);
            mServerIf.logout(RuntimeManager.getInstance().getCurrentUser());
        });
    }

    private class SettingsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    Intent mainIntent = new Intent(getContext(), WelcomeActivity.class);
                    startActivity(mainIntent);
                    requireActivity().finish();
                    break;

                default:
                    Toast.makeText(getContext(), "There's an issue logging you out. Please " +
                            "try again.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}