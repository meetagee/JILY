package com.example.jily.ui.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.jily.utility.CryptoHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private final List<Order> orders;
    private final Context mContext;
    private ServerInterface mServerIf;
    private Handler mHandler;
    //----------------------------------------------------------------------------------------------
    // QR CODE HANDLERS
    //----------------------------------------------------------------------------------------------
    private ImageView containerQrCode;
    private AlertDialog dialog;

    public OrdersAdapter(ArrayList<Order> myDataset, Context context) {
        orders = myDataset;
        mContext = context;

        mServerIf = ServerInterface.getInstance();
        mHandler = new OrdersAdapter.OrdersHandler();
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

        holder.textOrderTitle.setText(name);
        holder.textOrderStatus.setText(status);

        holder.buttonQrCode.setOnClickListener(v -> {
            // Inflate a dialog that will hold the QR code of the encrypted message
            initDialog(v, position);

            // Retrieve the encrypted order secret that will be included in the QR code
            // TODO: The point at which this is called may change once integrated with Firebase
            User currentUser = RuntimeManager.getInstance().getCurrentUser();
            Order order = orders.get(position);
            mServerIf.setHandler(mHandler);
            mServerIf.getOrderSecret(currentUser, order);
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of orders (invoked by the layout manager)
        return orders.size();
    }

    private void initDialog(View v, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(
                R.layout.dialog_qr_code, (ViewGroup) v.getParent(), false);
        containerQrCode = view.findViewById(R.id.container_qr_code);

        // Create the actual dialog to display the QR code to the user
        dialog = new AlertDialog.Builder(mContext, R.style.Theme_JILY_Dialog)
                .setView(view)
                .setTitle("Your confirmation QR code")
                .setPositiveButton("Close", (dialog, which) -> getOrder(position)).create();
    }

    private Bitmap generateQrCode(String status) {
        MultiFormatWriter writer = new MultiFormatWriter();
        Bitmap bitmap = null;
        int dimen = 650;

        try {
            BitMatrix matrix = writer.encode(status, BarcodeFormat.QR_CODE, dimen, dimen);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private void getOrder(int position) {
        User currentUser = RuntimeManager.getInstance().getCurrentUser();
        mServerIf.setHandler(mHandler);
        mServerIf.getOrderById(currentUser, orders.get(position));
    }

    private void refreshOrder(Order order) {
        for (int position = 0; position < orders.size(); position++) {
            if (orders.get(position).getOrderId().equals(order.getOrderId())) {
                // Update the order status and current view
                orders.get(position).setStatus(order.getStatus());
                notifyItemRemoved(position);
                notifyItemInserted(position);
                break;
            }
        }
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView textOrderTitle;
        public TextView textOrderStatus;
        public ImageButton buttonQrCode;
        public View layout;

        // Provide a reference to the views for each order
        public OrdersViewHolder(View v) {
            super(v);
            layout = v;
            textOrderTitle = v.findViewById(R.id.text_order_title);
            textOrderStatus = v.findViewById(R.id.text_order_status);
            buttonQrCode = v.findViewById(R.id.button_qr_code);
        }
    }

    private class OrdersHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            StdResponse error;
            switch (msg.arg2) {
                case MessageConstants.OPERATION_SUCCESS:
                    Order order = (Order) msg.obj;
                    if (msg.what == MessageConstants.MESSAGE_SECRET_RESPONSE) {
                        String finalString =
                                CryptoHandler.getInstance().decryptPrivate(order.getSecret());
                        Log.w("[OrdersHandler] HandleMessage", "Final: " + finalString);
                        containerQrCode.setImageBitmap(generateQrCode(finalString));
                        dialog.show();
                    } else {
                        refreshOrder(order);
                    }
                    break;

                case MessageConstants.OPERATION_FAILURE_BAD_REQUEST:
                    error = (StdResponse) msg.obj;
                    Toast.makeText(mContext.getApplicationContext(), error.getStatusErr(),
                            Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_UNAUTHORIZED:
                    error = (StdResponse) msg.obj;
                    Toast.makeText(mContext.getApplicationContext(), error.getMerchantErr(),
                            Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_NOT_FOUND:
                    error = (StdResponse) msg.obj;
                    String message = (error.getOrderErr() == null) ?
                            error.getMerchantErr() : error.getOrderErr();

                    Toast.makeText(mContext.getApplicationContext(), message,
                            Toast.LENGTH_SHORT).show();
                    break;

                case MessageConstants.OPERATION_FAILURE_SERVER_ERROR:
                    error = (StdResponse) msg.obj;
                    Toast.makeText(mContext.getApplicationContext(), error.getOrderErr(),
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(mContext.getApplicationContext(),
                            "There's an issue with your order. Please try again.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
