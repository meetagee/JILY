package com.example.jily.ui.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jily.databinding.FragmentOrdersBinding;
import com.google.zxing.client.android.Intents;

public class OrdersFragment extends Fragment {

    private OrdersViewModel ordersViewModel;
    private FragmentOrdersBinding binding;

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
                        String encryptedSecret = intent.getStringExtra(Intents.Scan.RESULT);
                        int test = encryptedSecret.length();
                    }
                });

        final RecyclerView recyclerView = binding.listOrders;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ordersViewModel.getList().observe(getViewLifecycleOwner(), inList -> {
            recyclerView.setAdapter(new OrdersAdapter(inList, getContext(), activityResultLauncher));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}