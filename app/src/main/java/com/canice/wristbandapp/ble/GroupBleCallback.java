package com.canice.wristbandapp.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

import com.canice.wristbandapp.ble.data.HistoryResult;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.canice.wristbandapp.ble.data.VersionInfoResult;

import java.util.concurrent.CopyOnWriteArraySet;

public class GroupBleCallback implements BleCallback {

    private CopyOnWriteArraySet<BleCallback> listeners = new CopyOnWriteArraySet<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    public synchronized void addListener(BleCallback listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(BleCallback listener) {
        listeners.remove(listener);
    }

    public synchronized boolean isActiveListener(BleCallback listener) {
        return listeners.contains(listener);
    }

    @Override
    public void onGattDisconnected(final BluetoothDevice device) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onGattDisconnected(device);
                }
            }
        });
    }

    @Override
    public void onGattConnected(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onGattConnected(device);
        }
    }

    @Override
    public void onGattServicesDiscovered(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onGattServicesDiscovered(device);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onLeScan(device);
        }
    }

    @Override
    public void onLeScanFailed(int error) {
        for (BleCallback l : listeners) {
            l.onLeScanFailed(error);
        }
    }

    @Override
    public void onGattConnecting(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onGattConnecting(device);
        }
    }

    @Override
    public void onBindDeviceFailed(BluetoothDevice device) {
        for (BleCallback l : listeners) {
            l.onBindDeviceFailed(device);
        }
    }

    @Override
    public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBinded) {
        for (BleCallback l : listeners) {
            l.onBindDeviceSuccess(device, firstBinded);
        }
    }

    @Override
    public void onFetchPedometerDataSuccess(PedometerDataResult pedometerDataResult) {
        for (BleCallback l : listeners) {
            l.onFetchPedometerDataSuccess(pedometerDataResult);
        }
    }

    @Override
    public void onFetchPedometerDataFailed(PedometerDataResult pedometerDataResult) {
        for (BleCallback l : listeners) {
            l.onFetchPedometerDataFailed(pedometerDataResult);
        }
    }

    @Override
    public void onFetchVersionInfoFailed() {
        for (BleCallback l : listeners) {
            l.onFetchVersionInfoFailed();
        }
    }

    @Override
    public void onFetchVersionInfoSuccess(VersionInfoResult result) {
        for (BleCallback l : listeners) {
            l.onFetchVersionInfoSuccess(result);
        }
    }

    @Override
    public void onFetchHistoryFailed(int error) {
        for (BleCallback l : listeners) {
            l.onFetchHistoryFailed(error);
        }
    }

    @Override
    public void onFetchHistorySuccess() {
        for (BleCallback l : listeners) {
            l.onFetchHistorySuccess();
        }
    }

    @Override
    public void onFetchHistoryStart() {
        for (BleCallback l : listeners) {
            l.onFetchHistoryStart();
        }
    }

    public void onSosNotify(byte[] data) {
        for (BleCallback l : listeners) {
            l.onSosNotify(data);
        }
    }

    public void onFetchHistory(String address, HistoryResult result) {
        for (BleCallback l : listeners) {
            l.onFetchHistory(address, result);
        }
    }

    public void onRemoteRssi(BluetoothDevice device, int rssi) {
        for (BleCallback l : listeners) {
            l.onRemoteRssi(device, rssi);
        }
    }

    @Override
    public void onBluetoothOff() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onBluetoothOff();
                }
            }
        });
    }

    @Override
    public void onRefreshGoal() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onRefreshGoal();
                }
            }
        });
    }

    @Override
    public void onGetHeartRateStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onGetHeartRateStart();
                }
            }
        });
    }

    @Override
    public void onGetHeartRateSuccess(final int value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onGetHeartRateSuccess(value);
                }
            }
        });
    }

    @Override
    public void onGetHeartRateFailed() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onGetHeartRateFailed();
                }
            }
        });
    }

    @Override
    public void onCloseHeartRateStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onCloseHeartRateStart();
                }
            }
        });
    }

    @Override
    public void onCloseHeartRateFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (BleCallback l : listeners) {
                    l.onCloseHeartRateFinish();
                }
            }
        });
    }
}