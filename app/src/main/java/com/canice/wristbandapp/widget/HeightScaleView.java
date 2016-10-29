package com.canice.wristbandapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HeightScaleView extends View {

    private OnValueChangeListener mListener;

    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public float getValue() {
        return scale;
    }

    public void setValue(int value) {
        scale = value;
        distance = (scale - startScale) * divider;
        notifyValueChange();
    }

    private int mWidth, mHeight;
    private Paint lablepPaint, scalePaint, textPaint;
    private float lastY;

    public HeightScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
        drawLableLine(canvas);
    }

    private void initPaint() {
        lablepPaint = new Paint();
        lablepPaint.setColor(Color.RED);
        lablepPaint.setStrokeWidth(3);

        scalePaint = new Paint();
        scalePaint.setColor(Color.BLACK);
        scalePaint.setStrokeWidth(2);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(26);
    }

    private void drawLableLine(Canvas canvas) {
        canvas.save();
        canvas.drawLine(0, mHeight / 2, mWidth / 2, mHeight / 2, lablepPaint);
        canvas.restore();
    }

    private void drawLongLine(Canvas canvas, float ystart) {
        canvas.drawLine(0, ystart, mWidth * 2 / 3, ystart, scalePaint);
    }

    private void drawMiddleLine(Canvas canvas, float ystart) {
        canvas.drawLine(0, ystart, mWidth / 2, ystart, scalePaint);
    }

    private void drawShortLine(Canvas canvas, float ystart) {
        canvas.drawLine(0, ystart, mWidth / 3, ystart, scalePaint);
    }

    private void drawText(Canvas canvas, float ystart, float scale) {
        canvas.drawText(String.valueOf((int) scale), mWidth * 3 / 4, ystart + 10,
                textPaint);
    }

    float divider = 40; // 刻度间隔
    int startScale = 100; // 初始刻度
    float startY; // 初始坐标
    int drawCount = 0; // 刻度数量

    // 从中间往两边划线
    private void drawScaleLine(Canvas canvas) {
        canvas.save();
        drawCount = (int) (mHeight / divider);
        startY = mHeight / 2 + distance;

        // 向上画线
        float s = startY;
        for (int i = 0; i < drawCount * 20; i++) {
            if (i % 10 == 0) {
                drawLongLine(canvas, s);
                drawText(canvas, s, startScale + i);
            } else if (i % 10 == 5) {
                drawMiddleLine(canvas, s);
                drawText(canvas, s, startScale + i);
            } else {
                drawShortLine(canvas, s);
            }
            s -= divider;
        }

        // 向下画线
        float s1 = startY;
        for (int i = 0; i < drawCount * 20; i++) {
            if (i % 10 == 0) {
                drawLongLine(canvas, s1);
                if (startScale - i >= 0)
                    drawText(canvas, s1, startScale - i);
            } else if (i % 10 == 5) {
                drawMiddleLine(canvas, s1);
                if (startScale - i >= 0)
                    drawText(canvas, s1, startScale - i);
            } else {
                drawShortLine(canvas, s1);
            }
            s1 += divider;
        }
        canvas.restore();
    }

    private float inity = 0;
    private float distance = 0; // 滑动距离
    private float scale = 0;

    // @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                inity = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                lastY = event.getY();
                distance = lastY - inity + distance;
                distance = processDistance(distance);
                scale = startScale + distance / divider;
                postInvalidate();
                inity = lastY;
                break;
            case MotionEvent.ACTION_UP:
                float f = (scale - (int) scale) * 40;
                if (f < 20) {
                    distance = distance - f;
                } else {
                    distance = distance + 40 - f;
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        notifyValueChange();
        return true;
    }

    protected float processDistance(float distance) {
        distance = distance <= -50* divider ? -50 * divider : distance; // 最少到50
        distance = distance > 150 * divider ? 150 * divider : distance; // 最多到250
        return distance;
    }

    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(scale);
        }
    }
}
