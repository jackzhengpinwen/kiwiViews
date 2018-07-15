package com.zpw.views.exercise24;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.Size;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by zpw on 2018/7/15.
 */

public class MyNestedScrollChild extends LinearLayout implements NestedScrollingChild {
    private NestedScrollingChildHelper mChildHelper;
    private int mLastTouchY;
    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];
    private int mCanScrollY;
    private int mVisiableHeight;
    private int mFullHeight;

    //fling
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private OverScroller mScroller;
    private int mFlingY;
    private boolean mIsBeingFling;
    private boolean mIsBeingDragged;

    public MyNestedScrollChild(Context context) {
        this(context, null);
    }

    public MyNestedScrollChild(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mChildHelper = new NestedScrollingChildHelper(this);
        mChildHelper.setNestedScrollingEnabled(true);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mScroller = new OverScroller(context);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable @Size(value = 2) int[] consumed, @Nullable @Size(value = 2) int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                initOrResetVelocityTracker();
                mIsBeingFling = false;
                mScroller.computeScrollOffset();
                if (mIsBeingDragged = !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mIsBeingDragged) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                mLastTouchY = (int) (event.getRawY() + 0.5f);
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int y = (int) (event.getRawY() + 0.5f);
                if (!mIsBeingDragged && Math.abs(mLastTouchY - y) > mTouchSlop) {
                    mIsBeingDragged = true;
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (mIsBeingDragged) {
                    int dy = mLastTouchY - y;
                    mLastTouchY = y;
                    if (dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffset)) {
                        dy -= mScrollConsumed[1];
                    }
                    scrollBy(0, dy);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                final int yVelocity = - (int) velocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > mMinimumFlingVelocity && !dispatchNestedPreFling(0, yVelocity)) {
                    mScroller.fling(
                            0,
                            mFlingY,
                            0,
                            yVelocity,
                            0,
                            0,
                            Integer.MIN_VALUE,
                            Integer.MAX_VALUE
                    );
                    mFlingY = mScroller.getStartY();
                    mIsBeingFling = true;
                    invalidate();
                }
                mIsBeingDragged = false;
                resetVelocityTracker();
                invalidate();
                break;
            }
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            int disY = currY - mFlingY;
            scrollBy(0, disY);
            mFlingY = currY;
            postInvalidateOnAnimation();
        } else if (mIsBeingFling) {
            mIsBeingFling = false;
        }
    }

    private void resetVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y > mCanScrollY) {
            y = mCanScrollY;
        }
        if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mVisiableHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mVisiableHeight = getMeasuredHeight();
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (mFullHeight <= 0) {
                mFullHeight = getMeasuredHeight();
                mCanScrollY = mFullHeight - mVisiableHeight;
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    //////////////////////////////////////
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final float ppi = getContext().getResources().getDisplayMetrics().density * 160.0f;
        mPhysicalCoeff = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f; // look and feel tuning
    }

    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)

    // A context-specific coefficient adjusted to physical values.
    private float mPhysicalCoeff;


    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));

    // Fling friction
    private float mFlingFriction = ViewConfiguration.getScrollFriction();

    /* Returns the duration, expressed in milliseconds */
    //通过初始速度获取最终滑动距离
    private int getSplineFlingDuration(int velocit) {
        final double l = getSplineDeceleration(velocit);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return (int) (1000.0 * Math.exp(l / decelMinusOne));
    }

    private double getSplineDeceleration(float velocity) {
        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics()
        );
    }
}
