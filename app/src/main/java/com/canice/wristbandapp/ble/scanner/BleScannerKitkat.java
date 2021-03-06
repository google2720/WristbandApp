package com.canice.wristbandapp.ble.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.canice.wristbandapp.ble.BleController;
import com.github.yzeaho.log.Lg;

/**
 * android5.0以下版本的蓝牙扫描器
 * Created by y on 2016/6/28.
 */
public class BleScannerKitkat implements BleScanner {
    public static final String TAG = "BleScannerKitkat";
    private BleController mBle;
    private BluetoothAdapter mBleAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            String name = device.getName();
            String address = device.getAddress();
            Lg.i(TAG, "onLeScan " + name + " " + address + " " + rssi);
            mBle.getCallbacks().onLeScan(device);
        }
    };

    public BleScannerKitkat(BleController bleController, BluetoothAdapter bleAdapter) {
        this.mBle = bleController;
        this.mBleAdapter = bleAdapter;
    }

    @Override
    public void startLeScan() {
        boolean r = mBleAdapter.startLeScan(mLeScanCallback);
        if (!r) {
            mBle.getCallbacks().onLeScanFailed(0);
        }
    }

    @Override
    public void stopLeScan() {
        mBleAdapter.stopLeScan(mLeScanCallback);
    }
}
