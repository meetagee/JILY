package com.example.jily.ui.restaurants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jily.R;
import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.Order;
import com.example.jily.model.Restaurant;
import com.example.jily.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private final ArrayList<User> restaurants;
    private final Context mContext;

    private ServerInterface mServerIf;
    private Handler mHandler;

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

        mServerIf = ServerInterface.getInstance();
        mHandler = new RestaurantsHandler();
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
            // Inflate a dialog with a menu
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(
                    R.layout.dialog_menu, (ViewGroup) v.getParent(), false);
            initDialog(view, position);
        });

        holder.textRestaurantStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        // Return the number of restaurants (invoked by the layout manager)
        return restaurants.size();
    }

    //----------------------------------------------------------------------------------------------
    // MENU HANDLERS
    //----------------------------------------------------------------------------------------------
    private void initDialog(@NonNull View v, int position) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(v)
                .setTitle("Create your order")
                .setPositiveButton("Confirm", (dialog1, which) -> createOrder(position))
                .setNegativeButton("Cancel", null).create();

        dialog.setOnShowListener(arg -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(mContext.getResources().getColor(R.color.primary)));
        dialog.setOnShowListener(arg -> dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(mContext.getResources().getColor(R.color.primary_dark)));

        dialog.show();
    }

    private void createOrder(int position) {
        User currentUser = RuntimeManager.getInstance().getCurrentUser();
        // TODO: Replace items with checkboxes user can select?
        List<String> items = Arrays.asList("veggie bowl", "veggie drink", "veggie salad");
        Order order = new Order(
                currentUser.getUserId(),
                restaurants.get(position).getUserId(),
                null,
                null,
                items);

        mServerIf.setHandler(mHandler);
        mServerIf.createOrder(currentUser, order);
    }

    private class RestaurantsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    Order Order = (Order) msg.obj;
                    // TODO: Send order_id to Orders fragment?
                    break;

                case MessageConstants.OPERATION_FAILURE_BAD_REQUEST:
                    String error = (String) msg.obj;
                    Toast.makeText(mContext.getApplicationContext(), error, Toast.LENGTH_SHORT)
                            .show();
                    break;

                default:
                    Toast.makeText(mContext.getApplicationContext(),
                            "There's an issue creating your order. Please try again.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
