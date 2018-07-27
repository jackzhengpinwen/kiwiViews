package com.zpw.views.exercise33;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by zpw on 2018/7/24.
 */

public class VDHLayout extends LinearLayout {

    private final ViewDragHelper mDragger;

    public VDHLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

            //当 captureView 被捕获时回调
            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }

            //是否需要 capture 这个 View
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;//返回true代表可滑动，false代表不可滑动
            }

            //这个地方实际上left就代表 你将要移动到的位置的坐标。返回值就是最终确定的移动的位置。
            //我们要让view滑动的范围在我们的layout之内
            //实际上就是判断如果这个坐标在layout之内 那我们就返回这个坐标值。
            //如果这个坐标在layout的边界处 那我们就只能返回边界的坐标给他。不能让他超出这个范围
            //除此之外就是如果你的layout设置了padding的话，也可以让子view的活动范围在padding之内的.
            //横向移动的时候回调
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                VDHLayout parent = (VDHLayout) child.getParent();
                int right = parent.getRight() - child.getWidth();
                return left >= 0 ? (left > right ? right : left) : 0;
            }

            //纵向移动的时候回调
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                VDHLayout parent = (VDHLayout) child.getParent();
                int bottom = parent.getBottom() - child.getHeight();
                return top >= 0 ? (top > bottom ? bottom : top) : 0;
            }

            //当ViewDragHelper状态发生变化时回调（IDLE,DRAGGING,SETTING[自动滚动时]）
            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            //当 captureView 的位置发生改变时回调
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            //当触摸到边界时回调
            @Override
            public void onEdgeTouched(int edgeFlags, int pointerId) {
                super.onEdgeTouched(edgeFlags, pointerId);
            }

            //true 的时候会锁住当前的边界，false 则 unLock
            @Override
            public boolean onEdgeLock(int edgeFlags) {
                return super.onEdgeLock(edgeFlags);
            }

            //边界拖动开始的时候回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                super.onEdgeDragStarted(edgeFlags, pointerId);
            }

            //改变同一个坐标（ x , y ）去寻找 captureView 位置的方法
            @Override
            public int getOrderedChildIndex(int index) {
                View bottomView = getChildAt(0);
                View topView = getChildAt(1);
                int indexTop = indexOfChild(topView);
                int indexBottom = indexOfChild(bottomView);
                if (index == indexTop) {
                    return indexBottom;
                }
                return index;
            }

            //最大横滑动的滑动距离
            @Override
            public int getViewHorizontalDragRange(View child) {
                return super.getViewHorizontalDragRange(child);
            }

            //最大纵向滑动的距离
            @Override
            public int getViewVerticalDragRange(View child) {
                return super.getViewVerticalDragRange(child);
            }

            //当 captureView 被释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                VDHLayout parent = (VDHLayout) releasedChild.getParent();
                int finalLeft = parent.getRight() - releasedChild.getWidth();
                int finalTop = parent.getTop();
//                mDragger.settleCapturedViewAt(finalLeft, finalTop);
//                mDragger.flingCapturedView(0, 0, finalLeft, finalTop);
                mDragger.smoothSlideViewTo(releasedChild, finalLeft, finalTop);
                invalidate();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this); // 滚动还未停止继续刷新
        }
    }
}
