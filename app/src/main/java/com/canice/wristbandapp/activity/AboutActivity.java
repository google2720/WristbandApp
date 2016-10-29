package com.canice.wristbandapp.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.canice.wristbandapp.BuildConfig;
import com.canice.wristbandapp.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.personal_about);
        setContentView(R.layout.activity_about);
        TextView textView = (TextView) this.findViewById(R.id.tv_about);
        textView.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));
    }
}
