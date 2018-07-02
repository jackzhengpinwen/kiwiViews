package com.zpw.views.exercise13;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by zpw on 2018/7/2.
 */

public class HealthStateView extends View {

    private String mText = "综合得分";
    private int mPoint = 83;
    private String[] mIndexText = new String[] {"高风险", "低风险", "亚健康", "健康"};
    private int[] mIndexColor = new int[] {Color.rgb(255, 80, 101), Color.rgb(255, 115, 0), Color.rgb(97, 149, 255), Color.rgb(119, 233, 133)};

    private int mWidth;
    private int mHeight;
    private Paint mTextPaint;//绘制文字
    private Paint mPointPaint;//绘制分数
    private Paint mIndexPaint;//绘制柱状体
    private Paint mIndexTextPaint;//绘制状态

    private int mIndexWidth = 30;
    private int mIndexHeight = 10;
    private int mIndexHeightMax = 50;
    private int mIndexInternal = 22;

    private ValueAnimator mRunAnimator;
    private int currentState = 1;
    private int mOffsetTop = 0;

    public HealthStateView(Context context) {
        this(context, null);
    }

    public HealthStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        init();
    }

    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(sp2px(18));
        mTextPaint.setColor(Color.rgb(0, 0, 0));

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setTextSize(sp2px(24));
        mPointPaint.setColor(Color.rgb(255, 115, 0));

        mIndexPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndexPaint.setStrokeWidth(sp2px(30));

        mIndexTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndexTextPaint.setTextSize(sp2px(10));
        mIndexTextPaint.setTextAlign(Paint.Align.CENTER);
        mIndexTextPaint.setColor(Color.rgb(74, 74, 74));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        startAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        drawText(canvas);//绘制文字
        drawPoint(canvas);//绘制分数
        drawState(canvas);//绘制状态
        invalidate();
    }

    private void drawState(Canvas canvas) {
        canvas.translate(sp2px(94), sp2px(129));
        for (int i = 0; i < mIndexText.length; i++) {
            mIndexPaint.setColor(mIndexColor[i]);
            int top = 0;
            if (currentState == i) {
//                top -= dp2px(mIndexHeightMax);
                canvas.drawRoundRect(i * sp2px(mIndexWidth + mIndexInternal), -mOffsetTop, i * sp2px(mIndexWidth + mIndexInternal) + dp2px(30), dp2px(10), dp2px(5), dp2px(5), mIndexPaint);
            } else {
                canvas.drawRoundRect(i * sp2px(mIndexWidth + mIndexInternal), 0, i * sp2px(mIndexWidth + mIndexInternal) + dp2px(30), dp2px(10), dp2px(5), dp2px(5), mIndexPaint);
            }

            canvas.drawText(mIndexText[i], i * sp2px(mIndexWidth + mIndexInternal) + dp2px(30/2), sp2px(9 + 14), mIndexTextPaint);
        }
    }

    private void drawPoint(Canvas canvas) {
        Rect rect = new Rect();
        mPointPaint.getTextBounds(String.valueOf(mPoint), 0, String.valueOf(mPoint).length(), rect);
        canvas.drawText(String.valueOf(mPoint), mWidth/2, rect.height() + dp2px(49), mPointPaint);
    }

    private void drawText(Canvas canvas) {
        Rect rect = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
        canvas.drawText(mText, mWidth/2, rect.height() + dp2px(12), mTextPaint);
    }

    private void startAnim() {
        if (mRunAnimator != null) {
            mRunAnimator.cancel();
        }
        mRunAnimator = ValueAnimator.ofInt(0, mIndexHeightMax);
        mRunAnimator.setDuration(1000);
        mRunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetTop = sp2px((int) animation.getAnimatedValue());
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
