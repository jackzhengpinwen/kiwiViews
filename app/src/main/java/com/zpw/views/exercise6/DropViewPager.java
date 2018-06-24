package com.zpw.views.exercise6;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zpw on 2018/6/24.
 */

public class DropViewPager extends ViewPager implements Touchable {
    private boolean touchable;

    public DropViewPager(Context context) {
        super(context);
    }

    public DropViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchable) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (touchable) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }
}
