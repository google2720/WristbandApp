package com.canice.wristbandapp.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.adapter.SleepRecordAdapter;
import com.canice.wristbandapp.adapter.SportRecordAdapter;
import com.canice.wristbandapp.model.SleepRecordInfo;
import com.canice.wristbandapp.model.SportRecordInfo;

/**
 * 数据中心界面
 * @author canice_yuan
 *
 */
public class DataCenterActivity extends BaseActivity implements OnClickListener{

	private ListView lv_sport;
	private ListView lv_sleep;
	
	private SportRecordAdapter sportRecordAdapter;
	private SleepRecordAdapter sleepRecordAdapter;
	
	private List<SportRecordInfo> sportRecordInfos;
	private List<SleepRecordInfo> sleepRecordInfos;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_datacenter);
		
		initData();
		initViews();
	}
	
	private void initData(){
		sportRecordInfos = new ArrayList<SportRecordInfo>();
		for(int i = 5, j = 0; i > j; j++){
			SportRecordInfo info = new SportRecordInfo();
			sportRecordInfos.add(info);
		}
		
		sleepRecordInfos = new ArrayList<SleepRecordInfo>();
		for(int i = 5, j = 0; i > j; j++){
			SleepRecordInfo info = new SleepRecordInfo();
			sleepRecordInfos.add(info);
		}
	}
	
	private void initViews(){
		setLeftBtnEnabled(true);
		setTitle(R.string.datacenter_title);
		setLeftCloseBtnListener(new OnClickListener(){
			
			public void onClick(View v) {
				DataCenterActivity.this.finish();
			}
		});
		
		lv_sport = (ListView) findViewById(R.id.lv_sport);
		sportRecordAdapter = new SportRecordAdapter(this, sportRecordInfos);
		lv_sport.setAdapter(sportRecordAdapter);
		
		lv_sleep = (ListView) findViewById(R.id.lv_sleep);
		sleepRecordAdapter = new SleepRecordAdapter(this, sleepRecordInfos);
		lv_sleep.setAdapter(sleepRecordAdapter);
		
		findViewById(R.id.btn_datacenter_sport).setOnClickListener(this);
		findViewById(R.id.btn_datacenter_sleep).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
			case R.id.btn_datacenter_sport:
				lv_sport.setVisibility(View.VISIBLE);
				lv_sleep.setVisibility(View.GONE);
				break;
			case R.id.btn_datacenter_sleep:
				lv_sport.setVisibility(View.GONE);
				lv_sleep.setVisibility(View.VISIBLE);
				break;
		}
	}
}

	
