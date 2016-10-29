package com.canice.wristbandapp.activity.fragment;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.activity.MainActivity;
import com.canice.wristbandapp.adapter.RangeAdapter;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.RangeInfo;
import com.canice.wristbandapp.model.RangeResponseInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class RangeFragment extends BaseFragment {

    private MainActivity mActivity;
    private ListView lv_range;
    private RangeAdapter rangeAdapter;
    // private List<RangeInfo> rersonalInfos;
    private TextView tv_range;
    private TextView tv_name;
    private TextView tv_sumSteps;
    private ImageView iv_me;


    @Override
    public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_range, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getRank();
        }
    }

    private void initViews(View view) {
        lv_range = (ListView) view.findViewById(R.id.lv_range);
        tv_range = (TextView) view.findViewById(R.id.tv_range_range);
        tv_name = (TextView) view.findViewById(R.id.tv_range_name);
        tv_sumSteps = (TextView) view.findViewById(R.id.tv_sumSteps);
        iv_me = (ImageView) view.findViewById(R.id.iv_me);
        getRank();
    }

    public void getRank() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        date = calendar.getTime();
        String startDate = df.format(date);
        String endDate = df.format(new Date());
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(mActivity));
        params.put(Constants.RANK_STARTDATE, startDate);
        params.put(Constants.RANK_ENDDATE, endDate);
        HttpUtil.get(Constants.SERVER_RANK, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                RangeResponseInfo info = JSON.parseObject(content, RangeResponseInfo.class);
                Collections.sort(info.rankInfos);
                String id = UserController.getUserId(mActivity);
                if (info.getRetCode().equals(Constants.RETCODE_SUCCESS)) {
                    for (int i = 0; i < info.rankInfos.size(); i++) {
                        if (info.rankInfos.get(i).getUserId().equals(id)) {
                            RangeInfo data = info.rankInfos.get(i);
                            tv_range.setText((i + 1) + "");
                            tv_name.setText(data.getUserName());
                            tv_sumSteps.setText(data.getSumSteps());
                            break;
                        }
                    }
                    rangeAdapter = new RangeAdapter(mActivity, info.rankInfos);
                    lv_range.setAdapter(rangeAdapter);
                } else if (info.getRetCode().equals(Constants.RETCODE_FAILURE)) {
                    getRankFailure(info.getRetMsg());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                getRankFailure(getString(R.string.internet_error));
            }
        });
    }

    private void getRankFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.dialog_title_tip);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.action_retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getRank();
            }
        });
        builder.setPositiveButton(R.string.action_cancel, null);
        builder.show();
    }
}
