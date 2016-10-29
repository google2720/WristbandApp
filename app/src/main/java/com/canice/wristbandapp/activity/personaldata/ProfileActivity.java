package com.canice.wristbandapp.activity.personaldata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.ResponseInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.HttpUtil;

public class ProfileActivity extends BaseActivity implements OnClickListener {

    private static final int REQUESTCODE_NICK_NAME = 0;
    private static final int REQUESTCODE_SEX = 1;
    private static final int REQUESTCODE_AGE = 2;
    private static final int REQUESTCODE_HIGH = 3;
    private static final int REQUESTCODE_WEIGHT = 4;
    private static final int REQUESTCODE_STEPLONG = 5;
    private TextView nicknameView;
    private TextView sexView;
    private TextView ageView;
    private TextView highView;
    private TextView weightView;
    private TextView steplongView;
    private String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);
        initViews();
        initData();
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.profile_title);
        nicknameView = (TextView) findViewById(R.id.nick_name);
        findViewById(R.id.nick_name_container).setOnClickListener(this);

        sexView = (TextView) findViewById(R.id.sex);
        findViewById(R.id.sex_container).setOnClickListener(this);

        ageView = (TextView) findViewById(R.id.age);
        findViewById(R.id.age_container).setOnClickListener(this);

        highView = (TextView) findViewById(R.id.high);
        findViewById(R.id.high_container).setOnClickListener(this);

        weightView = (TextView) findViewById(R.id.weight);
        findViewById(R.id.weight_container).setOnClickListener(this);

        steplongView = (TextView) findViewById(R.id.steplong);
        findViewById(R.id.steplong_container).setOnClickListener(this);

        findViewById(R.id.confirm).setOnClickListener(this);
    }

    private void initData() {
        sex = UserController.getSex(this);
        nicknameView.setText(UserController.getNickname(this));
        sexView.setText(isMale() ? R.string.male : R.string.female);
        ageView.setText(UserController.getAge(this));
        highView.setText(UserController.getHeight(this));
        weightView.setText(UserController.getWeight(this));
        steplongView.setText(UserController.getStepLong(this));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.nick_name_container:
                intent = new Intent(this, ProfileNameSettingActivity.class);
                intent.putExtra("nickname", nicknameView.getText().toString());
                startActivityForResult(intent, REQUESTCODE_NICK_NAME);
                break;
            case R.id.sex_container:
                intent = new Intent(this, ProfileSexSettingActivity.class);
                startActivityForResult(intent, REQUESTCODE_SEX);
                break;
            case R.id.age_container:
                intent = new Intent(this, ProfileAgeSettingActivity.class);
                intent.putExtra("male", isMale());
                intent.putExtra("age", Integer.parseInt(ageView.getText().toString()));
                startActivityForResult(intent, REQUESTCODE_AGE);
                break;
            case R.id.high_container:
                intent = new Intent(this, ProfileHeightSettingActivity.class);
                intent.putExtra("male", isMale());
                intent.putExtra("height", Integer.parseInt(highView.getText().toString()));
                startActivityForResult(intent, REQUESTCODE_HIGH);
                break;
            case R.id.weight_container:
                intent = new Intent(this, ProfileWeightSettingActivity.class);
                intent.putExtra("male", isMale());
                intent.putExtra("weight", Integer.parseInt(weightView.getText().toString()));
                startActivityForResult(intent, REQUESTCODE_WEIGHT);
                break;
            case R.id.steplong_container:
                intent = new Intent(this, ProfileStepSettingActivity.class);
                intent.putExtra("steplong", Integer.parseInt(steplongView.getText().toString()));
                startActivityForResult(intent, REQUESTCODE_STEPLONG);
                break;
            case R.id.confirm:
                setupUserinfo();
                break;
            default:
                break;
        }
    }

    private boolean isMale() {
        return Constants.USER_SEX_MAN.equals(sex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String v = data.getStringExtra("data");
            if (requestCode == REQUESTCODE_NICK_NAME) {
                nicknameView.setText(v);
            } else if (requestCode == REQUESTCODE_SEX) {
                sex = v;
                sexView.setText(isMale() ? R.string.male : R.string.female);
            } else if (requestCode == REQUESTCODE_AGE) {
                ageView.setText(v);
            } else if (requestCode == REQUESTCODE_HIGH) {
                highView.setText(v);
            } else if (requestCode == REQUESTCODE_WEIGHT) {
                weightView.setText(v);
            } else if (requestCode == REQUESTCODE_STEPLONG) {
                steplongView.setText(v);
            }
        }
    }

    private void setupUserinfo() {
        final Activity context = this;
        final String name = nicknameView.getText().toString();
        final String high = highView.getText().toString();
        final String weight = weightView.getText().toString();
        final String age = ageView.getText().toString();
        final String steplong = steplongView.getText().toString();

        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(this));
        params.put(Constants.NICKNAME, name);
        params.put(Constants.SEX, sex);
        params.put(Constants.HIGH, high);
        params.put(Constants.WEIGHT, weight);
        params.put(Constants.AGE, age);
        params.put(Constants.STEPLONG, steplong);
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
                    UserController.saveUserInfo(context, name, sex, age, high, weight, steplong);
                } else if (info.getRetCode().equals(Constants.RETCODE_FAILURE)) {
                    HintUtils.showLongToast(context, info.getRetMsg());
                }
                finish();
                sendBroadcast(new Intent(Constants.QUIT_ONE_ACTIVTY));
                BleController.getInstance().syncBaseDataAsync();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                HintUtils.dissDialog();
                HintUtils.showLongToast(context, getString(R.string.internet_error));
            }
        });
    }
}
