package com.canice.wristbandapp.data;

import com.canice.wristbandapp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class PagerTitleLayout extends LinearLayout {

    private Paint paint;
    private Rect rect = new Rect();
    private int itemWidth;
    private ViewPager viewPager;
    private int lineHeight;
    private int left;
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            left = (int) (itemWidth * (position + positionOffset));
            ViewCompat.postInvalidateOnAnimation(PagerTitleLayout.this);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    @SuppressWarnings("deprecation")
    public PagerTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        lineHeight = Math.round(5 * getResources().getDisplayMetrics().density);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.green_theme));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        itemWidth = getChildAt(0).getMeasuredWidth();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.text1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        findViewById(R.id.text2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        findViewById(R.id.text3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        rect.left = left;
        rect.right = left + itemWidth;
        rect.bottom = getHeight();
        rect.top = rect.bottom - lineHeight;
        canvas.drawRect(rect, paint);
    }
}
