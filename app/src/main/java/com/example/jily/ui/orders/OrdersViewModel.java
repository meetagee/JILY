package com.example.jily.ui.orders;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jily.connectivity.ServerInterface;

import java.util.Arrays;
import java.util.List;

public class OrdersViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<String>> mList;
    private ServerInterface mServerIf;
    private Handler mHandler;

    public OrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");

        List<String> resList = Arrays.asList("Order 1", "Order 2", "Order 3");
        mList = new MutableLiveData<List<String>>(resList);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<String>> getList() {
        return mList;
    }
}