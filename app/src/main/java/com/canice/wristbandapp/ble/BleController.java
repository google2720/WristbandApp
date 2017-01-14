package com.canice.wristbandapp.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.canice.wristbandapp.BuildConfig;
import com.canice.wristbandapp.SleepTime;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.ble.data.AntiAlarm;
import com.canice.wristbandapp.ble.data.AntiLost;
import com.canice.wristbandapp.ble.data.BaseData;
import com.canice.wristbandapp.ble.data.Bind;
import com.canice.wristbandapp.ble.data.BindQuery;
import com.canice.wristbandapp.ble.data.BindQueryResult;
import com.canice.wristbandapp.ble.data.BindResult;
import com.canice.wristbandapp.ble.data.CloseHeartRateData;
import com.canice.wristbandapp.ble.data.Data;
import com.canice.wristbandapp.ble.data.HeartRateDataResult;
import com.canice.wristbandapp.ble.data.History;
import com.canice.wristbandapp.ble.data.History2;
import com.canice.wristbandapp.ble.data.HistoryResult;
import com.canice.wristbandapp.ble.data.MessageRemind;
import com.canice.wristbandapp.ble.data.OpenHeartRateData;
import com.canice.wristbandapp.ble.data.OpenRateDataResult;
import com.canice.wristbandapp.ble.data.PedometerData;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.canice.wristbandapp.ble.data.QQRemind;
import com.canice.wristbandapp.ble.data.Remind;
import com.canice.wristbandapp.ble.data.RemindResult;
import com.canice.wristbandapp.ble.data.SystemDate;
import com.canice.wristbandapp.ble.data.VersionInfo;
import com.canice.wristbandapp.ble.data.VersionInfoResult;
import com.canice.wristbandapp.ble.data.WeChatRemind;
import com.canice.wristbandapp.ble.db.BleDao;
import com.canice.wristbandapp.ble.scanner.BleScanner;
import com.canice.wristbandapp.ble.scanner.BleScannerHelper;
import com.canice.wristbandapp.clock.Clock;
import com.canice.wristbandapp.clock.ClockController;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.remind.RemindController;
import com.canice.wristbandapp.util.Tools;
import com.github.yzeaho.log.Lg;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BleController {

    private static final String TAG = "BleController";

    private final static UUID UUID_DATA_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final static UUID UUID_DATA_CHARACTERISTIC = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private final static UUID UUID_SOS_CHARACTERISTIC = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private final static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final String BLE = "ble.main";
    private static final String BLE_ADDRESS = "ble_address";
    private static final String BLE_ID = "ble_id";
    private static final String BLE_UNUSED_ID = "ble_unused_id";
    private static final String BLE_FAILED_ADDRESS = "ble_failed_address_";
    private static final String BLE_SYNC_BASE_DATA_FAILED = "ble_sync_base_data_failed";
    private static final String BLE_BINDED_TIME = "ble_binded_time";

    private static final BleController sInstance = new BleController();
    private Context mContext;
    private static final Object mLock = new Object();
    private final ExecutorService EXECUTOR_SERVICE_SINGLE = Executors.newSingleThreadExecutor();
    private final Executor EXECUTOR_SERVICE_POOL = AsyncTask.THREAD_POOL_EXECUTOR;
    private BluetoothAdapter mBleAdapter;
    private BleConnection mBleConnection;
    private GroupBleCallback mCallbacks;
    private BluetoothGattService mDefaultGattService;
    private BluetoothGattCharacteristic mDataCharacteristic;
    private BluetoothGattDescriptor mDataDescriptor;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            Lg.i(TAG, "onReceive " + action);
            EXECUTOR_SERVICE_POOL.execute(new Runnable() {
                @Override
                public void run() {
                    doReceiver(context, intent, action);
                }
            });
        }
    };
    private boolean mScanning;
    private boolean mInitialize;
    private boolean mAutoConnect = true;
    private boolean mAutoReconnect = true;
    private boolean mDeviceReady;
    private BleScanner mScanner;
    private AsyncTask<Void, Void, Void> mAntiLostTask;
    private RssiHelper mRssiHelper = new RssiHelper();
    private HistoryController historyController;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mCloseHeartRateRunnable = new Runnable() {
        @Override
        public void run() {
            closeHeartRateAsync();
        }
    };
    private Runnable mHistoryTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mHistoryTimeOutRunnable);
            EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Lg.i(TAG, "fetch history timeout");
                        // 同步72小时数据成功之后关闭通知
                        setCharacteristicNotification(mDataCharacteristic, mDataDescriptor, false);
                        mCallbacks.onFetchHistoryFailed(BleError.TIME_OUT);
                    } catch (Exception e) {
                        Lg.w(TAG, "failed to close history timeout", e);
                    }
                }
            });
        }
    };
    private volatile boolean heartRateStart;

    private BleController() {
        mCallbacks = new GroupBleCallback();
        historyController = new HistoryController(this);
    }

    public static BleController getInstance() {
        return sInstance;
    }

    public void initialize(Context context) {
        if (mInitialize) {
            return;
        }
        mInitialize = true;
        mContext = context.getApplicationContext();
        mBleAdapter = BluetoothAdapter.getDefaultAdapter();
        mBleConnection = new BleConnection(context);
        mScanner = BleScannerHelper.create(this, mBleAdapter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BleConnection.ACTION_GATT_CONNECTED);
        filter.addAction(BleConnection.ACTION_GATT_CONNECTING);
        filter.addAction(BleConnection.ACTION_GATT_DISCONNECTED);
        filter.addAction(BleConnection.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BleConnection.ACTION_GATT_RSSI);
        filter.addAction(BleConnection.ACTION_DATA_AVAILABLE);
        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, filter);
    }

    public HistoryController getHistoryController() {
        return historyController;
    }

    private void doReceiver(Context context, Intent intent, String action) {
        if (BleConnection.ACTION_GATT_CONNECTED.equals(action)) {
            mCallbacks.onGattConnected(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_GATT_CONNECTING.equals(action)) {
            mCallbacks.onGattConnecting(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_GATT_DISCONNECTED.equals(action)) {
            disconnectInner();
            mCallbacks.onGattDisconnected(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_GATT_RSSI.equals(action)) {
            int rssi = intent.getIntExtra(BleConnection.EXTRA_DATA, 0);
            int newRssi = mRssiHelper.add(rssi);
            Lg.i(TAG, "onRemoteRssi newRssi:" + newRssi);
            if (newRssi != RssiHelper.NO_RSSI) {
                mCallbacks.onRemoteRssi(mBleConnection.getDevice(), newRssi);
            }
        } else if (BleConnection.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            mDefaultGattService = mBleConnection.getService(UUID_DATA_SERVICE);
            if (mDefaultGattService == null) {
                // TODO 设备没有该服务？
                Lg.i(TAG, "It find no gatt service");
                mDeviceReady = false;
                mBleConnection.disconnect();
                return;
            }
            mDataCharacteristic = mDefaultGattService.getCharacteristic(UUID_DATA_CHARACTERISTIC);
            if (mDataCharacteristic == null) {
                // TODO 设备没有该特征？
                Lg.i(TAG, "It find no characteristic");
                mDeviceReady = false;
                mBleConnection.disconnect();
                return;
            }
            if (BuildConfig.DEBUG) {
                List<BluetoothGattDescriptor> descriptors = mDataCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor d : descriptors) {
                    Lg.i(TAG, "  d:" + d.getUuid() + " " + d.getPermissions());
                }
            }
            mDataDescriptor = mDataCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            mDeviceReady = true;
            mCallbacks.onGattServicesDiscovered(mBleConnection.getDevice());
        } else if (BleConnection.ACTION_DATA_AVAILABLE.equals(action)) {
            String uuid = intent.getStringExtra(BleConnection.EXTRA_UUID);
            byte[] data = intent.getByteArrayExtra(BleConnection.EXTRA_DATA);
            if (data != null && data.length > 0) {
                if (UUID_SOS_CHARACTERISTIC.toString().equals(uuid)) {
                    mCallbacks.onSosNotify(data);
                } else {
                    Data d = NotifyDataHelper.parser(data);
                    if (d == null) {
                        Lg.i(TAG, "no support notify data?");
                    } else if (d instanceof HeartRateDataResult) {
                        mCallbacks.onGetHeartRateSuccess(((HeartRateDataResult) d).getHeartRate());
                    } else if (d instanceof HistoryResult) {
                        HistoryResult r = (HistoryResult) d;
                        if (r.isFinish()) {
                            // 同步72小时数据成功之后关闭通知
                            setCharacteristicNotification(mDataCharacteristic, mDataDescriptor, false);
                            mCallbacks.onFetchHistorySuccess();
                            mHandler.removeCallbacks(mHistoryTimeOutRunnable);
                        } else {
                            mCallbacks.onFetchHistory(getBindedDeviceAddress(), r);
                            mHandler.removeCallbacks(mHistoryTimeOutRunnable);
                            mHandler.postDelayed(mHistoryTimeOutRunnable, 10000);
                        }
                    }
                }
            }
        }
    }

    public void addCallback(BleCallback cb) {
        mCallbacks.addListener(cb);
    }

    public void removeCallback(BleCallback cb) {
        mCallbacks.removeListener(cb);
    }

    public BleCallback getCallbacks() {
        return mCallbacks;
    }

    public void startLeScan() {
        if (!mScanning) {
            mScanning = true;
            Lg.i(TAG, "startLeScan");
            mScanner.startLeScan();
        }
    }

    public void stopLeScan() {
        if (mScanning) {
            mScanning = false;
            Lg.i(TAG, "stopLeScan");
            mScanner.stopLeScan();
        }
    }

    public boolean isScanning() {
        return mScanning;
    }

    public boolean isEnabled() {
        return mBleAdapter != null && mBleAdapter.isEnabled();
    }

    public boolean isBinded() {
        return !TextUtils.isEmpty(getBindedDeviceAddress());
    }

    public boolean isDeviceReady() {
        return mDeviceReady;
    }

    public boolean isAutoConnect() {
        return mAutoConnect;
    }

    public void setAutoConnect(boolean auto) {
        mAutoConnect = auto;
    }

    public boolean isAutoReconnect() {
        return mAutoReconnect;
    }

    public void setAutoReconnect(boolean auto) {
        mAutoReconnect = auto;
    }

    public String getBindedDeviceAddress() {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        return shared.getString(BLE_ADDRESS, null);
    }

    public String getBindedDeviceId() {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        return shared.getString(BLE_ID, null);
    }

    public void saveBindedDevice(String deviceId, String deviceAddress) {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        Editor editor = shared.edit();
        editor.putString(BLE_ID, deviceId);
        editor.putString(BLE_ADDRESS, deviceAddress);
        editor.apply();
    }

    private String getUnusedDeviceId() {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        return shared.getString(BLE_UNUSED_ID, null);
    }

    private void saveUnusedDeviceId(String deviceId) {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        Editor editor = shared.edit();
        editor.putString(BLE_UNUSED_ID, deviceId);
        editor.apply();
    }

    public void connect(BluetoothDevice device) {
        mBleConnection.connect(device);
    }

    public void disconnect() {
        Lg.i(TAG, "disconnect");
        disconnectInner();
        if (mBleConnection != null) {
            mBleConnection.disconnect();
        }
    }

    private byte[] write(byte[] data) throws InterruptedException {
        synchronized (mLock) {
            checkThread();
            checkConnectionState();
            mBleConnection.write(mDataCharacteristic, data);
            return mBleConnection.read(mDataCharacteristic);
        }
    }

    private void write2(byte[] data) throws InterruptedException {
        synchronized (mLock) {
            checkThread();
            checkConnectionState();
            mBleConnection.write(mDataCharacteristic, data);
        }
    }

    private byte[] read() throws InterruptedException {
        synchronized (mLock) {
            checkThread();
            checkConnectionState();
            return mBleConnection.read(mDataCharacteristic);
        }
    }

    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, BluetoothGattDescriptor descriptor, boolean enable) {
        synchronized (mLock) {
            checkThread();
            checkConnectionState();
            mBleConnection.setCharacteristicNotification(characteristic, descriptor, enable);
        }
    }

    private void checkConnectionState() {
        if (!mDeviceReady) {
            throw new IllegalStateException("Bluetooth is not ready.");
        }
    }

    private void checkThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("It must be called on the background thread.");
        }
    }

    @WorkerThread
    public void bindDeviceNew(boolean reconnect) throws InterruptedException {
        checkConnectionState();
        Lg.i(TAG, "bindDeviceNew start " + reconnect);
        BluetoothDevice device = mBleConnection.getDevice();
        String address = device.getAddress();
        if (!reconnect) {
            write2(new BindQuery().toValue());
        }
        saveBindedDevice("", address);
        saveFailedBindedDevice(address, false);
        mCallbacks.onBindDeviceSuccess(device, !(address.equals(getBindedDeviceAddress())));
        Lg.i(TAG, "bindDeviceNew end");
    }

    @WorkerThread
    public void bindDeviceOld() {
        checkConnectionState();
        Lg.i(TAG, "bindDeviceOld start");
        BluetoothDevice device = mBleConnection.getDevice();
        if (device != null) {
            String address = device.getAddress();
            saveBindedDevice("", address);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            saveBindedDeviceTime(calendar.getTimeInMillis());
            saveFailedBindedDevice(address, false);
            mCallbacks.onBindDeviceSuccess(device, !(address.equals(getBindedDeviceAddress())));
        } else {
            mCallbacks.onBindDeviceFailed(null);
        }
        Lg.i(TAG, "bindDeviceOld end");
    }

    private void saveBindedDeviceTime(long timeMillis) {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        Editor editor = shared.edit();
        editor.putLong(BLE_BINDED_TIME, timeMillis);
        editor.apply();
    }

    private long getBindedDeviceTime() {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        return shared.getLong(BLE_BINDED_TIME, 0);
    }

    @WorkerThread
    public void bindDevice() throws InterruptedException {
        checkConnectionState();
        Lg.i(TAG, "bindDevice start");
        BluetoothDevice device = mBleConnection.getDevice();
        BindQuery query = new BindQuery();
        BindQueryResult result = BindQueryResult.parser(write(query.toValue()));
        if (result != null) {
            boolean firstBinded = true;
            String deviceId = result.getDeviceId();
            Lg.i(TAG, "fetch deviceId:" + deviceId);
            boolean binded = result.isBinded();
            if (binded) {
                firstBinded = !(device.getAddress().equals(getBindedDeviceAddress()));
                Lg.i(TAG, device + " is alread bind, firstBinded:" + firstBinded);
            } else {
                deviceId = getUnusedDeviceId();
                if (deviceId == null) {
                    deviceId = HttpController.getInstance().createDeviceId();
                    if (deviceId == null) {
                        Lg.w(TAG, "failed to create device id from server");
                        mCallbacks.onBindDeviceFailed(device);
                        return;
                    }
                }
                // 前面补0
                int l = deviceId.length();
                for (int i = 0; i < 7 - l; i++) {
                    deviceId = "0" + deviceId;
                }
                byte[] ids1 = deviceId.getBytes();
                byte[] ids2 = new byte[7];
                for (int i = 0; i < 7; i++) {
                    ids2[i] = (byte) (ids1[i] - 0x30);
                }
                Bind bind = new Bind();
                BindResult r = BindResult.parser(write(bind.toValue(result.data, ids2)));
                if (r != null) {
                    saveUnusedDeviceId(null);
                } else {
                    saveUnusedDeviceId(deviceId);
                    mCallbacks.onBindDeviceFailed(device);
                    return;
                }
            }
            saveBindedDevice(deviceId, device.getAddress());
            mCallbacks.onBindDeviceSuccess(device, firstBinded);
        } else {
            mCallbacks.onBindDeviceFailed(device);
        }
    }

    public void bindDeviceAsync(final boolean reconnect) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (BuildConfig.newFit) {
                        bindDeviceNew(reconnect);
                    } else {
                        bindDeviceOld();
                    }
                } catch (Exception e) {
                    Lg.w(TAG, "failed to bind device", e);
                    mCallbacks.onBindDeviceFailed(null);
                }
            }
        });
    }

    @WorkerThread
    private PedometerDataResult fetchPedometerData() throws InterruptedException {
        checkConnectionState();
        String address = getBindedDeviceAddress();
        Lg.i(TAG, address + " fetch pedometer data start");
        PedometerData data = new PedometerData();
        PedometerDataResult r = PedometerDataResult.parser(write(data.toValue()));
        Lg.i(TAG, address + " fetch pedometer data finish");
        if (r != null) {
            Lg.i(TAG, "fetch pedometer data " + r);
            BleDao.saveCurrentData(mContext, address, r);
        }
        return r;
    }

    public void fetchPedometerDataAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mCallbacks.onFetchPedometerDataSuccess(fetchPedometerData());
                } catch (Exception e) {
                    Lg.w(TAG, "failed to fetch pedometer data", e);
                    mCallbacks.onFetchPedometerDataFailed(null);
                }
            }
        });
    }

    @WorkerThread
    private void syncSystemDate() throws InterruptedException {
        checkConnectionState();
        String address = getBindedDeviceAddress();
        long currentTime = (System.currentTimeMillis() - Tools.get2000Time()) / 1000;
        Lg.i(TAG, address + " sync system date");
        Lg.d(TAG, "currentTime:" + currentTime + " 0x" + Long.toHexString(currentTime));
        SystemDate data = new SystemDate();
        Calendar calendar = Calendar.getInstance();
        data.year = calendar.get(Calendar.YEAR) - 2000;
        data.month = calendar.get(Calendar.MONTH) + 1;
        data.day = calendar.get(Calendar.DAY_OF_MONTH);
        data.hour = calendar.get(Calendar.HOUR_OF_DAY);
        data.minute = calendar.get(Calendar.MINUTE);
        data.second = calendar.get(Calendar.SECOND);
        write(data.toValue(currentTime));
    }

    /**
     * 同步系统时间
     */
    public void syncSystemDateAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    syncSystemDate();
                } catch (Exception e) {
                    // ignore
                }
            }
        });
    }

    public void fetchVersionInfoAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    fetchVersionInfo();
                } catch (Exception e) {
                    mCallbacks.onFetchVersionInfoFailed();
                }
            }
        });
    }

    @WorkerThread
    private void fetchVersionInfo() throws InterruptedException {
        checkConnectionState();
        String address = getBindedDeviceAddress();
        Lg.i(TAG, address + " fetch version info start");
        VersionInfo data = new VersionInfo();
        VersionInfoResult result = VersionInfoResult.parser(write(data.toValue()));
        if (result == null) {
            mCallbacks.onFetchVersionInfoFailed();
        } else {
            BleDao.saveVersionInfo(mContext, address, result);
            Lg.i(TAG, "success to save version info to db");
            mCallbacks.onFetchVersionInfoSuccess(result);
        }
    }

    /**
     * 报警
     */
    public void antiAlarmAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    antiAlarm();
                } catch (Exception e) {
                    // ignore
                }
            }
        });
    }

    private void antiAlarm() throws InterruptedException {
        checkConnectionState();
        Lg.i(TAG, "anti alarm start");
//        closeAntiLose();
//         antiLose(false);
        AntiAlarm data = new AntiAlarm();
        write2(data.toValue());
    }

    private void antiLose(boolean enable) throws InterruptedException {
        checkConnectionState();
        String address = getBindedDeviceAddress();
        mRssiHelper.clear();
        Lg.i(TAG, address + (enable ? " open" : " close") + " anti-lost start");
        AntiLost data = new AntiLost();
        write2(data.toValue(enable));
        if (enable) {
            while (mAntiLostTask != null && !mAntiLostTask.isCancelled()) {
                SleepTime sleep = new SleepTime();
                synchronized (mLock) {
                    checkConnectionState();
                    mBleConnection.readRemoteRssi();
                }
                sleep.sleep(300);
            }
        }
    }

    /**
     * 打开防丢失功能
     */
    public void antiLoseAsync() {
        if (mAntiLostTask != null) {
            Lg.i(TAG, "anti-lose is already running.");
            return;
        }
        mAntiLostTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    antiLose(true);
                } catch (Exception e) {
                    // ignore
                }
                return null;
            }
        };
        mAntiLostTask.executeOnExecutor(EXECUTOR_SERVICE_POOL);
    }

    /**
     * 关闭防丢失
     */
    public void closeAntiLose() {
        Lg.i(TAG, "closeAntiLose " + mAntiLostTask);
        if (mAntiLostTask != null) {
            mAntiLostTask.cancel(true);
            mAntiLostTask = null;
        }
    }

    /**
     * 关闭防丢失功能
     */
    public void closeAntiLoseAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    closeAntiLose();
                    antiLose(false);
                } catch (Exception e) {
                    // ignore
                }
            }
        });
    }

    public void fetchHistoryAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (BuildConfig.newFit) {
                        fetchHistoryByNotify();
                        mHandler.removeCallbacks(mHistoryTimeOutRunnable);
                        mHandler.postDelayed(mHistoryTimeOutRunnable, 10000);
                    } else {
                        fetchHistory();
                    }
                } catch (Exception e) {
                    Lg.w(TAG, "failed to fetch history", e);
                    mCallbacks.onFetchHistoryFailed(BleError.SYSTEM);
                }
            }
        });
    }

    @WorkerThread
    private void fetchHistory() throws InterruptedException {
        mCallbacks.onFetchHistoryStart();
        checkConnectionState();
        String address = getBindedDeviceAddress();
        Lg.i(TAG, address + " fetch history start");
        long time = BleDao.getMaxHistotyTime(mContext, address);
        Lg.i(TAG, "history max time1:" + time);
        long phoneTime = Tools.changeDeviceTime2Phone(time);
        Lg.i(TAG, "history max time2:" + phoneTime + " " + new Date(phoneTime).toString());
        phoneTime = Math.max(phoneTime, getBindedDeviceTime());
        Lg.i(TAG, "history max time3:" + phoneTime + " " + new Date(phoneTime).toString());
        Lg.i(TAG, "history max time4: currentTime " + new Date().toString());
        int h;
        if (phoneTime > System.currentTimeMillis()) {
            h = 71;
        } else {
            h = Tools.getDistanceTime(phoneTime);
        }
        Lg.i(TAG, "request history h:" + h);
        if (h <= 0) {
            Lg.i(TAG, "Do not need access to historical records.");
            mCallbacks.onFetchHistorySuccess();
            return;
        }
        h = Math.min(71, h);
        for (int i = 1; i <= h; i++) {
            Lg.i(TAG, address + " fetch history " + i);
            History data = new History(i);
            HistoryResult result = HistoryResult.parser(write(data.toValue()));
            if (result != null) {
                mCallbacks.onFetchHistory(address, result);
            } else {
                Lg.i(TAG, "failed to fetch history(" + i + ")");
            }
        }
        mCallbacks.onFetchHistorySuccess();
    }

    private void fetchHistoryByNotify() throws InterruptedException {
        mCallbacks.onFetchHistoryStart();
        checkThread();
        checkConnectionState();
        String address = getBindedDeviceAddress();
        Lg.i(TAG, "fetchHistoryByNotify start");
//        long time = BleDao.getMaxHistotyTime(mContext, address);
//        int h = Tools.getDistanceTime(time);
//        if (h <= 0) {
//            Lg.i(TAG, "it do not fetch history.");
//            mCallbacks.onFetchHistorySuccess();
//            return;
//        }
//        if (h >= 71) {
//            h = 0xFF;
//        }
        int h = 0xFF;
        Lg.i(TAG, "fetch history " + h);
        History2 data = new History2(h);
        setCharacteristicNotification(mDataCharacteristic, mDataDescriptor, true);
        write2(data.toValue());
    }

    private OpenRateDataResult openHeartRate() throws InterruptedException {
        checkConnectionState();
        Lg.i(TAG, " open heart rate start");
        if (BuildConfig.HEART_RATE_NOTIFY) {
            setCharacteristicNotification(mDataCharacteristic, mDataDescriptor, true);
        }
        OpenHeartRateData data = new OpenHeartRateData();
        return OpenRateDataResult.parser(write(data.toValue()));
    }

    public boolean isHeartRateStart() {
        return heartRateStart;
    }

    /**
     * 打开心率测试
     */
    public void openHeartRateAsync(boolean single) {
        if (BuildConfig.HEART_RATE_NOTIFY) {
            EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        heartRateStart = true;
                        openHeartRate();
                        closeHeartRateAsync(30 * 1000);
                    } catch (Exception e) {
                        heartRateStart = false;
                        mCallbacks.onGetHeartRateFailed();
                    }
                }
            });
        } else {
            heartRateStart = true;
            HeartRateTask heartRateTask = new HeartRateTask(single);
            heartRateTask.executeOnExecutor(EXECUTOR_SERVICE_POOL);
        }
    }

    private class HeartRateTask extends AsyncTask<Void, Void, Void> {

        private boolean single;

        private HeartRateTask(boolean single) {
            this.single = single;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                OpenRateDataResult result = openHeartRate();
                if (result == null) {
                    mCallbacks.onGetHeartRateFailed();
                    return null;
                }
                if (heartRateStart && single) {
                    closeHeartRateAsync(30 * 1000);
                }
                PedometerDataResult r;
                while (heartRateStart) {
                    synchronized (mLock) {
                        checkThread();
                        checkConnectionState();
                        mBleConnection.write(mDataCharacteristic, new PedometerData().toValue());
                        if (heartRateStart) {
                            r = PedometerDataResult.parser(mBleConnection.read(mDataCharacteristic));
                        } else {
                            r = null;
                        }
                    }
                    if (heartRateStart && r != null) {
                        mCallbacks.onGetHeartRateSuccess(r.getHeartRate());
                    }
                    if (heartRateStart) {
                        SystemClock.sleep(2000);
                    }
                }
            } catch (Exception e) {
                Lg.w(TAG, "failed to heart rate", e);
                mCallbacks.onGetHeartRateFailed();
            }
            return null;
        }
    }

    private void closeHeartRate() throws InterruptedException {
        try {
            checkConnectionState();
            Lg.i(TAG, "close heart rate start");
            if (BuildConfig.HEART_RATE_NOTIFY) {
                setCharacteristicNotification(mDataCharacteristic, mDataDescriptor, false);
            }
            write(new CloseHeartRateData().toValue());
        } finally {
            mCallbacks.onCloseHeartRate();
        }
    }

    public void closeHeartRateAsync(long delayMillis) {
        mHandler.removeCallbacks(mCloseHeartRateRunnable);
        mHandler.postDelayed(mCloseHeartRateRunnable, delayMillis);
    }

    /**
     * 关闭心率测试
     */
    public void closeHeartRateAsync() {
        Lg.i(TAG, "close heart rate aysnc start");
        mHandler.removeCallbacks(mCloseHeartRateRunnable);
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    heartRateStart = false;
                    closeHeartRate();
                } catch (Exception e) {
                    Lg.w(TAG, "failed to close heart rate", e);
                }
            }
        });
    }

    public void bindDeviceAsync(String userId, final String address) {
        HttpController.getInstance().bindDeviceAsync(userId, address, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                Lg.i(TAG, "bind device success " + content);
                saveFailedBindedDevice(address, false);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                Lg.i(TAG, "bind device failed " + content);
                saveFailedBindedDevice(address, true);
            }
        });
    }

    public boolean isFailedBindedDevice(String address) {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        return shared.getBoolean(BLE_FAILED_ADDRESS + address, false);
    }

    private void saveFailedBindedDevice(String address, boolean b) {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        Editor editor = shared.edit();
        if (b) {
            editor.putBoolean(BLE_FAILED_ADDRESS + address, true);
        } else {
            editor.remove(BLE_FAILED_ADDRESS + address);
        }
        editor.apply();
    }

    /**
     * 发送来电提醒
     *
     * @param phone 来电者电话
     */
    public void sendCallRemindAsync(final String phone) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendCallRemind(phone);
                } catch (Exception e) {
                    Lg.e(TAG, "failed to sendCallRemind", e);
                }
            }
        });
    }

    /**
     * 发送短息提醒
     *
     * @param phone 发送者电话
     */
    public void sendMessageRemindAsync(final String phone) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessageRemind(phone);
                } catch (Exception e) {
                    Lg.e(TAG, "failed to sendMessageRemind", e);
                }
            }
        });
    }

    public void sendWeChatRemindAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    checkConnectionState();
                    String address = getBindedDeviceAddress();
                    Lg.i(TAG, address + " send WeChat remind start");
                    WeChatRemind data = new WeChatRemind();
                    write(data.toValue());
                    Lg.i(TAG, address + " send WeChat remind finish");
                } catch (Exception e) {
                    Lg.e(TAG, "failed to sendWeChatRemind", e);
                }
            }
        });
    }

    public void sendQQRemindAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    checkConnectionState();
                    String address = getBindedDeviceAddress();
                    Lg.i(TAG, address + " send qq remind start");
                    QQRemind data = new QQRemind();
                    write(data.toValue());
                    Lg.i(TAG, address + " send qq remind finish");
                } catch (Exception e) {
                    Lg.e(TAG, "failed to send qq remind", e);
                }
            }
        });
    }

    public String filterPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return phone;
        }
        if (phone.startsWith("+86")) {
            phone = phone.substring(3);
        } else if (phone.startsWith("0086")) {
            phone = phone.substring(4);
        } else if (phone.startsWith("86")) {
            phone = phone.substring(2);
        }
        return phone;
    }

    private void sendCallRemind(String phone) throws InterruptedException {
        checkConnectionState();
        phone = filterPhone(phone);
        String address = getBindedDeviceAddress();
        Lg.i(TAG, address + " send call remind " + phone + " start");
        Remind data = new Remind();
        RemindResult result = RemindResult.parser(write(data.toValue(phone.getBytes())));
        boolean success = (result != null && result.isSuccess());
        if (success) {
            Lg.i(TAG, address + " send call remind " + phone + " success");
        } else {
            Lg.i(TAG, address + " send call remind " + phone + " fail");
            write(data.toValue(phone.getBytes()));
        }
    }

    private void sendMessageRemind(String phone) throws InterruptedException {
        checkConnectionState();
        phone = filterPhone(phone);
        String address = getBindedDeviceAddress();
        Lg.i(TAG, address + " send message remind " + phone + " start");
        MessageRemind data = new MessageRemind();
        RemindResult result = RemindResult.parser(write(data.toValue(phone.getBytes())));
        boolean success = (result != null && result.isSuccess());
        if (success) {
            Lg.i(TAG, address + " send message remind " + phone + " success");
        } else {
            Lg.i(TAG, address + " send message remind " + phone + " fail");
            write(data.toValue(phone.getBytes()));
        }
    }

    public void syncBaseDataAsync() {
        syncBaseDataAsync(false);
    }

    /**
     * 同步基本数据接口
     */
    public void syncBaseDataAsync(final boolean clear) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!clear && BuildConfig.newFit) {
                        syncSystemDate();
                    }
                    syncBaseData(clear);
                    saveSyncBaseDataFailed(false);
                } catch (Exception e) {
                    Lg.e(TAG, "failed to syncBaseDataAsync", e);
                    saveSyncBaseDataFailed(true);
                }
            }
        });
    }

    private void saveSyncBaseDataFailed(boolean b) {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        Editor editor = shared.edit();
        editor.putBoolean(BLE_SYNC_BASE_DATA_FAILED, b);
        editor.apply();
    }

    public boolean isSyncBaseDataFailed() {
        SharedPreferences shared = mContext.getSharedPreferences(BLE, Context.MODE_PRIVATE);
        return shared.getBoolean(BLE_SYNC_BASE_DATA_FAILED, false);
    }

    private void syncBaseData(boolean clear) throws InterruptedException {
        checkConnectionState();
        String address = getBindedDeviceAddress();
        Lg.i(TAG, address + " sync base data start");
        BaseData data = new BaseData();
        Calendar calendar = Calendar.getInstance();
        data.year = calendar.get(Calendar.YEAR) - 2000;
        data.month = calendar.get(Calendar.MONTH) + 1;
        data.day = calendar.get(Calendar.DAY_OF_MONTH);
        data.hour = calendar.get(Calendar.HOUR_OF_DAY);
        data.minute = calendar.get(Calendar.MINUTE);
        data.height = data.change2int(UserController.getHeight(mContext));
        data.weight = data.change2int(UserController.getWeight(mContext));
        data.step = data.change2int(UserController.getStepLong(mContext));
        data.goal = data.change2goal(UserController.getGoal(mContext) * data.step * 0.01 * 0.001);
        String email = UserController.getEmail(mContext);
        Clock clock = ClockController.get(mContext, email);
        if (clock != null) {
            fillClock(data, clock);
        }
        com.canice.wristbandapp.remind.Remind remind = RemindController.get(mContext, email);
        if (remind != null) {
            fillRemind(data, remind);
        }
        write(data.toValue(clear));
    }

    private void fillRemind(BaseData data, com.canice.wristbandapp.remind.Remind remind) {
        if (remind.enabled) {
            data.remindIntervalTime = remind.intervalTime;
            data.remindStartTimeHour = remind.startTimeHour;
            data.remindEndTimeHour = remind.endTimeHour;
            if (remind.mondayEnabled) {
                data.remind |= Byte.parseByte("00000001", 2);
            }
            if (remind.tuesdayEnabled) {
                data.remind |= Byte.parseByte("00000010", 2);
            }
            if (remind.wednesdayEnabled) {
                data.remind |= Byte.parseByte("00000100", 2);
            }
            if (remind.thursdayEnabled) {
                data.remind |= Byte.parseByte("00001000", 2);
            }
            if (remind.fridayEnabled) {
                data.remind |= Byte.parseByte("00010000", 2);
            }
            if (remind.saturdayEnabled) {
                data.remind |= Byte.parseByte("00100000", 2);
            }
            if (remind.sundayEnabled) {
                data.remind |= Byte.parseByte("01000000", 2);
            }
        }
    }

    private void fillClock(BaseData data, Clock clock) {
        if (clock.enabled) {
            data.clockIntervalTime = clock.intervalTime;
            data.clockTimeHour = clock.timeHour;
            data.clockTimeMinute = clock.timeMinute;
            if (clock.mondayEnabled) {
                data.clock |= Byte.parseByte("00000001", 2);
            }
            if (clock.tuesdayEnabled) {
                data.clock |= Byte.parseByte("00000010", 2);
            }
            if (clock.wednesdayEnabled) {
                data.clock |= Byte.parseByte("00000100", 2);
            }
            if (clock.thursdayEnabled) {
                data.clock |= Byte.parseByte("00001000", 2);
            }
            if (clock.fridayEnabled) {
                data.clock |= Byte.parseByte("00010000", 2);
            }
            if (clock.saturdayEnabled) {
                data.clock |= Byte.parseByte("00100000", 2);
            }
            if (clock.sundayEnabled) {
                data.clock |= Byte.parseByte("01000000", 2);
            }
        }
    }

    /**
     * 打开或者关闭sos求助
     *
     * @param enable true:打开，false：关闭
     */
    public void setSosEnableAsync(final boolean enable) {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    setSosEnable(enable);
                } catch (Exception e) {
                    // ignore
                }
            }
        });
    }

    private void setSosEnable(boolean enable) {
        checkThread();
        checkConnectionState();
        String address = getBindedDeviceAddress();
        Lg.i(TAG, address + " sos " + (enable ? "enable" : "disable") + " start");
        BluetoothGattCharacteristic helpCharacteristic = mDefaultGattService.getCharacteristic(UUID_SOS_CHARACTERISTIC);
        if (helpCharacteristic != null) {
            if (BuildConfig.DEBUG) {
                List<BluetoothGattDescriptor> descriptors = helpCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor d : descriptors) {
                    Lg.i(TAG, "sos:" + d.getUuid() + " " + d.getPermissions());
                }
            }
            BluetoothGattDescriptor d = helpCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            if (d != null) {
                setCharacteristicNotification(helpCharacteristic, d, enable);
            }
        }
    }

    private void disconnectInner() {
        mDeviceReady = false;
        stopLeScan();
        closeAntiLose();
        int state = historyController.getState();
        if (state == HistoryController.STATE_START || state == HistoryController.STATE_FETCHING) {
            mCallbacks.onFetchHistoryFailed(BleError.SYSTEM);
        }
        heartRateStart = false;
        mHandler.removeCallbacks(mCloseHeartRateRunnable);
    }

    public void fetchDataAsync() {
        EXECUTOR_SERVICE_SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mCallbacks.onFetchPedometerDataSuccess(fetchPedometerData());
                } catch (Exception e) {
                    Lg.w(TAG, "failed to fetch pedometer data", e);
                    mCallbacks.onFetchPedometerDataFailed(null);
                    return;
                }
                try {
                    if (BuildConfig.newFit) {
                        fetchHistoryByNotify();
                        mHandler.removeCallbacks(mHistoryTimeOutRunnable);
                        mHandler.postDelayed(mHistoryTimeOutRunnable, 10000);
                    } else {
                        fetchHistory();
                    }
                } catch (Exception e) {
                    Lg.w(TAG, "failed to fetch history data", e);
                    mCallbacks.onFetchHistoryFailed(BleError.SYSTEM);
                }
            }
        });
    }
}