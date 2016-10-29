package com.canice.wristbandapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.model.UserInfo;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.Tools;

public class SosActivity extends BaseActivity {
    Switch sosSwitch;
    LinearLayout ln_phone;
    EditText editText;
    LinearLayout ln_type;
    RadioButton rb_phone;
    RadioButton rb_sms;
    RadioGroup radioGroup;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this.getApplicationContext();
        setTitle(R.string.personal_sos_setup);
        setContentView(R.layout.activity_sos);
        initView();
    }

    private void initView() {
        ln_phone = (LinearLayout) this.findViewById(R.id.ln_phone);
        editText = (EditText) this.findViewById(R.id.edt_phone);
        sosSwitch = (Switch) this.findViewById(R.id.swith);
        ln_type = (LinearLayout) this.findViewById(R.id.ln_type);
        rb_phone = (RadioButton) this.findViewById(R.id.rb_phone);
        rb_sms = (RadioButton) this.findViewById(R.id.rb_sms);
        radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
        sosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ln_phone.setVisibility(View.VISIBLE);
                    ln_type.setVisibility(View.VISIBLE);
                } else {
                    ln_phone.setVisibility(View.GONE);
                    ln_type.setVisibility(View.GONE);
                }
                UserController.setSosEnable(SosActivity.this, isChecked, UserController.getSosPhone(SosActivity.this));
            }
        });
        if (UserController.isSosEnable(getApplicationContext())) {
            sosSwitch.setChecked(true);
        } else {
            sosSwitch.setChecked(false);
        }
        editText.setText(UserController.getSosPhone(this));
        editText.setSelection(editText.getText().length());
        if (sosSwitch.isChecked()) {
            ln_phone.setVisibility(View.VISIBLE);
            ln_type.setVisibility(View.VISIBLE);
        } else {
            ln_phone.setVisibility(View.GONE);
            ln_type.setVisibility(View.GONE);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId==rb_phone.getId()){
                    UserController.setSosBySms(mContext,false);
                }else{
                    UserController.setSosBySms(mContext,true);
                }
            }
        });
        if (UserController.isSosBySms(mContext)){
            rb_sms.setChecked(true);
        }else{
            rb_phone.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onLeftButtonClick() {
        back();
    }

    private void back() {
        String pNo = editText.getText().toString();
        boolean on = sosSwitch.isChecked();
        BleController.getInstance().setSosEnableAsync(on);
        if (!Tools.isEmpty(pNo)) {
            UserController.setSosEnable(this, on, pNo);
            super.onBackPressed();
        } else {
            if (on) {
                HintUtils.showShortToast(this, R.string.sos_error_no_phone);
            } else {
                super.onBackPressed();
            }
        }
    }


}
