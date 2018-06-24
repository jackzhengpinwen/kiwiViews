package com.zpw.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.zpw.views.exercise6.BlankFragment;
import com.zpw.views.exercise6.DropIndicator;
import com.zpw.views.exercise6.DropViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DropViewPager viewPager = (DropViewPager) findViewById(R.id.mViewPager);
        DropIndicator circleIndicator = (DropIndicator) findViewById(R.id.circleIndicator);
        final ArrayList<Fragment> mTabContents = new ArrayList<>();
        mTabContents.add(BlankFragment.newInstance("0", 0));
        mTabContents.add(BlankFragment.newInstance("1", 1));
        mTabContents.add(BlankFragment.newInstance("2", 2));
        mTabContents.add(BlankFragment.newInstance("3", 3));
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        viewPager.setTouchable(true);
        circleIndicator.setViewPager(viewPager);
    }
}
