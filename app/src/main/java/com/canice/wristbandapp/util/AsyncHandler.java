package com.canice.wristbandapp.util;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.activity.LoginActivity;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.model.ResponseInfo;

public class AsyncHandler extends AsyncHttpResponseHandler {
	
	private Context mContext = null;
	private ResponseInfo mInfo = null;
	private Class<ResponseInfo> mRespons = null;
	private Dialog mDialog = null;
	private String mSuccessMessage = null;
	private String mFailureMessage = null;
	private String mTag =  null;
	
	
	public AsyncHandler(Context context, String tag, Dialog dialog, Class<ResponseInfo> respons, String successsMsg, String failureMsg) {
		this.mContext = context;
		this.mDialog = dialog;
		this.mRespons = respons;
		this.mSuccessMessage = successsMsg;
		this.mFailureMessage = failureMsg;
		this.mTag = tag;
	}
	
	public AsyncHandler(Context context, String tag, Class<ResponseInfo> respons, String successsMsg, String failureMsg) {
		this.mContext = context;
		this.mRespons = respons;
		this.mSuccessMessage = successsMsg;
		this.mFailureMessage = failureMsg;
		this.mTag = tag;
	}
	
	public AsyncHandler(Context context, String tag, Dialog dialog, Class<ResponseInfo> respons) {
		this.mContext = context;
		this.mDialog = dialog;
		this.mRespons= respons;
		this.mTag = tag;
	}
	
	public AsyncHandler(Context context, String tag, Class<ResponseInfo> respons) {
		this.mContext = context;
		this.mRespons= respons;
		this.mTag = tag;
	}

	@Override
	public void onStart() {
		super.onStart();
		startDialog();
	}
	
	@Override
	public void onSuccess(int statusCode, String content) {
		super.onSuccess(statusCode, content);
		stopDialog();
		mInfo = JSON.parseObject(content,mRespons);
		if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(mInfo.getRetCode()) 
				&& mInfo.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
			showToast(mInfo.getRetMsg());
		}else if (!TextUtils.isEmpty(mInfo.getRetMsg())) {
			showToast(mInfo.getRetMsg());
		}else {
			showToast(mSuccessMessage);;
		}
	}
	
	@Override
	public void onFailure(Throwable error, String content) {
		super.onFailure(error, content);
		stopDialog();
		showToast(mFailureMessage);
		Log.e(mTag, content);
		Log.e(mTag, error.getMessage());
	}
	
	private void startDialog(){
		if (mDialog != null && !mDialog.isShowing()) {
			mDialog.show();
		}
	}
	
	private void stopDialog(){
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}else if (mDialog != null){
			mDialog = null;
		}
	}
	
	private void showToast(String messgae ){
		if (!TextUtils.isEmpty(messgae)) {
			HintUtils.showShortToast(mContext, messgae);
		}
	}

}
