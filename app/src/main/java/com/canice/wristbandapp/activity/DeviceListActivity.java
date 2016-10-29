package com.canice.wristbandapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.adapter.DeviceListAdapter;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;
import com.canice.wristbandapp.model.DeviceInfo;
import com.canice.wristbandapp.model.ResponseInfo;
import com.canice.wristbandapp.util.Constants;
import com.canice.wristbandapp.util.HintUtils;
import com.canice.wristbandapp.util.HttpUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 设备列表界面
 * 
 * @author canice_yuan
 */
public class DeviceListActivity extends BaseActivity {

    private ListView lv_device;
    private DeviceListAdapter deviceListAdapter;
    private List<DeviceInfo> deviceInfos;
    private int deviceAction;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device_list);
        mContext = this;
        initData();
        initViews();
    }

    private void initData() {
        deviceAction = this.getIntent().getIntExtra(Constants.DEVICE_ACTION, Constants.DEVICE_ACTION_REMOVE);
        deviceInfos = new ArrayList<DeviceInfo>();
        for (int i = 5, j = 0; i > j; j++) {
            DeviceInfo info = new DeviceInfo();
            info.setDeviceName("device" + j);
            deviceInfos.add(info);
        }
    }

    private void initViews() {
        setLeftBtnEnabled(true);
        if (deviceAction == Constants.DEVICE_ACTION_REMOVE) {
            setTitle(R.string.device_remove);
        } else {
            setTitle(R.string.device_add);
        }
        setLeftCloseBtnListener(new OnClickListener() {
            public void onClick(View v) {
                DeviceListActivity.this.finish();
            }
        });

        lv_device = (ListView) findViewById(R.id.lv_device);
        deviceListAdapter = new DeviceListAdapter(this, deviceInfos);
        lv_device.setAdapter(deviceListAdapter);
        lv_device.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deviceOperate(deviceInfos.get(position).getDeviceId());
            }
        });
    }

    private void deviceOperate(String id) {
        String url = "";
        if (deviceAction == Constants.DEVICE_ACTION_REMOVE) {
            url = Constants.SERVER_UNBIND_DIVICE;
        } else {
            url = Constants.SERVER_BIND_DIVICE;
        }
        RequestParams params = new RequestParams();
        params.put(Constants.ID, UserController.getUserId(getApplicationContext()));
        params.put(Constants.EXDEVICEID, id);
        HttpUtil.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                HintUtils.showDiaog(mContext);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                ResponseInfo info = JSON.parseObject(content, ResponseInfo.class);
                HintUtils.dissDialog();
                if (TextUtils.isEmpty(content) || TextUtils.isEmpty(info.getRetCode())) {
                    HintUtils.showLongToast(mContext, getString(R.string.internet_error));
                } else if (info.getRetCode().equals(Constants.RETCODE_SUCCESS)
                        || info.getRetCode().equals(Constants.RETCODE_FAILURE)) {
                    HintUtils.showLongToast(mContext, info.getRetMsg());
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                HintUtils.dissDialog();
                HintUtils.showLongToast(mContext, getString(R.string.internet_error));
            }
        });
    }
}