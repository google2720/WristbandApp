package com.canice.wristbandapp.activity.fragment;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public abstract class BaseFragment extends Fragment{

    protected String TAG = this.getClass().getSimpleName();

    private static final String TAG_LIFECYCLE = "Fragment";

    private static final boolean DEBUG_LIFECYCLE = false;

    protected Handler mHandler = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onAttach");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onCreate");
        }
        initHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onCreateView");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onActivityCreated");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onResume");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onStart");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onPause");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onStop");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onDestroy");
        }
    }

    
    @Override
    public void onDetach() {
        super.onDetach();
        if (DEBUG_LIFECYCLE) {
        	Log.d(TAG_LIFECYCLE, this + " onDetach");
        }
    }
   
    protected  void unregisterContentObserver(ContentObserver observer) {
    	getActivity().getContentResolver().unregisterContentObserver(observer);
    }
    
    protected void registerContentObserver(Uri uri, boolean notifyForDescendents, ContentObserver observer) {
    	getActivity().getContentResolver().registerContentObserver(uri, notifyForDescendents, observer);
    }
    

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                BaseFragment.this.handleMessage(msg);
            }
        };
    }

    public abstract void handleMessage(Message msg);

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 获取当前应用的 Activity数量
     * 
     * @return 当前存留的Activity数量
     */
    public int getCurrentActivitisSize() {
    	@SuppressWarnings("static-access")
		ActivityManager actM = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
    	List<ActivityManager.RunningTaskInfo> listm = actM.getRunningTasks(1);
    	int iNumActivity = listm.get(0).numActivities;
    	Log.i(TAG, "" + iNumActivity);
    	return iNumActivity;
    }
    public boolean onBackPressed() {
        return true;
    }
}
