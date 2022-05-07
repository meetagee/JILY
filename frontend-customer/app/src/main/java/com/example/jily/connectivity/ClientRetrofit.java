package com.example.jily.connectivity;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientRetrofit {

    private static String baseUrl;

    private static volatile ClientRetrofit instance;

    private ClientRetrofit() {
        // Prevent forming the reflection api
        if (instance != null) {
            throw new ExceptionInInitializerError(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ClientRetrofit getInstance() {
        // Double check locking pattern
        if (instance == null) {                     // Check for the first time
            synchronized (ClientRetrofit.class) {   // Check for the second time
                // If there is no instance available create a new one
                if (instance == null) instance = new ClientRetrofit();
            }
        }
        return instance;
    }

    public static void init(String path) { baseUrl = path; }

    public Retrofit createAdapter() {
        OkHttpClient.Builder OkHttpBuilder = new OkHttpClient.Builder();

        OkHttpBuilder.cookieJar(new MyCookieJar());

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

