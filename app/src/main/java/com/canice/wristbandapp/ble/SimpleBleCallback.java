package com.canice.wristbandapp.ble;

import android.bluetooth.BluetoothDevice;

import com.canice.wristbandapp.ble.data.HeartRateDataResult;
import com.canice.wristbandapp.ble.data.HistoryResult;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.canice.wristbandapp.ble.data.VersionInfoResult;

public class SimpleBleCallback implements BleCallback {

    @Override
    public void onLeScan(BluetoothDevice device) {
    }

    @Override
    public void onLeScanFailed(int error) {
    }

    @Override
    public void onGattConnecting(BluetoothDevice device) {
    }

    @Override
    public void onGattConnected(BluetoothDevice device) {
    }

    @Override
    public void onGattDisconnected(BluetoothDevice device) {
    }

    @Override
    public void onGattServicesDiscovered(BluetoothDevice device) {
    }

    @Override
    public void onBindDeviceFailed(BluetoothDevice device) {
    }

    @Override
    public void onBindDeviceSuccess(BluetoothDevice device, boolean firstBinded) {
    }

    @Override
    public void onFetchPedometerDataSuccess(PedometerDataResult pedometerDataResult) {

    }

    @Override
    public void onFetchPedometerDataFailed(PedometerDataResult pedometerDataResult) {

    }

    @Override
    public void onFetchVersionInfoFailed() {
    }

    @Override
    public void onFetchVersionInfoSuccess(VersionInfoResult result) {
    }

    @Override
    public void onFetchHistoryFailed(int error) {
    }

    @Override
    public void onFetchHistory(String address, HistoryResult result) {
    }

    @Override
    public void onFetchHistoryStart() {
    }

    @Override
    public void onFetchHistorySuccess() {
    }

    @Override
    public void onGetHeartRateSuccess(int value) {
    }

    @Override
    public void onGetHeartRateFailed() {
    }


    @Override
    public void onCloseHeartRate() {
    }

    @Override
    public void onSosNotify(byte[] data) {
    }

    @Override
    public void onRemoteRssi(BluetoothDevice device, int rssi) {
    }

    @Override
    public void onBluetoothOff() {
    }
}