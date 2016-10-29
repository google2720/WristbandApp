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
import com.canice.wristbandapp.model.DeviceInfo;

public class DeviceListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
    private List<DeviceInfo> deviceInfos;
    
    public DeviceListAdapter(Context context, List<DeviceInfo> deviceInfos){
        this.mInflater = LayoutInflater.from(context);
        this.deviceInfos = deviceInfos;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return deviceInfos.size();
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
             
            convertView = mInflater.inflate(R.layout.layout_device_list_item, null);
            holder.tv_name = (TextView)convertView.findViewById(R.id.tv_device_list_item_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tv_name.setText(deviceInfos.get(position).getDeviceName());
        
        return convertView;
    }
    
    class ViewHolder{
        public TextView tv_name;
    }
}
