package com.example.jily.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Secret {

    @SerializedName("secret")
    @Expose
    private String secret;

    public Secret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

