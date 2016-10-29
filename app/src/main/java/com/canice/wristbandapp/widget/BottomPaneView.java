package com.canice.wristbandapp.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canice.wristbandapp.R;

public class BottomPaneView extends LinearLayout implements View.OnClickListener, OnTouchListener {

	private FragmentActivity activity;

	// 所有的tab
	private List<Tab<?>> tabs = new ArrayList<Tab<?>>();

	// 当前显示的tab
	private Tab<?> selectedTab;

	/**
	 * 第一次按下时间
	 */
	private long firstTime = 0;
	/**
	 * 双击监听
	 */
	private OnDoubleClickListener mDoubleClickListener;

	public BottomPaneView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置当前tab
	 */
	public void setSelectedTab(int index) {
		if (tabs != null && !tabs.isEmpty()) {
			if (index >= tabs.size() || index < 0) {
				index = 0;
			}
			show(tabs.get(index).mTag);
		}
	}

	/**
	 * 添加tab
	 */
	public void addTab(FragmentActivity activity, Tab<?> tab) {
		this.activity = activity;
		View view = tab.createView(this);
		view.setOnClickListener(this);
		view.setOnTouchListener(this);
		view.setTag(tab.mTag);
		addView(view);
		tabs.add(tab);
	}

	@Override
	public void onClick(View v) {
		String tag = (String) v.getTag();
		if (tag == null) {
			return;
		}
		Log.e(this.getClass().getSimpleName(), "onClick~~~");
		show(tag);
	}

	private void show(String tag) {
		Log.e(this.getClass().getSimpleName(), "show~~~");
		FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
		if (selectedTab != null && tag.equals(selectedTab.mTag)) {
			selectedTab.onTabReselected(selectedTab, ft);
		} else {
			for (Tab<?> t : tabs) {
				if (t.mTag.equals(tag)) {
					selectedTab = t;
					t.onTabSelected(t, ft);
				} else {
					t.onTabUnselected(t, ft);
				}
			}
		}
		ft.commitAllowingStateLoss();
	}

	public static interface TabListener {
		void onTabSelected(Tab<?> tab, FragmentTransaction ft);

		void onTabUnselected(Tab<?> tab, FragmentTransaction ft);

		void onTabReselected(Tab<?> tab, FragmentTransaction ft);
	}

	public static class ViewData {
		private int layoutResId = R.layout.bottom_pane_item;
		private int imageResId;
		private int imageActivatedResId;
		private int textColorResId;
		private int textColorActivatedResId;
		private int textResId;
		private ImageView imageView;
		private TextView textView;
		private View rootView;

		public ViewData(int... ids) {
			this(ids[0], ids[1], ids[2], ids[3], ids[4], ids[5]);
		}

		public ViewData(int layoutResId, int imageResId, int imageActivatedResId, int textColorResId,
				int textColorActivatedResId, int textResId) {
			this.layoutResId = layoutResId;
			this.imageResId = imageResId;
			this.imageActivatedResId = imageActivatedResId;
			this.textColorResId = textColorResId;
			this.textColorActivatedResId = textColorActivatedResId;
			this.textResId = textResId;
		}

		public View createView(ViewGroup parent) {
			rootView = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
			imageView = (ImageView) rootView.findViewById(R.id.image);
			textView = (TextView) rootView.findViewById(R.id.text);
			textView.setText(textResId);
			setUnselectedRes();
			return rootView;
		}

		public void setUnselectedRes() {
			imageView.setImageResource(imageResId);
			textView.setTextColor(textView.getResources().getColor(textColorResId));
			rootView.setEnabled(true);
		}

		public void setSelectedRes() {
			imageView.setImageResource(imageActivatedResId);
			textView.setTextColor(textView.getResources().getColor(textColorActivatedResId));
			//rootView.setEnabled(false);
			//打开，可以多次点击，响应双击事件
			rootView.setEnabled(true);
		}
	}

	public static class Tab<T extends Fragment> implements TabListener {

		private FragmentActivity mActivity;
		private String mTag;
		private Class<T> mClass;
		private Bundle mArgs;
		private Fragment mFragment;
		private ViewData viewData;
		private int contentId;
		private TabListener listener;

		public Tab(FragmentActivity activity, int contentId, Class<T> clz, ViewData viewData) {
			this(activity, contentId, clz.getSimpleName(), clz, null, viewData, null);
		}

		public Tab(FragmentActivity activity, int contentId, String tag, Class<T> clz, ViewData viewData) {
			this(activity, contentId, tag, clz, null, viewData, null);
		}

		public Tab(FragmentActivity activity, int contentId, String tag, Class<T> clz, Bundle args, ViewData viewData,
				TabListener listener) {
			mActivity = activity;
			this.contentId = contentId;
			mTag = tag;
			mClass = clz;
			mArgs = args;
			this.viewData = viewData;
			this.listener = listener;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
		}

		public View createView(ViewGroup parent) {
			return viewData.createView(parent);
		}

		public void onTabSelected(Tab<?> tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
				ft.add(contentId, mFragment, mTag);
			} else {
				//                ft.attach(mFragment);
				ft.show(mFragment);
			}
			viewData.setSelectedRes();
			if (listener != null) {
				listener.onTabSelected(tab, ft);
			}
		}

		public void onTabUnselected(Tab<?> tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.hide(mFragment);
			}
			viewData.setUnselectedRes();
			if (listener != null) {
				listener.onTabUnselected(tab, ft);
			}
		}

		public void onTabReselected(Tab<?> tab, FragmentTransaction ft) {
			if (listener != null) {
				listener.onTabReselected(tab, ft);
			}
		}

		public String getTag() {
			return mTag;
		}
		
		public Fragment getCurrentFragment(){
		    return mFragment;
		}
	}

	public Tab<?> getSelectedTag() {
		return selectedTab;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		String tag = (String) v.getTag();
		if (tag == null) {
			return false;
		}

		if (!("chat".equals(tag))) {
			return false;
		}

		if (MotionEvent.ACTION_DOWN == event.getAction()) {

			if (firstTime > 0) {
				long secondTime = SystemClock.elapsedRealtime();
				if (secondTime - firstTime < 300) {//双击事件
					if (mDoubleClickListener != null) {
						firstTime = SystemClock.elapsedRealtime();
						return mDoubleClickListener.onDoubleClick(v);
					}
				}
			}
			firstTime = SystemClock.elapsedRealtime();
		}
		return false;
	}

	/**
	 * setOnDoubleClickListener:注册一个双击回调 <br/>
	 * 
	 * @param onDoubleClickListener
	 *            The callback that will run
	 */
	public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
		mDoubleClickListener = onDoubleClickListener;
	}

	public interface OnDoubleClickListener {
		/**
		 * onDoubleClick:Called when a view has been double clicked. <br/>
		 * 
		 * @param view
		 *            The view that was double clicked.
		 */
		boolean onDoubleClick(View view);
	}

}
