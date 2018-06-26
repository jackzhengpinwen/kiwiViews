package com.zpw.views.exercise9;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zpw on 2018/6/26.
 */

public class ViewGroup1 extends ViewGroup {
    private final String TAG = "ViewGroup1";
    
    public ViewGroup1(Context context) {
        super(context);
    }

    public ViewGroup1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {//安置所有子 View 的位置
            View child = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            child.layout(l + layoutParams.leftMargin, t + layoutParams.topMargin, r - layoutParams.rightMargin, b - layoutParams.bottomMargin);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: action=" + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: ");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: action=" + event.getAction());
        return super.onTouchEvent(event);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
