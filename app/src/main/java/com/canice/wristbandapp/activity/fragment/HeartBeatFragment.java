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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.BloodPressHelper;
import com.canice.wristbandapp.ble.HeartRateHelper;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.util.HintUtils;

public class HeartBeatFragment extends BaseFragment {

    private BleController ble = BleController.getInstance();
    private HeartRateHelper heartRatehelper = ble.getHeartRateHelper();
    private BloodPressHelper bloodPresshelper = ble.getBloodPressHelper();
    private TextView heartbeatView;
    private TextView bloodPressureDbp;
    private TextView bloodPressureSbp;
    private Switch singleView;
    private TextView rightTitle;
    private ImageView animView;
    private Button bloodBtnView;
    private TextView bloodTestState;
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
            heartRatehelper.closeHeartRateAsync(0);
        }

        @Override
        public void onCloseHeartRateStart() {
            Log.i(TAG, "onCloseHeartRateStart");
            if (getActivity() == null) {
                return;
            }
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



        @Override
        public void onGetBloodPressStart() {
            if (getActivity() == null) {
                return;
            }
            bloodTestState.setText(getString(R.string.blood_pressure_test_ing));
            bloodBtnView.setText(getString(R.string.blood_stop));
            bloodPressureDbp.setText("0");
            bloodPressureSbp.setText("0");
        }

        @Override
        public void onCloseBloodPressStart() {
            if (getActivity() == null) {
                return;
            }
            bloodTestState.setText(getString(R.string.blood_start_stop_ing));
        }

        @Override
        public void onGetBloodPressFailed() {
            bloodPresshelper.closeBloodPressAsync(0);
        }


        @Override
        public void onCloseBloodPressFinish() {
            if (getActivity() == null) {
                return;
            }
            bloodTestState.setText(getString(R.string.blood_pressure_test));
            bloodBtnView.setText(getString(R.string.blood_start));
        }

        @Override
        public void onGetBloodPressSuccess(int sbp, int dbp) {
            bloodPressureDbp.setText(String.valueOf(dbp));
            bloodPressureSbp.setText(String.valueOf(sbp));
        }

    };

    public void startBloodTest(){
        if (!ble.isDeviceReady()) {
            HintUtils.showShortToast(getActivity(), getString(R.string.bind_faild));
            return;
        }

        int bloodPressState = bloodPresshelper.getState();
        int heartState = heartRatehelper.getState();
        if(heartState==HeartRateHelper.STATE_STOP){

            if (bloodPressState == BloodPressHelper.STATE_START) {
                bloodPresshelper.closeBloodPressAsync(0);
            } else if (bloodPressState == BloodPressHelper.STATE_STOP) {
                bloodPresshelper.openBloodPressAsync(true);
            } else {
                HintUtils.showShortToast(getActivity(), getString(R.string.blood_pressure_pre_stop));
            }

        }else {
            HintUtils.showShortToast(getActivity(), getString(R.string.blood_pressure_test_not_available_tip));
        }

    }

    public void setRightTitle(TextView tv) {
        this.rightTitle = tv;
        rightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ble.isDeviceReady()) {
                    HintUtils.showShortToast(getActivity(), getString(R.string.bind_faild));
                    return;
                }
                int heartRateState = heartRatehelper.getState();
                int bloodState = bloodPresshelper.getState();
                if(bloodState==BloodPressHelper.STATE_STOP) {

                    if (heartRateState == HeartRateHelper.STATE_START) {
                        heartRatehelper.closeHeartRateAsync(0);
                    } else if (heartRateState == HeartRateHelper.STATE_STOP) {
                        heartRatehelper.openHeartRateAsync(singleView.isChecked());
                    } else {
                        HintUtils.showShortToast(getActivity(), getString(R.string.heartbeat_pre_stop));
                    }

                }else {
                    HintUtils.showShortToast(getActivity(), getString(R.string.heart_rate_test_not_available_tip));
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
        bloodPressureDbp = (TextView) root.findViewById(R.id.blood_pressure_dbp);
        bloodPressureSbp = (TextView) root.findViewById(R.id.blood_pressure_sbp);
        bloodBtnView = (Button)root.findViewById(R.id.blood_test);
        bloodTestState = (TextView)root.findViewById(R.id.blood_test_des);
        animView = (ImageView) root.findViewById(R.id.iv_anim);
        singleView = (Switch) root.findViewById(R.id.single);
        singleView.setChecked(true);
        bloodBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBloodTest();
            }
        });

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
