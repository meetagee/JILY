package com.example.jily.connectivity;

import com.example.jily.model.Profile;
import com.example.jily.model.Jily;
import com.example.jily.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServerEndpoint {

    //----------------------------------------------------------------------------------------------
    // AUTHENTICATION HANDLERS (TODO: Placeholders for now)
    //----------------------------------------------------------------------------------------------
    @POST("login/")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("username") String user,
                             @Field("password") String password);

    @GET("logout/")
    Call<ResponseBody> logout();

    //----------------------------------------------------------------------------------------------
    // USER HANDLERS (TODO: Placeholders for now)
    //----------------------------------------------------------------------------------------------
    @Headers("Content-Type:application/json")
    @POST("users/")
    Call<ResponseBody> createUser(@Body List<Jily<User>> user);

    @DELETE("users/{user_id}/")
    Call<ResponseBody> deleteUser(@Path("user_id") Integer user_id);

    @GET("users/{user_id}/")
    Call<ResponseBody> getUser(@Path("user_id") Integer user_id);

    @GET("users/{user_id}/profile/")
    Call<Integer> getUserProfile(@Path("user_id") Integer user_id);

    @Headers("Content-Type:application/json")
    @PUT("users/{user_id}/")
    Call<ResponseBody> updateUser(@Path("user_id") Integer user_id,
                                  @Body List<Jily<User>> user);

    //----------------------------------------------------------------------------------------------
    // PROFILE HANDLERS (TODO: Placeholders for now)
    //----------------------------------------------------------------------------------------------
    @GET("profiles/{profile_id}")
    Call<ResponseBody> getProfile(@Path("profile_id") Integer profile_id);

    @Headers("Content-Type:application/json")
    @PUT("profiles/{profile_id}/")
    Call<ResponseBody> updateProfile(@Path("profile_id") Integer profile_id,
                                     @Body List<Jily<Profile>> profile);

    //----------------------------------------------------------------------------------------------
    // RESTAURANT HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints

    //----------------------------------------------------------------------------------------------
    // ORDER HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints
}

