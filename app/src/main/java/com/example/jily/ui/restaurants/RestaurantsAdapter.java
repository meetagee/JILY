package com.example.jily.ui.restaurants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jily.R;
import com.example.jily.model.User;

import java.util.ArrayList;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private final ArrayList<User> restaurants;
    private final Context mContext;

    public static class RestaurantsViewHolder extends RecyclerView.ViewHolder {

        public TextView textRestaurantTitle;
        public TextView textRestaurantStatus;
        public View layout;

        // Provide a reference to the views for each restaurant
        public RestaurantsViewHolder(View v) {
            super(v);
            layout = v;
            textRestaurantTitle = v.findViewById(R.id.text_restaurant_title);
            textRestaurantStatus = v.findViewById(R.id.text_restaurant_status);
        }
    }

    public void add(int position, User item) {
        restaurants.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(int position) {
        restaurants.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public RestaurantsAdapter(ArrayList<User> myDataset, Context context) {
        restaurants = myDataset;
        mContext = context;
    }

    @NonNull
    @Override
    public RestaurantsAdapter.RestaurantsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // Create a new view (invoked by the layout manager)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_restaurants, parent, false);
        return new RestaurantsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RestaurantsViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        // Replace the contents of the view (invoked by the layout manager)
        final String name = restaurants.get(position).getUsername();
        final String status = "Open";

        holder.textRestaurantTitle.setText(name);
        holder.textRestaurantTitle.setOnClickListener(v -> {
            // TODO: Create AlertDialog to perform actual order (see OrdersAdapter for example)?
        });

        holder.textRestaurantStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        // Return the number of restaurants (invoked by the layout manager)
        return restaurants.size();
    }
}
