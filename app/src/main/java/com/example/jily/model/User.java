package com.example.jily.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.security.PrivateKey;

public class User {

    public static final int KEY_SIZE = 2048;
    public static final String DUMMY_FIREBASE_TOKEN = "DUMMY_FIREBASE_TOKEN";
    public static final String DUMMY_ACCESS_TOKEN = "DUMMY_ACCESS_TOKEN";
    public static final String DUMMY_USER_ID = "DUMMY_USER_ID";
    public static final String DUMMY_USER_TYPE = "DUMMY_USER_TYPE";

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("public_key")
    @Expose
    private String publicKey;
    private PrivateKey privateKey;

    @SerializedName("type")
    @Expose
    private String userType;

    @SerializedName("firebase_token")
    @Expose
    private String firebaseToken;

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("user")
    @Expose
    private String userId;

    @SerializedName(value = "user", alternate = "_id")
    @Expose
    private String userId;

    public User(String username,
                String password,
                String publicKey,
                String userType,
                String firebaseToken,
                String accessToken,
                @Nullable String userId) {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
        this.userType = userType;
        this.firebaseToken = firebaseToken;
        this.accessToken = accessToken;
        this.userId = userId;
    }

    public User(User that) {
        this(that.getUsername(),
                that.getPassword(),
                that.getPublicKey(),
                that.getUserType(),
                that.getFirebaseToken(),
                that.getAccessToken(),
                that.getUserId());
    }

    public User() {

    }

    public void clearCurrentUser() throws Throwable {
        this.finalize();
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

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public enum UserType {
        Customer,
        Merchant;

        @Nullable
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
