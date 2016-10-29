package com.canice.wristbandapp.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.canice.wristbandapp.APP;
import com.canice.wristbandapp.widget.CustomProgressDialog;

public class HintUtils {
	/**
	 * Public ToastClass
	 * Avoid excessive Toast, display time is too long
	 * @author Kaze_W
	 */
	
	private static final String TAG = "HintUtils";
	private static Toast toast;
    private static View view;
    
    private static CustomProgressDialog dialog = null;
 
    private HintUtils() {
    }
 
    @SuppressLint("ShowToast")
    private static void getToast(Context context) {
        if (toast == null) {
            toast = new Toast(context);
        }
        if (view == null) {
            view = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
        }
        toast.setView(view);
    }
 
    public static void showShortToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }
 
    public static void showShortToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
    }
 
    public static void showLongToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
    }
 
    public static void showLongToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
    }
 
    private static void showToast(Context context, CharSequence msg,
            int duration) {
        try {
            getToast(context);
            toast.setText(msg);
            toast.setDuration(duration);
            toast.show();
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }
 
    private static void showToast(Context context, int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            getToast(context);
            toast.setText(resId);
            toast.setDuration(duration);
            toast.show();
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }
    
    
    public static void showDiaog(Context context){
    	if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}else if (dialog == null) {
			dialog = (CustomProgressDialog) APP.getDialog(context);
			dialog.show();
		}
    }
    public static void showDiaog(Context context,String message){
    	if (dialog != null && !dialog.isShowing()) {
    		dialog.setMessage(message);
			dialog.show();
		}else if (dialog == null){
			dialog =  (CustomProgressDialog) APP.getDialog(context);
			dialog.setMessage(message);
			dialog.show();
		}else {
			dialog.setMessage(message);
		}
    }
    public static void dissDialog(){
    	if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}else if (dialog != null) {
			dialog = null;
		}
    }
}
