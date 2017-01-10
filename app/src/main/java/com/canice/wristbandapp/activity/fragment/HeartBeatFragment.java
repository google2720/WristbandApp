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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.SmsReceivedService;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.MainActivity;
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.util.HintUtils;

public class HeartBeatFragment extends BaseFragment {

    private MainActivity mActivity;
    private BleController ble;
//    AlertDialog dialog;

    private TextView tv_heartbeat;
    private boolean fisrtSuccess;
    private Switch single;
    TextView rightTitle;
    ScaleAnimation myAnimation_Scale;
    ImageView iv_anim;
    AnimationSet animationSet = new AnimationSet(true);
    private BleCallback cb = new SimpleBleCallback() {

        @Override
        public void onCloseHeartRate() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
                    stopAnim();
                    rightTitle.setText(R.string.heartbeat_start);
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
                        if (single.isChecked()) {
                            ble.closeHeartRateAsync(10 * 1000);
                            fisrtSuccess = false;
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
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
                    rightTitle.setText(R.string.heartbeat_start);
                    ble.closeHeartRateAsync();
                    Log.i("HeartBeatFragment", "onGetHeartRateFailed");
                }
            });
        }
    };

    public void setRightTitle(TextView tv) {
        this.rightTitle = tv;
        rightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightTitle.getText().toString().equals(getString(R.string.heartbeat_start))) {
                    refreshData();
                    rightTitle.setText(R.string.heartbeat_stop);
                } else {
                    closeHeart();
                    stopAnim();
                    rightTitle.setText(R.string.heartbeat_start);
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
        single.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        myAnimation_Scale = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        myAnimation_Scale.setDuration(1000);
        myAnimation_Scale.setRepeatMode(Animation.RESTART);
        myAnimation_Scale.setRepeatCount(Integer.MAX_VALUE);
        animationSet.addAnimation(myAnimation_Scale);
        return root;
    }

    private void startAnim() {
        single.setEnabled(false);
        iv_anim.startAnimation(animationSet);

    }

    public void stopAnim() {
        if (single!=null){
            single.setEnabled(true);
        }

        if (myAnimation_Scale != null) {
            myAnimation_Scale.cancel();
        }
    }


    public void closeHeart() {
        ble.closeHeartRateAsync(0);

    }

    public void refreshData() {
        fisrtSuccess = true;
        if (BleController.getInstance().isDeviceReady()) {
//            BleController.getInstance().openHeartRateAsync(single.isChecked());
//            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//            builder.setMessage(R.string.heartbeat_testing);
//            builder.setOnCancelListener(new OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    ble.closeHeartRateAsync(0);
//                }
//            });
//            dialog = builder.create();
//            dialog.show();

            startAnim();
            BleController.getInstance().openHeartRateAsync(single.isChecked());
            rightTitle.setText(R.string.heartbeat_start);
            tv_heartbeat.setText("0");
        } else {
            HintUtils.showShortToast(getActivity(), getString(R.string.bind_faild));
        }
    }

    @Override
    public void handleMessage(Message msg) {
    }
}
