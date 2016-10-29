package com.canice.wristbandapp.widget;

import android.content.Context;
import android.util.AttributeSet;

public class StepScaleView extends HeightScaleView {

    public StepScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        divider = 40;
        startScale = 50;
    }

    @Override
    protected float processDistance(float distance) {
        distance = distance <= -49 * divider ? -49 * divider : distance; // 最少到0
        distance = distance > 35 * divider ? 35 * divider : distance; // 最多到85
        return distance;
    }
}
