/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canice.wristbandapp.ble.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.canice.wristbandapp.BuildConfig;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.util.HintUtils;

import java.util.ArrayList;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends BaseActivity implements OnItemClickListener {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private BleController mBle = BleController.getInstance();
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        String name = device.getName();
                        if (name != null && name.startsWith("BCD")) {
                            mLeDeviceListAdapter.addDevice(device);
                        }
                    }
                }
            });
        }

        @Override
        public void onLeScanFailed(int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        HintUtils.showShortToast(getApplicationContext(), R.string.device_scan_failed);
                        scanLeDevice(false);
                        mLeDeviceListAdapter.clear();
                        scanLeDevice(true);
                    }
                }
            });
        }

        @Override
        public void onGattDisconnected(final BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        HintUtils.showShortToast(getApplicationContext(), R.string.device_connect_failed);
                        scanLeDevice(false);
                        mLeDeviceListAdapter.clear();
                        scanLeDevice(true);
                    }
                }
            });
        }

        @Override
        public void onGattServicesDiscovered(final BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        if (dialog != null) {
                            dialog.setMessage(getString(R.string.device_binding));
                        }
                        mBle.bindDeviceAsync(false);
                    }
                }
            });
        }

        @Override
        public void onBindDeviceFailed(BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        HintUtils.showShortToast(getApplicationContext(), R.string.device_bind_failed);
                        scanLeDevice(false);
                        mLeDeviceListAdapter.clear();
                        scanLeDevice(true);
                    }
                }
            });
        }

        @Override
        public void onBindDeviceSuccess(BluetoothDevice device, final boolean firstBinded) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        mBle.setAutoConnect(true);
                        mBle.setAutoReconnect(true);
                        HintUtils.showShortToast(getApplicationContext(), R.string.device_bind_success);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (firstBinded && !BuildConfig.newFit) {
                            HintUtils.showShortToast(getApplicationContext(), R.string.device_bind_delay);
                        }
                        finish();
                    }
                }
            });
        }
    };
    private ProgressDialog dialog;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        setLeftBtnEnabled(true);
        setTitle(R.string.device_add);
        setLeftCloseBtnListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        mBle.addCallback(cb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!BleController.getInstance().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mListView.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device != null) {
            connect(device);
        }
    }

    private void connect(BluetoothDevice device) {
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.device_connecting));
        dialog.show();
        mBle.setAutoConnect(false);
        mBle.setAutoReconnect(false);
        mBle.disconnect();
        mBle.connect(device);
        myHandler.sendEmptyMessageDelayed(100, 60 * 1000);
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBle.removeCallback(cb);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mBle.startLeScan();
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mBle.stopLeScan();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflater;

        public LeDeviceListAdapter() {
            mLeDevices = new ArrayList<>();
            mInflater = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                notifyDataSetChanged();
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflater.inflate(R.layout.listitem_device, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceAddress.setText(device.getAddress());
            } else {
                viewHolder.deviceName.setText("Unknown device");
                viewHolder.deviceAddress.setText(device.getAddress());
            }
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}