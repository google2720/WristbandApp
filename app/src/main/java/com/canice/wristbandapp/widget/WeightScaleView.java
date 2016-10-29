package com.canice.wristbandapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

public class WeightScaleView extends View {

	private int mValue = 50;
	private OnValueChangeListener mListener;

	public void setValueChangeListener(OnValueChangeListener listener) {
		mListener = listener;
	}

	public interface OnValueChangeListener {
		public void onValueChange(float value);
	}

	public float getValue() {
		return mValue = (int) Scale;
	}

	/**
	 * 3.现在刻度值使用像素数
	 */

	private float mDensity;
	private int mWidth, mHeight;
	private Paint lablepPaint, scalePaint, textPaint;
	private float lastY;

	public WeightScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}

	public WeightScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public WeightScaleView(Context context) {
		super(context);
		initPaint();
		mDensity = getContext().getResources().getDisplayMetrics().density;
		// setBackgroundDrawable(createBackground());
	}

    public void setValue(int value) {
        Scale = value;
        distance = (startScale - Scale) * divider;
        notifyValueChange();
    }

	private GradientDrawable createBackground() {
		float strokeWidth = 2 * mDensity; // 边框宽度
		float roundRadius = 6 * mDensity; // 圆角半径
		int strokeColor = Color.parseColor("#FFF8F4EF");// 边框颜色
		setPadding((int) strokeWidth, (int) strokeWidth, (int) strokeWidth, 0);
		int colors[] = { 0xFFF8F4EF, 0xFFFFFFFF, 0xFFF8F4EF };// 分别为开始颜色，中间夜色，结束颜色
		GradientDrawable bgDrawable = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, colors);// 创建drawable
		bgDrawable.setCornerRadius(roundRadius);
		bgDrawable.setStroke((int) strokeWidth, strokeColor);
		return bgDrawable;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
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
		canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight / 2, lablepPaint);
		canvas.restore();
	}

	private void drawLongLine(Canvas canvas, float ystart) {
		canvas.drawLine(ystart, 0, ystart, mHeight * 2 / 3, scalePaint);
	}

	private void drawMiddleLine(Canvas canvas, float ystart) {
		canvas.drawLine(ystart, 0, ystart, mHeight / 2, scalePaint);
	}

	private void drawShortLine(Canvas canvas, float ystart) {
		canvas.drawLine(ystart, 0, ystart, mHeight / 3, scalePaint);
	}

	private void drawText(Canvas canvas, float ystart, float scale) {
		canvas.drawText(String.valueOf((int) scale), ystart-12, mHeight * 3 / 4+10,
				textPaint);
	}

	float divider = 40; // 刻度间隔
	int startScale = 50; // 初始刻度
	float startY; // 初始坐标
	int drawCount = 0; // 刻度数量

	// 从中间往两边划线
	private void drawScaleLine(Canvas canvas) {
		canvas.save();
		drawCount = (int) (mWidth / divider);
		startY = mWidth / 2 + distance;

		// 向上画线
		float s = startY;
		for (int i = 0; i < drawCount * 20; i++) {
			if (i % 10 == 0) {
				drawLongLine(canvas, s);
				drawText(canvas, s, startScale - i);
			} else if (i % 10 == 5) {
				drawMiddleLine(canvas, s);
				drawText(canvas, s, startScale - i);
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
				if (startScale + i >= 0)
					drawText(canvas, s1, startScale + i);
			} else if (i % 10 == 5) {
				drawMiddleLine(canvas, s1);
				if (startScale + i >= 0)
					drawText(canvas, s1, startScale + i);
			} else {
				drawShortLine(canvas, s1);
			}
			s1 += divider;
		}
		canvas.restore();
	}

	private static float inity = 0;
	private float distance = 0; // 滑动距离
	float Scale = 0;

	// @Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			inity = event.getX();
			break;

		case MotionEvent.ACTION_MOVE:
			lastY = event.getX();
			distance = lastY - inity + distance;
			distance = distance <= -150 * divider ? -150 * divider : distance;
			distance = distance > 49 * divider ? 49 * divider : distance;
			Scale = startScale - distance / divider;
			postInvalidate();
			inity = lastY;
			break;
		case MotionEvent.ACTION_UP:
			float f = (Scale - (int) Scale) * divider;
			if (f < (divider / 2)) {
				distance = distance + f;
			} else {
				distance = distance - divider + f;
			}
			postInvalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		notifyValueChange();
		return true;
	}

	private void notifyValueChange() {
		if (null != mListener) {
			mListener.onValueChange(Scale);
		}
	}
}
