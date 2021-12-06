package com.example.jily.connectivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jily.model.Id;
import com.example.jily.model.Jily;
import com.example.jily.model.StdResponse;
import com.example.jily.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerInterface {

    // Placeholder for server address:port pair to connect to backend
    private static final String DEBUG_IP = "192.168.10.102";
    private static final String DEBUG_PORT = "5000";
    private static final String BASE_URL = "http://" + DEBUG_IP + ":" + DEBUG_PORT + "/";
    private static volatile ServerInterface instance;
    private final int UNAUTHORIZED = 401;
    private final int FORBIDDEN = 403;
    private final int NOT_FOUND = 404;
    private final int UNPROCESSABLE = 422;
    private final ServerEndpoint server;
    public int userId, profileId;
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
        ClientRetrofit.init(BASE_URL);
        return ClientRetrofit.getInstance().createAdapter().create(ServerEndpoint.class);
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    //----------------------------------------------------------------------------------------------
    // STDRESPONSE HANDLER (TODO: Placeholders for now)
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
    public void login(String username, String password) {
        server.login(username, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Save IDs
                        Gson gson = new Gson();
                        assert response.body() != null;
                        Id id = gson.fromJson(response.body().string(), Id.class);
                        userId = id.getUserId();
                        profileId = id.getProfileId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Tell user we successfully logged in
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_LOGIN_RESPONSE,
                            0,
                            MessageConstants.OPERATION_SUCCESS);
                    readMsg.sendToTarget();
                } else if (response.code() == UNAUTHORIZED) {
                    // Tell user login was not successful
                    stdResponse(response,
                            MessageConstants.MESSAGE_LOGIN_RESPONSE,
                            0,
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED);
                } else {
                    // Tell user that no account was associated with those credentials
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_LOGIN_RESPONSE,
                            0,
                            MessageConstants.OPERATION_FAILURE_SERVER_ERROR);
                    readMsg.sendToTarget();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Login Error", t.getMessage());
            }
        });
    }

    public void logout() {
        server.logout().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Tell user we successfully logged out
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_LOGOUT_RESPONSE,
                            0,
                            MessageConstants.OPERATION_SUCCESS);
                    readMsg.sendToTarget();
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("Logout Response", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Logout Error", t.getMessage());
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
                        User user = gson.fromJson(response.body().string(), User.class);
                        RuntimeManager.getInstance().getCurrentUser().setAccessToken(user.getAccessToken());
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
                Log.e("[ServerInterface] CreateUser:", "Failure:" + t.getMessage());
            }
        });
    }

    public void deleteUser(Integer user_id) {
        server.deleteUser(user_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Tell user we successfully deleted their account
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_DELETE,
                            MessageConstants.OPERATION_SUCCESS);
                    readMsg.sendToTarget();
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("Delete User Response", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Delete User Error", t.getMessage());
            }
        });
    }

    public void getUser(Integer user_id) {
        server.getUser(user_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Gson gson = new Gson();
                        Type fields = new TypeToken<List<Jily<User>>>() {
                        }.getType();
                        assert response.body() != null;
                        List<Jily<User>> user = gson.fromJson(response.body().string(), fields);
                        // Send user details
                        Message readMsg = mHandler.obtainMessage(
                                MessageConstants.MESSAGE_USER_RESPONSE,
                                MessageConstants.REQUEST_GET,
                                MessageConstants.OPERATION_SUCCESS,
                                user);
                        readMsg.sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == NOT_FOUND) {
                    // Tell user their details were not found
                    stdResponse(response,
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_GET,
                            MessageConstants.OPERATION_FAILURE_NOT_FOUND);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("User Response", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }

    public void updateUser(Integer user_id, List<Jily<User>> user) {
        server.updateUser(user_id, user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Tell user their details were updated
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_SUCCESS);
                    readMsg.sendToTarget();
                } else if (response.code() == UNAUTHORIZED) {
                    // Tell user updating was unsuccessful
                    stdResponse(response,
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_UNAUTHORIZED);
                } else if (response.code() == UNPROCESSABLE) {
                    // Tell user they cannot change their username
                    stdResponse(response,
                            MessageConstants.MESSAGE_USER_RESPONSE,
                            MessageConstants.REQUEST_UPDATE,
                            MessageConstants.OPERATION_FAILURE_UNPROCESSABLE);
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("Update User Response", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Update User Error", t.getMessage());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // RESTAURANT HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints

    //----------------------------------------------------------------------------------------------
    // ORDER HANDLERS
    //----------------------------------------------------------------------------------------------
    // TODO: Specify endpoints
}

