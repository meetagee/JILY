package com.example.jily.ui.restaurants;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.jily.model.User;

import java.util.ArrayList;
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
        holder.textRestaurantTitle.setOnClickListener(v -> initDialog(position));

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
    private void initDialog(int position) {
        // Create a dialog to display the menu to the user
        ArrayList<Integer> selection = new ArrayList<>();
        AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.Theme_JILY_Dialog)
                .setTitle("Create your order")
                .setMultiChoiceItems(R.array.menu, null, (dialog1, which, isChecked) -> {
                    if (isChecked) {
                        selection.add(which);
                    } else if (selection.contains(which)) {
                        selection.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton("Confirm", (dialog2, which) ->
                        createOrder(position, selection))
                .setNegativeButton("Cancel", null).create();

        dialog.setOnShowListener(arg -> dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(mContext.getResources().getColor(R.color.primary_dark)));

        dialog.show();
    }

    private void createOrder(int position, ArrayList<Integer> selection) {
        // Retrieve the list of selected items from the menu
        String[] menu = mContext.getResources().getStringArray(R.array.menu);
        List<String> items = new ArrayList<>();

        for (int i = 0; i < selection.size(); i++) {
            items.add(menu[selection.get(i)]);
        }

        // Create a new order
        User currentUser = RuntimeManager.getInstance().getCurrentUser();
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
                    // Orders are retrieved when user navigates to Orders fragment
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
