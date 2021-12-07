package com.example.jily.ui.restaurants;

import android.os.Bundle;
import android.util.Log;
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

import com.example.jily.databinding.FragmentRestaurantsBinding;

import java.util.Objects;

public class RestaurantsFragment extends Fragment {

    private RestaurantsViewModel restaurantsViewModel;
    private FragmentRestaurantsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantsViewModel =
                new ViewModelProvider(this).get(RestaurantsViewModel.class);

        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textRestaurants;
        restaurantsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final RecyclerView recyclerView = binding.listRestaurants;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        restaurantsViewModel.getList().observe(getViewLifecycleOwner(), inList ->
                recyclerView.setAdapter(new RestaurantsAdapter(inList, getContext())));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}