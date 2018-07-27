package com.zpw.views.exercise35;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.zpw.views.R;

import java.util.Timer;
import java.util.TimerTask;

public class SlideShowActivity extends AppCompatActivity {

    private ImageSwitcher mImageSwitcher;
    private AlphaAnimation inAlphaAnimation;
    private AlphaAnimation outAlphaAnimation;
    private AnimationSet inAnimationSet;
    private AnimationSet outAnimationSet;

    Bitmap bitmap;
    Bitmap bitmap2;
    BitmapDrawable bitmapDrawable;
    boolean bitmapFlag;

    int[] mImageResources = {
            R.mipmap.pos0, R.mipmap.pos1
            , R.mipmap.pos2, R.mipmap.pos3
            , R.mipmap.pos0, R.mipmap.pos1
            , R.mipmap.pos2, R.mipmap.pos3
    };

    int mPosition = 0;

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    movePositionBetter();
                }
            });
        }
    }

    private void movePosition() {
        mPosition = mPosition + 1;
        if (mPosition >= mImageResources.length) {
            mPosition = 0;
        }
        bitmap = BitmapFactory.decodeResource(getResources(), mImageResources[mPosition]);
        bitmapDrawable = new BitmapDrawable(getResources(), bitmap);

        mImageSwitcher.setImageDrawable(bitmapDrawable);
    }

    private void movePositionBetter() {
        mPosition = mPosition + 1;
        if (mPosition >= mImageResources.length) {
            mPosition = 0;
        }

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        bitmap = BitmapFactory.decodeResource(getResources(), mImageResources[mPosition]);
        bitmapDrawable = new BitmapDrawable(getResources(), bitmap);

        mImageSwitcher.setImageDrawable(bitmapDrawable);
    }

    private void movePositionBest() {
        mPosition = mPosition + 1;
        if (mPosition >= mImageResources.length) {
            mPosition = 0;
        }
        if (!bitmapFlag) {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            bitmap = BitmapFactory.decodeResource(getResources(), mImageResources[mPosition]);
            bitmapDrawable = new BitmapDrawable(getResources(), bitmap);

        } else {
            if (bitmap2 != null) {
                bitmap2.recycle();
                bitmap2 = null;
            }
            bitmap2 = BitmapFactory.decodeResource(getResources(), mImageResources[mPosition]);
            bitmapDrawable = new BitmapDrawable(getResources(), bitmap2);

        }
        mImageSwitcher.setImageDrawable(bitmapDrawable);
        bitmapFlag = !bitmapFlag;
    }

    Timer mTimer = new Timer();
    TimerTask mTimerTask = new MainTimerTask();
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageswitcher_layout);

        mImageSwitcher = (ImageSwitcher) findViewById(R.id.img_switcher);

        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        setMotion();

        mTimer.schedule(mTimerTask, 0, 4000);
    }

    private void setMotion() {
        inAlphaAnimation = new AlphaAnimation(0, 1);
        inAlphaAnimation.setDuration(1500);
        inAlphaAnimation.setStartOffset(0);
        inAlphaAnimation.setInterpolator(new DecelerateInterpolator());
        inAnimationSet = new AnimationSet(false);
        inAnimationSet.addAnimation(inAlphaAnimation);

        outAlphaAnimation = new AlphaAnimation(1, 0);
        outAlphaAnimation.setDuration(1000);
        outAlphaAnimation.setStartOffset(0);
        outAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        outAnimationSet = new AnimationSet(false);
        outAnimationSet.addAnimation(outAlphaAnimation);
        mImageSwitcher.setInAnimation(inAnimationSet);
        mImageSwitcher.setOutAnimation(outAnimationSet);
    }
}
