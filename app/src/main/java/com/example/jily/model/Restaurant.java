package com.example.jily.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Restaurant {

    @SerializedName("merchants")
    @Expose
    private List<User> merchants;

    public Restaurant(List<User> merchants) {
        this.merchants = merchants;
    }

    public List<User> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<User> merchants) {
        this.merchants = merchants;
    }
}
