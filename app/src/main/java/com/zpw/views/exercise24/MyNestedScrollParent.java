package com.zpw.views.exercise24;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.zpw.views.R;

/**
 * Created by zpw on 2018/7/15.
 */

public class MyNestedScrollParent extends LinearLayout implements NestedScrollingParent {
    private NestedScrollingParentHelper mParentHelper;
    private int mImgHeight;
    private boolean mIsImageVisiable;

    //fling
    private OverScroller mScroller;
    private int mFlingY;
    private int mLastTouchY;
    private VelocityTracker mVelocityTracker;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private boolean mIsBeingFling;
    private boolean mIsBeingDragged;
    private int mTouchSlop;

    public MyNestedScrollParent(Context context) {
        this(context, null);
    }

    public MyNestedScrollParent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mParentHelper = new NestedScrollingParentHelper(this);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mScroller = new OverScroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final ImageView imageview = (ImageView) findViewById(R.id.imageview);
        imageview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mImgHeight <= 0) {
                    mImgHeight = imageview.getHeight();
                }
            }
        });
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        mIsImageVisiable = (dy > 0 && getScrollY() < mImgHeight) || (dy < 0 && target.getScrollY() <= 0);
        if (mIsImageVisiable) {
            mFlingY += dy;
            consumed[1] = dy;
            scrollBy(0, dy);
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (mIsImageVisiable) {
            mScroller.fling(
                    0,
                    mFlingY,
                    0,
                    (int) velocityY,
                    0,
                    0,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
            );
            invalidate();
            return true;
        } else {
            return false;
        }
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

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y > mImgHeight) {
            y = mImgHeight;
        }
        if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
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
                    scrollBy(0, dy);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                final int yVelocity = -(int) velocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > mMinimumFlingVelocity) {
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

    private void resetVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }
}
