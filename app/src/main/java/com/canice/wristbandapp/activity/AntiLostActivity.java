package com.canice.wristbandapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.ble.BleController;

/**
 * 防丢失设置界面
 * <p/>
 * Created by y on 2016/6/29.
 */
public class AntiLostActivity extends BaseActivity {

    private Switch s1;
    private View c1;
    private View c2;
    private View c3;
    private CheckBox checkbox1;
    private CheckBox checkbox2;
    private CheckBox checkbox3;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener1 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setCheckBoxState(true, false, false);
            UserController.setAntiLostValue(getBaseContext(), -80);
        }
    };
    private CompoundButton.OnCheckedChangeListener checkedChangeListener2 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setCheckBoxState(false, true, false);
            UserController.setAntiLostValue(getBaseContext(), -90);
        }
    };
    private CompoundButton.OnCheckedChangeListener checkedChangeListener3 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setCheckBoxState(false, false, true);
            UserController.setAntiLostValue(getBaseContext(), -100);
        }
    };
    private View.OnClickListener clickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setCheckBoxState(true, false, false);
            UserController.setAntiLostValue(getBaseContext(), -80);
        }
    };
    private View.OnClickListener clickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setCheckBoxState(false, true, false);
            UserController.setAntiLostValue(getBaseContext(), -90);
        }
    };
    private View.OnClickListener clickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setCheckBoxState(false, false, true);
            UserController.setAntiLostValue(getBaseContext(), -100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_lost);
        setTitle(R.string.personal_anti_lost);
        initView();
    }

    private void initView() {
        s1 = (Switch) findViewById(R.id.s1);
        c1 = findViewById(R.id.c1);
        c1.setOnClickListener(clickListener1);
        c2 = findViewById(R.id.c2);
        c2.setOnClickListener(clickListener2);
        c3 = findViewById(R.id.c3);
        c3.setOnClickListener(clickListener3);
        checkbox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkbox2 = (CheckBox) findViewById(R.id.checkbox2);
        checkbox3 = (CheckBox) findViewById(R.id.checkbox3);

        boolean enable = UserController.isAntiLostEnable(getBaseContext());
        s1.setChecked(enable);
        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserController.setAntiLostEnable(getBaseContext(), isChecked);
                syncViewState(isChecked);
                if (isChecked) {
                    BleController.getInstance().antiLoseAsync();
                }
            }
        });
        syncViewState(enable);

        int value = UserController.getAntiLostValue(this);
        setCheckBoxState(value == -80, value == -90, value == -100);
    }

    private void syncViewState(boolean enable) {
        if (enable) {
            showAnim();
        } else {
            hiddenAnim();
        }
    }

    private void hiddenAnim() {
        hidden(c1);
        hidden(c2);
        hidden(c3);
    }

    private void hidden(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        view.setVisibility(View.GONE);
    }

    private void showAnim() {
        show(c1);
        show(c2);
        show(c3);
    }

    private void show(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        view.setVisibility(View.VISIBLE);
    }

    private void setCheckBoxState(boolean b1, boolean b2, boolean b3) {
        setCheckBoxState(checkbox1, b1, checkedChangeListener1);
        setCheckBoxState(checkbox2, b2, checkedChangeListener2);
        setCheckBoxState(checkbox3, b3, checkedChangeListener3);
    }

    private void setCheckBoxState(CheckBox checkbox, boolean checked, CompoundButton.OnCheckedChangeListener listener) {
        checkbox.setOnCheckedChangeListener(null);
        checkbox.setChecked(checked);
        checkbox.setOnCheckedChangeListener(listener);
    }
}
