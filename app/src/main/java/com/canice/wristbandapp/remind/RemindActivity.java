package com.canice.wristbandapp.remind;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.clock.NumberPickerDialog;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class RemindActivity extends BaseActivity {

    private Switch remindView;
    private Remind remind;
    private CheckBox mondayView;
    private CheckBox tuesdayView;
    private CheckBox wednesdayView;
    private CheckBox thursdayView;
    private CheckBox fridayView;
    private CheckBox saturdayView;
    private CheckBox sundayView;
    private View intervalTimeContainer;
    private TextView intervalTimeView;
    private String email;
    private View startTimeContainer;
    private TextView startTimeView;
    private View endTimeContainer;
    private TextView endTimeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        setTitle(R.string.personal_remind);
        setRightBtnTextVisible(View.VISIBLE);
        setRightBtnText(R.string.action_confirm);
        setRightBtnTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemindController.save(getBaseContext(), email, remind);
                BleController.getInstance().syncBaseDataAsync();
                finish();
            }
        });
        email = UserController.getEmail(this);
        remind = RemindController.get(this, email);
        initView();
    }

    private void initView() {
        startTimeContainer = findViewById(R.id.startTimeContainer);
        startTimeView = (TextView) findViewById(R.id.startTime);
        updateStartTimeText();

        endTimeContainer = findViewById(R.id.endTimeContainer);
        endTimeView = (TextView) findViewById(R.id.endTime);
        updateEndTimeText();

        intervalTimeContainer = findViewById(R.id.intervalTimeContainer);
        intervalTimeView = (TextView) findViewById(R.id.intervalTime);
        intervalTimeView.setText(remind.intervalTime + getString(R.string.minute));

        mondayView = (CheckBox) findViewById(R.id.monday);
        mondayView.setChecked(remind.mondayEnabled);
        mondayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.mondayEnabled = isChecked;
            }
        });

        tuesdayView = (CheckBox) findViewById(R.id.tuesday);
        tuesdayView.setChecked(remind.tuesdayEnabled);
        tuesdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.tuesdayEnabled = isChecked;
            }
        });

        wednesdayView = (CheckBox) findViewById(R.id.wednesday);
        wednesdayView.setChecked(remind.wednesdayEnabled);
        wednesdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.wednesdayEnabled = isChecked;
            }
        });

        thursdayView = (CheckBox) findViewById(R.id.thursday);
        thursdayView.setChecked(remind.thursdayEnabled);
        thursdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.thursdayEnabled = isChecked;
            }
        });

        fridayView = (CheckBox) findViewById(R.id.friday);
        fridayView.setChecked(remind.fridayEnabled);
        fridayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.fridayEnabled = isChecked;
            }
        });

        saturdayView = (CheckBox) findViewById(R.id.saturday);
        saturdayView.setChecked(remind.saturdayEnabled);
        saturdayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.saturdayEnabled = isChecked;
            }
        });

        sundayView = (CheckBox) findViewById(R.id.sunday);
        sundayView.setChecked(remind.sundayEnabled);
        sundayView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.sundayEnabled = isChecked;
            }
        });

        remindView = (Switch) findViewById(R.id.remind);
        remindView.setChecked(remind.enabled);
        remindView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remind.enabled = isChecked;
                updateContainerState(isChecked);
            }
        });
        updateContainerState(remind.enabled);
    }

    private void updateContainerState(boolean enabled) {
        startTimeContainer.setEnabled(enabled);
        startTimeView.setEnabled(enabled);
        endTimeContainer.setEnabled(enabled);
        endTimeView.setEnabled(enabled);
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

    public void onClickStartTime(View v) {
        TimePickerDialog d = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                remind.startTimeHour = hourOfDay;
                remind.startTimeMinute = minute;
                updateStartTimeText();
            }
        }, remind.startTimeHour, remind.startTimeMinute, true);
        d.show();
    }

    public void onClickEndTime(View v) {
        TimePickerDialog d = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                remind.endTimeHour = hourOfDay;
                remind.endTimeMinute = minute;
                updateEndTimeText();
            }
        }, remind.endTimeHour, remind.endTimeMinute, true);
        d.show();
    }

    public void onClickIntervalTime(View v) {
        NumberPickerDialog d = new NumberPickerDialog(this, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                remind.intervalTime = newVal;
                intervalTimeView.setText(newVal + getString(R.string.minute));
            }
        });
        d.setValue(remind.intervalTime);
        d.setTitle(getString(R.string.clock_notify_time_interval_dialog_title));
        d.show();
    }

    private void updateStartTimeText() {
        StringBuilder t = new StringBuilder();
        int h = remind.startTimeHour;
        if (h < 10) {
            t.append("0");
        }
        t.append(h);
        t.append(":");
        int m = remind.startTimeMinute;
        if (m < 10) {
            t.append("0");
        }
        t.append(m);
        startTimeView.setText(t.toString());
    }

    private void updateEndTimeText() {
        StringBuilder t = new StringBuilder();
        int h = remind.endTimeHour;
        if (h < 10) {
            t.append("0");
        }
        t.append(h);
        t.append(":");
        int m = remind.endTimeMinute;
        if (m < 10) {
            t.append("0");
        }
        t.append(m);
        endTimeView.setText(t.toString());
    }
}
