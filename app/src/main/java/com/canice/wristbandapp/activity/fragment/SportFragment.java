package com.canice.wristbandapp.activity.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.MainActivity;
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.ble.data.PedometerDataResult;
import com.canice.wristbandapp.ble.db.BleDao;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.data.SportLayout;
import com.canice.wristbandapp.model.CurrentSportRecordInfo;
import com.canice.wristbandapp.model.ResponseInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.HttpUtil;
import com.canice.wristbandapp.util.Tools;
import com.canice.wristbandapp.widget.BatteryView;

public class SportFragment extends BaseFragment {

    private MainActivity mActivity;
    private Button refresh;
    private View view;
    private RotateAnimation animation;
    private boolean isAnimRun = false, isSaveStep = false;
    private BatteryView pb_battery;
    private TextView tv_steps;
    private TextView tv_distance;
    private TextView tv_cal;
    public CurrentSportRecordInfo recordInfo;
    private BleController ble;
    private SportLayout sportView;
    private TextView pg_battery;
    private TextView tv_fat;

    private BleCallback cb = new SimpleBleCallback() {

        @Override
        public void onFetchHistorySuccess() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopAnim();
                }
            });
        }

        @Override
        public void onFetchHistoryFailed(int error) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopAnim();
                }
            });
        }

        @Override
        public void onFetchPedometerDataSuccess(final PedometerDataResult pedometerDataResult) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pedometerDataResult != null) {
                        Log.i(TAG, "onFetchPedometerDataSuccess");
                        refreshCurrentData();
                    } else {
                        stopAnim();
                        Log.i(TAG, "onFetchPedometerDataFailed");
                        refreshCurrentData();
                    }
                }
            });
        }

        @Override
        public void onFetchPedometerDataFailed(PedometerDataResult pedometerDataResult) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onFetchPedometerDataFailed");
                    stopAnim();
                    refreshCurrentData();
                    if (ble.isBinded()) {
                        HintUtils.showShortToast(mActivity, R.string.sport_error_device_connecting);
                    } else {
                        HintUtils.showShortToast(mActivity, R.string.sport_error_device_no_bind);
                    }
                }
            });
        }

        @Override
        public void onRefreshGoal() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshCurrentData();
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        recordInfo = new CurrentSportRecordInfo();
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
        view = inflater.inflate(R.layout.layout_sport, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        sportView = (SportLayout) view.findViewById(R.id.sport);
        tv_steps = (TextView) view.findViewById(R.id.tv_steps);
        pb_battery = (BatteryView) view.findViewById(R.id.pb_battery);
        tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        tv_cal = (TextView) view.findViewById(R.id.tv_cal);
        tv_fat = (TextView) view.findViewById(R.id.tv_fat);
        pg_battery = (TextView) view.findViewById(R.id.pg_battery);
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(1);
        animation.setDuration(1000);
        refresh = (Button) view.findViewById(R.id.btn_sport_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "start sync sport data");
                startAnim();
                ble.fetchDataAsync();
            }
        });
        refreshCurrentData();
    }

    public void refreshCurrentData() {
        new AsyncTask<Void, Void, CurrentSportRecordInfo>() {
            @Override
            protected CurrentSportRecordInfo doInBackground(Void... params) {
                return BleDao.getCurrentData(mActivity, ble.getBindedDeviceAddress());
            }

            @Override
            protected void onPostExecute(CurrentSportRecordInfo result) {
                if (getActivity() == null) {
                    return;
                }
                recordInfo = result;
                tv_steps.setText(String.valueOf(recordInfo.stepNum));
                tv_distance.setText(Tools.format(recordInfo.distance / 100d));
                tv_cal.setText(Tools.format(recordInfo.cal / 1000d));
                tv_fat.setText(Tools.format(recordInfo.cal / 9000d));
                pb_battery.setProgress(recordInfo.battery);
                pg_battery.setText(recordInfo.battery + "%");
                sportView.setData(recordInfo.stepNum, UserController.getGoal(mActivity));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void saveStepNum() {
        isSaveStep = true;
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(getActivity()));
        params.put(Constants.RECORDDATE, recordInfo.date);
        params.put(Constants.STEPNUM, recordInfo.stepNum + "");
        HttpUtil.get(Constants.SERVER_SAVE_STEPNUM, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                ResponseInfo info = JSON.parseObject(content, ResponseInfo.class);
                if (TextUtils.isEmpty(content) || TextUtils.isEmpty(info.getRetCode())) {
                    Log.i(TAG, getString(R.string.internet_error));
                } else if (info.getRetCode().equals(Constants.RETCODE_SUCCESS) || info.getRetCode().equals(Constants.RETCODE_FAILURE)) {
                    Log.i(TAG, info.getRetMsg());
                }
                isSaveStep = false;
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                Log.i(TAG, getString(R.string.internet_error));
                isSaveStep = false;
            }
        });
    }

    @Override
    public void handleMessage(Message msg) {
    }

    private void startAnim() {
        refresh.setEnabled(false);
        if (!isAnimRun) {
            mHandler.post(animRunnable);
            isAnimRun = true;
        }
    }

    private void stopAnim() {
        refresh.setEnabled(true);
        if (isAnimRun) {
            isAnimRun = false;
            mHandler.removeCallbacks(animRunnable);
        }
    }

    Runnable animRunnable = new Runnable() {
        @Override
        public void run() {
            refresh.startAnimation(animation);
            mHandler.postDelayed(animRunnable, 1000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        ble.removeCallback(cb);
    }
}
