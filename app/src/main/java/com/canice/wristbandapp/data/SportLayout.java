package com.canice.wristbandapp.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canice.wristbandapp.R;

public class SportLayout extends LinearLayout {

    private Paint paint;
    private RectF oval = new RectF();
    private View contentContainer;
    private long progress;
    private float strokeWidth;

    private TextView tv_sport_gols;
    private TextView tv_sport_step;
    private TextView tv_completion_rate;

    public SportLayout(Context context, AttributeSet attrs) {
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
        tv_sport_gols = (TextView) this.findViewById(R.id.tv_sport_gols);
        tv_sport_step = (TextView) this.findViewById(R.id.tv_sport_step);
        tv_completion_rate = (TextView) this.findViewById(R.id.tv_completion_rate);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        draw1(canvas);
        super.dispatchDraw(canvas);
    }

    public void setData(long stepNum, long goal) {
        if (goal == 0) {
            progress = 0;
        } else {
            progress = stepNum * 100 / goal;
        }
        tv_sport_gols.setText(getResources().getString(R.string.sport_goal, goal));
        tv_completion_rate.setText(getResources().getString(R.string.sport_goal_completion_rate, progress));
        tv_sport_step.setText(String.valueOf(stepNum));
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
        //paint.setColor(0xff1b6cff);
        paint.setColor(0xff8cb5ff);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawArc(oval, -90, 360 * progress / 100, false, paint);
    }

    private void drawBlueCircle(Canvas canvas) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = cx - getPaddingLeft() / 2;

        paint.setColor(Color.WHITE);
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
