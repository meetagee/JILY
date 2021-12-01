package com.example.jily.ui.restaurants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.jily.databinding.FragmentRestaurantsBinding;

import java.util.List;

public class RestaurantsFragment extends Fragment {

    private RestaurantsViewModel galleryViewModel;
    private FragmentRestaurantsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(RestaurantsViewModel.class);

        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textRestaurants;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final ListView listView = binding.listRestaurants;
        galleryViewModel.getList().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> inList) {
                listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_selectable_list_item, inList));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}