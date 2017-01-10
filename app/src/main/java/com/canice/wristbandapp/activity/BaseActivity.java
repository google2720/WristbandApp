package com.canice.wristbandapp.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.ble.BleCallback;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.BleError;
import com.canice.wristbandapp.ble.HistoryController;
import com.canice.wristbandapp.ble.SimpleBleCallback;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.widget.HeadView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author canice_yuan
 */
public class BaseActivity extends AppCompatActivity {

    protected String TAG = this.getClass().getSimpleName();

    /**
     * 头部 View
     */
    private HeadView mHeadView;
    /**
     * 核心内容
     */
    protected FrameLayout mLinearLayoutContent;

    /**
     * 布局实例器
     */
    private LayoutInflater mLayoutInflater;

    protected Handler mHandler = null;

    private TextView mTextViewLog;
    private LinearLayout mLayoutDebug;
    private AlertDialog dialog;
    private BleCallback cb = new SimpleBleCallback() {

        @Override
        public void onBluetoothOff() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissHistoryDialog();
                }
            });
        }

        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissHistoryDialog();
                }
            });
        }

        @Override
        public void onFetchHistoryStart() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        showHistoryDialog();
                    }
                }
            });
        }

        @Override
        public void onFetchHistorySuccess() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("SleepFragment", "onFetchHistorySuccess");
                    if (!isFinishing()) {
                        dismissHistoryDialog();
                        HintUtils.showShortToast(BaseActivity.this, R.string.sync_data_successful);
                    }
                }
            });
        }

        @Override
        public void onFetchHistoryFailed(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onFetchHistoryFailed");
                    if (!isFinishing()) {
                        dismissHistoryDialog();
                        if (error == BleError.TIME_OUT) {
                            HintUtils.showShortToast(BaseActivity.this, R.string.sync_data_timeout);
                        } else {
                            HintUtils.showShortToast(BaseActivity.this, R.string.sync_data_failed);
                        }
                    }
                }
            });
        }
    };

    private void dismissHistoryDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void showHistoryDialog() {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.sync_data);
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.show();
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (UserController.getLanguage(this) == SetLanguageActivity.CN) {
            config.locale = Locale.CHINA;
        } else if (UserController.getLanguage(this) == SetLanguageActivity.EN) {
            config.locale = Locale.ENGLISH;
        } else {
            if (!"en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
                config.locale = Locale.CHINA;
            }
        }
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_include_title);
        mLayoutInflater = LayoutInflater.from(this);
        // 初始化 headView
        mHeadView = (HeadView) findViewById(R.id.headview);
        mHeadView.setLeftBtnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftButtonClick();
            }
        });
        mHeadView.setRightBtnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightButtonClick();
            }
        });

        // 初始化内容布局
        mLinearLayoutContent = (FrameLayout) findViewById(R.id.linearlayout_container);

        mHeadView.setLeftCloseBtnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLayoutDebug = (LinearLayout) findViewById(R.id.ll_debug);
        mTextViewLog = (TextView) findViewById(R.id.tv_log);
        mLayoutDebug.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BleController ble = BleController.getInstance();
        int state = ble.getHistoryController().getState();
        Log.i(TAG, "history state:" + state);
        if (state == HistoryController.STATE_SUCCESS || state == HistoryController.STATE_FAILED) {
            dismissHistoryDialog();
        } else if (state == HistoryController.STATE_START || state == HistoryController.STATE_FETCHING) {
            showHistoryDialog();
        }
        ble.addCallback(cb);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BleController ble = BleController.getInstance();
        ble.removeCallback(cb);
    }

    protected void onRightButtonClick() {

    }

    protected void onLeftButtonClick() {
        finish();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                BaseActivity.this.handleMessage(msg);
            }
        };
    }

    public void handleMessage(Message msg) {
        Log.w(TAG, "handleMessage <" + this.getClass().getSimpleName() + "> ");
    }

    public void goneHeadView() {
        mHeadView.setVisibility(View.GONE);
    }

    public void visibleHeadView() {
        mHeadView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取左边按钮的背景资源ID
     *
     * @return
     */
    public int getLeftBtnBGResource() {
        return mHeadView.getLeftBtnBGResource();
    }

    public void setLeftBtnEnabled(boolean enabled) {
        mHeadView.getBtnLeft().setEnabled(enabled);
    }

    public void setLeftBtnVisibility(int visible) {
        mHeadView.setLeftBtnVisibility(visible);
    }

    public void setLeftBtnClickListener(OnClickListener listener) {
        mHeadView.setLeftBtnClickListener(listener);
    }

    public void setRightBtnEnabled(boolean enabled) {
        mHeadView.setRightBtnEnabled(enabled);
    }

    public void setRightBtnVisibility(int visible) {
        mHeadView.setRightBtnVisibility(visible);
    }

    public void setRightBtnImageResId(int resource) {
        mHeadView.setRightBtnImageResId(resource);
    }

    public void setRightBtnDescription(String desc) {
        mHeadView.setRightBtnDesc(desc);
    }

    public void setRightBtnText(int resid) {
        mHeadView.setRightBtnText(resid);
    }
    public TextView getRightTitle(){
        return mHeadView.getRightTitle();
    }
    public void setRightBtnText(String text) {
        mHeadView.setRightBtnText(text);
    }

    public void setRightBtnTextClick(View.OnClickListener clickListener) {
        mHeadView.setRightBtnTextClick(clickListener);
    }

    public void setRightBtnTextVisible(int visible) {
        mHeadView.setRightBtnTextVisible(visible);
    }

    public void setRightBtnClickListener(OnClickListener listener) {
        mHeadView.setRightBtnClickListener(listener);
    }

    public void addRightView(View v) {
        mHeadView.addRightView(v);
    }

    public ViewGroup getRightView() {
        return mHeadView.getRightView();
    }

    /**
     * 获取右边按钮的背景资源ID
     *
     * @return
     */
    public int getRightBtnBGResource() {
        return mHeadView.getRightBtnBGResource();
    }

    @Override
    public void setTitle(int resource) {
        try {
            if (resource > 0) {
                mHeadView.setTitle(resource);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setTitle(String title) {
        if (null != title) {
            mHeadView.setTitle(title);
        }
    }

    @Override
    public void setTitleColor(int color) {
        mHeadView.setTitleColor(color);
    }

    public void setHeadBackgroudColor(int color) {
        mHeadView.setHeadBackground(color);
    }

    public void setTitleRightDrawable(Drawable resource) {
        mHeadView.setTitleRightDrawable(resource);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = mLayoutInflater.inflate(layoutResID, null);
        @SuppressWarnings("deprecation")
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        mLinearLayoutContent.removeAllViews();
        mLinearLayoutContent.addView(view, lp);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mLinearLayoutContent.removeAllViews();
        mLinearLayoutContent.addView(view, params);
    }

    @Override
    public void setContentView(View view) {
        @SuppressWarnings("deprecation")
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        mLinearLayoutContent.removeAllViews();
        mLinearLayoutContent.addView(view, lp);
    }

    protected int getContendId() {
        return R.id.linearlayout_container;
    }

    /**
     * @param aClass Activity类
     * @return void
     * @Title: actionStart
     * @Description: 启动某个Activity
     */
    public void actionStart(Class<?> aClass) {
        BaseActivity.actionStart(this, aClass);
    }

    /**
     * @param context 你懂得
     * @param aClass  Activity类
     * @return void
     * @Title: actionStart
     * @Description: 构建并启动某个Activity
     */
    public static void actionStart(Context context, Class<?> aClass) {
        if (context == null || aClass == null) {
            throw new AssertionError("No context or no class");
        }
        Intent intent = new Intent(context, aClass);
        context.startActivity(intent);
    }

    public HeadView getHeadView() {
        return mHeadView;
    }

    public void cancelTask(List<AsyncTask<?, ?, ?>> tasks) {
        if (tasks == null || tasks.size() == 0) {
            return;
        }

        for (AsyncTask<?, ?, ?> task : tasks) {
            if (task != null && AsyncTask.Status.RUNNING == task.getStatus()) {
                task.cancel(true);
            }
        }
    }

    public void setHeadViewVisibility(int visible) {
        mHeadView.setVisibility(visible);
    }

    public void setLeftCloseBtnVisibility(int visible) {
        mHeadView.setLeftCloseBtnVisibility(visible);
    }

    public void setLeftCloseBtnListener(OnClickListener listener) {
        mHeadView.setLeftCloseBtnClickListener(listener);
    }

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat logTime = new SimpleDateFormat("mm:ss");
    private ArrayList<String> logList = new ArrayList<String>();

    /**
     * 在屏幕上打印出日志
     *
     * @param log
     */
    public void logOnScreen(String log) {
        if (logList.size() > 16) {
            logList.remove(0);
        }
        logList.add("\r\n" + logTime.format(new Date()) + " > " + log);
        StringBuffer sb = new StringBuffer();
        for (String s : logList) {
            sb.append(s);
        }
        mTextViewLog.setText(sb.toString());
    }

    /**
     * 清除window日志
     */
    public void cleanWindowLog() {
        mTextViewLog.setText("");
    }

    /**
     * 获取debug布局
     *
     * @return
     */
    public LinearLayout getDebugLayout() {
        return mLayoutDebug;
    }
}
