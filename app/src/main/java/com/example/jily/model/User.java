package com.example.jily.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    public static final String DUMMY_PUBKEY = "DUMMY_PUBKEY";
    public static final String DUMMY_FIREBASE_TOKEN = "DUMMY_FIREBASE_TOKEN";
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("public_key")
    @Expose
    private String publicKey;
    @SerializedName("type")
    @Expose
    private String userType;
    @SerializedName("firebase_token")
    @Expose
    private String firebaseToken;
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public User(String username,
                String password,
                String publicKey,
                String userType,
                String firebaseToken) {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
        this.userType = userType;
        this.firebaseToken = firebaseToken;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public enum UserType {
        Customer,
        Merchant;

        public static UserType fromInt(int x) {
            switch (x) {
                case 0:
                    return Customer;
                case 1:
                    return Merchant;
                default:
                    return null;
            }
        }
    }
}
