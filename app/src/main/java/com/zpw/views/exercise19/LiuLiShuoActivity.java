package com.zpw.views.exercise19;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.zpw.views.R;

import java.util.ArrayList;
import java.util.List;

public class LiuLiShuoActivity extends AppCompatActivity {
    private final String TAG = "LiuLiShuoActivity";

    private int[] mImageResIds = new int[]{R.mipmap.intro_text_1, R.mipmap.intro_text_2, R.mipmap.intro_text_3};
    private List<View> mViewList = new ArrayList<>();

    private ViewPager mVpImage;
    private VideoView mVvPreview;
    private CustomPagerAdapter mAdapter;

    private int mCurrentPage;

    Handler mHanlder = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mVvPreview.seekTo(mCurrentPage * 6 * 1000);
            if (!mVvPreview.isPlaying()) {
                mVvPreview.start();
            }
            mHanlder.postDelayed(this, 6 * 1000);
        }
    };
    private PreviewIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liulishuo);

        mVpImage = (ViewPager) findViewById(R.id.vp_image);
        mVvPreview = (VideoView) findViewById(R.id.vv_preview);
        mIndicator = (PreviewIndicator) findViewById(R.id.indicator);

        mVvPreview.setVideoURI(Uri.parse(getVideoPath()));

        for (int i = 0; i < mImageResIds.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_preview_item, null, false);
            ((ImageView) view.findViewById(R.id.iv_intro_text)).setImageResource(mImageResIds[i]);
            mViewList.add(view);
        }

        mAdapter = new CustomPagerAdapter(mViewList);
        mVpImage.setAdapter(mAdapter);
        mVpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                mIndicator.setSelected(mCurrentPage);
                startLoop();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        startLoop();
    }

    private void startLoop() {
        mHanlder.removeCallbacks(mRunnable);
        mHanlder.postDelayed(mRunnable, 0);
    }

    private String getVideoPath() {
        return "android.resource://" + getPackageName() + "/" + R.raw.intro_video;
    }

    public static class CustomPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public CustomPagerAdapter(List<View> viewList) {
            mViewList = viewList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "dispatchTouchEvent: ");
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }


}
