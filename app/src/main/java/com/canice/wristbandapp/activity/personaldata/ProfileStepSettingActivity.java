package com.canice.wristbandapp.activity.personaldata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.widget.HeightScaleView;
import com.canice.wristbandapp.widget.StepScaleView;

public class ProfileStepSettingActivity extends BaseActivity implements OnClickListener, HeightScaleView.OnValueChangeListener {

    private TextView tv_step_value;
    private StepScaleView scaleView;
    private boolean setup;
    private int defaultSteplong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup = getIntent().getBooleanExtra("setup", false);
        defaultSteplong = getIntent().getIntExtra("steplong", -1);
        setContentView(R.layout.layout_profile_step);
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
        tv_step_value = (TextView) findViewById(R.id.tv_step_value);
        scaleView = (StepScaleView) findViewById(R.id.step_scaleView);
        scaleView.setValueChangeListener(this);
        if (defaultSteplong != -1) {
            scaleView.setValue(defaultSteplong);
        }
        findViewById(R.id.btn_before).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        if (setup) {
            findViewById(R.id.tools).setVisibility(View.VISIBLE);
            findViewById(R.id.confirm).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tools).setVisibility(View.GONE);
            findViewById(R.id.confirm).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String h = tv_step_value.getText().toString();
                Intent data = new Intent();
                data.putExtra("data", h);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    public void onValueChange(float value) {
        tv_step_value.setText(String.valueOf(Math.round(value)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                String h = tv_step_value.getText().toString();
                if (setup) {
                    ProfileData.getInstance().setStepLong(h);
                    Intent intent = new Intent(getBaseContext(), ProfileGoalSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", getIntent().getBooleanExtra("male", true));
                    startActivity(intent);
                } else {
                    Intent data = new Intent();
                    data.putExtra("data", h);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            case R.id.btn_before:
                finish();
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.QUIT_ONE_ACTIVTY)) {
                ProfileStepSettingActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
