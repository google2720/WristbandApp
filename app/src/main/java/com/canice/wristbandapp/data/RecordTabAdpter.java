package com.canice.wristbandapp.data;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class RecordTabAdpter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;

    public RecordTabAdpter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

}
