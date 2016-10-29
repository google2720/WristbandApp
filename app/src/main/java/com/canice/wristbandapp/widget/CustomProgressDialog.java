
/**************************************************************************************
* [Project]
*       MyProgressDialog
* [Package]
*       com.lxd.widgets
* [FileName]
*       CustomProgressDialog.java
* [Copyright]
*       Copyright 2012 LXD All Rights Reserved.
* [History]
*       Version          Date              Author                        Record
*--------------------------------------------------------------------------------------
*       1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
**************************************************************************************/

package com.canice.wristbandapp.widget;

import com.canice.wristbandapp.R;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProgressDialog extends AlertDialog {

    private TextView msgView;
    private ImageView imageView;
    private AnimationDrawable animationDrawable;
    private String msg;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("yy", "onCreate");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.customprogressdialog, null);
        msgView = (TextView) view.findViewById(R.id.id_tv_loadingmsg);
        setMessage(msg);
        imageView = (ImageView) view.findViewById(R.id.loadingImageView);
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        setView(view);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i("yy", "onWindowFocusChanged " + hasFocus);
        if (hasFocus) {
            animationDrawable.start();
        } else {
            animationDrawable.stop();
        }
    }

    public CustomProgressDialog setTitile(String strTitle) {
        return this;
    }

    public CustomProgressDialog setMessage(String msg) {
        this.msg = msg;
        if (msgView != null) {
            msgView.setText(msg);
        }
        return this;
    }
}
