package com.example.jily.ui.restaurants;

import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.Restaurant;
import com.example.jily.model.User;

import java.util.ArrayList;

public class RestaurantsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<User>> mList;

    private ServerInterface mServerIf;
    private Handler mHandler;

    public RestaurantsViewModel() {
        mText = new MutableLiveData<>();
        mServerIf = ServerInterface.getInstance();
        mHandler = new RestaurantHandler();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<User>> getList() {
        if (mList == null) {
            mList = new MutableLiveData<>();
            User currentUser = RuntimeManager.getInstance().getCurrentUser();
            mServerIf.setHandler(mHandler);
            mServerIf.getMerchants(currentUser);
        }
        return mList;
    }

    private class RestaurantHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == MessageConstants.OPERATION_SUCCESS) {
                Restaurant restaurant = (Restaurant) msg.obj;
                ArrayList<User> merchants = new ArrayList<>(restaurant.getMerchants());
                mList.postValue(merchants);
                mText.setValue("");
            }
        }
    }
}
