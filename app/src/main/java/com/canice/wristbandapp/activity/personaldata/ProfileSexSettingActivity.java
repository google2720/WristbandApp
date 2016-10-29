package com.canice.wristbandapp.activity.personaldata;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.util.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 性别设置界面
 * 
 * @author canice_yuan
 */
public class ProfileSexSettingActivity extends BaseActivity implements OnClickListener {

    private boolean setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup = getIntent().getBooleanExtra("setup", false);
        setContentView(R.layout.layout_profile_sex);
        initViews();
        registerReceiver(mReceiver, new IntentFilter(Constants.QUIT_ONE_ACTIVTY));
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.profile_title);
        setLeftCloseBtnListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        setRightBtnDescription(this.getString(R.string.profile_passby));
        findViewById(R.id.iv_profile_sex_man).setOnClickListener(this);
        findViewById(R.id.iv_profile_sex_woman).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent data = new Intent();
        switch (view.getId()) {
            case R.id.iv_profile_sex_man:
                if (setup) {
                    ProfileData.getInstance().setSex(ProfileData.SEX_MALE);
                    Intent intent = new Intent(this, ProfileAgeSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", true);
                    startActivity(intent);
                } else {
                    data.putExtra("data", Constants.USER_SEX_MAN);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            case R.id.iv_profile_sex_woman:
                if (setup) {
                    ProfileData.getInstance().setSex(ProfileData.SEX_FEMALE);
                    Intent intent = new Intent(this, ProfileAgeSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", false);
                    startActivity(intent);
                } else {
                    data.putExtra("data", Constants.USER_SEX_WOMAN);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.QUIT_ONE_ACTIVTY)) {
                ProfileSexSettingActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
