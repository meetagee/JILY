package com.example.jily.ui.restaurants;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.Restaurant;

import java.util.ArrayList;

public class RestaurantsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Restaurant>> mList;
    private ServerInterface mServerIf;
    private Handler mHandler;

    public RestaurantsViewModel() {
        mText = new MutableLiveData<>();
        mServerIf = ServerInterface.getInstance();
        mHandler = new Handler();

        boolean bMerchantsExist = false;
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        try {
            mServerIf.setHandler(mHandler);
            mServerIf.getMerchants();
            Message recvMessage = mHandler.obtainMessage();
            Log.d("RestaurantsViewModel", recvMessage.toString());
            /**
             * TODO: Check if message has a `merchants` field in the response, if not, don't
             * populate mList; else bMerchantsExist = true
             **/
        } catch (Exception e) {
            Log.e("RestaurantsViewModel", e.toString());
        }

        mList = new MutableLiveData<>(restaurants);
        if (bMerchantsExist) {
            mText.setValue("");
        }
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<Restaurant>> getList() {
        return mList;
    }
}