package com.zpw.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.zpw.views.exercise15.JiKeGalleryEntity;
import com.zpw.views.exercise15.JiKeGalleryView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private Integer[] mImgs = new Integer[]{
            R.mipmap.pos0,
            R.mipmap.pos1,
            R.mipmap.pos2
    };
    private String[] mTitles = new String[]{
            "测试1",
            "测试2",
            "测试3"
    };

    private List<JiKeGalleryEntity> mEntities = new ArrayList<>();
    private JiKeGalleryView mGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jike_gallery);
        mGallery = (JiKeGalleryView) findViewById(R.id.gallery0);

        for (int j = 0; j < mImgs.length; j++) {
            JiKeGalleryEntity entity = new JiKeGalleryEntity();
            entity.imgUrl = mImgs[j];
            entity.title = mTitles[j];
            mEntities.add(entity);
        }
        mGallery.addGalleryData(mEntities);
    }

    private void startSmooth() {
        mGallery.startSmooth();
    }

    public void onRefresh(View view) {
        startSmooth();
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
