package com.canice.wristbandapp.data;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.canice.wristbandapp.APP;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.activity.SetLanguageActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RecordDataActivity extends BaseActivity {

    private Calendar calendar;
    private Button mStepView;
    private Button mSleepView;
    private TextView timeView;
    private Locale locale = Locale.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SetLanguageActivity.AUTO == UserController.getLanguage(APP.getInstance().getApplicationContext())) {
            if (!"en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
                locale = Locale.CHINA;
            }
        } else if (SetLanguageActivity.EN == UserController.getLanguage(APP.getInstance().getApplicationContext())) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.CHINA;
        }
        calendar = Calendar.getInstance(locale);
        setContentView(R.layout.layout_record_data);
        setTitle(R.string.datacenter_title);
        addRightView(generateRightView());
        initViews();
        updateSelectDate();
        mStepView.performClick();
    }

    private View generateRightView() {
        View v = getLayoutInflater().inflate(R.layout.layout_record_data_right, getRightView(), false);
        timeView = (TextView) v.findViewById(R.id.time);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDate();
            }
        });
        return v;
    }

    private void onSelectDate() {
        Locale.setDefault(locale);
        DatePickerDialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateSelectDate();
                if (mStepView.isActivated()) {
                    showStepFragment();
                } else {
                    showSleepFragment();
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Locale.setDefault(locale);
        d.show();
    }

    private void updateSelectDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        timeView.setText(sdf.format(calendar.getTime()));
    }

    private void initViews() {
        mStepView = (Button) findViewById(R.id.btn_datarecord_step);
        mStepView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mStepView.isActivated()) {
                    mStepView.setActivated(true);
                    mSleepView.setActivated(false);
                    showStepFragment();
                }
            }
        });
        mSleepView = (Button) findViewById(R.id.btn_datarecord_sleep);
        mSleepView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSleepView.isActivated()) {
                    mStepView.setActivated(false);
                    mSleepView.setActivated(true);
                    showSleepFragment();
                }
            }
        });
    }

    private void showSleepFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, SleepDataFragment.newInstance(calendar));
        ft.commitAllowingStateLoss();
    }

    private void showStepFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, StepDataFragment.newInstance(calendar));
        ft.commitAllowingStateLoss();
    }
}