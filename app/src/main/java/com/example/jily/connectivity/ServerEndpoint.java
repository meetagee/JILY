package com.example.jily.connectivity;

import com.example.jily.model.Jily;
import com.example.jily.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @GET("user/logout/{user_id}")
    Call<ResponseBody> logout(@Path("user_id") String userId);

    //----------------------------------------------------------------------------------------------
    // USER HANDLERS (TODO: Placeholders for now)
    //----------------------------------------------------------------------------------------------
    @Headers("Content-Type:application/json")
    @POST("user/signup")
    Call<ResponseBody> createUser(@Body User user);

    @Headers("{access_token}")
    @GET("user/user/{user_id}")
    Call<ResponseBody> getUserType(@Path("access_token") String accessToken,
                                   @Path("user_id") String userId);

    @DELETE("users/{user_id}/")
    Call<ResponseBody> deleteUser(@Path("user_id") Integer user_id);

    @Headers("Content-Type:application/json")
    @PUT("users/{user_id}/")
    Call<ResponseBody> updateUser(@Path("user_id") Integer user_id,
                                  @Body List<Jily<User>> user);

    //----------------------------------------------------------------------------------------------
    // RESTAURANT HANDLERS
    //----------------------------------------------------------------------------------------------
    @GET("user/merchants")
    Call<ResponseBody> getMerchants(@Header("access_token") String accessToken);

    //----------------------------------------------------------------------------------------------
    // ORDER HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints
}

