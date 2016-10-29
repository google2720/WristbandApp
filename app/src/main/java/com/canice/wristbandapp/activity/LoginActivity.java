package com.canice.wristbandapp.activity;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.APP;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.personaldata.ProfileSexSettingActivity;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.LoginResponseInfo;
import com.canice.wristbandapp.model.UserInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

/**
 * 登录界面
 *
 * @author canice_yuan
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

    private SharedPreferences shared;
    private boolean login_state = false;
    private EditText et_username, et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, this + " onCreate");
        setContentView(R.layout.layout_login);
        Log.i(TAG, this + " onCreate2");
        registerReceiver(mReceiver, new IntentFilter(Constants.QUIT_ONE_ACTIVTY));
        shared = getSharedPreferences(Constants.SHARED_NAME, MODE_PRIVATE);
        login_state = shared.getBoolean(Constants.LOGIN_STATE, false);
        if (login_state) {
            final String name = UserController.getEmail(this);
            final String pwd = UserController.getPassword(this);
            if (name == null || pwd == null) {
                initViews();
            } else {
                login_verify(name, pwd);
            }
        } else {
            initViews();
        }
        Log.i(TAG, this + " onCreate3");
    }

    private void initViews() {
        setLeftBtnVisibility(View.GONE);
        setTitle(R.string.login_title_login);

        et_username = (EditText) findViewById(R.id.edittext_login_username);
        et_username.setText(UserController.getEmail(this));
        et_pwd = (EditText) findViewById(R.id.edittext_login_pwd);
        et_pwd.setText(UserController.getPassword(this));
        findViewById(R.id.tv_login_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_login_question).setOnClickListener(this);
    }

    private void login(View view) {
        if (verification()) {
            login_verify(et_username.getText().toString(), et_pwd.getText().toString());
            // final RequestParams requestParams = new RequestParams();
            // requestParams.put(Constants.EMAIL,
            // et_username.getText().toString());
            // requestParams.put(Constants.PASSWORD,
            // et_pwd.getText().toString());
            // HttpUtil.get(Constants.SERVER_LOGIN + requestParams, new
            // LoginResponseHandler(LoginActivity.this));
        } else {
            final TranslateAnimation animation = new TranslateAnimation(0, 20, 0, 0);
            animation.setInterpolator(new CycleInterpolator(50));
            animation.setDuration(1000);
            view.startAnimation(animation);
        }
    }

    private void login_verify(String name, String pwd) {
        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constants.EMAIL, name);
        requestParams.put(Constants.PASSWORD, pwd);
        APP.client.get(Constants.SERVER_LOGIN + requestParams, new LoginResponseHandler(this, name, pwd));
    }

    private boolean verification() {
        String username = et_username.getText().toString();
        String pwd = et_pwd.getText().toString();
        if (!Tools.isValidEmail(username)) {
            HintUtils.showLongToast(this, getString(R.string.login_tips_username));
            return false;
        }
        if (!Tools.isQualifiedPwd(pwd)) {
            HintUtils.showLongToast(this, getString(R.string.login_tips_pwd));
            return false;
        }

        return true;
    }

    public class LoginResponseHandler extends AsyncHttpResponseHandler {

        private Context mContext;
        private String email;
        private String pw;

        public LoginResponseHandler(Context mContext, String email, String pw) {
            this.mContext = mContext;
            this.email = email;
            this.pw = pw;
        }

        @Override
        public void onStart() {
            super.onStart();
            HintUtils.showDiaog(mContext, getString(R.string.login_tips_logining));
        }

        @Override
        public void onSuccess(int statusCode, String content) {
            super.onSuccess(statusCode, content);
            Log.i("yy", "login onSuccess:" + content);
            HintUtils.dissDialog();
            LoginResponseInfo loginResponseInfo = JSON.parseObject(content, LoginResponseInfo.class);
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(loginResponseInfo.getRetCode())
                    && loginResponseInfo.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
                saveLoginState(email, pw);
                saveUser(loginResponseInfo.getUserInfo());
                if (TextUtils.isEmpty(loginResponseInfo.getUserInfo().getSex())) {
                    // 没有设置个人信息的，需要先进行个人信息设置
                    Intent intent = new Intent(mContext, ProfileSexSettingActivity.class);
                    intent.putExtra("setup", true);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
                LoginActivity.this.finish();
            } else {
                if (login_state) {
                    login_failure(mContext);
                } else if (!TextUtils.isEmpty(loginResponseInfo.getRetMsg())) {
                    HintUtils.showLongToast(mContext, loginResponseInfo.getRetMsg());
                } else {
                    HintUtils.showLongToast(mContext, getString(R.string.login_tips_login_error));
                }
                // Toast.makeText(mContext,
                // getString(R.string.login_tips_login_error),
                // Toast.LENGTH_LONG).show();

            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            HintUtils.dissDialog();
            if (login_state) {
                login_failure(mContext);
            } else {
                HintUtils.showLongToast(mContext, getString(R.string.login_tips_login_error));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                this.startActivity(intent);
                break;
            case R.id.btn_login:
                login(view);
                break;
            case R.id.tv_login_question:
                Intent intent1 = new Intent(this, FindPasswordActivity.class);
                this.startActivity(intent1);
                break;
        }
    }

    private void saveUser(UserInfo info) {
        UserController.saveAllUserInfo(this, info);
    }

    private void saveLoginState(String email, String pw) {
        if (login_state) {
            return;
        }
        UserController.saveLoginState(this, email, pw, true);
    }

    private void login_failure(Context mContext) {
        initViews();
        login_state = false;
        HintUtils.showLongToast(mContext, getString(R.string.login_tips_login_error1));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.QUIT_ONE_ACTIVTY)) {
                LoginActivity.this.finish();
            }
        }
    };

    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    ;
}
