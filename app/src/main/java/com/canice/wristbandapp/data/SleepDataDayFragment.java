package com.canice.wristbandapp.data;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.RecordSleepData;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HttpUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SleepDataDayFragment extends Fragment {

    protected static final String TAG = "yy";
    private SleepLayout sleepView;
    private Context context;
    private AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(String content) {
            Log.i(TAG, "onSuccess " + content);
            if (getActivity() == null) {
                return;
            }
            RecordSleepData info = JSON.parseObject(content, RecordSleepData.class);
            if (info == null) {
                onFailure(null);
                return;
            }
            sleepView.setData(info.getSsmTime(), info.getQsmTime());
        }

        @Override
        public void onFailure(Throwable error) {
            if (getActivity() == null) {
                return;
            }
            sleepView.setData(0, 0);
        }
    };
    private String startDate;
    private String endDate;

    public static SleepDataDayFragment newInstance(String startDate, String endDate) {
        SleepDataDayFragment fragment = new SleepDataDayFragment();
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
        return inflater.inflate(R.layout.fragment_sleep_data_day, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sleepView = (SleepLayout) view.findViewById(R.id.sleep);
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
        String url = Constants.SERVER_GET_SLEEP_DATA;
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(context));
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        HttpUtil.get(context, url, params, responseHandler);
    }
}