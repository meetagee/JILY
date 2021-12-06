package com.example.jily.connectivity;

public interface MessageConstants {

    // Constants defining what type of response the server returned
    int MESSAGE_LOGIN_RESPONSE   = 0;
    int MESSAGE_LOGOUT_RESPONSE  = 1;
    int MESSAGE_USER_RESPONSE    = 2;

    // Constants defining the HTTP method requested
    int REQUEST_CREATE = 0;
    int REQUEST_DELETE = 1;
    int REQUEST_GET    = 2;
    int REQUEST_UPDATE = 3;

    // Constants defining the permission requested
    int PERMIT_AUDIO_RECORD          = 0;
    int PERMIT_AUDIO_DELETE          = 1;
    int PERMIT_AUDIO_RECEIVE         = 2;
    int PERMIT_BACKGROUND_LOCATION   = 3;
    int PERMIT_ENABLE_BLUETOOTH      = 4; // Must be greater than 0
    int PERMIT_READ_EXTERNAL_STORAGE = 5;
    int PERMIT_SIGN_IN               = 6;

    // Constants defining whether or not a request with the server was successful
    int OPERATION_SUCCESS               = 0;
    int OPERATION_FAILURE_UNAUTHORIZED  = 1;
    int OPERATION_FAILURE_NOT_FOUND     = 2;
    int OPERATION_FAILURE_UNPROCESSABLE = 3;
    int OPERATION_FAILURE_SERVER_ERROR  = 4;

    String DUPLICATE_USER_ERR_STRING = "The username already exists!";
    String USERNAME_NOT_REGISTERED = "The username has not been registered!";
    String PASSWORD_INCORRECT = "The password is incorrect!";
}

