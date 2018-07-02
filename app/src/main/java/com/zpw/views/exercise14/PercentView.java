package com.zpw.views.exercise14;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by zpw on 2018/7/2.
 */

public class PercentView extends View {

    private int mWidth;
    private int mHeight;

    private Paint mOutterCirclePaint;//外圈圆
    private int mOutterCircleX;
    private int mOutterCircleY;
    private int mOutterCircleRadius;

    private Paint mPercentTextPaint;//百分比
    private float mPercent = 0.75f;

    private Paint mInnerCirclePaint;//内圈圆
    private int mInnerCircleRadius;

    private ValueAnimator mRunAnimator;
    private int mOffsetAngle;

    private String mTodayState = "今日完成状态";
    private String mWalk = "步行";
    private String mCost = "消耗";
    private String mWalkUnit = "步";
    private String mCostUnit = "卡路里";

    private Paint mTodayStateTextPaint;

    public PercentView(Context context) {
        this(context, null);
    }

    public PercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        init();
    }

    private void init() {
        mOutterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutterCirclePaint.setColor(Color.rgb(210, 210, 210));
        mOutterCirclePaint.setStrokeWidth(dp2px(2));
        mOutterCirclePaint.setStyle(Paint.Style.STROKE);

        mPercentTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPercentTextPaint.setColor(Color.rgb(255, 115, 0));
        mPercentTextPaint.setTextSize(sp2px(24));
        mPercentTextPaint.setTextAlign(Paint.Align.CENTER);

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(Color.rgb(255, 115, 0));
        mInnerCirclePaint.setStrokeWidth(dp2px(12));
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mTodayStateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayStateTextPaint.setColor(Color.rgb(255, 115, 0));
        mTodayStateTextPaint.setTextSize(sp2px(18));
        mTodayStateTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mOutterCircleRadius = dp2px(106);
        mOutterCircleX = mWidth/2;
        mOutterCircleY = dp2px(6) + mOutterCircleRadius;

        mInnerCircleRadius = dp2px(78);

        startAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        drawCirclr(canvas);//绘制外圈
        drawPercentText(canvas);//绘制百分比数字
        drawPercentArc(canvas);//绘制百分比圆弧
        drawTodayState(canvas);//绘制信息
        invalidate();
    }

    private void drawTodayState(Canvas canvas) {
        Rect rect = new Rect();
        mTodayStateTextPaint.getTextBounds(mTodayState, 0, mTodayState.length(), rect);
        canvas.drawText(mTodayState, mOutterCircleX, sp2px(225) + rect.height(), mTodayStateTextPaint);
    }

    private void drawPercentArc(Canvas canvas) {
        canvas.drawArc(mOutterCircleX - mInnerCircleRadius
                , mOutterCircleY - mInnerCircleRadius
                , mOutterCircleX + mInnerCircleRadius
                , mOutterCircleY + mInnerCircleRadius
                , 90, mOffsetAngle, false, mInnerCirclePaint);
    }

    private void drawPercentText(Canvas canvas) {
        Rect rect = new Rect();
        String percentText = String.valueOf(mPercent * 100) + "%";
        mPercentTextPaint.getTextBounds(percentText, 0, percentText.length(), rect);
        canvas.drawText(percentText, mOutterCircleX, mOutterCircleY + rect.height(), mPercentTextPaint);
    }

    private void drawCirclr(Canvas canvas) {
        canvas.drawCircle(mOutterCircleX, mOutterCircleY, mOutterCircleRadius, mOutterCirclePaint);
    }

    private void startAnim() {
        if (mRunAnimator != null) {
            mRunAnimator.cancel();
        }
        mRunAnimator = ValueAnimator.ofInt(0, (int) (mPercent * 360));
        mRunAnimator.setDuration(1000);
        mRunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetAngle = ((int)animation.getAnimatedValue());
                Log.d(null, "onAnimationUpdate: " + mOffsetAngle);
                postInvalidateOnAnimation();
            }
        });
        mRunAnimator.start();
    }

    //=======================================================
    private int sp2px(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()));
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}
