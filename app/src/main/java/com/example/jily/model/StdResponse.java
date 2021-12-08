package com.example.jily.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StdResponse {

    @SerializedName("order")
    @Expose
    private String orderErr;

    @SerializedName("merchant")
    @Expose
    private String merchantErr;

    @SerializedName("status")
    @Expose
    private String statusErr;

    @SerializedName("secret")
    @Expose
    private String secretErr;

    public StdResponse(String orderErr,
                       String merchantErr,
                       String statusErr,
                       String secretErr) {
        this.orderErr = orderErr;
        this.merchantErr = merchantErr;
        this.statusErr = statusErr;
        this.secretErr = secretErr;
    }

    public String getOrderErr() { return orderErr; }

    public void setOrderErr(String orderErr) { this.orderErr = orderErr; }

    public String getMerchantErr() { return merchantErr; }

    public void setMerchantErr(String merchantErr) { this.merchantErr = merchantErr; }

    public String getStatusErr() { return statusErr; }

    public void setStatusErr(String date) { this.statusErr = statusErr; }

    public String getSecretErr() { return secretErr; }

    public void setSecretErr(String orderId) { this.secretErr = secretErr; }
}