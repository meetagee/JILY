package com.example.jily.ui.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jily.connectivity.MessageConstants;
import com.example.jily.databinding.FragmentOrdersBinding;
import com.example.jily.utility.CryptoHandler;
import com.google.zxing.client.android.Intents;

import java.util.Collections;

public class OrdersFragment extends Fragment {

    private OrdersViewModel ordersViewModel;
    private FragmentOrdersBinding binding;
    private OrdersAdapter ordersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                new ViewModelProvider(this).get(OrdersViewModel.class);

        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textOrders;
        ordersViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        // Decrypt the QR scan result with the user's private key
                        String encryptedSecret = intent.getStringExtra(Intents.Scan.RESULT);
                        String secret = CryptoHandler.getInstance().decryptPrivate(
                                Collections.singletonList(encryptedSecret));

                        // If secret successfully decrypted, verify it with the backend
                        if (!secret.equals(MessageConstants.ERROR_QR_CODE_NEEDS_UPDATE)) {
                            ordersAdapter.completeOrder(secret);
                        } else {
                            Toast.makeText(getActivity(),
                                    "Please ask the customer to regenerate the QR code",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        final RecyclerView recyclerView = binding.listOrders;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ordersViewModel.getList().observe(getViewLifecycleOwner(), inList -> {
            ordersAdapter = new OrdersAdapter(inList, getContext(), activityResultLauncher);
            recyclerView.setAdapter(ordersAdapter);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}