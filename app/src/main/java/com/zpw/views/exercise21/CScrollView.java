package com.zpw.views.exercise21;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by zpw on 2018/7/9.
 */

public class CScrollView extends LinearLayout {

    private Scroller mScroller;
    private BallView mBallView;
    private int realHeight;

    public CScrollView(Context context) {
        this(context, null);
    }

    public CScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context, new BounceInterpolator());
    }

    public void smoothScrollTo() {
        mBallView = (BallView) getChildAt(0);
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        realHeight = getHeight() - 2*mBallView.getRadius();
        mScroller.startScroll(scrollX, 0, 0, -realHeight, 1000);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
