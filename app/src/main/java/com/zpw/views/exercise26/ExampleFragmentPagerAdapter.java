package com.zpw.views.exercise26;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zpw.views.R;

/**
 * Created by zpw on 2018/7/15.
 */
public class ExampleFragmentPagerAdapter extends FragmentPagerAdapter {
    public ExampleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ExampleFragment.newInstance(R.mipmap.pos0);
            case 1:
                return ExampleFragment.newInstance(R.mipmap.pos1);
            case 2:
                return ExampleFragment.newInstance(R.mipmap.pos2);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "ページ" + (position + 1);
    }
}
