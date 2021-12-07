package com.example.jily.connectivity;

import com.example.jily.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServerEndpoint {

    //----------------------------------------------------------------------------------------------
    // AUTHENTICATION HANDLERS
    //----------------------------------------------------------------------------------------------
    @Headers("Content-Type:application/json")
    @POST("user/login")
    Call<ResponseBody> login(@Body User user);

    @GET("user/logout/{user_id}")
    Call<ResponseBody> logout(@Path("user_id") String userId);

    //----------------------------------------------------------------------------------------------
    // USER HANDLERS
    //----------------------------------------------------------------------------------------------
    @Headers("Content-Type:application/json")
    @POST("user/signup")
    Call<ResponseBody> createUser(@Body User user);

    @Headers("{access_token}")
    @GET("user/user/{user_id}")
    Call<ResponseBody> getUserType(@Path("access_token") String accessToken,
                                   @Path("user_id") String userId);

    //----------------------------------------------------------------------------------------------
    // ORDER HANDLERS
    //----------------------------------------------------------------------------------------------
    @GET("order/get-orders/{merchant_id}")
    Call<ResponseBody> getOrders(@Header("access_token") String accessToken,
                                 @Path("merchant_id") String userId);

    @Headers("Content-Type:application/json")
    @PUT("order/confirm/{order_id}")
    Call<ResponseBody> confirmOrder(@Header("access_token") String accessToken,
                                    @Path("order_id") String orderId);

    @Headers("Content-Type:application/json")
    @PUT("order/ready/{order_id}")
    Call<ResponseBody> readyOrder(@Header("access_token") String accessToken,
                                  @Path("order_id") String orderId);
}
