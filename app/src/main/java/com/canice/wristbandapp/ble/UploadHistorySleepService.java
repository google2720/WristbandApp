package com.canice.wristbandapp.ble;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.ble.db.BleDao;
import com.canice.wristbandapp.ble.db.BleDao.SleepData;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HttpUtil;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class UploadHistorySleepService extends IntentService {

    private static final String TAG = "yy";

    public UploadHistorySleepService() {
        super("UploadHistorySleepService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            upload();
        } catch (Throwable e) {
            Log.w(TAG, "", e);
        }

    }

    private void upload() {
        String address = BleController.getInstance().getBindedDeviceAddress();
        if (TextUtils.isEmpty(address)) {
            return;
        }
        List<SleepData> datas = BleDao.getHistorySleep(this, address);
        if (datas == null || datas.isEmpty()) {
            return;
        }
        String id = UserController.getUserId(this);
        if (TextUtils.isEmpty(id)) {
            return;
        }
        final CountDownLatch latch = new CountDownLatch(1);
        RequestParams params = new RequestParams();
        params.put(Constants.ID, id);
        params.put(Constants.SLEEP_INFOS, new Gson().toJson(datas));
        String url = Constants.SERVER_UPLOAD_SLEEP_DATA;
        HttpUtil.post(url, params, new BackgroudHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error) {
                Log.w(TAG, "failed to upload history sleep data", error);
                latch.countDown();
            }

            @Override
            public void onSuccess(String content) {
                Log.i(TAG, "success to upload history sleep data " + content);
                latch.countDown();
            }
        });
        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }
}
