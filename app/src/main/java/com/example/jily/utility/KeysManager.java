package com.example.jily.utility;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeysManager {
    public static final int KEY_SIZE = 2048;
    private static final String KEY_STORE_NAME = "AndroidKeyStore";
    private static volatile KeysManager mInstance;

    public KeysManager() {
        // Prevent forming the reflection server
        if (mInstance != null) {
            throw new ExceptionInInitializerError(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public static KeysManager getInstance() {
        // Double check locking pattern
        if (mInstance == null) {                     // Check for the first time
            synchronized (KeysManager.class) {   // Check for the second time
                // If there is no instance available create a new one
                if (mInstance == null) mInstance = new KeysManager();
            }
        }
        return mInstance;
    }

    public KeyPair getKeyPair(String username) {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE_NAME);
            keyPairGen.initialize(new KeyGenParameterSpec.Builder(
                    username,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setKeySize(KEY_SIZE)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return keyPairGen.generateKeyPair();
    }
}
