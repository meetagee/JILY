package com.example.jily.ui.merchants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.jily.model.StdResponse;
import com.example.jily.model.User;

import java.util.ArrayList;
import java.util.List;

public class MerchantsAdapter extends RecyclerView.Adapter<MerchantsAdapter.MerchantsViewHolder> {

    private final ArrayList<User> merchants;
    private final Context mContext;

    private ServerInterface mServerIf;
    private Handler mHandler;

    public static class MerchantsViewHolder extends RecyclerView.ViewHolder {

        public TextView textMerchantTitle;
        public TextView textMerchantStatus;
        public ImageButton buttonMenu;
        public View layout;

        // Provide a reference to the views for each merchant
        public MerchantsViewHolder(View v) {
            super(v);
            layout = v;
            textMerchantTitle = v.findViewById(R.id.text_merchant_title);
            textMerchantStatus = v.findViewById(R.id.text_merchant_status);
            buttonMenu = v.findViewById(R.id.button_menu);
        }
    }

    public void add(int position, User item) {
        merchants.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(int position) {
        merchants.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public MerchantsAdapter(ArrayList<User> myDataset, Context context) {
        merchants = myDataset;
        mContext = context;

        mServerIf = ServerInterface.getInstance();
        mHandler = new MerchantsHandler();
    }

    @NonNull
    @Override
    public MerchantsViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        // Create a new view (invoked by the layout manager)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_merchants, parent, false);
        return new MerchantsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MerchantsViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        // Replace the contents of the view (invoked by the layout manager)
        final String name = merchants.get(position).getUsername();
        final String status = "Open";

        holder.textMerchantTitle.setText(name);
        holder.textMerchantStatus.setText(status);

        holder.buttonMenu.setOnClickListener(v -> initDialog(position));
    }

    @Override
    public int getItemCount() {
        // Return the number of merchants (invoked by the layout manager)
        return merchants.size();
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
        if (selection.size() > 0) {
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
                    merchants.get(position).getUserId(),
                    null, null, null,
                    items);
            mServerIf.setHandler(mHandler);
            mServerIf.createOrder(currentUser, order);
        } else {
            Toast.makeText(mContext.getApplicationContext(),
                    "You need to select at least one item", Toast.LENGTH_SHORT).show();
        }
    }

    private class MerchantsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    Toast.makeText(mContext.getApplicationContext(), "Your order has been sent",
                            Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_BAD_REQUEST:
                    StdResponse error = (StdResponse) msg.obj;
                    Toast.makeText(mContext.getApplicationContext(), error.getStatusErr(),
                            Toast.LENGTH_SHORT).show();
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
