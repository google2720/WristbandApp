package com.canice.wristbandapp.activity;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.ResponseInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.HttpUtil;
import com.canice.wristbandapp.util.Tools;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 注册界面
 * 
 * @author canice_yuan
 */
public class FindPasswordActivity extends BaseActivity {

    private EditText et_email;
    private ImageView iv_logo;
    private TextView tv_prompt;
    Button btn_send;
    private LinearLayout ll_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_pwd);

        initViews();
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.find_pwd_title_txt);
        setLeftCloseBtnListener(new OnClickListener() {
            public void onClick(View v) {
                FindPasswordActivity.this.finish();
            }
        });
        ll_email = (LinearLayout) findViewById(R.id.ll_email);
        et_email = (EditText) findViewById(R.id.edittext_email);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        tv_prompt = (TextView) findViewById(R.id.tv_prompt);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verification()) {
                    findPassword();
                }
            }
        });
    }

    private void findPassword() {
        String emailName = et_email.getText().toString();
        if (!Tools.isValidEmail(emailName)) {
            HintUtils.showLongToast(this, getString(R.string.login_tips_username));
            return;
        }
        final RequestParams params = new RequestParams();
        params.put(Constants.EMAIL, emailName);
        // HttpUtil.get(Constants.SERVER_FIND_PASSWORD, params,
        // new AsyncHandler(FindPasswordActivity.this, TAG,
        // ResponseInfo.class,getString(R.string.find_faild),getString(R.string.internet_error)));
        HttpUtil.get(Constants.SERVER_FIND_PASSWORD, params, new FindPwdResponseHandler(FindPasswordActivity.this));
    }

    /**
     * 输入校验
     * 
     * @author canice_yuan
     */
    private boolean verification() {
        String username = et_email.getText().toString();
        if (!Tools.isValidEmail(username)) {
            HintUtils.showLongToast(this, getString(R.string.login_tips_username));
            return false;
        }
        return true;
    }

    public class FindPwdResponseHandler extends AsyncHttpResponseHandler {

        private Context mContext;

        public FindPwdResponseHandler(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onStart() {
            super.onStart();
            HintUtils.showDiaog(mContext, getString(R.string.find_pwd_tips));
        }

        @Override
        public void onSuccess(int statusCode, String content) {
            super.onSuccess(statusCode, content);
            HintUtils.dissDialog();
            ResponseInfo findPwdResponseInfo = JSON.parseObject(content, ResponseInfo.class);
            if (findPwdResponseInfo.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
                ll_email.setVisibility(View.GONE);
                iv_logo.setVisibility(View.VISIBLE);
                tv_prompt.setVisibility(View.VISIBLE);
                tv_prompt.setText(R.string.find_pw_email_send_success);
                btn_send.setVisibility(View.GONE);
            } else {
                ll_email.setVisibility(View.GONE);
                iv_logo.setVisibility(View.VISIBLE);
                tv_prompt.setVisibility(View.VISIBLE);
                tv_prompt.setText(R.string.find_pw_email_send_failed);
                btn_send.setText(R.string.find_pw_resend);
                btn_send.setVisibility(View.VISIBLE);
                HintUtils.showLongToast(mContext, findPwdResponseInfo.getRetMsg());
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            HintUtils.dissDialog();
            HintUtils.showLongToast(mContext, getString(R.string.find_pwd_tips_error));
        }
    }
}
