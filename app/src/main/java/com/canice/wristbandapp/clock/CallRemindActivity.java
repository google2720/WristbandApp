package com.canice.wristbandapp.clock;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.canice.wristbandapp.NotificationService;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.SmsReceivedService;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.BaseActivity;
import com.canice.wristbandapp.util.HintUtils;

public class CallRemindActivity extends BaseActivity {

    private Switch weChatRemindView;
    private CompoundButton.OnCheckedChangeListener weChatCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            UserController.saveWeChatRemind(getBaseContext(), isChecked);
            if (isChecked) {
                processNotification(new Runnable() {
                    @Override
                    public void run() {
                        UserController.saveWeChatRemind(getBaseContext(), false);
                        setWeChatRemindViewStatus(false);
                    }
                });
            }
        }
    };
    private Switch qqRemindView;
    private CompoundButton.OnCheckedChangeListener qqCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            UserController.saveQQRemind(getBaseContext(), isChecked);
            if (isChecked) {
                processNotification(new Runnable() {
                    @Override
                    public void run() {
                        UserController.saveQQRemind(getBaseContext(), false);
                        setQQRemindViewStatus(false);
                    }
                });
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_remind);
        setTitle(R.string.personal_call_remind);
        initView();
    }

    private void initView() {
        Switch callRemindView = (Switch) findViewById(R.id.call_remind);
        callRemindView.setChecked(UserController.isCallRemind(getBaseContext()));
        callRemindView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserController.saveCallRemind(getBaseContext(), isChecked);
                if (isChecked) {
                    HintUtils.showLongToast(getBaseContext(), R.string.open_phone_remind_tip);
                }
            }
        });

        Switch smsRemindView = (Switch) findViewById(R.id.sms_remind);
        smsRemindView.setChecked(UserController.isSmsRemind(getBaseContext()));
        smsRemindView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserController.saveSmsRemind(getBaseContext(), isChecked);
                if (isChecked) {
                    SmsReceivedService.actionStart(getApplicationContext());
                    HintUtils.showLongToast(getBaseContext(), R.string.open_sms_remind_tip);
                }
            }
        });

        weChatRemindView = (Switch) findViewById(R.id.wechat_remind);

        qqRemindView = (Switch) findViewById(R.id.qq_remind);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWeChatRemindViewStatus(UserController.isWeChatRemind(getBaseContext()) && NotificationService.isEnabled(this));
        setQQRemindViewStatus(UserController.isQQRemind(getBaseContext()) && NotificationService.isEnabled(this));
    }

    private void setWeChatRemindViewStatus(boolean checked) {
        weChatRemindView.setOnCheckedChangeListener(null);
        weChatRemindView.setChecked(checked);
        weChatRemindView.setOnCheckedChangeListener(weChatCheckedChangeListener);
    }

    private void setQQRemindViewStatus(boolean checked) {
        qqRemindView.setOnCheckedChangeListener(null);
        qqRemindView.setChecked(checked);
        qqRemindView.setOnCheckedChangeListener(qqCheckedChangeListener);
    }

    private void processNotification(final Runnable runnable) {
        if (!NotificationService.isEnabled(getBaseContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CallRemindActivity.this);
            builder.setMessage(R.string.remind_notify_tip);
            builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    runnable.run();
                }
            });
            builder.setPositiveButton(R.string.action_setting, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    runnable.run();
                }
            });
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
}