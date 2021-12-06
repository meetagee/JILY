package com.example.jily.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jily.model.Order;

import java.util.ArrayList;

public class OrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Order>> mList;

    public OrdersViewModel() {
        mText = new MutableLiveData<>();

        // TODO: Perform a GET request and populate orders with server's response
        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Order order = new Order("Order " + i, "Ready for pickup");
            orders.add(order);
        }

        mList = new MutableLiveData<>(orders);
        if (orders.size() > 0) {
            mText.setValue("");
        }
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<Order>> getList() {
        return mList;
    }
}