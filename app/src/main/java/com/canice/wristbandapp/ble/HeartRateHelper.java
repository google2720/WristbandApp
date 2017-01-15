package com.canice.wristbandapp.ble;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.canice.wristbandapp.ble.data.CloseHeartRateData;
import com.canice.wristbandapp.ble.data.OpenHeartRateData;
import com.canice.wristbandapp.ble.data.OpenRateDataResult;
import com.canice.wristbandapp.ble.data.PedometerData;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.github.yzeaho.log.Lg;

/**
 * 心率测试
 * Created by y on 2017/1/15.
 */
public class HeartRateHelper {

    private static final String TAG = "HeartRateHelper";
    private BleController ble;

    public static final int STATE_START = 2;
    public static final int STATE_PRE_STOP = 3;
    public static final int STATE_STOP = 4;
    private volatile int state = STATE_STOP;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable closeHeartRateRunnable = new Runnable() {
        @Override
        public void run() {
            closeHeartRateInner();
        }
    };

    public HeartRateHelper(BleController ble) {
        this.ble = ble;
        ble.addCallback(new SimpleBleCallback() {
            @Override
            public void onBluetoothOff() {
                closeHeartRateInner();
            }

            @Override
            public void onGattDisconnected(BluetoothDevice device) {
                closeHeartRateInner();
            }
        });
    }

    public boolean isStart() {
        return state == STATE_START;
    }

    public int getState() {
        return state;
    }

    public void openHeartRateAsync(boolean single) {
        if (state != STATE_STOP) {
            Lg.w(TAG, "Heart rate test is not over. state:" + state);
            return;
        }
        state = STATE_START;
        Lg.i(TAG, "open heart rate async start");
        ble.getCallbacks().onGetHeartRateStart();
        HeartRateTask heartRateTask = new HeartRateTask(single);
        heartRateTask.executeOnExecutor(ble.EXECUTOR_SERVICE_POOL);
    }

    public void closeHeartRateAsync(long delayMillis) {
        Lg.i(TAG, "close heart rate after " + delayMillis + "ms");
        handler.removeCallbacks(closeHeartRateRunnable);
        handler.postDelayed(closeHeartRateRunnable, delayMillis);
    }

    private void closeHeartRateInner() {
        if (state != STATE_START) {
            Lg.w(TAG, "Heart rate test is not start. state:" + state);
            return;
        }
        state = STATE_PRE_STOP;
        Lg.i(TAG, "pre-close heart rate");
        ble.getCallbacks().onCloseHeartRateStart();
        CloseTask task = new CloseTask();
        task.executeOnExecutor(ble.EXECUTOR_SERVICE_POOL);
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Lg.i(TAG, "close heart rate start");
                ble.write2(new CloseHeartRateData().toValue());
            } catch (Exception e) {
                Lg.w(TAG, "failed to close heart rate", e);
            }
            state = STATE_STOP;
            ble.getCallbacks().onCloseHeartRateFinish();
            Lg.i(TAG, "close heart rate finish");
            return null;
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
                Lg.i(TAG, " open heart rate start");
                OpenRateDataResult result = OpenRateDataResult.parser(ble.write(new OpenHeartRateData().toValue()));
                if (result == null) {
                    ble.getCallbacks().onGetHeartRateFailed();
                    return null;
                }
                boolean first = true;
                if (isStart() && single) {
                    closeHeartRateAsync(30 * 1000);
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
                        ble.getCallbacks().onGetHeartRateSuccess(r.getHeartRate());
                        if (first && isStart() && single) {
                            first = false;
                            closeHeartRateAsync(10 * 1000);
                        }
                    }
                    if (isStart()) {
                        SystemClock.sleep(2000);
                    }
                }
                Lg.i(TAG, "heart rate task is finish");
            } catch (Exception e) {
                Lg.w(TAG, "failed to heart rate", e);
                ble.getCallbacks().onGetHeartRateFailed();
            }
            return null;
        }
    }
}
