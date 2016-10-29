package com.canice.wristbandapp.activity.personaldata;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.widget.WeightScaleView;
import com.canice.wristbandapp.widget.WeightScaleView.OnValueChangeListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 体重设置界面
 * 
 * @author canice_yuan
 */
public class ProfileWeightSettingActivity extends BaseActivity implements OnValueChangeListener, OnClickListener {

    private TextView tv_weight_value;
    private WeightScaleView scale_weight;
    private boolean setup;
    private int defaultWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup = getIntent().getBooleanExtra("setup", false);
        defaultWeight = getIntent().getIntExtra("weight", -1);
        setContentView(R.layout.layout_profile_weight);
        initViews();
        registerReceiver(mReceiver, new IntentFilter(Constants.QUIT_ONE_ACTIVTY));
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        setTitle(R.string.profile_title);
        if (getIntent().getBooleanExtra("male", true)) {
            ((ImageView) findViewById(R.id.iv_sex)).setImageResource(R.drawable.man2);
        } else {
            ((ImageView) findViewById(R.id.iv_sex)).setImageResource(R.drawable.woman2);
        }
        tv_weight_value = (TextView) findViewById(R.id.tv_weight_value);
        scale_weight = (WeightScaleView) findViewById(R.id.scale_weight);
        scale_weight.setValueChangeListener(this);
        if (defaultWeight != -1) {
            scale_weight.setValue(defaultWeight);
        }
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_before).setOnClickListener(this);

        if (setup) {
            findViewById(R.id.ll_height_bottom).setVisibility(View.VISIBLE);
            findViewById(R.id.confirm).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_height_bottom).setVisibility(View.GONE);
            findViewById(R.id.confirm).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.confirm).setOnClickListener(this);
    }

    @Override
    public void onValueChange(float value) {
        tv_weight_value.setText(String.valueOf(Math.round(value)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next: {
                String w = tv_weight_value.getText().toString();
                if (setup) {
                    ProfileData.getInstance().setWeight(w);
                    Intent intent = new Intent(getBaseContext(), ProfileStepSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", getIntent().getBooleanExtra("male", true));
                    startActivity(intent);
                } else {
                    Intent data = new Intent();
                    data.putExtra("data", w);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
                break;
            case R.id.btn_before:
                finish();
                break;
            case R.id.confirm:{
                String w = tv_weight_value.getText().toString();
                Intent data = new Intent();
                data.putExtra("data", w);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.QUIT_ONE_ACTIVTY)) {
                ProfileWeightSettingActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
