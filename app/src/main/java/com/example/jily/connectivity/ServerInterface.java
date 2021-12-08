package com.example.jily.connectivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jily.BuildConfig;
import com.example.jily.model.Merchants;
import com.example.jily.model.Order;
import com.example.jily.model.Orders;
import com.example.jily.model.StdResponse;
import com.example.jily.model.User;
import com.example.jily.utility.DebugConstants;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerInterface {

    // Placeholder for server address:port pair to connect to backend
    private static final String SERVER_IP = "<YOUR IP HERE>";
    private static final String SERVER_PORT = "5000";
    private static volatile ServerInterface instance;

    private final int BAD_REQUEST = 400;
    private final int UNAUTHORIZED = 401;
    private final int FORBIDDEN = 403;
    private final int NOT_FOUND = 404;
    private final int UNPROCESSABLE = 422;
    private final int SERVER_ERROR = 500;

    private final ServerEndpoint server;
    private Handler mHandler;

    private ServerInterface() {
        server = getServerEndpoint();

        // Prevent forming the reflection server
        if (instance != null) {
            throw new ExceptionInInitializerError(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ServerInterface getInstance() {
        // Double check locking pattern
        if (instance == null) {                     // Check for the first time
            synchronized (ClientRetrofit.class) {   // Check for the second time
                // If there is no instance available create a new one
                if (instance == null) instance = new ServerInterface();
            }
        }
        return instance;
    }

    private static ServerEndpoint getServerEndpoint() {
        String baseUrl = "http://";
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            baseUrl += DebugConstants.SERVER_IP;
        } else {
            baseUrl += SERVER_IP;
        }
        baseUrl += (":" + SERVER_PORT + "/");
        ClientRetrofit.init(baseUrl);
        return ClientRetrofit.getInstance().createAdapter().create(ServerEndpoint.class);
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    //----------------------------------------------------------------------------------------------
    // STDRESPONSE HANDLER
    //----------------------------------------------------------------------------------------------
    private void stdResponse(Response<ResponseBody> response,
                             int responseType, int request, int reason) {
        Gson gson = new Gson();
        StdResponse error = null;
        try {
            assert response.errorBody() != null;
            error = gson.fromJson(response.errorBody().string(), StdResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message readMsg = mHandler.obtainMessage(responseType, request, reason, error);
        readMsg.sendToTarget();
    }

    //----------------------------------------------------------------------------------------------
    // AUTHENTICATION HANDLERS
    //----------------------------------------------------------------------------------------------
    public void login(User user) {
        server.login(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Message readMsg;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        User recvUser = gson.fromJson(response.body().string(), User.class);
                        User currentUser = RuntimeManager.getInstance().getCurrentUser();
                        currentUser.setAccessToken(recvUser.getAccessToken());
                        currentUser.setUserId(recvUser.getUserId());
                        currentUser.setUserType(recvUser.getUserType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Tell user we successfully created their details
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_LOGIN_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_SUCCESS);
                } else {
                    boolean bDoesUserExist = true;
                    boolean bPasswordIncorrect = false;
                    try {
                        assert response.errorBody() != null;
                        JSONObject responseJson = new JSONObject(response.errorBody().string());
                        JSONObject err = new JSONObject(responseJson.get("errors").toString());
                        if (err.get("username").equals(MessageConstants.USERNAME_NOT_REGISTERED)) {
                            bDoesUserExist = false;
                        } else if (err.get("password").equals(MessageConstants.PASSWORD_INCORRECT)) {
                            bPasswordIncorrect = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Tell user we encountered a failure
                    int messageRetCode = -1;
                    if (!bDoesUserExist) {
                        messageRetCode = MessageConstants.OPERATION_FAILURE_NOT_FOUND;
                    } else if (bPasswordIncorrect) {
                        messageRetCode = MessageConstants.OPERATION_FAILURE_UNAUTHORIZED;
                    } else {
                        messageRetCode = MessageConstants.OPERATION_FAILURE_UNPROCESSABLE;
                    }
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_LOGIN_RESPONSE,
                            MessageConstants.REQUEST_CREATE,
                            messageRetCode);
                }
                readMsg.sendToTarget();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("[ServerInterface] Login", t.getMessage());
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_LOGIN_RESPONSE,
                        MessageConstants.REQUEST_CREATE,
                        MessageConstants.OPERATION_FAILURE_SERVER_ERROR);
                readMsg.sendToTarget();
            }
        });
    }

    public void logout(User user) {
        server.logout(user.getUserId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Message readMsg;
                if (response.isSuccessful()) {
                    try {
                        User currentUser = RuntimeManager.getInstance().getCurrentUser();
                        currentUser.clearCurrentUser();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    // Tell user we successfully created their details
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_LOGOUT_RESPONSE,
                            MessageConstants.REQUEST_CREATE,
                            MessageConstants.OPERATION_SUCCESS);
                } else {
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_LOGOUT_RESPONSE,
                            MessageConstants.REQUEST_CREATE,
                            MessageConstants.OPERATION_FAILURE_UNPROCESSABLE);
                }
                readMsg.sendToTarget();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("[ServerInterface] Logout", t.getMessage());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // USER HANDLERS
    //----------------------------------------------------------------------------------------------
    public void createUser(User user) {
        server.createUser(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Message readMsg;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        User recvUser = gson.fromJson(response.body().string(), User.class);
                        User currentUser = RuntimeManager.getInstance().getCurrentUser();
                        currentUser.setAccessToken(recvUser.getAccessToken());
                        currentUser.setUserId(recvUser.getUserId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Tell user we successfully created their details
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_CREATE,
                            MessageConstants.OPERATION_SUCCESS);
                } else {
                    boolean bDoesUserExist = false;
                    try {
                        assert response.errorBody() != null;
                        JSONObject responseJson = new JSONObject(response.errorBody().string());
                        JSONObject err = new JSONObject(responseJson.get("errors").toString());
                        if (err.get("username").equals(MessageConstants.DUPLICATE_USER_ERR_STRING)) {
                            bDoesUserExist = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Tell user we encountered a failure
                    int messageRetCode = (bDoesUserExist) ?
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED :
                            MessageConstants.OPERATION_FAILURE_UNPROCESSABLE;
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_CREATE,
                            messageRetCode);
                }
                readMsg.sendToTarget();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("[ServerInterface] CreateUser", "Failure:" + t.getMessage());
            }
        });
    }

    public void getUserType(User user) {
        server.getUserType(user.getAccessToken(), user.getUserId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Message readMsg;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        User recvUser = gson.fromJson(response.body().string(), User.class);
                        User currentUser = RuntimeManager.getInstance().getCurrentUser();
                        currentUser.setUserType(recvUser.getUserType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Tell user we successfully created their details
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_SUCCESS);
                } else {
                    // Tell user we encountered a failure
                    readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_FAILURE_UNPROCESSABLE);
                }
                readMsg.sendToTarget();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("[ServerInterface] GetUserType", "Failure:" + t.getMessage());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // MERCHANT HANDLERS
    //----------------------------------------------------------------------------------------------
    public void getMerchants(User user) {
        server.getMerchants(user.getAccessToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Merchants merchants = gson.fromJson(response.body().string(), Merchants.class);
                        if (merchants.getMerchants().size() > 0) {
                            // Send merchants' details; a default message is already set up when
                            // there are no merchants as no explicit error is provided by the server
                            Message readMsg = mHandler.obtainMessage(
                                    MessageConstants.MESSAGE_MERCHANT_RESPONSE,
                                    MessageConstants.REQUEST_GET,
                                    MessageConstants.OPERATION_SUCCESS,
                                    merchants);
                            readMsg.sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] GetMerchants", "Response:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("[ServerInterface] GetMerchants", "Failure:" + t.getMessage());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // ORDER HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints
    public void createOrder(User user, Order order) {
        server.createOrder(user.getAccessToken(), order).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Order order = gson.fromJson(response.body().string(), Order.class);
                        // Create a new order
                        Message readMsg = mHandler.obtainMessage(
                                MessageConstants.MESSAGE_ORDER_RESPONSE,
                                MessageConstants.REQUEST_CREATE,
                                MessageConstants.OPERATION_SUCCESS,
                                order);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == BAD_REQUEST) {
                    // Tell user their order was not created properly
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_CREATE,
                            MessageConstants.OPERATION_FAILURE_BAD_REQUEST);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] CreateOrder", "Response:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("[ServerInterface] CreateOrder", "Failure:" + t.getMessage());
            }
        });
    }

    public void getOrders(User user) {
        server.getOrders(user.getAccessToken(), user.getUserId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Orders orders = gson.fromJson(response.body().string(), Orders.class);
                        if (orders.getOrders().size() > 0) {
                            // Send orders' details; a default message is already set up when
                            // there are no orders as no explicit error is provided by the server
                            Message readMsg = mHandler.obtainMessage(
                                    MessageConstants.MESSAGE_ORDER_RESPONSE,
                                    MessageConstants.REQUEST_GET,
                                    MessageConstants.OPERATION_SUCCESS,
                                    orders);
                            readMsg.sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] GetOrders", "Response:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("[ServerInterface] GetOrders", "Failure:" + t.getMessage());
            }
        });
    }

    public void updateFirebaseToken(User user, String firebaseToken) {
        user.setFirebaseToken(firebaseToken);
        server.updateFirebaseToken(user.getAccessToken(), user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Message readMsg;
                int messageRetCode = -1;
                if (response.isSuccessful()) {
                    // Tell user their details were updated
                    messageRetCode = MessageConstants.OPERATION_SUCCESS;
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] Update Firebase token", response.errorBody().string());
                        messageRetCode = MessageConstants.OPERATION_FAILURE_UNPROCESSABLE;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_LOGIN_RESPONSE,
                        MessageConstants.REQUEST_CREATE,
                        messageRetCode);
                readMsg.sendToTarget();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("[ServerInterface] UpdateFirebaseToken", "Failure:" + t.getMessage());
            }
        });
    }

    public void getOrderSecret(User user, Order order) {
        server.getOrderSecret(user.getAccessToken(), order.getOrderId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Order order = gson.fromJson(response.body().string(), Order.class);
                        Message readMsg = mHandler.obtainMessage(
                                MessageConstants.MESSAGE_ORDER_RESPONSE,
                                MessageConstants.REQUEST_GET,
                                MessageConstants.OPERATION_SUCCESS,
                                order);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == BAD_REQUEST) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_FAILURE_BAD_REQUEST);
                } else if (response.code() == UNAUTHORIZED) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED);
                } else if (response.code() == NOT_FOUND) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_FAILURE_NOT_FOUND);
                } else if (response.code() == SERVER_ERROR) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_FAILURE_SERVER_ERROR);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] GetQrCode", "Response:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("[ServerInterface] GetQrCode", "Failure:" + t.getMessage());
            }
        });
    }
}
