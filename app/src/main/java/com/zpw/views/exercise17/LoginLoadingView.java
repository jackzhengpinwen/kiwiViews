package com.zpw.views.exercise17;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.zpw.views.R;

/**
 * Created by zpw on 2018/7/5.
 */

public class LoginLoadingView extends View {

    public static final int STATUS_LOGIN = 0;//正常状态
    public static final int STATUS_LOGGING = 1;//正在登录中
    public static final int STATUS_LOGIN_SUCCESS = 2;//登录成功

    private int mDuration;//动画时长
    private Paint mPaint;
    private int mWidth, mHeight;//View 的宽高
    private int mStatus = STATUS_LOGIN;//动画状态
    private String mSuccessText = "SUCCESS";//成功Text的文案
    private String mLoginText = "SIGN UP";//登录Text的文案
    private float mLineWidth;//下方线条长度
    private float mSuccessTextX;//成功Text的x坐标
    private int mLoginTextAlpha;//登录Text的alpha值


    public LoginLoadingView(Context context) {
        this(context, null);
    }

    public LoginLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDuration = getResources().getInteger(R.integer.duration);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(sp2px(18));
        mPaint.setStrokeWidth(dp2px(3));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mStatus) {
            case STATUS_LOGIN:{
                canvas.drawText(mLoginText, (mWidth-getTextWidth(mLoginText))/2, (mHeight+getTextHeight(mLoginText))/2, mPaint);
                break;
            }
            case STATUS_LOGGING:{
                canvas.drawText(mLoginText, (mWidth-getTextWidth(mLoginText))/2, (mHeight+getTextHeight(mLoginText))/2, mPaint);
                canvas.drawLine((mWidth-getTextWidth(mLoginText))/2, mHeight, (mWidth-getTextWidth(mLoginText))/2+mLineWidth, mHeight, mPaint);
                break;
            }
            case STATUS_LOGIN_SUCCESS:{
                mPaint.setAlpha(mLoginTextAlpha);
                canvas.drawText(mLoginText, mSuccessTextX+getTextWidth(mSuccessText)+dp2px(10), (mHeight+getTextHeight(mLoginText))/2, mPaint);

                mPaint.setAlpha(255-mLoginTextAlpha);
                canvas.drawText(mSuccessText, mSuccessTextX, (mHeight+getTextHeight(mSuccessText))/2, mPaint);

                mPaint.setAlpha(255);
                canvas.drawLine((mWidth-getTextWidth(mSuccessText))/2, mHeight, (mWidth+getTextWidth(mSuccessText))/2, mHeight, mPaint);
                break;
            }
        }
    }

    private void startLoggingAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, getTextWidth(mLoginText));
        animator.setDuration(1000);
        animator.setRepeatCount(2);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLineWidth = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    private void startLoginSuccessAnim() {
        ValueAnimator textXAnim = ValueAnimator.ofFloat(0, (mWidth-getTextWidth(mSuccessText))/2);
        textXAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSuccessTextX = (float) animation.getAnimatedValue();
            }
        });

        ValueAnimator alphaAnim = ValueAnimator.ofInt(255, 0);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLoginTextAlpha = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(mDuration);
        set.playTogether(textXAnim, alphaAnim);
        set.setInterpolator(new LinearInterpolator());
        set.start();
    }

    private float getTextHeight(String text) {
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private float getTextWidth(String text) {
        return mPaint.measureText(text);
    }

    public void setStatus(int status) {
        mStatus = status;
        switch (status) {
            case STATUS_LOGIN:
                break;
            case STATUS_LOGGING:
                startLoggingAnim();
                break;
            case STATUS_LOGIN_SUCCESS:
                startLoginSuccessAnim();
                break;
        }
    }

    //=======================================================
    private int sp2px(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()));
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

}
