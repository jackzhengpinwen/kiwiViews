package com.zpw.views.exercise15;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by zpw on 2018/7/3.
 */

public abstract class SmoothViewGroup extends ViewGroup {
    //滑动状态
    protected static final int STATUS_SMOOTHING = 0;
    //停止状态
    protected static final int STATUS_STOP = 1;


    protected int mWidth, mHeight;//ViewGroup宽高
    protected int mSmoothMarginTop;//变化的marginTop值
    protected Context mContext;
    protected int mStatus = STATUS_STOP;//默认状态
    protected int mDuration = 500;//滚动时间间隔
    protected int mRepeatTimes = 0;//重复次数


    public SmoothViewGroup(Context context) {
        this(context, null);
    }

    public SmoothViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mSmoothMarginTop = -h;
        initView();
    }

    /**
     * 开启滑动
     */
    public void startSmooth() {
        if (mStatus != STATUS_STOP) {//设置状态值，多次启动动画无效
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(-mHeight, 0);
        animator.setDuration(mDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int marginTop = (int) animation.getAnimatedValue();
                mSmoothMarginTop = marginTop;

                if (marginTop == 0) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRepeatTimes++;//重复次数自增
                            mSmoothMarginTop = -mHeight;//回复初始高度
                            doAnimFinish();//动画结束回调
                            mStatus = STATUS_STOP;//更新滑动状态
                        }
                    }, 50);
                } else {
                    doAnim();//执行动画开始回调
                }
            }
        });
        animator.start();
        mStatus = STATUS_SMOOTHING;////更新滑动状态
    }

    protected abstract void initView();

    //动画结束
    protected abstract void doAnimFinish();

    //动画进行时
    protected abstract void doAnim();

    /**
     * 是否是奇数圈
     *
     * @return 结果
     */
    protected boolean isOddCircle() {
        return mRepeatTimes % 2 == 1;
    }
}
