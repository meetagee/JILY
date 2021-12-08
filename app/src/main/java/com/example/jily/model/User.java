package com.example.jily.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

public class User {
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
    private String publicKeyStr;
    private PublicKey publicKey;
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

    @SerializedName(value = "user", alternate = "_id")
    @Expose
    private String userId;

    public User(String username,
                String password,
                PublicKey publicKey,
                PrivateKey privateKey,
                String userType,
                String firebaseToken,
                String accessToken,
                @Nullable String userId) throws IOException {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
        byte[] pubBytes = publicKey.getEncoded();

        SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(pubBytes);
        ASN1Primitive primitive = spkInfo.parsePublicKey();
        byte[] publicKeyPKCS1 = primitive.getEncoded();

        PemObject pemObject = new PemObject("RSA PUBLIC KEY", publicKeyPKCS1);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PemWriter pemWriter = new PemWriter(new OutputStreamWriter(byteStream));
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        this.publicKeyStr = byteStream.toString();

        this.privateKey = privateKey;
        this.userType = userType;
        this.firebaseToken = firebaseToken;
        this.accessToken = accessToken;
        this.userId = userId;
    }

    public User(User that) throws IOException {
        this(that.getUsername(),
                that.getPassword(),
                that.getPublicKey(),
                that.getPrivateKey(),
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

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
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

    public String getPublicKeyStr() {
        return publicKeyStr;
    }

    public void setPublicKeyStr(String publicKeyStr) {
        this.publicKeyStr = publicKeyStr;
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
