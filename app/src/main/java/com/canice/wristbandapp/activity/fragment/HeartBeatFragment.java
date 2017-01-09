package com.canice.wristbandapp.activity.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.MainActivity;
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.util.HintUtils;

public class HeartBeatFragment extends BaseFragment {

    private MainActivity mActivity;
    private BleController ble;
    AlertDialog dialog;

    private TextView tv_heartbeat;
    private boolean fisrtSuccess;
    private BleCallback cb = new SimpleBleCallback() {

        @Override
        public void onCloseHeartRate() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        }

        @Override
        public void onGetHeartRateSuccess(final int value) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("HeartBeatFragment", "onGetHeartRateSuccess " + value);
                    tv_heartbeat.setText(String.valueOf(value));
                    if (fisrtSuccess) {
                        ble.closeHeartRateAsync(10 * 1000);
                        fisrtSuccess = false;
                    }
                }
            });
        }

        @Override
        public void onGetHeartRateFailed() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    ble.closeHeartRateAsync();
                    Log.i("HeartBeatFragment", "onGetHeartRateFailed");
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        ble = BleController.getInstance();
        ble.addCallback(cb);
        // 如果没有打开蓝牙，先提醒用户打开蓝牙
        if (!ble.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_heartbeat, container, false);
        tv_heartbeat = (TextView) root.findViewById(R.id.tv_heartbeat);
        return root;
    }

    public void refreshData() {
        fisrtSuccess = true;
        if (BleController.getInstance().isDeviceReady()) {
            BleController.getInstance().openHeartRateAsync();
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(R.string.heartbeat_testing);
            builder.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ble.closeHeartRateAsync(0);
                }
            });
            dialog = builder.create();
            dialog.show();
        } else {
            HintUtils.showShortToast(getActivity(), getString(R.string.bind_faild));
        }
    }

    @Override
    public void handleMessage(Message msg) {
    }
}
