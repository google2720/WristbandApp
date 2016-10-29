package com.canice.wristbandapp.activity.fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.MainActivity;
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.ble.db.BleDao;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.data.SleepLayout;
import com.canice.wristbandapp.model.ResponseInfo;
import com.canice.wristbandapp.model.SleepInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HttpUtil;

/**
 * 睡眠详情界面
 *
 * @author canice_yuan
 */
public class SleepFragment extends BaseFragment {

    private MainActivity mActivity;
    private SleepInfo sleepInfo = null;
    private Context mContext = null;
    private Button btn_sleep_refresh;
    private SleepLayout sleepView;
    private RotateAnimation animation;
    private boolean isAnimRun = false;
    private boolean isSaveStep = false;
    private BleController ble;
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onFetchHistorySuccess() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopAnim();
                    Log.i("SleepFragment", "onFetchHistorySuccess");
                    refreshData();
                }
            });
        }

        @Override
        public void onFetchHistoryFailed(int error) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopAnim();
                    Log.i("SleepFragment", "onFetchHistoryFailed");
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mContext = this.getActivity();
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
        View view = inflater.inflate(R.layout.layout_sleep, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            refreshData();
        }
    }

    @SuppressWarnings("deprecation")
    private void initViews(View view) {
        btn_sleep_refresh = (Button) view.findViewById(R.id.btn_sleep_refresh);
        btn_sleep_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SleepFragment", "sync sleep data start");
                startAnim();
                ble.fetchHistoryAsync();
            }
        });
        sleepView = (SleepLayout) view.findViewById(R.id.sleep);
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(1);
        animation.setDuration(1000);
    }

    private void refreshData() {
        new AsyncTask<Void, Void, SleepInfo>() {
            @Override
            protected SleepInfo doInBackground(Void... params) {
                return BleDao.getSleepTime(mContext, BleController.getInstance().getBindedDeviceAddress());
            }

            @Override
            protected void onPostExecute(SleepInfo result) {
                if (getActivity() != null) {
                    sleepInfo = result;
                    sleepView.setData(sleepInfo.ssmTime, sleepInfo.qsmTime);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void saveSleepData() {
        if (sleepInfo == null) {
            return;
        }
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(mActivity));
        params.put(Constants.EXDEVICEID, UserController.getExDeviceId(mActivity));
        params.put(Constants.SLEEPDATE, sleepInfo.sleepDate);
        params.put(Constants.QSMTIME, sleepInfo.qsmTime + "");
        params.put(Constants.QSMTIME, sleepInfo.ssmTime + "");

        HttpUtil.get(Constants.SERVER_SAVE_SLEEPDATA, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                ResponseInfo info = JSON.parseObject(content, ResponseInfo.class);
                if (TextUtils.isEmpty(content) || TextUtils.isEmpty(info.getRetCode())) {
                    Log.i("sleep", getString(R.string.internet_error));
                } else if (info.getRetCode().equals(Constants.RETCODE_SUCCESS)
                        || info.getRetCode().equals(Constants.RETCODE_FAILURE)) {
                    Log.i("sleep", info.getRetMsg());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                //HintUtils.showLongToast(mContext, getString(R.string.internet_error));
                Log.i("sleep", getString(R.string.internet_error));
            }
        });
    }

    private void startAnim() {
        if (!isAnimRun) {
            mHandler.post(animRunnable);
            isAnimRun = true;
        }
    }

    private void stopAnim() {
        if (isAnimRun) {
            isAnimRun = false;
            mHandler.removeCallbacks(animRunnable);
        }
    }

    Runnable animRunnable = new Runnable() {
        @Override
        public void run() {
            btn_sleep_refresh.startAnimation(animation);
            mHandler.postDelayed(animRunnable, 1000);
        }
    };

    @Override
    public void handleMessage(Message msg) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ble.removeCallback(cb);
    }
}
