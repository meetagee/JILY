package com.example.jily.ui.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jily.R;
import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.Order;
import com.example.jily.model.Secret;
import com.example.jily.model.StdResponse;
import com.example.jily.model.User;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {

    private final List<Order> orders;
    private final Context mContext;
    private final ActivityResultLauncher<Intent> mActivityResultLauncher;
    private int currentOrder;

    private final ServerInterface mServerIf;
    private final Handler mHandler;

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout layoutRowOrder;
        public TextView textOrderTitle;
        public TextView textOrderStatus;
        public ImageButton buttonQrCodeScanner;
        public View layout;

        // Provide a reference to the views for each order
        public OrdersViewHolder(View v) {
            super(v);
            layout = v;
            layoutRowOrder = v.findViewById(R.id.row_orders);
            textOrderTitle = v.findViewById(R.id.text_order_title);
            textOrderStatus = v.findViewById(R.id.text_order_status);
            buttonQrCodeScanner = v.findViewById(R.id.button_qr_code_scanner);
        }
    }

    public void add(int position, Order item) {
        orders.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void remove(int position) {
        orders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public OrdersAdapter(ArrayList<Order> myDataset, Context context,
                         ActivityResultLauncher<Intent> activityResultLauncher) {
        orders = myDataset;
        mContext = context;
        mActivityResultLauncher = activityResultLauncher;

        mServerIf = ServerInterface.getInstance();
        mHandler = new OrdersHandler();
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view (invoked by the layout manager)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_orders, parent, false);
        return new OrdersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrdersViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        // Replace the contents of the view (invoked by the layout manager)
        final String name = "Order " + orders.get(position).getOrderId();
        final String status = orders.get(position).getStatus();

        holder.layoutRowOrder.setOnClickListener(v -> initDialog(position));

        holder.textOrderTitle.setText(name);
        holder.textOrderStatus.setText(status);

        holder.buttonQrCodeScanner.setOnClickListener(v -> {
            currentOrder = position;
            scanOrder();
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of orders (invoked by the layout manager)
        return orders.size();
    }

    //----------------------------------------------------------------------------------------------
    // CONFIRMATION HANDLERS
    //----------------------------------------------------------------------------------------------
    private void initDialog(int position) {
        // Create a dialog to prompt the user to update the order status
        AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.Theme_JILY_Dialog)
                .setTitle("Update order status")
                .setSingleChoiceItems(R.array.status, 0, null)
                .setPositiveButton("Confirm", (dialog1, which) -> {
                    int pickup = ((AlertDialog) dialog1).getListView().getCheckedItemPosition();
                    updateOrder(position, pickup);
                })
                .setNegativeButton("Cancel", null).create();

        dialog.setOnShowListener(arg -> dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(mContext.getResources().getColor(R.color.primary_dark)));

        dialog.show();
    }

    private void updateOrder(int position, int pickup) {
        User currentUser = RuntimeManager.getInstance().getCurrentUser();
        mServerIf.setHandler(mHandler);

        if (pickup == 1) {
            mServerIf.readyOrder(currentUser, orders.get(position));
        } else {
            mServerIf.confirmOrder(currentUser, orders.get(position));
        }
    }

    private void scanOrder() {
        // Find the fragment associated with the Orders tab
        FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        Fragment host = manager.findFragmentById(R.id.nav_host_fragment_activity_main);
        assert host != null;
        OrdersFragment fragment =
                (OrdersFragment) host.getChildFragmentManager().getFragments().get(0);

        // Build an intent to launch a QR code scanner
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(fragment);
        integrator.setBarcodeImageEnabled(false);
        integrator.setBeepEnabled(false);
        integrator.setCameraId(0);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan and complete order");

        mActivityResultLauncher.launch(integrator.createScanIntent());
    }

    private String refreshOrder(Order order) {
        int position;
        for (position = 0; position < orders.size(); position++) {
            if (orders.get(position).getOrderId().equals(order.getOrderId())) {
                // Update the order status
                String status = order.getStatus();
                switch (status) {
                    case MessageConstants.STATUS_CONFIRM:
                        orders.get(position).setStatus(MessageConstants.STATUS_PROGRESS);
                        break;

                    case MessageConstants.STATUS_PROGRESS:
                        orders.get(position).setStatus(MessageConstants.STATUS_READY);
                        break;

                    case MessageConstants.STATUS_READY:
                        orders.get(position).setStatus(MessageConstants.STATUS_COMPLETED);
                        break;
                }

                // Notify the current view that the order has been updated
                // TODO: May need to send explicit notification too once integrated with Firebase
                notifyItemRemoved(position);
                notifyItemInserted(position);
                break;
            }
        }

        String retStatus;
        if (position == orders.size()) {
            retStatus = MessageConstants.ERROR_ORDER_NOT_FOUND;
        } else {
            retStatus = orders.get(position).getStatus();
        }

        return retStatus;
    }

    public void completeOrder(String secret) {
        User currentUser = RuntimeManager.getInstance().getCurrentUser();
        mServerIf.setHandler(mHandler);
        mServerIf.completeOrder(currentUser, orders.get(currentOrder), new Secret(secret));
    }

    private class OrdersHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            StdResponse error;
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    Order order = (Order) msg.obj;
                    String status = refreshOrder(order);
                    Toast.makeText(mContext.getApplicationContext(), "Order is " + status,
                            Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_BAD_REQUEST:
                    error = (StdResponse) msg.obj;
                    String message = (error.getStatusErr() == null) ? error.getSecretErr() :
                            error.getStatusErr();
                    Toast.makeText(mContext.getApplicationContext(), message,
                            Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_UNAUTHORIZED:
                    error = (StdResponse) msg.obj;
                    Toast.makeText(mContext.getApplicationContext(), error.getMerchantErr(),
                            Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_NOT_FOUND:
                    error = (StdResponse) msg.obj;
                    Toast.makeText(mContext.getApplicationContext(), error.getOrderErr(),
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(mContext.getApplicationContext(),
                            "There's an issue with the order. Please try again.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
