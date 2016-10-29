package com.canice.wristbandapp.ble;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.CreateDeviceId;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HttpUtil;

import android.util.Log;

public class HttpController {

    private static final String TAG = "yy";
    private static HttpController sInstance = new HttpController();

    public static final HttpController getInstance() {
        return sInstance;
    }

    public String createDeviceId() {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] vs = new String[1];
        HttpUtil.get(Constants.SERVER_CREATE_EXDEVICE_ID, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                Log.d(TAG, "http onSuccess " + content);
                vs[0] = content;
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable error) {
                Log.w(TAG, "http onFailure", error);
                latch.countDown();
            }
        });
        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        if (vs[0] != null) {
            CreateDeviceId info = JSON.parseObject(vs[0], CreateDeviceId.class);
            if (info != null && info.exDeviceId != null) {
                return info.exDeviceId;
            }
        }
        return null;
    }

    public void bindDeviceAsync(String userId, String address, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(Constants.ID, userId);
        params.put(Constants.EXDEVICEID, address);
        HttpUtil.get(Constants.SERVER_BIND_DIVICE, params, handler);
    }
}