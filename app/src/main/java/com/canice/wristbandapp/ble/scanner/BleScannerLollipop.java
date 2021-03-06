package com.canice.wristbandapp.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.util.Log;

import com.canice.wristbandapp.ble.BleController;
import com.github.yzeaho.log.Lg;

/**
 * android5.0的蓝牙扫描器
 * Created by y on 2016/6/28.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BleScannerLollipop implements BleScanner {

    public static final String TAG = "BleScannerLollipop";
    private BleController mBle;
    private BluetoothAdapter mBleAdapter;
    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Lg.i(TAG, "onLeScan " + result.getDevice().getName() + " " + result.getDevice().getAddress() + " " + result.getRssi());
            mBle.getCallbacks().onLeScan(result.getDevice());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "onScanFailed:" + errorCode);
            mBle.getCallbacks().onLeScanFailed(errorCode);
        }
    };

    public BleScannerLollipop(BleController bleController, BluetoothAdapter bleAdapter) {
        this.mBle = bleController;
        this.mBleAdapter = bleAdapter;
    }

    @Override
    public void startLeScan() {
        BluetoothLeScanner scanner = mBleAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            ScanSettings.Builder builder = new ScanSettings.Builder();
            builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            scanner.startScan(null, builder.build(), mScanCallback);
        }
    }

    @Override
    public void stopLeScan() {
        BluetoothLeScanner scanner = mBleAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            scanner.stopScan(mScanCallback);
        }
    }
}
