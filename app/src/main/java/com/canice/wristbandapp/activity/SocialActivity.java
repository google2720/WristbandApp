package com.canice.wristbandapp.activity;

import android.support.v7.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.ResponseInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.HttpUtil;

/**
 * 社交界面
 *
 * @author canice_yuan
 */
public class SocialActivity extends BaseActivity {

    LinearLayout ll_clip;
    Button btn_add;
    Context context;
    EditText social_edit;
    TextView tv_share_code;
    TextView tv_name;
    ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_social);
        context = this;
        initViews();
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.social_title);
        setLeftCloseBtnListener(new OnClickListener() {
            public void onClick(View v) {
                SocialActivity.this.finish();
            }
        });
        social_edit = (EditText) this.findViewById(R.id.social_edit);
        ll_clip = (LinearLayout) this.findViewById(R.id.ll_clip);
        ll_clip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(UserController.getInviteCode(getBaseContext()));
                HintUtils.showShortToast(context, R.string.social_copy_invite_code);

            }
        });
        btn_add = (Button) this.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addRelateUser();
            }
        });
        tv_share_code = (TextView) this.findViewById(R.id.tv_share_code);
        tv_share_code.setText(UserController.getInviteCode(this));
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_name.setText(UserController.getNickname(this));
        iv_logo = (ImageView) this.findViewById(R.id.iv_logo);
        if (Constants.USER_SEX_MAN.equals(UserController.getSex(context))) {
            iv_logo.setImageResource(R.drawable.man);
        } else {
            iv_logo.setImageResource(R.drawable.woman);
        }
    }

    public void addRelateUser() {
        if (TextUtils.isEmpty(social_edit.getText().toString())) {
            HintUtils.showLongToast(context, R.string.social_error_no_invite_code);
            return;
        }
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(getBaseContext()));
        params.put(Constants.INVITE_CODE, social_edit.getText().toString());
        HttpUtil.get(Constants.SERVER_RELATE_USER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                ResponseInfo info = JSON.parseObject(content, ResponseInfo.class);
                if (info.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
                    setResult(Constants.resultCode_social);
                    finish();
                } else if (info.getRetCode().equals(Constants.RETCODE_FAILURE)) {
                    relateUserFailure(info.getRetMsg());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                relateUserFailure(getString(R.string.internet_error));
            }
        });
    }

    private void relateUserFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title_tip);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.action_retry, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                addRelateUser();
            }
        });
        builder.setPositiveButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
