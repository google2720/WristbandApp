package com.canice.wristbandapp.activity.personaldata;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.activity.MainActivity;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.ResponseInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.HttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ProfileConfirmSettingActivity extends BaseActivity implements OnClickListener {
    private ProfileData userData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_confirm);
        userData = ProfileData.getInstance();
        initViews();
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.profile_title);
        findViewById(R.id.btn_confirm_set).setOnClickListener(this);
        TextView tv1 = (TextView) findViewById(R.id.tv_confirm_name);
        TextView tv2 = (TextView) findViewById(R.id.tv_confirm_age);
        TextView tv3 = (TextView) findViewById(R.id.tv_confirm_sex);
        TextView tv4 = (TextView) findViewById(R.id.tv_confirm_height);
        TextView tv5 = (TextView) findViewById(R.id.tv_confirm_weight);
        TextView tv6 = (TextView) findViewById(R.id.tv_confirm_step);
        TextView tv7 = (TextView) findViewById(R.id.tv_confirm_goal);
        tv1.setText(userData.getNickname());
        tv2.setText(userData.getAge());
        tv3.setText(userData.getSexText(this));
        tv4.setText(userData.getHigh() + getString(R.string.centimetre));
        tv5.setText(userData.getWeight() + getString(R.string.kilogram));
        tv6.setText(userData.getStepLong() + getString(R.string.centimetre));
        tv7.setText(getString(R.string.profile_goal_day1, userData.getGoal()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_set:
                setupUserinfo(false);
                break;
            default:
                break;
        }
    }

    private void setupUserinfo(final boolean flag) {
        final Activity context = this;
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(this));
        params.put(Constants.NICKNAME, userData.getNickname());
        params.put(Constants.SEX, userData.getSexString(this));
        params.put(Constants.HIGH, userData.getHigh());
        params.put(Constants.WEIGHT, userData.getWeight());
        params.put(Constants.AGE, userData.getAge());
        params.put(Constants.STEPLONG, userData.getStepLong());
        HttpUtil.get(Constants.SERVER_EDIT_USER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                HintUtils.showDiaog(context);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                ResponseInfo info = JSON.parseObject(content, ResponseInfo.class);
                HintUtils.dissDialog();
                if (TextUtils.isEmpty(content) || TextUtils.isEmpty(info.getRetCode())) {
                    HintUtils.showLongToast(context, getString(R.string.internet_error));
                } else if (info.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
                    HintUtils.showLongToast(context, info.getRetMsg());
                    UserController.saveUserInfo(context, userData);
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                    sendBroadcast(new Intent(Constants.QUIT_ONE_ACTIVTY));
                    BleController.getInstance().syncBaseDataAsync();
                } else if (info.getRetCode().equals(Constants.RETCODE_FAILURE)) {
                    HintUtils.showLongToast(context, info.getRetMsg());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                HintUtils.dissDialog();
                HintUtils.showLongToast(context, getString(R.string.internet_error));
            }
        });
    }
}
