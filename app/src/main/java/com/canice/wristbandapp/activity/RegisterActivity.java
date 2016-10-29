package com.canice.wristbandapp.activity;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.APP;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.personaldata.ProfileSexSettingActivity;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.LoginResponseInfo;
import com.canice.wristbandapp.model.RegisterResponseInfo;
import com.canice.wristbandapp.model.UserInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.Tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

/**
 * 注册界面
 * 
 * @author canice_yuan
 */
public class RegisterActivity extends BaseActivity {

    private EditText et_email, et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        initViews();
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.register_title_register);
        setLeftCloseBtnListener(new OnClickListener() {

            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });

        et_email = (EditText) findViewById(R.id.edittext_register_email);
        et_pwd = (EditText) findViewById(R.id.edittext_register_pwd);
        findViewById(R.id.btn_login_login).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register(view);
            }
        });
    }

    private void register(View view) {
        if (verification()) {
            final RequestParams requestParams = new RequestParams();
            String email = et_email.getText().toString().trim();
            String pw = et_pwd.getText().toString();
            requestParams.put(Constants.EMAIL, email);
            requestParams.put(Constants.PASSWORD, pw);
            APP.client.get(Constants.SERVER_REGISTER + requestParams, new RegisterResponseHandler(this, email, pw));
        } else {
            final TranslateAnimation animation = new TranslateAnimation(0, 20, 0, 0);
            animation.setInterpolator(new CycleInterpolator(50));
            animation.setDuration(1000);
            view.startAnimation(animation);
        }
    }

    /**
     * 输入校验
     * 
     * @author canice_yuan
     */
    private boolean verification() {
        String username = et_email.getText().toString();
        String pwd = et_pwd.getText().toString();

        if (!Tools.isValidEmail(username)) {
            HintUtils.showShortToast(this, getString(R.string.login_tips_username));
            return false;
        }
        if (!Tools.isQualifiedPwd(pwd)) {
            HintUtils.showShortToast(this, getString(R.string.login_tips_pwd));
            return false;
        }

        return true;
    }

    /**
     * 注册回调
     * 
     * @author canice_yuan
     */
    public class RegisterResponseHandler extends AsyncHttpResponseHandler {

        private Context mContext;
        private String email;
        private String pw;

        public RegisterResponseHandler(Context mContext, String email, String pw) {
            this.mContext = mContext;
            this.email = email;
            this.pw = pw;
        }

        @Override
        public void onStart() {
            super.onStart();
            HintUtils.showDiaog(mContext, getString(R.string.login_tips_registering));
        }

        @Override
        public void onSuccess(int statusCode, String content) {
            super.onSuccess(statusCode, content);
            HintUtils.dissDialog();
            Log.i("yy", "r onSuccess:" + content);
            RegisterResponseInfo registerResponseInfo = JSON.parseObject(content, RegisterResponseInfo.class);
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(registerResponseInfo.getRetCode())
                    && registerResponseInfo.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
               login(mContext,email,pw);
            } else {
                if (!TextUtils.isEmpty(registerResponseInfo.getRetMsg())) {
                    HintUtils.showShortToast(mContext, registerResponseInfo.getRetMsg());
                } else {
                    HintUtils.showShortToast(mContext, getString(R.string.login_tips_register_error));
                }
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            HintUtils.dissDialog();
            HintUtils.showShortToast(mContext, getString(R.string.login_tips_register_error));
        }
    }

    private void saveLoginState(String email, String pw) {
        UserController.saveLoginState(this, email, pw, true);
    }
    private void login(Context context ,String email,String pw){
        saveLoginState(email, pw);
        RegisterActivity.this.sendBroadcast(new Intent(Constants.QUIT_ONE_ACTIVTY));
        HintUtils.showShortToast(context, R.string.login_tips_register_success);
        String name = UserController.getEmail(this);
        String pwd = UserController.getPassword(this);
        if (name == null || pwd == null) {
            gotoLogin();
        } else {
            login_verify(name, pwd);
        }
    }
    private void gotoLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
        RegisterActivity.this.finish();
    }

    private void login_verify(String name, String pwd) {
        final RequestParams requestParams = new RequestParams();
        requestParams.put(Constants.EMAIL, name);
        requestParams.put(Constants.PASSWORD, pwd);
        APP.client.get(Constants.SERVER_LOGIN + requestParams, new LoginResponseHandler(this, name, pwd));
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
          //  HintUtils.showDiaog(mContext, getString(R.string.login_tips_logining));
        }

        @Override
        public void onSuccess(int statusCode, String content) {
            super.onSuccess(statusCode, content);
            Log.i("yy", "login onSuccess:" + content);
           // HintUtils.dissDialog();
            LoginResponseInfo loginResponseInfo = JSON.parseObject(content, LoginResponseInfo.class);
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(loginResponseInfo.getRetCode())
                    && loginResponseInfo.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
                saveLoginState(email, pw);
                saveUser(loginResponseInfo.getUserInfo());
                Intent intent = new Intent(mContext, ProfileSexSettingActivity.class);
                intent.putExtra("setup", true);
                mContext.startActivity(intent);
                RegisterActivity.this.finish();
            } else {
                if (!TextUtils.isEmpty(loginResponseInfo.getRetMsg())) {
                    HintUtils.showShortToast(mContext, loginResponseInfo.getRetMsg());
                } else {
                    HintUtils.showShortToast(mContext, getString(R.string.login_tips_login_error));
                }
                gotoLogin();

            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
          //  HintUtils.dissDialog();
            HintUtils.showShortToast(mContext, getString(R.string.login_tips_login_error));
            gotoLogin();
        }
    }
    private void saveUser(UserInfo info) {
        UserController.saveAllUserInfo(this, info);
    }

}
