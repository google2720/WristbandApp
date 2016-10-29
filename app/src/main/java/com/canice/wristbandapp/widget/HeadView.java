package com.canice.wristbandapp.widget;

import com.canice.wristbandapp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HeadView extends LinearLayout {
    /**
     * 标题
     */
    private TextView mTextViewTitle;

    /**
     * 标题icon
     */
    private TextView mRightTitle;
    /**
     * 左边按钮
     */
    private ImageButton mBtnLeft;

    /**
     * 右边按钮
     */
    private ImageButton mBtnRight;

    /**
     * 整个View
     */
    private View mHeadView;

    /**
     * 当前左边按钮采用的背景图片
     */
    private int mBtnLeftBGResource = -1;
    /**
     * 当前左边按钮采用的背景图片
     */
    private int mBtnRightBGResource = -1;
    /**
     * 当前左边按钮采用的背景图片
     */
    private int mBtnLeftTextResource = -1;

    /**
     * 当前左边按钮采用的背景图片
     */
    private int mBtnRightTextResource = -1;

    private TextView mBtnLeftClose;

    private ViewGroup rightContainer;

    public HeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public int getRightBtnTextResource() {
        return mBtnRightTextResource;
    }

    public int getLeftBtnTextResource() {
        return mBtnLeftTextResource;
    }

    /**
     * 初始化View
     * 
     * @param context
     * @param screenWidth
     */
    @SuppressLint("InflateParams")
    public void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mHeadView = layoutInflater.inflate(R.layout.layout_head, this);

        mTextViewTitle = (TextView) mHeadView.findViewById(R.id.headerView);
        mRightTitle = (TextView) mHeadView.findViewById(R.id.right_title);
        mBtnRight = (ImageButton) mHeadView.findViewById(R.id.btn_right_title);
        // icon = getResources().getDrawable(R.drawable.notify_icon);
        mBtnLeft = (ImageButton) mHeadView.findViewById(R.id.btn_left_title);
        mBtnLeftClose = (TextView) mHeadView.findViewById(R.id.btn_close_title);
        rightContainer = (ViewGroup) mHeadView.findViewById(R.id.linearlayout_right_title);
    }

    /**
     * 获取左边按钮的背景资源ID
     * 
     * @return
     */
    public int getLeftBtnBGResource() {
        return mBtnLeftBGResource;
    }

    /**
     * 设置左边按钮显示状态
     * 
     * @param visible
     */
    public void setLeftBtnVisibility(int visible) {
        mBtnLeft.setVisibility(visible);
    }

    /**
     * 设置左边按钮背景 一旦为左边按钮设置监听，将会导致无法切换menu菜单
     * 
     * @param resource
     */
    public void setLeftBtnClickListener(OnClickListener listener) {
        mBtnLeft.setOnClickListener(listener);
    }

    /**
     * 设置右边按钮显示状态
     * 
     * @param visible
     */
    public void setRightBtnVisibility(int visible) {
        mBtnRight.setVisibility(visible);
    }

    /**
     * 设置右边按钮文字
     */
    public void setRightBtnText(int resid) {
        mRightTitle.setText(resid);
    }
    
    /**
     * 设置右边按钮文字
     */
    public void setRightBtnText(String text) {
        mRightTitle.setText(text);
    }

    /**
     * 设置右边按钮描述
     */
    public void setRightBtnDesc(String desc) {
        mBtnRight.setContentDescription(desc);
    }

    /**
     * 设置左边按钮背景 一旦为左边按钮设置监听，将会导致无法切换menu菜单
     * 
     * @param resource
     */
    public void setRightBtnClickListener(OnClickListener listener) {
        mBtnRight.setOnClickListener(listener);
    }
    
    public void addRightView(View view) {
        rightContainer.removeAllViews();
        rightContainer.addView(view);
    }
    
    public ViewGroup getRightView() {
        return rightContainer;
    }

    /**
     * 获取右边按钮的背景资源ID
     * 
     * @return
     */
    public int getRightBtnBGResource() {
        return mBtnRightBGResource;
    }

    /**
     * 设置标题
     * 
     * @param resource
     */
    public void setTitle(int resource) {
        mTextViewTitle.setText(resource);
    }

    /**
     * 设置标题 icon
     * 
     * @param resource
     */
    public void setTitleIcon(int resource) {
        if (resource >= 0) {
            mRightTitle.setBackgroundResource(resource);
            mRightTitle.setVisibility(View.VISIBLE);
        } else {
            mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            mRightTitle.setVisibility(View.GONE);
        }

    }

    /**
     * 设置标题
     * 
     * @param title
     */
    public void setTitle(String title) {
        mTextViewTitle.setText(title);
    }

    /**
     * 设置标题的右侧图片,与文字相距10dip
     * 
     * @param resource
     */
    public void setTitleRightDrawable(Drawable resource) {
        mTextViewTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, resource, null);
    }

    /**
     * 设置标题颜色
     * 
     * @param color 标题颜色ID
     */
    public void setTitleColor(int color) {
        mTextViewTitle.setTextColor(color);
    }

    public ImageButton getBtnLeft() {
        return mBtnLeft;
    }

    /**
     * 设置头部背景
     */
    public void setHeadBackground(int color) {
        mHeadView.setBackgroundColor(color);
    }

    public void setLeftCloseBtnVisibility(int visible) {
        mBtnLeftClose.setVisibility(visible);
    }

    public void setLeftCloseBtnClickListener(OnClickListener listener) {
        mBtnLeftClose.setOnClickListener(listener);
    }

    public void setRightBtnEnabled(boolean enabled) {
        mBtnRight.setEnabled(enabled);
    }

    public void setRightBtnImageResId(int resource) {
        mBtnRight.setImageResource(resource);
    }

    public void setRightBtnTextClick(OnClickListener clickListener) {
        mRightTitle.setOnClickListener(clickListener);
    }

    public void setRightBtnTextVisible(int visible) {
        mRightTitle.setVisibility(visible);
    }
}