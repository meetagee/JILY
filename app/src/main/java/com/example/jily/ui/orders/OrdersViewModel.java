package com.example.jily.ui.orders;

import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.Order;
import com.example.jily.model.Orders;
import com.example.jily.model.User;

import java.util.ArrayList;

public class OrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Order>> mList;

    private ServerInterface mServerIf;
    private Handler mHandler;

    public OrdersViewModel() {
        mText = new MutableLiveData<>();
        mServerIf = ServerInterface.getInstance();
        mHandler = new OrdersViewModel.OrdersHandler();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<Order>> getList() {
        if (mList == null) {
            mList = new MutableLiveData<>();
            User currentUser = RuntimeManager.getInstance().getCurrentUser();
            mServerIf.setHandler(mHandler);
            mServerIf.getOrders(currentUser);
        }
        return mList;
    }

    private class OrdersHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == MessageConstants.OPERATION_SUCCESS) {
                Orders orders = (Orders) msg.obj;
                ArrayList<Order> listOfOrders = new ArrayList<>(orders.getOrders());
                mList.postValue(listOfOrders);
                mText.postValue("");
            }
        }
    }
}