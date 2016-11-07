package com.canice.wristbandapp;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

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

    private static final String TAG = "BcdActivity";
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
        super.onCreate();
        Lg.setLg(new AndroidLgImpl(this));
        Lg.setLevel(BuildConfig.LOG_LEVEL);
        Log.i(TAG, this + " onCreate");
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
        Log.i(TAG, this + " onCreate2");
        client = new AsyncHttpClient();
        Log.i(TAG, this + " onCreate3");
        BleController.getInstance().initialize(this);
        BleService.actionStart(this);
        Log.i(TAG, this + " onCreate4");
    }

    public static Dialog getDialog(Context context) {
        mProgressDialog = new CustomProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.please_wait));
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }
}
