package com.canice.wristbandapp.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.model.SleepRecordInfo;

public class SleepRecordAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
    private List<SleepRecordInfo> sleepRecordInfos;
    
    public SleepRecordAdapter(Context context, List<SleepRecordInfo> sleepRecordInfos){
        this.mInflater = LayoutInflater.from(context);
        this.sleepRecordInfos = sleepRecordInfos;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sleepRecordInfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();  
             
            convertView = mInflater.inflate(R.layout.layout_datacenter_sleep_list_item, null);
            holder.tv_date = (TextView)convertView.findViewById(R.id.tv_sleep_item_date);
            holder.tv_duration = (TextView)convertView.findViewById(R.id.tv_sleep_item_duration);
            convertView.setTag(holder);
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }
        
        return convertView;
    }
    
    class ViewHolder{
        public TextView tv_date;
        public TextView tv_duration;
    }
}
