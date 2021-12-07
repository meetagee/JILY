package com.example.jily.connectivity;

public interface MessageConstants {

    // Constants defining what type of response the server returned
    int MESSAGE_LOGIN_RESPONSE      = 0;
    int MESSAGE_LOGOUT_RESPONSE     = 1;
    int MESSAGE_USER_RESPONSE       = 2;
    int MESSAGE_MERCHANT_RESPONSE = 3;
    int MESSAGE_ORDER_RESPONSE      = 4;

    // Constants defining the HTTP method requested
    int REQUEST_CREATE = 0;
    int REQUEST_GET    = 1;

    // Constants defining whether or not a request with the server was successful
    int OPERATION_SUCCESS               = 0;
    int OPERATION_FAILURE_BAD_REQUEST   = 1;
    int OPERATION_FAILURE_UNAUTHORIZED  = 2;
    int OPERATION_FAILURE_NOT_FOUND     = 3;
    int OPERATION_FAILURE_UNPROCESSABLE = 4;
    int OPERATION_FAILURE_SERVER_ERROR  = 5;

    String DUPLICATE_USER_ERR_STRING = "The username already exists!";
    String USERNAME_NOT_REGISTERED = "The username has not been registered!";
    String PASSWORD_INCORRECT = "The password is incorrect!";
}

