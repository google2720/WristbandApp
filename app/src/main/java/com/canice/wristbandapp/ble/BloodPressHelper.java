package com.canice.wristbandapp.ble;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.canice.wristbandapp.ble.data.CloseBloodPressData;
import com.canice.wristbandapp.ble.data.OpenBloodPressData;
import com.canice.wristbandapp.ble.data.OpenBloodPressResult;
import com.canice.wristbandapp.ble.data.PedometerData;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.github.yzeaho.log.Lg;

/**
 * 血压测试
 * Created by y on 2017/1/15.
 */
public class BloodPressHelper {

    private static final String TAG = "BloodPressHelper";
    private BleController ble;

    public static final int STATE_START = 2;
    public static final int STATE_PRE_STOP = 3;
    public static final int STATE_STOP = 4;
    private volatile int state = STATE_STOP;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable closeBloodPressRunnable = new Runnable() {
        @Override
        public void run() {
            closeBloodPressInner();
        }
    };

    public BloodPressHelper(BleController ble) {
        this.ble = ble;
        ble.addCallback(new SimpleBleCallback() {
            @Override
            public void onBluetoothOff() {
                closeBloodPressInner();
            }

            @Override
            public void onGattDisconnected(BluetoothDevice device) {
                closeBloodPressInner();
            }
        });
    }

    public boolean isStart() {
        return state == STATE_START;
    }

    public int getState() {
        return state;
    }

    public void openBloodPressAsync(boolean single) {
        if (state != STATE_STOP) {
            Lg.w(TAG, "Blood press test is not over. state:" + state);
            return;
        }
        state = STATE_START;
        Lg.i(TAG, "open blood press async start");
        ble.getCallbacks().onGetBloodPressStart();
        BloodPressTask bloodPressTask = new BloodPressTask(single);
        bloodPressTask.executeOnExecutor(ble.EXECUTOR_SERVICE_POOL);
    }

    public void closeBloodPressAsync(long delayMillis) {
        Lg.i(TAG, "close blood press after " + delayMillis + "ms");
        handler.removeCallbacks(closeBloodPressRunnable);
        handler.postDelayed(closeBloodPressRunnable, delayMillis);
    }

    private void closeBloodPressInner() {
        if (state != STATE_START) {
            Lg.w(TAG, "blood press test is not start. state:" + state);
            return;
        }
        state = STATE_PRE_STOP;
        Lg.i(TAG, "pre-close blood press");
        ble.getCallbacks().onCloseBloodPressStart();
        CloseTask task = new CloseTask();
        task.executeOnExecutor(ble.EXECUTOR_SERVICE_POOL);
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Lg.i(TAG, "close blood press start");
                ble.write2(new CloseBloodPressData().toValue());
            } catch (Exception e) {
                Lg.w(TAG, "failed to close blood press", e);
            }
            state = STATE_STOP;
            ble.getCallbacks().onCloseBloodPressFinish();
            Lg.i(TAG, "close blood press finish");
            return null;
        }
    }

    private class BloodPressTask extends AsyncTask<Void, Void, Void> {

        private boolean single;

        private BloodPressTask(boolean single) {
            this.single = single;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Lg.i(TAG, "open blood press start");
                OpenBloodPressResult result = OpenBloodPressResult.parser(ble.write(new OpenBloodPressData().toValue()));
                if (result == null) {
                    ble.getCallbacks().onGetBloodPressFailed();
                    return null;
                }
                boolean first = true;
                if (isStart() && single) {
                    closeBloodPressAsync(30 * 1000);
                }
                PedometerDataResult r;
                while (isStart()) {
                    synchronized (ble.getLock()) {
                        ble.checkThread();
                        ble.checkConnectionState();
                        ble.writeInner(new PedometerData().toValue());
                        if (isStart()) {
                            r = PedometerDataResult.parser(ble.readInner());
                        } else {
                            r = null;
                        }
                    }
                    if (isStart() && r != null) {
                        ble.getCallbacks().onGetBloodPressSuccess(r.getSBPData(),r.getDBPData());
                        if (first && isStart() && single) {
                            first = false;
                            closeBloodPressAsync(10 * 1000);
                        }
                    }
                    if (isStart()) {
                        SystemClock.sleep(2000);
                    }
                }
                Lg.i(TAG, "blood press task is finish");
            } catch (Exception e) {
                Lg.w(TAG, "failed to blood press", e);
                ble.getCallbacks().onGetBloodPressFailed();
            }
            return null;
        }
    }
}
