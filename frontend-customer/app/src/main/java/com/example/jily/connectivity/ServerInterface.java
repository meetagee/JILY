package com.example.jily.connectivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jily.BuildConfig;
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
import com.example.jily.model.Merchants;
=======
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
import com.example.jily.model.Order;
import com.example.jily.model.Orders;
import com.example.jily.model.Secret;
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
                int messageRetCode = -1;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    messageRetCode = MessageConstants.OPERATION_SUCCESS;
                    try {
                        assert response.body() != null;
                        User recvUser = gson.fromJson(response.body().string(), User.class);
                        User currentUser = RuntimeManager.getInstance().getCurrentUser();
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                        if (User.UserType.fromString(recvUser.getUserType()) != User.UserType.Customer) {
=======
                        if (User.UserType.fromString(recvUser.getUserType()) != User.UserType.Merchant) {
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                            messageRetCode = MessageConstants.OPERATION_FAILURE_INCOMPATIBLE_UI;
                        } else {
                            currentUser.setAccessToken(recvUser.getAccessToken());
                            currentUser.setUserId(recvUser.getUserId());
                            currentUser.setUserType(recvUser.getUserType());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    if (!bDoesUserExist) {
                        messageRetCode = MessageConstants.OPERATION_FAILURE_NOT_FOUND;
                    } else if (bPasswordIncorrect) {
                        messageRetCode = MessageConstants.OPERATION_FAILURE_UNAUTHORIZED;
                    } else {
                        messageRetCode = MessageConstants.OPERATION_FAILURE_UNPROCESSABLE;
                    }
                }
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_LOGIN_RESPONSE,
                        MessageConstants.REQUEST_GET,
                        messageRetCode);
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
                int messageRetCode = -1;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        messageRetCode = MessageConstants.OPERATION_SUCCESS;
                        User recvUser = gson.fromJson(response.body().string(), User.class);
                        User currentUser = RuntimeManager.getInstance().getCurrentUser();
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                        if (User.UserType.fromString(recvUser.getUserType()) != User.UserType.Customer) {
=======
                        if (User.UserType.fromString(recvUser.getUserType()) != User.UserType.Merchant) {
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                            messageRetCode = MessageConstants.OPERATION_FAILURE_INCOMPATIBLE_UI;
                        } else {
                            currentUser.setAccessToken(recvUser.getAccessToken());
                            currentUser.setUserId(recvUser.getUserId());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    messageRetCode = (bDoesUserExist) ?
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED :
                            MessageConstants.OPERATION_FAILURE_UNPROCESSABLE;
                }
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_USER_RESPONSE,
                        MessageConstants.REQUEST_CREATE,
                        messageRetCode);
                readMsg.sendToTarget();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("[ServerInterface] CreateUser", "Failure:" + t.getMessage());
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_USER_RESPONSE,
                        MessageConstants.REQUEST_CREATE,
                        MessageConstants.OPERATION_FAILURE_SERVER_ERROR);
                readMsg.sendToTarget();
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
    // ORDER HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints
    public void getOrders(User user) {
        server.getOrders(user.getAccessToken(), user.getUserId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
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
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                Log.e("[ServerInterface] GetMerchants", "Failure:" + t.getMessage());
=======
                Log.e("[ServerInterface] GetOrders", "Failure:" + t.getMessage());
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
            }
        });
    }

    public void confirmOrder(User user, Order order) {
        server.confirmOrder(user.getAccessToken(), order.getOrderId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Order order = gson.fromJson(response.body().string(), Order.class);
                        Message readMsg = mHandler.obtainMessage(
                                MessageConstants.MESSAGE_ORDER_RESPONSE,
                                MessageConstants.REQUEST_UPDATE,
                                MessageConstants.OPERATION_SUCCESS,
                                order);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == BAD_REQUEST) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_BAD_REQUEST);
                } else if (response.code() == UNAUTHORIZED) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED);
                } else if (response.code() == NOT_FOUND) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_NOT_FOUND);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] ConfirmOrder", "Response:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                Log.e("[ServerInterface] CreateOrder", "Failure:" + t.getMessage());
=======
                Log.e("[ServerInterface] ConfirmOrder", "Failure:" + t.getMessage());
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
            }
        });
    }

    public void readyOrder(User user, Order order) {
        server.readyOrder(user.getAccessToken(), order.getOrderId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Order order = gson.fromJson(response.body().string(), Order.class);
                        Message readMsg = mHandler.obtainMessage(
                                MessageConstants.MESSAGE_ORDER_RESPONSE,
                                MessageConstants.REQUEST_UPDATE,
                                MessageConstants.OPERATION_SUCCESS,
                                order);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == BAD_REQUEST) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_BAD_REQUEST);
                } else if (response.code() == UNAUTHORIZED) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED);
                } else if (response.code() == NOT_FOUND) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_NOT_FOUND);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] ReadyOrder", "Response:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                Log.e("[ServerInterface] GetOrders", "Failure:" + t.getMessage());
            }
        });
    }
  
    public void getOrderById(User user, Order order) {
        server.getOrderById(user.getAccessToken(), order.getOrderId()).enqueue(new Callback<ResponseBody>() {
=======
                Log.e("[ServerInterface] ReadyOrder", "Failure:" + t.getMessage());
            }
        });
    }

    public void completeOrder(User user, Order order, Secret secret) {
        server.completeOrder(user.getAccessToken(), order.getOrderId(), secret).enqueue(new Callback<ResponseBody>() {
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Order order = gson.fromJson(response.body().string(), Order.class);
                        Message readMsg = mHandler.obtainMessage(
                                MessageConstants.MESSAGE_ORDER_RESPONSE,
                                MessageConstants.REQUEST_UPDATE,
                                MessageConstants.OPERATION_SUCCESS,
                                order);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == BAD_REQUEST) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_BAD_REQUEST);
                } else if (response.code() == UNAUTHORIZED) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED);
                } else if (response.code() == NOT_FOUND) {
                    stdResponse(response,
                            MessageConstants.MESSAGE_ORDER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_NOT_FOUND);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("[ServerInterface] CompleteOrder", "Response:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("[ServerInterface] CompleteOrder", "Failure:" + t.getMessage());
            }
        });
    }

    public void updateFirebaseToken(User user, String firebaseToken) {
        user.setFirebaseToken(firebaseToken);
        server.updateFirebaseToken(user.getAccessToken(), user).enqueue(new Callback<ResponseBody>() {
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
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
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
=======
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Message readMsg;
                int messageRetCode = -1;
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
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
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
<<<<<<< HEAD:frontend-customer/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
                Log.e("[ServerInterface] GetQrCode", "Failure:" + t.getMessage());
=======
                Log.e("[ServerInterface] UpdateFirebaseToken", "Failure:" + t.getMessage());
>>>>>>> frontend-merchant:frontend-merchant/app/src/main/java/com/example/jily/connectivity/ServerInterface.java
            }
        });
    }
}