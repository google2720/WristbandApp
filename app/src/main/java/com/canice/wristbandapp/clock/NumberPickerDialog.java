package com.canice.wristbandapp.clock;

import com.canice.wristbandapp.R;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class NumberPickerDialog extends AlertDialog implements DialogInterface.OnClickListener {

    private NumberPicker numberPicker;
    private OnValueChangeListener valueChangedListener;

    public NumberPickerDialog(Context context, OnValueChangeListener valueChangedListener) {
        super(context);
        this.valueChangedListener = valueChangedListener;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_number_pick, null);
        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(60);
        numberPicker.setMinValue(1);
        numberPicker.setValue(30);
        setView(view);
        setButton(BUTTON_POSITIVE, context.getString(R.string.action_confirm), this);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), this);
    }
    
    public void setValue(int v) {
        numberPicker.setValue(v);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (valueChangedListener != null) {
                    valueChangedListener.onValueChange(numberPicker, 0, numberPicker.getValue());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }
}
