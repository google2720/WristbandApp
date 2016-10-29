package com.canice.wristbandapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.SmsReceivedService;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.clock.ClockController;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;

import java.util.Locale;

public class SetLanguageActivity extends BaseActivity {

    RadioGroup group_rb;
    RadioButton rb_auto;
    RadioButton rb_cn;
    RadioButton rb_en;
    public static final int AUTO = 0;
    public static final int CN = 1;
    public static final int EN = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_language);
        setTitle(R.string.language_set);
        initView();
        initData();
    }

    private void initData() {
        int languageType = UserController.getLanguage(this.getApplicationContext());
        switch (languageType) {
            case 0: {
                rb_auto.setChecked(true);
            }
            break;
            case 1: {
                rb_cn.setChecked(true);
            }
            break;
            case 2: {
                rb_en.setChecked(true);
            }
            break;
        }
    }

    private void initView() {
        rb_auto = (RadioButton) this.findViewById(R.id.rb_auto);
        rb_cn = (RadioButton) this.findViewById(R.id.rb_cn);
        rb_en = (RadioButton) this.findViewById(R.id.rb_en);

        group_rb = (RadioGroup) this.findViewById(R.id.group_id);
        setRightBtnText(R.string.language_save);
        setRightBtnEnabled(true);
        setRightBtnTextVisible(View.VISIBLE);
        setRightBtnTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage();

            }
        });
    }

    private void changeLanguage() {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        int id = group_rb.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rb_auto: {
                UserController.setLanguage(this.getApplicationContext(), 0);
                config.locale = Locale.getDefault();

            }
            break;
            case R.id.rb_cn: {
                UserController.setLanguage(this.getApplicationContext(), 1);
                config.locale = Locale.CHINA;
            }
            break;
            case R.id.rb_en: {
                UserController.setLanguage(this.getApplicationContext(), 2);
                config.locale = Locale.ENGLISH;
            }
            break;
        }
        resources.updateConfiguration(config, dm);
//        Intent intent=new Intent(this,MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        this.startActivity(intent);
        Intent intent = new Intent(Constants.CHANGE_LANGUAGE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

}