package com.canice.wristbandapp.activity.personaldata;

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

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.widget.HeightScaleView;
import com.canice.wristbandapp.widget.HeightScaleView.OnValueChangeListener;

/**
 * 身高设置界面
 *
 * @author canice_yuan
 */
public class ProfileHeightSettingActivity extends BaseActivity implements OnClickListener, OnValueChangeListener {

    private TextView heightValue;
    private HeightScaleView heightView;
    private boolean setup;
    private int defaultHeight;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup = getIntent().getBooleanExtra("setup", false);
        defaultHeight = getIntent().getIntExtra("height", -1);
        setContentView(R.layout.layout_profile_height);
        initViews();
        registerReceiver(mReceiver, new IntentFilter(Constants.QUIT_ONE_ACTIVTY));
    }

    private void initViews() {
        setTitle(R.string.profile_title);
        if (getIntent().getBooleanExtra("male", true)) {
            ((ImageView) findViewById(R.id.iv_sex)).setImageResource(R.drawable.man2);
        } else {
            ((ImageView) findViewById(R.id.iv_sex)).setImageResource(R.drawable.woman2);
        }
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_before).setOnClickListener(this);
        heightValue = (TextView) findViewById(R.id.tv_height_value);
        heightView = (HeightScaleView) findViewById(R.id.height_scaleView);
        heightView.setValueChangeListener(this);
        if (defaultHeight != -1) {
            heightView.setValue(defaultHeight);
        }
        if (setup) {
            findViewById(R.id.ll_height_bottom).setVisibility(View.VISIBLE);
            findViewById(R.id.confirm).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_height_bottom).setVisibility(View.GONE);
            findViewById(R.id.confirm).setVisibility(View.VISIBLE);
        }
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next: {
                String h = heightValue.getText().toString();
                if (setup) {
                    ProfileData.getInstance().setHigh(h);
                    Intent intent = new Intent(getBaseContext(), ProfileWeightSettingActivity.class);
                    intent.putExtra("setup", true);
                    intent.putExtra("male", getIntent().getBooleanExtra("male", true));
                    startActivity(intent);
                } else {
                    Intent data = new Intent();
                    data.putExtra("data", h);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
            break;
            case R.id.btn_before:
                finish();
                break;
            case R.id.confirm: {
                String h = heightValue.getText().toString();
                Intent data = new Intent();
                data.putExtra("data", h);
                setResult(RESULT_OK, data);
                finish();
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onValueChange(float value) {
        heightValue.setText(String.valueOf(Math.round(value)));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.QUIT_ONE_ACTIVTY)) {
                ProfileHeightSettingActivity.this.finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
