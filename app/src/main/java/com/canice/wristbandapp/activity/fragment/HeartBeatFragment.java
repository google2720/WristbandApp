package com.canice.wristbandapp.activity.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Switch;
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
    private TextView tv_heartbeat;
    private boolean firstSuccess;
    private Switch single;
    private TextView rightTitle;
    private ScaleAnimation myAnimation_Scale;
    private ImageView iv_anim;
    private AnimationSet animationSet = new AnimationSet(true);
    private BleCallback cb = new SimpleBleCallback() {

        @Override
        public void onBluetoothOff() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onBluetoothOff");
                    stopAnim();
                }
            });
        }

        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onGattDisconnected");
                    stopAnim();
                }
            });
        }

        @Override
        public void onCloseHeartRate() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onCloseHeartRate");
                    stopAnim();
                }
            });
        }

        @Override
        public void onGetHeartRateSuccess(final int value) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onGetHeartRateSuccess " + value);
                    tv_heartbeat.setText(String.valueOf(value));
                    if (firstSuccess) {
                        firstSuccess = false;
                        if (single.isChecked()) {
                            ble.closeHeartRateAsync(10 * 1000);
                        }
                    }
                }
            });
        }

        @Override
        public void onGetHeartRateFailed() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onGetHeartRateFailed");
                    rightTitle.setText(R.string.heartbeat_start);
                    ble.closeHeartRateAsync();
                }
            });
        }
    };

    public void setRightTitle(TextView tv) {
        this.rightTitle = tv;
        rightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ble.isDeviceReady()) {
                    HintUtils.showShortToast(getActivity(), getString(R.string.bind_faild));
                    return;
                }
                if (rightTitle.getText().toString().equals(getString(R.string.heartbeat_start))) {
                    openHeart();
                } else {
                    closeHeart();
                }
            }
        });
    }

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
        iv_anim = (ImageView) root.findViewById(R.id.iv_anim);
        single = (Switch) root.findViewById(R.id.single);
        single.setChecked(true);

        myAnimation_Scale = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        myAnimation_Scale.setDuration(1000);
        myAnimation_Scale.setRepeatMode(Animation.RESTART);
        myAnimation_Scale.setRepeatCount(Integer.MAX_VALUE);
        animationSet.addAnimation(myAnimation_Scale);
        return root;
    }

    private void startAnim() {
        single.setEnabled(false);
        iv_anim.startAnimation(animationSet);
        rightTitle.setText(R.string.heartbeat_stop);
        tv_heartbeat.setText("0");
    }

    public void stopAnim() {
        if (single != null) {
            single.setEnabled(true);
        }
        if (myAnimation_Scale != null) {
            myAnimation_Scale.cancel();
        }
        if (rightTitle != null) {
            rightTitle.setText(getString(R.string.heartbeat_start));
        }
    }

    public void closeHeart() {
        ble.closeHeartRateAsync(0);
        stopAnim();
    }

    public void openHeart() {
        firstSuccess = true;
        BleController.getInstance().openHeartRateAsync(single.isChecked());
        startAnim();
    }

    @Override
    public void handleMessage(Message msg) {
    }
}
