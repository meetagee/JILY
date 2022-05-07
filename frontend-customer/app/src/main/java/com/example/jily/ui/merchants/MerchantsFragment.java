package com.example.jily.ui.merchants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jily.databinding.FragmentMerchantsBinding;

public class MerchantsFragment extends Fragment {

    private MerchantsViewModel merchantsViewModel;
    private FragmentMerchantsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Customize back button press (?)
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        merchantsViewModel =
                new ViewModelProvider(this).get(MerchantsViewModel.class);

        binding = FragmentMerchantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMerchants;
        merchantsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final RecyclerView recyclerView = binding.listMerchants;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        merchantsViewModel.getList().observe(getViewLifecycleOwner(), inList ->
                recyclerView.setAdapter(new MerchantsAdapter(inList, getContext())));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}