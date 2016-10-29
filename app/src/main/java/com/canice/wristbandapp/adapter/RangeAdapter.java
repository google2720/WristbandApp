package com.canice.wristbandapp.adapter;

import java.util.List;

import com.canice.wristbandapp.R;
import com.canice.wristbandapp.UserController;
import com.canice.wristbandapp.model.RangeInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RangeAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<RangeInfo> rersonalInfos;
    private Context context;
    private String userId;

    public RangeAdapter(Context context, List<RangeInfo> rersonalInfos) {
        userId = UserController.getUserId(context);
        this.mInflater = LayoutInflater.from(context);
        this.rersonalInfos = rersonalInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rersonalInfos == null ? 0 : rersonalInfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_range_list_item, null);
            holder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_range_portrait);
            holder.tv_range = (TextView) convertView.findViewById(R.id.tv_range_range);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_range_name);
            holder.tv_step = (TextView) convertView.findViewById(R.id.tv_range_step);
            holder.tv_sumSteps = (TextView) convertView.findViewById(R.id.tv_sumSteps);
            holder.iv_me = (ImageView) convertView.findViewById(R.id.iv_me);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        RangeInfo info = rersonalInfos.get(position);
        int range = position + 1;
        holder.tv_range.setText("");
        if (1 == range) {
            holder.tv_range.setBackgroundResource(R.drawable.goldmedal);
        } else if (2 == range) {
            holder.tv_range.setBackgroundResource(R.drawable.silvermedal);
        } else if (3 == range) {
            holder.tv_range.setBackgroundResource(R.drawable.bronzemedal);
        } else {
            holder.tv_range.setText(range + "");
        }
        holder.tv_name.setText(info.getUserName());
        holder.tv_sumSteps.setText(info.getSumSteps());
        if (info.getUserId().equals(userId)) {
            holder.iv_me.setVisibility(View.VISIBLE);
            holder.tv_range.setTextColor(context.getResources().getColor(R.color.cff9c00));
            holder.tv_sumSteps.setTextColor(context.getResources().getColor(R.color.cff9c00));
        } else {
            holder.iv_me.setVisibility(View.GONE);
            holder.tv_range.setTextColor(context.getResources().getColor(R.color.black));
            holder.tv_sumSteps.setTextColor(context.getResources().getColor(R.color.black));
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_portrait;
        public TextView tv_range;
        public TextView tv_name;
        public TextView tv_step;
        public TextView tv_sumSteps;
        public ImageView iv_me;
    }
}
