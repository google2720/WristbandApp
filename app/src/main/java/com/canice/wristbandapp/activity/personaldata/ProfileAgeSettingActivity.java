package com.canice.wristbandapp.activity.personaldata;

import java.util.ArrayList;
import java.util.List;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.widget.WheelView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 年龄设置界面
 *
 * @author canice_yuan
 */
public class ProfileAgeSettingActivity extends BaseActivity {

    private WheelView wv_age;
    private TextView tv_age;
    private boolean setup;
    private int defaultAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup = getIntent().getBooleanExtra("setup", false);
        defaultAge = getIntent().getIntExtra("age", 18);
        setContentView(R.layout.layout_profile_age);
        initViews();
        registerReceiver(mReceiver, new IntentFilter(Constants.QUIT_ONE_ACTIVTY));
    }

    private void initViews() {

        setTitle(R.string.profile_title);
        if (getIntent().getBooleanExtra("male", true)) {
            ((ImageView) findViewById(R.id.iv_profile_age_photo)).setImageResource(R.drawable.man);
        } else {
            ((ImageView) findViewById(R.id.iv_profile_age_photo)).setImageResource(R.drawable.woman);
        }

        tv_age = (TextView) findViewById(R.id.tv_profile_age_age1);
        List<String> ageList = new ArrayList<>();
        for (int i = 99; i >0 ; i--) {
            ageList.add(String.valueOf(i));
        }
        wv_age = (WheelView) findViewById(R.id.wv_profile_age);
        wv_age.setOffset(1);
        wv_age.setItems(ageList);
        wv_age.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                tv_age.setText(item);
            }
        });
        wv_age.setSeletion(99-defaultAge);
        tv_age.setText(String.valueOf(defaultAge));
        findViewById(R.id.btn_next).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = tv_age.getText().toString();
                if (setup) {
                    ProfileData.getInstance().setAge(age);
                    Intent intent = new Intent(getBaseContext(), ProfileHeightSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", getIntent().getBooleanExtra("male", true));
                    startActivity(intent);
                } else {
                    Intent data = new Intent();
                    data.putExtra("data", age);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        findViewById(R.id.btn_before).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (setup) {
            findViewById(R.id.ll_height_bottom).setVisibility(View.VISIBLE);
            findViewById(R.id.confirm).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_height_bottom).setVisibility(View.GONE);
            findViewById(R.id.confirm).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String age = tv_age.getText().toString();
                Intent data = new Intent();
                data.putExtra("data", age);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.QUIT_ONE_ACTIVTY)) {
                ProfileAgeSettingActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
