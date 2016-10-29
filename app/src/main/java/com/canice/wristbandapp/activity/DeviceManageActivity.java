package com.canice.wristbandapp.activity;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.ui.DeviceScanActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 设备管理界面
 * 
 * @author canice_yuan
 */
public class DeviceManageActivity extends BaseActivity implements OnClickListener {

    private View removeView;
    private View addView;
    private TextView deviceIdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.device_title);
        setLeftCloseBtnListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        deviceIdView = (TextView) findViewById(R.id.device_id);
        removeView = findViewById(R.id.rl_device_remove);
        removeView.setOnClickListener(this);
        addView = findViewById(R.id.rl_device_add);
        addView.setOnClickListener(this);
    }

    private void initData() {
        String address = BleController.getInstance().getBindedDeviceAddress();
        if (TextUtils.isEmpty(address)) {
            addView.setVisibility(View.VISIBLE);
            removeView.setVisibility(View.GONE);
            deviceIdView.setVisibility(View.GONE);
        } else {
            addView.setVisibility(View.GONE);
            removeView.setVisibility(View.VISIBLE);
            deviceIdView.setVisibility(View.GONE);
            deviceIdView.setText(getString(R.string.device_num, BleController.getInstance().getBindedDeviceId()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_device_remove:
                removeDevice();
                break;
            case R.id.rl_device_add:
                addDevice();
                break;
            default:
                break;
        }
    }

    private void addDevice() {
        Intent enableBtIntent = new Intent(this, DeviceScanActivity.class);
        startActivity(enableBtIntent);
    }

    private void removeDevice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.device_remove_tip);
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleController.getInstance().saveBindedDevice(null, null);
                BleController.getInstance().disconnect();
                initData();
            }
        });
        builder.show();
    }
}
