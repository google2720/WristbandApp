package com.canice.wristbandapp.widget;

import com.canice.wristbandapp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BatteryView extends ImageView {

    private Drawable drawable;
    private Rect bounds = new Rect();
    private int battery;

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.ic_battery_bg);
        drawable = context.getResources().getDrawable(R.drawable.ic_battery);
    }

    public void setProgress(int battery) {
        this.battery = battery;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = getHeight() - getPaddingTop() - getPaddingBottom();
        bounds.left = getPaddingLeft();
        bounds.top = getPaddingTop();
        bounds.right = bounds.left + Math.round(battery * 1f / 100 * w);
        bounds.bottom = bounds.top + h;
        drawable.setBounds(bounds);
        drawable.draw(canvas);
    }
}
