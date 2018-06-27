package com.zpw.views.exercise10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

/**
 * Created by zpw on 2018/6/27.
 */

public class ChartView extends View {
    private final long MAXIMUM_FLING_DURATION = 600l;

    private Paint mBgPaint;
    private float mBgPaintWidth = 2.0f;
    private int mBgColor = Color.rgb(210, 210, 210);
    private float mBgInternalSize = dp2px(2);

    private Paint mHCoordinatePaint;
    private int mHCoordinateNum = 8;
    private int mHCoordinateNumColor = Color.rgb(155, 155, 155);
    private int mHCoordinateStartNum = 40;
    private int mHCoordinateInternalSize = 20;
    private int mMaxHCoordinateTextWidth = 0;
    private int mMaxHCoordinateTextHeight = 0;

    private float mAbscissaMsgSize = sp2px(15);
    private int mWidth;
    private int mHeight;
    private float mVCoordinate;
    private float mHCoordinate;


    private Paint mVCoordinatePaint;
    private int mVCoordinateNumColor = Color.rgb(155, 155, 155);
    private int mVCoordinateInternalNum = 5;

    private List<HealthData> mHealthDatas = new ArrayList<>();
    private Paint mHealthPaint;
    private int mHealthDataBarWidth = (int) dp2px(5);

    private int mHealthColor = Color.rgb(255, 115, 0);
    private Rect mContentRect;

    /**
     * touch
     */
    private boolean mIsBeingDragged;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private OverScroller mScroller;
    private int mActivePointerId = INVALID_POINTER;
    private float mDownX;
    private float mLastX;
    private boolean mIsBeingFling;
    private long mStartFlingTime;
    private long mFlingDuration = MAXIMUM_FLING_DURATION;
    private float mFlingX;

    private int mDistanceX;
    private int mMaximumDistanceX;
    private int mMinimumDistanceX;
    private float mPxToOne;

    private int mUpperIndex = -1;
    private int mLowerIndex = -1;
    private Paint mHealthNumPaint;
    private int mSelected = -1;//选中下标
    private float mSelectedMargin = 10.0f;
    private Paint mSelectedPaint;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mScroller = new OverScroller(context);

        init();
    }

    private void init() {
        //绘制背景的画笔
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStrokeWidth(mBgPaintWidth);
        mBgPaint.setColor(mBgColor);

        //绘制纵坐标数值的画笔
        mHCoordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHCoordinatePaint.setTextSize(mAbscissaMsgSize);
        mHCoordinatePaint.setTextAlign(Paint.Align.CENTER);
        mHCoordinatePaint.setColor(mHCoordinateNumColor);

        //绘制横坐标数值的画笔
        mVCoordinatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mVCoordinatePaint.setTextSize(mAbscissaMsgSize);
        mVCoordinatePaint.setTextAlign(Paint.Align.CENTER);
        mVCoordinatePaint.setColor(mVCoordinateNumColor);

        //绘制健康数据的画笔
        mHealthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHealthPaint.setColor(mHealthColor);
        mHealthPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mHealthPaint.setStrokeWidth(mHealthDataBarWidth);
        mHealthPaint.setStrokeCap(Paint.Cap.ROUND);

        //绘制健康数据最值的画笔
        mHealthNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHealthNumPaint.setColor(mHealthColor);
        mHealthNumPaint.setTextSize(mAbscissaMsgSize);
        mHealthNumPaint.setTextAlign(Paint.Align.CENTER);

        mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedPaint.setColor(mHealthColor);

        Random random = new Random();
        for (int i = 0; i < 31; i++) {
            mHealthDatas.add(new HealthData(100 + random.nextInt(60), 60 + random.nextInt(40), (i + 1) + "日"));
        }
        setHealthDatas(mHealthDatas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        calculateTextSize();
        mVCoordinate = mWidth - mMaxHCoordinateTextWidth;
        mHCoordinate = mHeight - mMaxHCoordinateTextHeight;
        mContentRect = new Rect(0, (int) -dp2px(30), mWidth - mMaxHCoordinateTextWidth, mHeight);
        calculateDistanceY();
        int step = (int) (mHCoordinate / mHCoordinateNum);
        mPxToOne = step / mHCoordinateInternalSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroud(canvas);//绘制背景
        drawVerticleNum(canvas);//绘制纵轴
        canvas.clipRect(mContentRect);
        drawHealthData(canvas);//绘制数据
        drawCoordinateAxes(canvas);//绘制坐标轴
    }

    private void drawCoordinateAxes(Canvas canvas) {
        mBgPaint.setStrokeWidth(mBgInternalSize);
        canvas.drawLine(0, mHCoordinate, mVCoordinate, mHCoordinate, mBgPaint);
        canvas.drawLine(mVCoordinate, 0, mVCoordinate, mHCoordinate, mBgPaint);
        mBgPaint.setStrokeWidth(mBgPaintWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!hasDataSource()) {
            return super.onTouchEvent(event);
        }
        Log.d(null, "onTouchEvent: " + event.getActionMasked());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
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
                mActivePointerId = event.getPointerId(0);
                mDownX = event.getX(0);
                mLastX = mDownX;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                final int actionIndex = event.getActionIndex();
                mActivePointerId = event.getPointerId(actionIndex);
                mDownX = event.getX(actionIndex);
                mLastX = mDownX;
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = event.findPointerIndex(activePointerId);
                final float moveX = event.getX(pointerIndex);
                if (!mIsBeingDragged && Math.abs(mDownX - moveX) > mTouchSlop) {
                    mIsBeingDragged = true;
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (mIsBeingDragged) {
                    mDistanceX += mLastX - moveX;
                    if (mDistanceX > mMaximumDistanceX) {
                        mDistanceX = mMaximumDistanceX;
                    }
                    if (mDistanceX < mMinimumDistanceX) {
                        mDistanceX = mMinimumDistanceX;
                    }
                    postInvalidateOnAnimation();
                }
                mLastX = moveX;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                if (Math.abs(velocityTracker.getXVelocity()) > mMinimumFlingVelocity) {
                    final int xVelocity = (int) velocityTracker.getXVelocity();
                    mFlingDuration = Math.max(
                            MAXIMUM_FLING_DURATION,
                            getSplineFlingDuration(xVelocity)
                    );
                    mScroller.fling(
                            0,
                            0,
                            xVelocity,
                            0,
                            Integer.MIN_VALUE,
                            Integer.MAX_VALUE,
                            0,
                            0
                    );
                    mFlingX = mScroller.getStartX();
                    if (Math.abs(mMaximumDistanceX - mDistanceX) < getWidth()) {
                        mFlingDuration = mFlingDuration / 3;
                    }
                    mIsBeingFling = true;
                    mStartFlingTime = SystemClock.elapsedRealtime();
                    postInvalidateOnAnimation();
                }
                mActivePointerId = INVALID_POINTER;
                mIsBeingDragged = false;
                resetVelocityTracker();
                PointF tup = new PointF(event.getX(), event.getY());
                clickWhere(tup);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsBeingFling = false;
                mActivePointerId = INVALID_POINTER;
                mIsBeingDragged = false;
                resetVelocityTracker();
                break;
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
        return true;
    }

    private void clickWhere(PointF tup) {
        for (int i = 0; i < mHealthDatas.size(); i++) {
            HealthData healthData = mHealthDatas.get(i);
            RectF rect = healthData.getRect();
            if (tup.x > rect.left && tup.x < rect.right && tup.y > rect.top && tup.y < rect.bottom) {
                mSelected = i;
                break;
            }
            mSelected = -1;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            final int currX = mScroller.getCurrX();
            mDistanceX += mFlingX - currX;
            if (mDistanceX > mMaximumDistanceX) {
                mDistanceX = mMaximumDistanceX;
            }
            if (mDistanceX < mMinimumDistanceX) {
                mDistanceX = mMinimumDistanceX;
            }
            mFlingX = currX;
            if ((SystemClock.elapsedRealtime() - mStartFlingTime) >= mFlingDuration || currX == mScroller.getFinalX()) {
                mScroller.abortAnimation();
            }
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

    private void onSecondaryPointerUp(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        if (pointerId == mActivePointerId) {
            actionIndex = actionIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(actionIndex);
            mDownX = event.getX(actionIndex);
            mLastX = mDownX;
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

    private boolean hasDataSource() {
        return mHealthDatas != null && !mHealthDatas.isEmpty();
    }

    private void drawHealthData(Canvas canvas) {
        Log.d(null, "drawHealthData: " + mDistanceX);
        for (int i = 0; i < mHealthDatas.size(); i++) {
            HealthData healthData = mHealthDatas.get(i);
            float lower = healthData.getLower();
            float upper = healthData.getUpper();
            String time = healthData.getTime();
            float startX = mVCoordinate / mVCoordinateInternalNum * (i + 1) - mDistanceX;
            float startY = mHCoordinate - (lower - mHCoordinateStartNum) * mPxToOne;
            float endY = startY - (upper - lower) * mPxToOne;
            //绘制竖线
            canvas.drawLine(startX, startY, startX, endY, mHealthPaint);
            //设置点击范围
            RectF rect = new RectF(startX - dp2px(mSelectedMargin), endY - dp2px(mSelectedMargin), startX + dp2px(mSelectedMargin), startY + dp2px(mSelectedMargin));
            healthData.setRect(rect);
            //绘制最大值和最小值
            if (i == mLowerIndex) {
                canvas.drawText("最低" + (int)lower, startX, startY + mMaxHCoordinateTextHeight, mHealthNumPaint);
            }
            if (i == mUpperIndex) {
                canvas.drawText("最高" + (int)upper, startX, endY - mMaxHCoordinateTextHeight / 2, mHealthNumPaint);
            }
            //绘制底部文字
            canvas.drawText(time, startX, mHCoordinate + mMaxHCoordinateTextHeight - mBgInternalSize, mVCoordinatePaint);
        }
        if (mSelected != -1) {
            float startX = mVCoordinate / mVCoordinateInternalNum * (mSelected + 1) - mDistanceX;
            //绘制竖线
            canvas.drawLine(startX, 0, startX, mHCoordinate, mSelectedPaint);
            //绘制详细数据
//            canvas.drawRoundRect(startX - mVCoordinate/4, 0, startX + mVCoordinate/4, dp2px(30), dp2px(5), dp2px(5), mSelectedPaint);
        }
    }

    private void drawVerticleNum(Canvas canvas) {
        int step = (int) (mHCoordinate / mHCoordinateNum);
        for (int i = 0; i < mHCoordinateNum; i++) {
            //画文字
            canvas.drawText(String.valueOf(mHCoordinateStartNum + i * mHCoordinateInternalSize), mVCoordinate + mMaxHCoordinateTextWidth / 2, step * (mHCoordinateNum - i), mHCoordinatePaint);
        }
    }

    private void drawBackgroud(Canvas canvas) {
        int step = (int) mBgInternalSize;
        int width = (int) mVCoordinate;
        int height = (int) mHCoordinate;

        int widthLineNums = width / step;//绘制的背景纵线
        for (int i = 0; i <= widthLineNums; i++) {
            canvas.drawLine(step * i, 0, step * i, height, mBgPaint);
        }
        int heightLineNums = height / step;
        for (int i = 0; i <= heightLineNums; i++) {//绘制的背景横线
            canvas.drawLine(0, step * i, width, step * i, mBgPaint);
        }
    }

    private void calculateTextSize() {
        for (int i = 0; i < mHCoordinateNum; i++) {
            String num = String.valueOf(mHCoordinateStartNum + i * mHCoordinateInternalSize);
            mMaxHCoordinateTextWidth = (int) Math.max(mHCoordinatePaint.measureText(num), mMaxHCoordinateTextWidth);
        }
        final Paint.FontMetrics fontMetrics = mVCoordinatePaint.getFontMetrics();
        mMaxHCoordinateTextHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    private void calculateDistanceY() {
        mMaximumDistanceX = 0;
        mMinimumDistanceX = 0;
        if (!hasDataSource()) {
            return;
        }
        mMaximumDistanceX = (int) ((mVCoordinate / mVCoordinateInternalNum) * (mHealthDatas.size() - 1));
    }

    private static class HealthData {
        private float upper;
        private float lower;
        private String time;
        private RectF mRect = new RectF();

        public HealthData(float upper, float lower, String time) {
            this.upper = upper;
            this.lower = lower;
            this.time = time;
        }

        public float getUpper() {
            return upper;
        }

        public void setUpper(float upper) {
            this.upper = upper;
        }

        public float getLower() {
            return lower;
        }

        public void setLower(float lower) {
            this.lower = lower;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public RectF getRect() {
            return mRect;
        }

        public void setRect(RectF rect) {
            mRect = rect;
        }
    }

    public void setHealthDatas(List<HealthData> healthDatas) {
        mHealthDatas = healthDatas;
        float minLower = healthDatas.get(0).getLower();
        float maxUpper = healthDatas.get(0).getUpper();
        mLowerIndex = 0;
        mUpperIndex = 0;
        for (int i = 1; i < mHealthDatas.size(); i++) {
            HealthData healthData = mHealthDatas.get(i);
            if (healthData.getLower() < minLower) {
                minLower = healthData.getLower();
                mLowerIndex = i;
            }
            if (healthData.getUpper() > maxUpper) {
                maxUpper = healthData.getUpper();
                mUpperIndex = i;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Help Method
    ///////////////////////////////////////////////////////////////////////////

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
