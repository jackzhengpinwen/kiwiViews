package com.zpw.views.exercise35;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.zpw.views.R;

public class ImageSwitcherActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory {

    private ImageSwitcher mImageSwitcher = null;
    private int[] mImageIds = {
            R.mipmap.pos0, R.mipmap.pos1,
            R.mipmap.pos2, R.mipmap.pos3
    };
    private int mIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageswitcher_layout);

        // 获取界面组件
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.img_switcher);

        // 为他它指定一个ViewFactory，也就是定义它是如何把内容显示出来的，
        // 实现ViewFactory接口并覆盖对应的makeView方法。
        mImageSwitcher.setFactory(this);

        // 添加动画效果
        mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));

        // 为ImageSwitcher绑定监听事件
        mImageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndex ++;
                if(mIndex >= mImageIds.length){
                    mIndex = 0;
                }
                mImageSwitcher.setImageResource(mImageIds[mIndex]);
            }
        });

        mImageSwitcher.setImageResource(mImageIds[0]);
    }

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(0xFF000000);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }
}
