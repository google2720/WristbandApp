package com.canice.wristbandapp.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canice.wristbandapp.BuildConfig;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.AboutActivity;
import com.canice.wristbandapp.activity.AntiLostActivity;
import com.canice.wristbandapp.activity.DeviceManageActivity;
import com.canice.wristbandapp.activity.MainActivity;
import com.canice.wristbandapp.activity.SetLanguageActivity;
import com.canice.wristbandapp.activity.SosActivity;
import com.canice.wristbandapp.activity.personaldata.ProfileActivity;
import com.canice.wristbandapp.activity.personaldata.ProfileGoalSettingActivity;
import com.canice.wristbandapp.clock.CallRemindActivity;
import com.canice.wristbandapp.clock.ClockActivity;
import com.canice.wristbandapp.data.RecordDataActivity;
import com.canice.wristbandapp.remind.RemindActivity;

public class PersonalFragment extends BaseFragment implements OnClickListener {

    private MainActivity mActivity;
    private TextView nickNameView;
    private ImageView HeadView;

    @Override
    public void handleMessage(Message msg) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_personal, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        view.findViewById(R.id.rl_personal_profile).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_datacenter).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_device).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_target).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_remind).setOnClickListener(this);
        view.findViewById(R.id.personal_sos_setup).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_clock).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_call_remind).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_about).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_anti_lost).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_language).setOnClickListener(this);
        nickNameView = (TextView) view.findViewById(R.id.nick_name);
        HeadView = (ImageView) view.findViewById(R.id.imageview_user_head);
        if (BuildConfig.newFit){
            view.findViewById(R.id.personal_sos_setup).setVisibility(View.VISIBLE);
            view.findViewById(R.id.rl_personal_anti_lost).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.personal_sos_setup).setVisibility(View.GONE);
            view.findViewById(R.id.rl_personal_anti_lost).setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        nickNameView.setText(getNickName());
        HeadView.setImageResource(getHeadResId());
    }

    private int getHeadResId() {
        return UserController.isMan(getActivity()) ? R.drawable.man : R.drawable.woman;
    }

    private String getNickName() {
        return UserController.getNickname(getActivity());
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_personal_profile:
                intent = new Intent(mActivity, ProfileActivity.class);
                this.startActivity(intent);
                break;
            case R.id.rl_personal_datacenter:
                intent = new Intent(mActivity, RecordDataActivity.class);
                this.startActivity(intent);
                break;
            case R.id.rl_personal_device:
                intent = new Intent(mActivity, DeviceManageActivity.class);
                this.startActivity(intent);
                break;
            case R.id.rl_personal_target:
                intent = new Intent(mActivity, ProfileGoalSettingActivity.class);
                this.startActivity(intent);
                break;
            case R.id.rl_personal_clock:
                intent = new Intent(mActivity, ClockActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_remind:
                intent = new Intent(mActivity, RemindActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_call_remind:
                intent = new Intent(mActivity, CallRemindActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_about:
                intent = new Intent(mActivity, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.personal_sos_setup:
                intent = new Intent(mActivity, SosActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_anti_lost:
                intent = new Intent(mActivity, AntiLostActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_language:
                intent = new Intent(mActivity, SetLanguageActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
