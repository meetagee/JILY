package com.example.jily.ui.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jily.R;
import com.example.jily.model.Order;
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

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView textOrderTitle;
        public TextView textOrderStatus;
        public ImageView containerQrCode;
        public View layout;

        // Provide a reference to the views for each order
        public OrdersViewHolder(View v) {
            super(v);
            layout = v;
            textOrderTitle = v.findViewById(R.id.text_order_title);
            textOrderStatus = v.findViewById(R.id.text_order_status);
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

    public OrdersAdapter(ArrayList<Order> myDataset, Context context) {
        orders = myDataset;
        mContext = context;
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
        final String name = orders.get(position).getName();
        final String status = orders.get(position).getStatus();

        holder.textOrderTitle.setText(name);
        holder.textOrderTitle.setOnClickListener(v -> {
            // TODO: Generate QR code and attach to ImageView
            // generateQrCode(holder.containerQrCode, status);

            // Inflate a dialog with the contents of the QR code
            LayoutInflater inflater = LayoutInflater.from(mContext);
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setView(inflater.inflate(R.layout.view_qr_code, null))
                    .setTitle("Your confirmation QR code")
                    .setPositiveButton("Close", null).create();

            dialog.setOnShowListener(arg -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(mContext.getResources().getColor(R.color.primary)));

            dialog.show();
        });

        holder.textOrderStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        // Return the number of orders (invoked by the layout manager)
        return orders.size();
    }

    // TODO: Fix generation of QR code
    public void generateQrCode(ImageView container, String status) {
        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(status, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            container.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
