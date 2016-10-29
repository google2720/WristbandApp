package com.canice.wristbandapp;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.canice.wristbandapp.ble.BleController;

import java.util.Set;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "NotificationService";

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Log.i(TAG, "onNotificationPosted packageName:" + packageName);
        if ("com.tencent.mm".equals(packageName)) {
            if (UserController.isWeChatRemind(this)) {
                BleController.getInstance().sendWeChatRemindAsync();
            }
        } else if ("com.tencent.mobileqq".equals(packageName)) {
            if (UserController.isQQRemind(this)) {
                BleController.getInstance().sendQQRemindAsync();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Log.i(TAG, "onNotificationRemoved packageName:" + packageName);
    }

    public static boolean isEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return packageNames.contains(context.getPackageName());
    }
}
