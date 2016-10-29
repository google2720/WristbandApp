package com.canice.wristbandapp.activity.personaldata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.Tools;

public class ProfileGoalSettingActivity extends BaseActivity implements OnClickListener {

    private TextView tv_goal_value;
    private SeekBar sb_goal_scale;
    private boolean setup;
    private TextView distanceView;
    private TextView fireView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup = getIntent().getBooleanExtra("setup", false);
        setContentView(R.layout.layout_profile_goal);
        initViews();
        registerReceiver(mReceiver, new IntentFilter(Constants.QUIT_ONE_ACTIVTY));
    }

    private void initViews() {
        setTitle(R.string.profile_title);
        distanceView = (TextView) findViewById(R.id.distance);
        fireView = (TextView) findViewById(R.id.fire);
        tv_goal_value = (TextView) findViewById(R.id.tv_goal_value);
        sb_goal_scale = (SeekBar) findViewById(R.id.sb_goal_scale);
        sb_goal_scale.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setProcessValue(progress);
            }
        });
        int p;
        if (setup) {
            p = (ProfileData.getInstance().getGoal() - 5000) / 100;
        } else {
            p = (UserController.getGoal(getBaseContext()) - 5000) / 100;
        }
        sb_goal_scale.setProgress(p);
        setProcessValue(p);
        findViewById(R.id.btn_before).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        View toolsView = findViewById(R.id.tools);
        toolsView.setVisibility(setup ? View.VISIBLE : View.GONE);

        if (!setup) {
            setRightBtnText(R.string.action_complete);
            setRightBtnTextVisible(View.VISIBLE);
            setRightBtnTextClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int goal = sb_goal_scale.getProgress() * 100 + 5000;
                    UserController.saveGoal(getBaseContext(), goal);
                    BleController.getInstance().syncBaseDataAsync();
                    BleController.getInstance().fetchPedometerDataAsync();
                    finish();
                }
            });
        }
    }

    private void setProcessValue(int progress) {
        int goal = progress * 100 + 5000;
        double step = Double.parseDouble(getStepLong());
        double weight = Double.parseDouble(getWeight());
        tv_goal_value.setText(String.valueOf(goal));
        distanceView.setText(Tools.getDistanceStr(goal, step) + getString(R.string.kilometre));
        fireView.setText(Tools.getFireStr(goal, step, weight) + getString(R.string.cul_unit));
    }

    private String getWeight() {
        if (setup) {
            return ProfileData.getInstance().getWeight();
        } else {
            return UserController.getWeight(getBaseContext());
        }
    }

    private String getStepLong() {
        if (setup) {
            return ProfileData.getInstance().getStepLong();
        } else {
            return UserController.getStepLong(getBaseContext());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_before:
                finish();
                break;
            case R.id.btn_next:
                int g = sb_goal_scale.getProgress() * 100 + 5000;
                if (setup) {
                    ProfileData.getInstance().setGoal(g);
                    Intent intent = new Intent(this, ProfileNameSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", getIntent().getBooleanExtra("male", true));
                    startActivity(intent);
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
                ProfileGoalSettingActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
