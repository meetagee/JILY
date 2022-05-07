package com.example.jily.ui.merchants;

import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jily.connectivity.JilyFirebaseMessagingService;
import com.example.jily.connectivity.MessageConstants;
import com.example.jily.connectivity.RuntimeManager;
import com.example.jily.connectivity.ServerInterface;
import com.example.jily.model.Merchants;
import com.example.jily.model.User;

import java.util.ArrayList;

public class MerchantsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<User>> mList;

    private ServerInterface mServerIf;
    private Handler mHandler;

    public MerchantsViewModel() {
        mText = new MutableLiveData<>();
        mServerIf = ServerInterface.getInstance();
        mHandler = new MerchantsHandler();
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

    private class MerchantsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == MessageConstants.OPERATION_SUCCESS) {
                Merchants merchants = (Merchants) msg.obj;
                ArrayList<User> listOfMerchants = new ArrayList<>(merchants.getMerchants());
                mList.postValue(listOfMerchants);
                mText.postValue("");
                User currentUser = RuntimeManager.getInstance().getCurrentUser();
                if (!currentUser.isFirebaseTokenInitialized()) {
                    JilyFirebaseMessagingService.getInstance().updateFirebaseToken();
                    currentUser.setFirebaseTokenInitialized(true);
                }
            }
        }
    }
}
