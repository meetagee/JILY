package com.example.jily.connectivity;

import com.example.jily.model.Jily;
import com.example.jily.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServerEndpoint {

    //----------------------------------------------------------------------------------------------
    // AUTHENTICATION HANDLERS (TODO: Placeholders for now)
    //----------------------------------------------------------------------------------------------
    @Headers("Content-Type:application/json")
    @POST("user/login")
    Call<ResponseBody> login(@Body User user);

    @GET("logout/")
    Call<ResponseBody> logout();

    //----------------------------------------------------------------------------------------------
    // USER HANDLERS (TODO: Placeholders for now)
    //----------------------------------------------------------------------------------------------
    @Headers("Content-Type:application/json")
    @POST("user/signup")
    Call<ResponseBody> createUser(@Body User user);

    @DELETE("users/{user_id}/")
    Call<ResponseBody> deleteUser(@Path("user_id") Integer user_id);

    @GET("users/{user_id}/")
    Call<ResponseBody> getUser(@Path("user_id") Integer user_id);

    @Headers("Content-Type:application/json")
    @PUT("users/{user_id}/")
    Call<ResponseBody> updateUser(@Path("user_id") Integer user_id,
                                  @Body List<Jily<User>> user);

    //----------------------------------------------------------------------------------------------
    // RESTAURANT HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints

    //----------------------------------------------------------------------------------------------
    // ORDER HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints
}

