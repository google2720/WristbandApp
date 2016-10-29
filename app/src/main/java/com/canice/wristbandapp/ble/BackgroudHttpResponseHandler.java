package com.canice.wristbandapp.ble;

import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;

import android.os.Message;

public class BackgroudHttpResponseHandler extends AsyncHttpResponseHandler {
    
    @Override
    protected void sendMessage(Message msg) {
        handleMessage(msg);
    }

    @Override
    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = Message.obtain();
        msg.what = responseMessage;
        msg.obj = response;
        return msg;
    }
}
