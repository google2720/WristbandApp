package com.canice.wristbandapp.activity.personaldata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;

public class ProfileNameSettingActivity extends BaseActivity {

    private EditText et_name;
    private Button btn_ok;
    private boolean setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup = getIntent().getBooleanExtra("setup", false);

        setContentView(R.layout.layout_profile_name);
        initViews();
        registerReceiver(mReceiver, new IntentFilter(Constants.QUIT_ONE_ACTIVTY));
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.profile_title);
        et_name = (EditText) findViewById(R.id.et_name_name);
        btn_ok = (Button) findViewById(R.id.btn_name_ok);
        String nickname="";
        if (setup) {
           // nickname = ProfileData.getInstance().getNickname();
        } else {
            nickname = getIntent().getStringExtra("nickname");
        }
        et_name.setText(nickname);
        if (!TextUtils.isEmpty(nickname)) {
            et_name.setSelection(et_name.length());
        }
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    HintUtils.showShortToast(ProfileNameSettingActivity.this, R.string.profile_nickname_error_empty);
                    return;
                }
                int length = name.length();
                if (length < 4 || length > 16) {
                    HintUtils.showShortToast(ProfileNameSettingActivity.this, R.string.profile_nickname_error);
                    return;
                }
                if (setup) {
                    ProfileData.getInstance().setNickname(name);
                    Intent intent = new Intent(getBaseContext(), ProfileConfirmSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", getIntent().getBooleanExtra("male", true));
                    startActivity(intent);
                } else {
                    Intent data = new Intent();
                    data.putExtra("data", name);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.QUIT_ONE_ACTIVTY)) {
                ProfileNameSettingActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
