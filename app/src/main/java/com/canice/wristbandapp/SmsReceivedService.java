package com.canice.wristbandapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.canice.wristbandapp.ble.BleController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 短信接收服务
 *
 * @author y
 */
public class SmsReceivedService extends Service {

    private static final String TAG = "SmsReceivedService";
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private ExecutorService executor;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (ACTION_SMS_RECEIVED.equals(intent.getAction())) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parser(intent);
                        } catch (Throwable e) {
                            Log.e(TAG, "failed to parser message.", e);
                        }
                    }
                });
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, this + " onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, this + " onCreate");
        executor = Executors.newFixedThreadPool(1);
        IntentFilter filter = new IntentFilter(ACTION_SMS_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, this + " onDestroy");
        unregisterReceiver(receiver);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SmsReceivedService.class);
        context.startService(intent);
    }

    /**
     * 解析接收的短信
     */
    private void parser(final Intent intent) {
        if(UserController.isSmsRemind(getApplicationContext())) {
            SmsMessage[] messages = getMessagesFromIntent(intent);
            for (SmsMessage message : messages) {
                String address = message.getOriginatingAddress();
                BleController.getInstance().sendMessageRemindAsync(address);
            }
        }
    }

    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }
}