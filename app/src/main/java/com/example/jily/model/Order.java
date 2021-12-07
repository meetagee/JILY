package com.example.jily.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("merchant_id")
    @Expose
    private String merchantId;

    @SerializedName("order_id")
    @Expose
    private String orderId;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("items")
    @Expose
    private List<String> items;

    public Order(String userId,
                 String merchantId,
                 @Nullable String orderId,
                 @Nullable String status,
                 List<String> items) {
        this.userId = userId;
        this.merchantId = merchantId;
        this.orderId = orderId;
        this.status = status;
        this.items = items;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getMerchantId() { return merchantId; }

    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getOrderId() { return orderId; }

    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }

    public void setStatus(String date) { this.status = date; }

    public List<String> getItems() { return items; }

    public void setItems(List<String> items) { this.items = items; }
}