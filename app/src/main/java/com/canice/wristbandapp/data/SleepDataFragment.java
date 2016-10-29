package com.canice.wristbandapp.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.canice.wristbandapp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SleepDataFragment extends Fragment {

    private static final String TAG = "yy";
    private ViewPager viewPager;
    private RecordTabAdpter tabAdpter;
    private Calendar calendar;
    private PagerTitleLayout pagerTitleLayout;

    public static SleepDataFragment newInstance(Calendar calendar) {
        SleepDataFragment fragment = new SleepDataFragment();
        Bundle b = new Bundle();
        b.putSerializable("bundle_date", calendar);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            calendar = (Calendar) b.getSerializable("bundle_date");
        } else {
            calendar = Calendar.getInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sleep_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(createDayFragment(calendar));
        fragments.add(createWeekFragment(calendar));
        fragments.add(createMonthFragment(calendar));
        viewPager.setOffscreenPageLimit(fragments.size());
        tabAdpter = new RecordTabAdpter(getFragmentManager(), fragments);
        viewPager.setAdapter(tabAdpter);

        pagerTitleLayout = (PagerTitleLayout) view.findViewById(R.id.pagerTitle);
        pagerTitleLayout.setViewPager(viewPager);
    }

    private Fragment createDayFragment(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String startDate = sdf.format(calendar.getTime());
        String endDate = startDate;
        Log.d(TAG, "createDayFragment " + startDate + " " + endDate);
        return SleepDataDayFragment.newInstance(startDate, endDate);
    }

    private Fragment createWeekFragment(Calendar calendar) {
        String startDate = getMondayOfThisWeek(calendar);
        String endDate = getSundayOfThisWeek(calendar);
        Log.d(TAG, "createWeekFragment " + startDate + " " + endDate);
        return SleepDataDayFragment.newInstance(startDate, endDate);
    }

    private Fragment createMonthFragment(Calendar calendar) {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String ms = (m < 10 ? "0" + m : String.valueOf(m));
        String startDate = y + "-" + ms + "-01";
        String endDate = y + "-" + ms + "-" + d;
        Log.d(TAG, "createMonthFragment " + startDate + " " + endDate);
        return SleepDataDayFragment.newInstance(startDate, endDate);
    }

    public static String getMondayOfThisWeek(Calendar calendar) {
        Calendar c = (Calendar) calendar.clone();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(c.getTime());
    }

    public static String getSundayOfThisWeek(Calendar calendar) {
        Calendar c = (Calendar) calendar.clone();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + 7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(c.getTime());
    }
}