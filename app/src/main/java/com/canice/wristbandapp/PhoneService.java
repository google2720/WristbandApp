package com.canice.wristbandapp;

import com.canice.wristbandapp.ble.BleController;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class PhoneService extends Service {

    private static final String TAG = "yy";

    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d(TAG, "onCallStateChanged:" + state + " " + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    boolean a = UserController.isCallRemind(getApplicationContext());
                    Log.i(TAG, "CallRemind " + (a ? "enabled" : "disabled"));
                    if (a && !TextUtils.isEmpty(incomingNumber)) {
                        BleController.getInstance().sendCallRemindAsync(incomingNumber);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}