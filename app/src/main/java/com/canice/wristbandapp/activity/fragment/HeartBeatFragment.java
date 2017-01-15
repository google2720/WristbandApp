package com.canice.wristbandapp.activity.fragment;

import android.bluetooth.BluetoothAdapter;
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
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.HeartRateHelper;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.util.HintUtils;

public class HeartBeatFragment extends BaseFragment {

    private BleController ble = BleController.getInstance();
    private HeartRateHelper helper = ble.getHeartRateHelper();
    private TextView heartbeatView;
    private Switch singleView;
    private TextView rightTitle;
    private ImageView animView;
    private AnimationSet animationSet = new AnimationSet(true);
    private BleCallback cb = new SimpleBleCallback() {

        @Override
        public void onGetHeartRateStart() {
            Log.i(TAG, "onGetHeartRateStart");
            startAnim();
        }

        @Override
        public void onGetHeartRateSuccess(final int value) {
            Log.i(TAG, "onGetHeartRateSuccess " + value);
            heartbeatView.setText(String.valueOf(value));
        }

        @Override
        public void onGetHeartRateFailed() {
            Log.i(TAG, "onGetHeartRateFailed");
            stopAnim();
        }

        @Override
        public void onCloseHeartRateStart() {
            Log.i(TAG, "onCloseHeartRateStart");
            if (animView != null) {
                animView.clearAnimation();
            }
            if (rightTitle != null) {
                rightTitle.setText(getString(R.string.heartbeat_pre_stop));
            }
        }

        @Override
        public void onCloseHeartRateFinish() {
            Log.i(TAG, "onCloseHeartRateFinish");
            stopAnim();
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
                int state = helper.getState();
                if (state == HeartRateHelper.STATE_START) {
                    helper.closeHeartRateAsync(0);
                } else if (state == HeartRateHelper.STATE_STOP) {
                    helper.openHeartRateAsync(singleView.isChecked());
                } else {
                    HintUtils.showShortToast(getActivity(), getString(R.string.heartbeat_pre_stop));
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        heartbeatView = (TextView) root.findViewById(R.id.tv_heartbeat);
        animView = (ImageView) root.findViewById(R.id.iv_anim);
        singleView = (Switch) root.findViewById(R.id.single);
        singleView.setChecked(true);

        ScaleAnimation a = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        a.setDuration(1000);
        a.setRepeatMode(Animation.RESTART);
        a.setRepeatCount(Integer.MAX_VALUE);
        animationSet.addAnimation(a);
        return root;
    }

    private void startAnim() {
        singleView.setEnabled(false);
        animView.startAnimation(animationSet);
        rightTitle.setText(R.string.heartbeat_stop);
        heartbeatView.setText("0");
    }

    public void stopAnim() {
        stopAnim(true);
    }

    public void stopAnim(boolean changeRightView) {
        if (!isAdded() || getActivity() == null) {
            return;
        }
        if (singleView != null) {
            singleView.setEnabled(true);
        }
        if (animView != null) {
            animView.clearAnimation();
        }
        if (rightTitle != null && changeRightView) {
            rightTitle.setText(getString(R.string.heartbeat_start));
        }
    }

    @Override
    public void handleMessage(Message msg) {
    }
}
