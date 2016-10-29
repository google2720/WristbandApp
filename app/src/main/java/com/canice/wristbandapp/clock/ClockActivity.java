package com.canice.wristbandapp.clock;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.ble.BleController;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class ClockActivity extends BaseActivity {

    private Switch clockView;
    private Clock clock;
    private TextView timeView;
    private TextView intervalTimeView;
    private CheckBox mondayView;
    private CheckBox tuesdayView;
    private CheckBox wednesdayView;
    private CheckBox thursdayView;
    private CheckBox fridayView;
    private CheckBox saturdayView;
    private CheckBox sundayView;
    private View timeContainer;
    private View intervalTimeContainer;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        setTitle(R.string.personal_clock);
        setRightBtnTextVisible(View.VISIBLE);
        setRightBtnText(R.string.action_confirm);
        setRightBtnTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClockController.save(getBaseContext(), email, clock);
                BleController.getInstance().syncBaseDataAsync();
                finish();
            }
        });
        email = UserController.getEmail(this);
        clock = ClockController.get(this, email);
        initView();
    }

    private void initView() {
        timeContainer = findViewById(R.id.timeContainer);
        timeView = (TextView) findViewById(R.id.time);
        updateTimeText();

        intervalTimeContainer = findViewById(R.id.intervalTimeContainer);
        intervalTimeView = (TextView) findViewById(R.id.intervalTime);
        intervalTimeView.setText(clock.intervalTime + getString(R.string.minute));

        mondayView = (CheckBox) findViewById(R.id.monday);
        mondayView.setChecked(clock.mondayEnabled);
        mondayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.mondayEnabled = isChecked;
            }
        });

        tuesdayView = (CheckBox) findViewById(R.id.tuesday);
        tuesdayView.setChecked(clock.tuesdayEnabled);
        tuesdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.tuesdayEnabled = isChecked;
            }
        });

        wednesdayView = (CheckBox) findViewById(R.id.wednesday);
        wednesdayView.setChecked(clock.wednesdayEnabled);
        wednesdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.wednesdayEnabled = isChecked;
            }
        });

        thursdayView = (CheckBox) findViewById(R.id.thursday);
        thursdayView.setChecked(clock.thursdayEnabled);
        thursdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.thursdayEnabled = isChecked;
            }
        });

        fridayView = (CheckBox) findViewById(R.id.friday);
        fridayView.setChecked(clock.fridayEnabled);
        fridayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.fridayEnabled = isChecked;
            }
        });

        saturdayView = (CheckBox) findViewById(R.id.saturday);
        saturdayView.setChecked(clock.saturdayEnabled);
        saturdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.saturdayEnabled = isChecked;
            }
        });

        sundayView = (CheckBox) findViewById(R.id.sunday);
        sundayView.setChecked(clock.sundayEnabled);
        sundayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.sundayEnabled = isChecked;
            }
        });

        clockView = (Switch) findViewById(R.id.clock);
        clockView.setChecked(clock.enabled);
        clockView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock.enabled = isChecked;
                updateContainerState(isChecked);
            }
        });
        updateContainerState(clock.enabled);
    }

    private void updateContainerState(boolean enabled) {
        timeContainer.setEnabled(enabled);
        timeView.setEnabled(enabled);
        intervalTimeContainer.setEnabled(enabled);
        intervalTimeView.setEnabled(enabled);
        mondayView.setEnabled(enabled);
        tuesdayView.setEnabled(enabled);
        wednesdayView.setEnabled(enabled);
        thursdayView.setEnabled(enabled);
        fridayView.setEnabled(enabled);
        saturdayView.setEnabled(enabled);
        sundayView.setEnabled(enabled);
    }

    public void onClickTime(View v) {
        TimePickerDialog d = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                clock.timeHour = hourOfDay;
                clock.timeMinute = minute;
                updateTimeText();
            }
        }, clock.timeHour, clock.timeMinute, true);
        d.show();
    }

    public void onClickIntervalTime(View v) {
        NumberPickerDialog d = new NumberPickerDialog(this, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                clock.intervalTime = newVal;
                intervalTimeView.setText(newVal + getString(R.string.minute));
            }
        });
        d.setValue(clock.intervalTime);
        d.setTitle(R.string.clock_notify_time_interval_dialog_title);
        d.show();
    }

    private void updateTimeText() {
        StringBuilder t = new StringBuilder();
        int h = clock.timeHour;
        if (h < 10) {
            t.append("0");
        }
        t.append(h);
        t.append(":");
        int m = clock.timeMinute;
        if (m < 10) {
            t.append("0");
        }
        t.append(m);
        timeView.setText(t.toString());
    }
}
