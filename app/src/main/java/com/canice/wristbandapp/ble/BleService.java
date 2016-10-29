package com.canice.wristbandapp.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.canice.wristbandapp.BuildConfig;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.SmsReceivedService;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.ble.data.HistoryResult;
import com.canice.wristbandapp.ble.db.BleDao;
import com.canice.wristbandapp.util.Tools;

public class BleService extends Service {

    private static final String TAG = "BleService";

    private ServiceHandler mHandler;
    private BleController mBle;
    private long lastSosTime;
    private BleCallback mCallback = new SimpleBleCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device) {
            String address = device.getAddress();
            if (mBle.isAutoConnect() && address.equals(mBle.getBindedDeviceAddress())) {
                mBle.connect(device);
            }
        }

        @Override
        public void onGattConnected(BluetoothDevice device) {
            mBle.stopLeScan();
        }

        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            mHandler.removeMessages(MSG_SYNC_CURRENT_DATA);
            mHandler.removeMessages(MSG_SYNC_HISTORY_DATA);
            if (mBle.isAutoReconnect()) {
                mHandler.sendEmptyMessage(MSG_SCAN);
            }
        }

        @Override
        public void onGattServicesDiscovered(BluetoothDevice device) {
            String address = device.getAddress();
            if (mBle.isAutoConnect() && address.equals(mBle.getBindedDeviceAddress())) {
                mBle.bindDeviceAsync();
            }
        }

        @Override
        public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBinded) {
            if (firstBinded || mBle.isFailedBindedDevice(device.getAddress())) {
                String userId = UserController.getUserId(getApplicationContext());
                mBle.bindDeviceAsync(userId, device.getAddress());
            }
            mBle.syncBaseDataAsync(false);
            // mBle.fetchVersionInfoAsync();
            mHandler.sendEmptyMessage(MSG_SYNC_CURRENT_DATA);
            if (firstBinded) {
                mHandler.removeMessages(MSG_SYNC_HISTORY_DATA);
                if (BuildConfig.newFit){
                    mHandler.sendEmptyMessage(MSG_SYNC_HISTORY_DATA);
                }
            }
            if (UserController.isSmsRemind(getApplicationContext())) {
                SmsReceivedService.actionStart(getApplicationContext());
            }
            if (UserController.isSosEnable(getApplicationContext())) {
                mBle.setSosEnableAsync(true);
            }
            if (UserController.isAntiLostEnable(getApplicationContext())) {
                mBle.antiLoseAsync();
            }
        }

        @Override
        public void onFetchHistory(String address, HistoryResult result) {
            Log.i(TAG, "history:" + result);
            BleDao.saveHistory(getApplicationContext(), address, result);
            Log.i(TAG, "success to save history info to db");
            Log.i(TAG, "h time:" + result.getTime());
            Log.i(TAG, "h time2:" + Tools.changeDeviceTime2PhoneTime(result.getTime()));
        }

        @Override
        public void onFetchHistorySuccess() {
            Intent service = new Intent(BleService.this, UploadHistoryStepService.class);
            startService(service);

            service = new Intent(BleService.this, UploadHistorySleepService.class);
            startService(service);
        }

        @Override
        public void onRemoteRssi(BluetoothDevice device, int rssi) {
            if (UserController.isAntiLostEnable(getApplicationContext())) {
                int v = UserController.getAntiLostValue(getApplicationContext());
                if (rssi <= v) {
                    mBle.antiAlarmAsync();
                }
            } else {
                mBle.closeAntiLoseAsync();
            }
        }

        @Override
        public void onSosNotify(byte[] data) {
            if (UserController.isSosEnable(getApplicationContext())) {
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - lastSosTime > 10 * 1000) {
                    lastSosTime = currentTime;
                    if (UserController.isSosBySms(getApplicationContext())) {
                        Tools.sendSMS(getApplicationContext(), UserController.getSosPhone(getApplicationContext()), getResources().getString(R.string.sos_sms_body));
                    } else {
                        Tools.callPhone(getApplicationContext(), UserController.getSosPhone(getApplicationContext()));
                    }
                }
            }
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "onReceive " + action);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                Log.i(TAG, "bluetooth state " + state);
                if (state == BluetoothAdapter.STATE_OFF) {
                    mBle.disconnect();
                    mBle.getCallbacks().onBluetoothOff();
                    mHandler.sendEmptyMessage(MSG_STOP_SCAN);
                } else if (state == BluetoothAdapter.STATE_ON) {
                    mHandler.sendEmptyMessage(MSG_SCAN);
                }
            }
        }
    };

    private static final int MSG_INIT = 0;
    private static final int MSG_SCAN = 1;
    private static final int MSG_STOP_SCAN = 2;
    private static final int MSG_SYNC_CURRENT_DATA = 3;
    private static final int MSG_SYNC_HISTORY_DATA = 4;

    private class ServiceHandler extends Handler {

        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage " + msg.what);
            switch (msg.what) {
                case MSG_INIT:
                    String address = mBle.getBindedDeviceAddress();
                    Log.i(TAG, "bind device address " + address);
                    if (!TextUtils.isEmpty(address)) {
                        sendEmptyMessage(MSG_SCAN);
                    }
                    break;
                case MSG_SCAN:
                    if (mBle.isEnabled()) {
                        mBle.startLeScan();
                    }
                    break;
                case MSG_STOP_SCAN:
                    mBle.stopLeScan();
                    break;
                case MSG_SYNC_CURRENT_DATA:
                    mBle.fetchPedometerDataAsync();
                    break;
                case MSG_SYNC_HISTORY_DATA:
                    mBle.fetchHistoryAsync();
                    if (mBle.isDeviceReady()) {
                        sendEmptyMessageDelayed(MSG_SYNC_HISTORY_DATA, 6 * 60 * 60 * 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BleService.class);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mHandler = new ServiceHandler(Looper.getMainLooper());
        mBle = BleController.getInstance();
        mBle.initialize(this);
        mBle.addCallback(mCallback);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(getText(R.string.app_name));
        builder.setContentText("正在连接");
//        startForeground(R.string.app_name, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand " + startId);
        mHandler.sendEmptyMessage(MSG_INIT);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        unregisterReceiver(receiver);
        mBle.removeCallback(mCallback);
    }
}