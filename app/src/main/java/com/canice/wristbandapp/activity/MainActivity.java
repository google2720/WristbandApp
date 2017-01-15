package com.canice.wristbandapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.canice.wristbandapp.BuildConfig;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.fragment.HeartBeatFragment;
import com.canice.wristbandapp.activity.fragment.PersonalFragment;
import com.canice.wristbandapp.activity.fragment.RangeFragment;
import com.canice.wristbandapp.activity.fragment.SleepFragment;
import com.canice.wristbandapp.activity.fragment.SportFragment;
import com.canice.wristbandapp.ble.BleController;
import com.canice.wristbandapp.ble.HeartRateHelper;
import com.canice.wristbandapp.ble.db.BleDao;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.widget.BottomPaneView;
import com.canice.wristbandapp.widget.BottomPaneView.Tab;
import com.canice.wristbandapp.widget.BottomPaneView.TabListener;

public class MainActivity extends BaseActivity implements TabListener {

    private static int[] SPORT_RES_IDS = new int[]{
            R.layout.bottom_pane_chat_item,
            R.drawable.movement2,
            R.drawable.movement1,
            R.color.bottom_bar_text_color_off,
            R.color.green_theme, R.string.sport_title};

    private static int[] RANGE_RES_IDS = new int[]{
            R.layout.bottom_pane_chat_item,
            R.drawable.ranking2,
            R.drawable.ranking1,
            R.color.bottom_bar_text_color_off,
            R.color.green_theme, R.string.range_title};

    private static int[] SLEEP_RES_IDS = new int[]{
            R.layout.bottom_pane_chat_item,
            R.drawable.sleep2,
            R.drawable.sleep1,
            R.color.bottom_bar_text_color_off,
            R.color.green_theme, R.string.sleep_title};

    private static int[] HEARTBEAT_RES_IDS = new int[]{
            R.layout.bottom_pane_chat_item,
            R.drawable.heartbeat2,
            R.drawable.heartbeat1,
            R.color.bottom_bar_text_color_off,
            R.color.green_theme, R.string.heartbeat_title};

    private static int[] PERSONAL_RES_IDS = new int[]{
            R.layout.bottom_pane_chat_item,
            R.drawable.personal2,
            R.drawable.personal1,
            R.color.bottom_bar_text_color_off,
            R.color.green_theme, R.string.personal_title};

    private BottomPaneView bottomPane;

    private Tab<SportFragment> sportTab;
    private Tab<RangeFragment> rangeTab;
    private Tab<SleepFragment> sleepTab;
    private Tab<HeartBeatFragment> heartBeatTab;
    private Tab<PersonalFragment> personalTab;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.CHANGE_LANGUAGE.equals(intent.getAction())) {
                recreate();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initViews();
        IntentFilter filter = new IntentFilter(Constants.CHANGE_LANGUAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        bottomPane = (BottomPaneView) findViewById(R.id.footer);
        BottomPaneView.ViewData viewData = new BottomPaneView.ViewData(SPORT_RES_IDS);
        sportTab = new BottomPaneView.Tab<SportFragment>(this,
                R.id.content,
                "sport",
                SportFragment.class,
                null,
                viewData, this);

        viewData = new BottomPaneView.ViewData(RANGE_RES_IDS);
        rangeTab = new BottomPaneView.Tab<RangeFragment>(this,
                R.id.content,
                "range",
                RangeFragment.class,
                null,
                viewData, this);

        viewData = new BottomPaneView.ViewData(SLEEP_RES_IDS);
        sleepTab = new BottomPaneView.Tab<SleepFragment>(this,
                R.id.content,
                "sleep",
                SleepFragment.class,
                null,
                viewData, this);

        viewData = new BottomPaneView.ViewData(HEARTBEAT_RES_IDS);
        heartBeatTab = new BottomPaneView.Tab<HeartBeatFragment>(this,
                R.id.content,
                "heartbeat",
                HeartBeatFragment.class,
                null,
                viewData, this);

        viewData = new BottomPaneView.ViewData(PERSONAL_RES_IDS);
        personalTab = new BottomPaneView.Tab<PersonalFragment>(this,
                R.id.content,
                "personal",
                PersonalFragment.class,
                null,
                viewData, this);

        bottomPane.addTab(this, sportTab);
        bottomPane.addTab(this, rangeTab);
        bottomPane.addTab(this, sleepTab);
        if (BuildConfig.newFit) {
            bottomPane.addTab(this, heartBeatTab);
        }
        bottomPane.addTab(this, personalTab);

        bottomPane.setSelectedTab(0);
    }

    public void bottomPaneShow(boolean isShow) {
        if (isShow) {
            bottomPane.setVisibility(View.VISIBLE);
        } else {
            bottomPane.setVisibility(View.GONE);
        }
    }

    public void bottomPaneSelected(int index) {
        bottomPane.setSelectedTab(index);
    }

    @Override
    public void onTabSelected(Tab<?> tab, FragmentTransaction ft) {
        this.setRightBtnTextVisible(View.GONE);
        if (tab == personalTab) {
            this.setLeftBtnVisibility(View.GONE);
            this.setRightBtnVisibility(View.VISIBLE);
            this.setRightBtnImageResId(R.drawable.exit);
            this.setTitle(R.string.personal_title);
            this.setLeftBtnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    MainActivity.this.bottomPaneShow(true);
                    MainActivity.this.bottomPaneSelected(0);
                }
            });
            this.setRightBtnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.logout_message);
                    builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(intent);
                            UserController.logout(MainActivity.this);
                            BleDao.clearData(MainActivity.this);
                            finish();
                        }
                    });
                    builder.setNegativeButton(R.string.action_cancel, null);
                    builder.create().show();
                }
            });
            this.setTitle(R.string.personal_title);
        } else if (tab == rangeTab) {
            this.setLeftBtnVisibility(View.GONE);
            this.setRightBtnVisibility(View.VISIBLE);
            this.setTitle(R.string.range_title);
            this.setRightBtnImageResId(R.drawable.more);
            this.setRightBtnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SocialActivity.class);
                    startActivityForResult(intent, Constants.requestCode_social);
                }
            });
        } else if (tab == sleepTab) {
            this.setLeftBtnVisibility(View.GONE);
            this.setRightBtnVisibility(View.GONE);
            this.setTitle(R.string.sleep_title);
        } else if (tab == heartBeatTab) {
            this.setLeftBtnVisibility(View.GONE);
            this.setTitle(R.string.heartbeat_title);
            this.setRightBtnVisibility(View.GONE);
            TextView rightTextView = getRightTitle();
            int state = BleController.getInstance().getHeartRateHelper().getState();
            if (state == HeartRateHelper.STATE_START) {
                rightTextView.setText(R.string.heartbeat_stop);
            } else if (state == HeartRateHelper.STATE_STOP) {
                rightTextView.setText(R.string.heartbeat_start);
            } else {
                rightTextView.setText(R.string.heartbeat_pre_stop);
            }
            this.setRightBtnTextVisible(View.VISIBLE);
            HeartBeatFragment heart = (HeartBeatFragment) heartBeatTab.getCurrentFragment();
            heart.setRightTitle(getRightTitle());
        } else if (tab == sportTab) {
            this.setLeftBtnVisibility(View.GONE);
            if (BuildConfig.newFit) {
                this.setRightBtnVisibility(View.VISIBLE);
            } else {
                this.setRightBtnVisibility(View.GONE);
            }
            this.setRightBtnImageResId(R.drawable.share2);
            this.setTitle(R.string.sport_title);
            this.setRightBtnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                    SportFragment sportFragment = (SportFragment) sportTab.getCurrentFragment();
                    intent.putExtra("recordInfo", sportFragment.recordInfo);
                    startActivity(intent);
                }
            });
        }
        if (tab != heartBeatTab) {
            HeartBeatFragment heart = (HeartBeatFragment) heartBeatTab.getCurrentFragment();
            if (heart != null && BleController.getInstance().getHeartRateHelper().isStart()) {
                BleController.getInstance().getHeartRateHelper().closeHeartRateAsync(0);
            }
        }
    }

    @Override
    public void onTabUnselected(Tab<?> tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab<?> tab, FragmentTransaction ft) {
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        switch (arg0) {
            case Constants.requestCode_social: {
                switch (arg1) {
                    case Constants.resultCode_social: {
                        RangeFragment range = (RangeFragment) rangeTab.getCurrentFragment();
                        range.getRank();
                    }
                    break;

                    default:
                        break;
                }
            }
            break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        Log.i(TAG, "onDestroy");
    }
}
