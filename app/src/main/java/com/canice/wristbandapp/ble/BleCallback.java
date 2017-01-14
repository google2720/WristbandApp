package com.canice.wristbandapp.ble;

import android.bluetooth.BluetoothDevice;

import com.canice.wristbandapp.ble.data.HeartRateDataResult;
import com.canice.wristbandapp.ble.data.HistoryResult;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.canice.wristbandapp.ble.data.VersionInfoResult;

public interface BleCallback {

    void onGattDisconnected(BluetoothDevice device);

    void onGattConnected(BluetoothDevice device);

    void onGattServicesDiscovered(BluetoothDevice device);

    void onLeScan(BluetoothDevice device);

    void onLeScanFailed(int error);

    void onGattConnecting(BluetoothDevice device);

    void onBindDeviceFailed(BluetoothDevice device);

    void onBindDeviceSuccess(BluetoothDevice device, boolean firstBinded);

    void onFetchPedometerDataSuccess(PedometerDataResult pedometerDataResult);

    void onFetchPedometerDataFailed(PedometerDataResult pedometerDataResult);

    void onFetchVersionInfoFailed();

    void onFetchVersionInfoSuccess(VersionInfoResult result);

    void onFetchHistoryFailed(int error);

    void onFetchHistory(String address, HistoryResult result);

    void onFetchHistoryStart();

    void onFetchHistorySuccess();

    void onGetHeartRateSuccess(int value);

    void onGetHeartRateFailed();

    void onCloseHeartRate();

    void onSosNotify(byte[] data);

    void onRemoteRssi(BluetoothDevice device, int rssi);

    void onBluetoothOff();

    void onRefreshGoal();
}