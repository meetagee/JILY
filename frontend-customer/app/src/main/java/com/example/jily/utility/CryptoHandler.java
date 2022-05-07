package com.example.jily.utility;

import android.util.Log;

import com.example.jily.connectivity.RuntimeManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

public class CryptoHandler {
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String PW_HASH_FUNC = "SHA-256";
    private static volatile CryptoHandler mInstance;

    private CryptoHandler() {
        // Prevent forming the reflection server
        if (mInstance != null) {
            throw new ExceptionInInitializerError(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public static CryptoHandler getInstance() {
        // Double check locking pattern
        if (mInstance == null) {                     // Check for the first time
            synchronized (CryptoHandler.class) {   // Check for the second time
                // If there is no instance available create a new one
                if (mInstance == null) mInstance = new CryptoHandler();
            }
        }
        return mInstance;
    }

    public String decryptPrivate(List<String> input) {
        StringBuilder finalStringBuilder = new StringBuilder();
        Cipher decryptCipher;
        for (String chunk : input) {
            byte[] secretBytes = Base64.getDecoder().decode(chunk);
            byte[] decryptedSecretBytes = null;
            try {
                decryptCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
                decryptCipher.init(Cipher.DECRYPT_MODE, RuntimeManager.getInstance().getCurrentUser().getPrivateKey());
                decryptedSecretBytes = decryptCipher.doFinal(secretBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String decryptedSecretStr = new String(decryptedSecretBytes, StandardCharsets.UTF_8);
            finalStringBuilder.append(decryptedSecretStr);
            Log.d("[CryptoHandler] DecryptPrivate", "Decrypted: " + decryptedSecretStr);
        }
        return finalStringBuilder.toString();
    }

    public String sha256Hash(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(PW_HASH_FUNC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert digest != null;
        byte[] hashedInput = digest.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hashedInput);
    }
}
