package com.canice.wristbandapp.data;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.RecordStepData;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HttpUtil;
import com.canice.wristbandapp.util.Tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StepDataDayFragment extends Fragment {

    private static final String TAG = "yy";
    private TextView text1View;
    private TextView text2View;
    private TextView text3View;
    private TextView text4View;
    private Context context;
    private AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(String content) {
            Log.i(TAG, "onSuccess " + content);
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            RecordStepData info = JSON.parseObject(content, RecordStepData.class);
            if (info == null) {
                onFailure(null);
                return;
            }
            long t = info.getTotal();
            text1View.setText(generateLine1(t));
            text2View.setText(String.valueOf(t));
            double step = Double.parseDouble(UserController.getStepLong(activity));
            double weight = Double.parseDouble(UserController.getWeight(activity));
            text3View.setText(Tools.getDistanceStr(t, step));
            text4View.setText(Tools.getFireStr(t, step, weight));
        }

        @Override
        public void onFailure(Throwable error) {
            if (getActivity() == null) {
                return;
            }
            text1View.setText(generateLine1(0));
            text2View.setText("0");
            text3View.setText("0");
            text4View.setText("0");
        }
    };
    private String startDate;
    private String endDate;

    public static StepDataDayFragment newInstance(String startDate, String endDate) {
        StepDataDayFragment fragment = new StepDataDayFragment();
        Bundle b = new Bundle();
        b.putString("bundle_start_date", startDate);
        b.putString("bundle_end_date", endDate);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            startDate = b.getString("bundle_start_date");
            endDate = b.getString("bundle_end_date");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_data_day, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        text1View = (TextView) view.findViewById(R.id.text1);
        text2View = (TextView) view.findViewById(R.id.text2);
        text3View = (TextView) view.findViewById(R.id.text3);
        text4View = (TextView) view.findViewById(R.id.text4);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchRecordData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(context);
    }

    private void fetchRecordData() {
        String url = Constants.SERVER_GET_STEP_DATA;
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(context));
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        HttpUtil.get(context, url, params, responseHandler);
    }

    private CharSequence generateLine1(long t) {
        String t1 = String.valueOf(t);
        String t2 = getResources().getString(R.string.range_step);
        int l1 = t1.length();
        int l2 = t2.length();
        SpannableString msp = new SpannableString(t1 + t2);
        int length = msp.length();
        msp.setSpan(new AbsoluteSizeSpan(30, true), 0, l1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(0xff333333), 0, l1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(12, true), l1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(0xff999999), l1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }
}