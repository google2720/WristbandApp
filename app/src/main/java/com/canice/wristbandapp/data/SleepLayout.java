package com.canice.wristbandapp.data;

import com.canice.wristbandapp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SleepLayout extends LinearLayout {

    private Paint paint;
    private RectF oval = new RectF();
    private View contentContainer;
    private TextView totalhView;
    private TextView totalmView;
    private TextView qhView;
    private TextView qmView;
    private TextView shView;
    private TextView smView;
    private long progress;
    private float strokeWidth;

    public SleepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        strokeWidth = 15 * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w1 = contentContainer.getMeasuredWidth();
        int h1 = contentContainer.getMeasuredHeight();
        int d1 = Math.max(w1, h1);
        int childSpec = MeasureSpec.makeMeasureSpec(d1, MeasureSpec.EXACTLY);
        contentContainer.measure(childSpec, childSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int d = Math.max(w, h);
        setMeasuredDimension(d, d);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentContainer = findViewById(R.id.content_container);
        totalhView = (TextView) findViewById(R.id.total_h);
        totalmView = (TextView) findViewById(R.id.total_m);
        qhView = (TextView) findViewById(R.id.q_h);
        qmView = (TextView) findViewById(R.id.q_m);
        shView = (TextView) findViewById(R.id.s_h);
        smView = (TextView) findViewById(R.id.s_m);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        draw1(canvas);
        super.dispatchDraw(canvas);
    }

    public void setData(long ssmTime, long qsmTime) {
        long totalTime = ssmTime + qsmTime;
//        long h = totalTime / 60;
//        long m = totalTime % 60;
        long h = totalTime ;
        long m = 0;
        totalhView.setText(h < 10 ? "" + h : String.valueOf(h));
        totalmView.setText(m < 10 ? "" + m : String.valueOf(m));

//        h = qsmTime / 60;
//        m = qsmTime % 60;
        h = qsmTime;
        m = 0;
        qhView.setText(h < 10 ? "" + h : String.valueOf(h));
        qmView.setText(m < 10 ? "" + m : String.valueOf(m));

//        h = ssmTime / 60;
//        m = ssmTime % 60;
        h = ssmTime;
        m = 0;
        shView.setText(h < 10 ? "" + h : String.valueOf(h));
        smView.setText(m < 10 ? "" + m : String.valueOf(m));

        if (totalTime != 0) {
            progress = ssmTime * 100 / totalTime;
        } else {
            progress = 0;
        }
    }

    private void draw1(Canvas canvas) {
        drawBlueCircle(canvas);
        drawWhiteCircle(canvas);
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        int cx = getWidth() / 2;
        int radius = cx - getPaddingLeft() / 2;
        oval.left = cx - radius;
        oval.top = cx - radius;
        oval.right = cx + radius;
        oval.bottom = cx + radius;
        paint.setColor(0xff1b6cff);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawArc(oval, -90, 360 * progress / 100, false, paint);
    }

    private void drawBlueCircle(Canvas canvas) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = cx - getPaddingLeft() / 2;
        paint.setColor(0xff8cb5ff);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(cx, cy, radius, paint);
    }

    private void drawWhiteCircle(Canvas canvas) {
        int cx = contentContainer.getWidth() / 2;
        int cy = contentContainer.getHeight() / 2;
        int radius = cx;
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx + getPaddingLeft(), cy + getPaddingTop(), radius, paint);
    }
}
