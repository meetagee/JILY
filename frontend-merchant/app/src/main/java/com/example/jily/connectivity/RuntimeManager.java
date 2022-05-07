package com.example.jily.connectivity;

import com.example.jily.model.User;

public class RuntimeManager {
    private static volatile RuntimeManager mInstance;
    private static User mCurrentUser;

    public RuntimeManager() {
        // Prevent forming the reflection server
        if (mInstance != null) {
            throw new ExceptionInInitializerError(
                    "Use getInstance() method to get the single instance of this class.");
        }

        mCurrentUser = new User();
    }

    public static RuntimeManager getInstance() {
        // Double check locking pattern
        if (mInstance == null) {                     // Check for the first time
            synchronized (RuntimeManager.class) {   // Check for the second time
                // If there is no instance available create a new one
                if (mInstance == null) mInstance = new RuntimeManager();
            }
        }
        return mInstance;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(User user) {
        mCurrentUser = user;
    }
}
