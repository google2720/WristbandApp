package com.canice.wristbandapp;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;

import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.BleService;
import com.canice.wristbandapp.core.net.AsyncHttpClient;
import com.canice.wristbandapp.widget.CustomProgressDialog;
import com.github.yzeaho.log.AndroidLgImpl;
import com.github.yzeaho.log.Lg;

/**
 * 项目启动Appliaction
 *
 * @author canice_yuan
 */
public class APP extends Application {

    private static final String TAG = "APP";
    private static APP mInstance;
    public static AsyncHttpClient client = null;
    private static CustomProgressDialog mProgressDialog;

    public static APP getInstance() {
        if (mInstance == null) {
            mInstance = new APP();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        mInstance = this;
        client = new AsyncHttpClient();
        super.onCreate();
        Lg.setLg(new AndroidLgImpl(this));
        Lg.setLevel(BuildConfig.LOG_LEVEL);
        Lg.i(TAG, this + " onCreate " + BuildConfig.VERSION_NAME + " " + BuildConfig.VERSION_CODE + " " + Build.FINGERPRINT);
        String curProcessName = getCurProcessName(this);
        String processName = getApplicationInfo().processName;
        Lg.i(TAG, "curProcessName:" + curProcessName + ", processName:" + processName);
        // 主进程才初始化界面相关的数据
        if (processName.equals(curProcessName)) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
            BleController.getInstance().initialize(this);
            BleService.actionStart(this);
        }
    }

    public static Dialog getDialog(Context context) {
        mProgressDialog = new CustomProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.please_wait));
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }

    /**
     * 获取当前进程的进程名字
     */
    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo p : am.getRunningAppProcesses()) {
            if (p.pid == pid) {
                return p.processName;
            }
        }
        return null;
    }
}
