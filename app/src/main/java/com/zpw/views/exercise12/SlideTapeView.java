package com.zpw.views.exercise12;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import java.math.BigDecimal;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

/**
 * Created by zpw on 2018/7/1.
 */

public class SlideTapeView extends AppCompatImageView {
    private static final String TAG = "SlideTapeView";

    private final Paint mTextPaint;//绘制刻度数字
    private final Paint mShortPaint;//绘制短刻度
    private final Paint mLongPaint;//绘制长刻度
    private final Paint mIndicatorPaint;//绘制指示
    private final Rect mContentRect = new Rect();
    private final Rect mTextRect = new Rect();

    //长指针宽高
    private int mLongPointWidth, mLongPointHeight;
    //长指针之间的间隔
    private int mLongPointInterval;
    //长指针的数量
    private int mLongPointCount;

    //短指针宽高
    private int mShortPointWidth, mShortPointHeight;
    //短指针之间的间隔
    private float mShortPointInterval;
    //短指针的数量
    private int mShortPointCount;

    //指示器宽高
    private int mIndicatorWidth, mIndicatorHeight;

    private final int mMinimumHeight;

    //起始值，结束值
    private int mStartValue, mEndValue;
    //长指针单位
    private int mLongUnix;
    //短指针单位
    private BigDecimal mShortUnix;

    //左边的偏移量，向左为正
    private int mOffsetLeft;
    private int mMinOffsetLeft, mMaxOffsetLeft;

    //触摸事件
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    private float mDownMotionX;
    private float mLastMotionX;
    private int mTouchSlop;
    private EdgeEffect mEdgeGlowLeft;
    private EdgeEffect mEdgeGlowRight;

    //fling
    private boolean mStartFling;
    private ValueAnimator mRunAnimator;
    private VelocityTracker mVelocityTracker;
    private final int mMaximumFlingVelocity;
    private final int mMinimumFlingVelocity;
    private final OverScroller mScroller;
    private int mLastFlingX;

    private static final int INDICATOR_COLOR = Color.rgb(77, 166, 104);
    private static final int BACKGROUP_COLOR = Color.rgb(244, 248, 243);
    private static final int POINT_COLOR = Color.rgb(210, 215, 209);
    private static final int TEXT_COLOR = Color.BLACK;

    private static final int MAXIMUM_SHORT_POINT_COUNT = 10;//短指针最大数量

    {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setTextSize(sp2px(16f));

        mShortPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShortPaint.setColor(POINT_COLOR);

        mLongPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLongPaint.setColor(POINT_COLOR);

        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(INDICATOR_COLOR);
        mIndicatorPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public SlideTapeView(Context context) {
        this(context, null);
    }

    public SlideTapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mMinimumHeight = dp2px(95);

        mIndicatorWidth = dp2px(5);
        mIndicatorHeight = dp2px(50);

        mLongPointWidth = dp2px(2);
        mLongPointHeight = dp2px(40);

        mShortPointWidth = dp2px(1);
        mShortPointHeight = dp2px(20);

        mLongPointInterval = dp2px(100);

        mLongUnix = 1;

        final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();

        mScroller = new OverScroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wantWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int wantHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        //将起始和结束数作为最大字符考虑
        int maxTextHeight;
        mTextPaint.getTextBounds(String.valueOf(mStartValue), 0, String.valueOf(mStartValue).length(), mTextRect);
        maxTextHeight = mTextRect.height();
        mTextPaint.getTextBounds(String.valueOf(mEndValue), 0, String.valueOf(mEndValue).length(), mTextRect);
        maxTextHeight = Math.max(maxTextHeight, mTextRect.height()) + dp2px(5);
        Log.d(TAG, "onMesure: maxTextHeight:" + maxTextHeight);

        int drawMaxHeight = Math.max(mIndicatorHeight, Math.max(mLongPointHeight, mShortPointHeight)) + maxTextHeight;
        wantHeight += drawMaxHeight;
        wantHeight = Math.max(wantHeight, mMinimumHeight);

        setMeasuredDimension(resolveSize(wantWidth, widthMeasureSpec), resolveSize(wantHeight, widthMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mContentRect.left = getPaddingLeft();
        mContentRect.top = getPaddingTop();
        mContentRect.right = w - getPaddingRight();
        mContentRect.bottom = h - getPaddingBottom();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //HorzontalScrollView
        if (mEdgeGlowLeft != null && !mEdgeGlowLeft.isFinished()) {
            final int restoreCount = canvas.save();

            canvas.rotate(270);
            //坐标系旋转后，需要将绘制的 EdgeEffect 偏移回来
            canvas.translate(-getHeight(), 0);

            mEdgeGlowLeft.setSize(getHeight(), getWidth());
            if (mEdgeGlowLeft.draw(canvas)) {
                postInvalidateOnAnimation();
            }

            canvas.restoreToCount(restoreCount);
        } else if (mEdgeGlowRight != null && !mEdgeGlowRight.isFinished()) {
            final int restoreCount = canvas.save();
            canvas.rotate(90);
            canvas.translate(0, -getWidth());

            mEdgeGlowRight.setSize(getHeight(), getWidth());
            if (mEdgeGlowRight.draw(canvas)) {
                postInvalidateOnAnimation();
            }
            canvas.restoreToCount(restoreCount);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(BACKGROUP_COLOR);//绘制背景色

        if (correctRangeOfValues()) {
            if (mShortPointCount > 0) {//绘制短指针
                drawShortPaints(canvas);
            }
            drawLongPaints(canvas);//绘制长指针
            drawText(canvas);//绘制文字
            drawIndicator(canvas);//绘制指示器
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!correctRangeOfValues()) {
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:{
                initOrResetVelocityTracker();
                if (mRunAnimator != null) {
                    mRunAnimator.cancel();
                }
                mScroller.computeScrollOffset();
                if (mIsBeingDragged = !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mStartFling = false;
                if (mIsBeingDragged) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                mActivePointerId = event.getPointerId(0);
                mDownMotionX = event.getX(0);
                mLastMotionX = event.getX(0);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = event.getActionIndex();
                mDownMotionX = event.getX(index);
                mLastMotionX = event.getX(index);
                mActivePointerId = event.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = event.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    Log.e(TAG, "onTouchEvent: Invalid pointerId = " + activePointerId);
                    break;
                }

                float distanceX = mLastMotionX - event.getX(pointerIndex);
                Log.d(TAG, "onTouchEvent: distanceX:" + distanceX);
                if (!mIsBeingDragged && Math.abs(mDownMotionX - event.getX(pointerIndex)) > mTouchSlop) {
                    mIsBeingDragged = true;
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (mIsBeingDragged) {
                    mOffsetLeft += distanceX;
                    int range = getOffsetLeftRange();
                    final int overscrollMode = getOverScrollMode();
                    boolean canOverscroll =
                            overscrollMode == View.OVER_SCROLL_ALWAYS ||
                            (overscrollMode == View.OVER_SCROLL_IF_CONTENT_SCROLLS && range > 0);
                    if (mOffsetLeft > mMaxOffsetLeft) {
                        if (canOverscroll) {
                            ensureGlows();
                            mEdgeGlowRight.onPull(distanceX / getWidth(), event.getY(pointerIndex) / getHeight());
                            if (!mEdgeGlowLeft.isFinished()) {
                                mEdgeGlowLeft.onRelease();
                            }
                        }
                        mOffsetLeft = mMaxOffsetLeft;
                    }
                    if (mOffsetLeft < mMinOffsetLeft) {
                        if (canOverscroll){
                            //edge
                            ensureGlows();
                            mEdgeGlowLeft.onPull(distanceX / getWidth(), 1 - event.getY(pointerIndex) / getHeight());
                            if (!mEdgeGlowRight.isFinished()) {
                                mEdgeGlowRight.onRelease();
                            }
                        }
                        mOffsetLeft = mMinOffsetLeft;
                    }
                    postInvalidateOnAnimation();
                }
                mLastMotionX = event.getX(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_UP: {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                if (Math.abs(velocityTracker.getXVelocity()) > mMinimumFlingVelocity) {
                    //fling
                    Log.d(TAG, "onTouchEvent: velocityX:" + velocityTracker.getXVelocity());
                    mScroller.fling(0, 0, (int) (velocityTracker.getXVelocity()), 0, -mMaxOffsetLeft, mMaxOffsetLeft, 0, 0);
                    mStartFling = true;
                    mLastFlingX = mScroller.getStartX();
                    postInvalidateOnAnimation();
                }
            }
            case MotionEvent.ACTION_CANCEL:{
                if (mIsBeingDragged && !mStartFling) {
                    checkOffsetLeft();
                }
                mIsBeingDragged = false;
                recycleVelocityTracker();
                if (mEdgeGlowLeft != null) {
                    mEdgeGlowLeft.onRelease();
                }
                if (mEdgeGlowRight != null) {
                    mEdgeGlowRight.onRelease();
                }
                mActivePointerId = INVALID_POINTER;
                break;
            }
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int newOffsetLeft = mOffsetLeft + (mLastFlingX - mScroller.getCurrX());

            final int range = getOffsetLeftRange();
            final int overscrollMode = getOverScrollMode();
            boolean canOverscroll = overscrollMode == View.OVER_SCROLL_ALWAYS || (overscrollMode == View.OVER_SCROLL_IF_CONTENT_SCROLLS
                    && range > 0);

            if (canOverscroll && mOffsetLeft < mMaxOffsetLeft && newOffsetLeft > mMaxOffsetLeft) {
                ensureGlows();
                mEdgeGlowRight.onAbsorb((int) mScroller.getCurrVelocity());
            } else if (canOverscroll && mOffsetLeft > mMinOffsetLeft && newOffsetLeft < mMinOffsetLeft) {
                ensureGlows();
                mEdgeGlowLeft.onAbsorb((int) mScroller.getCurrVelocity());
            }

            if (newOffsetLeft > mMaxOffsetLeft) {
                newOffsetLeft = mMaxOffsetLeft;
            }
            if (newOffsetLeft < mMinOffsetLeft) {
                newOffsetLeft = mMinOffsetLeft;
            }

            mOffsetLeft = newOffsetLeft;

            postInvalidateOnAnimation();
            mLastFlingX = mScroller.getCurrX();
        } else {
            if (mStartFling) {
                mStartFling = false;
                Log.d(TAG, "computeScroll: checkOffsetLeft");
                checkOffsetLeft();
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        } else {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    private void checkOffsetLeft() {
        if (mShortPointCount == 0) {
            int current = mOffsetLeft / mLongPointInterval;
            int offset = mOffsetLeft % mLongPointInterval;
            Log.d(TAG, "computeScroll: current：" + current + "  offset:" + offset);
            if (offset > (mLongPointInterval / 2f)) {
                current++;
                Log.d(TAG, "computeScroll: current：" + current + "  offset:" + offset);
            }
            offsetAnim(current * mLongPointInterval);
        } else {
            int current = (int) (mOffsetLeft / mShortPointInterval);
            int offset = (int) (mOffsetLeft % mShortPointInterval);
            Log.d(TAG, "computeScroll: current：" + current + "  offset:" + offset);
            if (offset > (mShortPointInterval / 2f)) {
                current++;
                Log.d(TAG, "computeScroll: current：" + current + "  offset:" + offset);
            }
            offsetAnim((int) (current * mShortPointInterval));
        }
    }

    private void offsetAnim(int offsetLeft) {
        if (mRunAnimator != null) {
            mRunAnimator.cancel();
        }
        mRunAnimator = ValueAnimator.ofInt(mOffsetLeft, offsetLeft);
        mRunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetLeft = (int) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });
        mRunAnimator.start();
    }

    private void ensureGlows() {
        if (getOverScrollMode() != View.OVER_SCROLL_NEVER) {
            if (mEdgeGlowLeft == null) {
                mEdgeGlowLeft = new EdgeEffect(getContext());
                mEdgeGlowLeft.setColor(INDICATOR_COLOR);
            }
            if (mEdgeGlowRight == null) {
                mEdgeGlowRight = new EdgeEffect(getContext());
                mEdgeGlowRight.setColor(INDICATOR_COLOR);
            }
        } else {
            mEdgeGlowLeft = null;
            mEdgeGlowRight = null;
        }
    }

    private int getOffsetLeftRange() {
        return Math.max(0, mMaxOffsetLeft);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
            mDownMotionX = ev.getX(newPointerIndex);
            mLastMotionX = ev.getX(newPointerIndex);
        }
    }

    private void drawIndicator(Canvas canvas) {
        mIndicatorPaint.setStrokeWidth(mIndicatorWidth);
        final int halfWdith = (int) (getWidth() / 2f);
        canvas.drawLine(halfWdith, mContentRect.top, halfWdith, mIndicatorHeight, mIndicatorPaint);
    }

    private void drawText(Canvas canvas) {
        int halfWidth = getWidth()/2;
        for (int i = 0; i < mLongPointCount; i++) {
            float startX = halfWidth + i * mLongPointInterval - mOffsetLeft;
            String text = String.valueOf(mStartValue + i * mLongUnix);
            if (TextUtils.isEmpty(text)) {
                continue;
            }
            mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
            canvas.drawText(text, startX - mTextRect.width()/2, mContentRect.bottom-mTextRect.bottom, mTextPaint);
        }
    }

    private void drawLongPaints(Canvas canvas) {
        mLongPaint.setStrokeWidth(mLongPointWidth);
        int halfWidth = getWidth()/2;
        for (int i = 0; i < mLongPointCount; i++) {
            float startX = halfWidth + i * mLongPointInterval - mOffsetLeft;
            if (!mContentRect.contains((int) startX, 0)) {
                continue;
            }
            canvas.drawLine(startX, mContentRect.top, startX, mLongPointHeight, mLongPaint);
        }
    }

    private void drawShortPaints(Canvas canvas) {
        mShortPaint.setStrokeWidth(mShortPointWidth);
        int halfWidth = getWidth()/2;
        for (int i = 0; i < (mLongPointCount - 1); i++) {
            float longStartX = halfWidth + i * mLongPointInterval - mOffsetLeft;
            for (int j = 1; j < mShortPointCount; j++) {
                float startX = longStartX + j * mShortPointInterval;
                if (!mContentRect.contains((int) startX, 0)) {
                    continue;
                }
                canvas.drawLine(startX, mContentRect.top, startX, mShortPointHeight, mShortPaint);
            }
        }
    }

    private boolean correctRangeOfValues() {
        return !((mStartValue == 0) && (mEndValue == 0));
    }

    /**
     * 设置短指针的个数
     * 简单处理
     *
     * @param shortPointCount 短指针个数
     */
    public void setShortPointCount(int shortPointCount) {
        if (shortPointCount > MAXIMUM_SHORT_POINT_COUNT) {
            shortPointCount = MAXIMUM_SHORT_POINT_COUNT;
        }
        if (mShortPointCount == shortPointCount) {
            return;
        }
        mShortPointCount = shortPointCount;
        calculate();
        postInvalidate();
    }

    public void setLongUnix(int longUnix) {
        if (longUnix == 0) {
            throw new IllegalArgumentException("longUnix 不能为 0");
        }
        if (mLongUnix == longUnix) {
            return;
        }
        if (longUnix > mEndValue) {
            longUnix = mEndValue;
        }
        mLongUnix = longUnix;
        if (correctRangeOfValues()) {
            calculate();
            postInvalidateOnAnimation();
        }
    }

    /**
     * 设置起始值和结束值
     * 结束值必须大于起始值
     *
     * @param startValue 起始值
     * @param endValue   结束值
     */
    public void setValue(int startValue, int endValue) {
        if (!(endValue > startValue)) {
            throw new IllegalArgumentException("endValue 必须大于 startValue");
        }
        if (mStartValue == startValue && mEndValue == endValue) {
            return;
        }
        mStartValue = startValue;
        mEndValue = endValue;
        if (mLongUnix > mEndValue) {
            mLongUnix = mEndValue;
        }
        calculate();
        postInvalidateOnAnimation();
    }

    private void calculate() {
        mLongPointCount = (mEndValue - mStartValue + 1) / mLongUnix;
        mMinOffsetLeft = 0;
        mMaxOffsetLeft = (mLongPointCount - 1) * mLongPointInterval;
        if (mShortPointCount > 0) {
            mShortPointInterval = (float) mLongPointInterval / mShortPointCount;
            mShortUnix = new BigDecimal(mLongUnix).divide(new BigDecimal(mShortPointCount), 2, BigDecimal.ROUND_DOWN);
            Log.d(TAG, "calculate: mShortUnix:" + mShortUnix);
        }
        Log.d(TAG, "calculate: mLongPointInterval:" + mLongPointInterval + "    mShortPointInterval:" + mShortPointInterval);
    }

    //=======================================================
    private int sp2px(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()));
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }


}
