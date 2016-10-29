package com.canice.wristbandapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneReceiver extends BroadcastReceiver {

    private static final String TAG = "yy";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive:" + intent.getAction());
        Intent service = new Intent(context, PhoneService.class);
        context.startService(service);
    }
}