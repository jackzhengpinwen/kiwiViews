package com.zpw.views.exercise11;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

/**
 * Created by zpw on 2018/6/29.
 */

public class PhotoView extends AppCompatImageView implements View.OnLayoutChangeListener, View.OnTouchListener {
    private final Matrix mBaseMatrix = new Matrix();//drawable对应的矩阵
    private ScaleType mScaleType = ScaleType.FIT_CENTER;//drawable对应的缩放类型
    private final Matrix mSuppMatrix = new Matrix();// 用于计算矩阵
    private final Matrix mDrawMatrix = new Matrix();//实际绘制矩阵
    private final RectF mDisplayRect = new RectF();//drawable现实的矩形
    private FlingRunnable mCurrentFlingRunnable;//实现fling操作的Runnable封装
    private final float[] mMatrixValues = new float[9];//返回操作的矩阵的数组
    private CustomGestureDetector mScaleDragDetector;//缩放识别器
    private GestureDetector mGestureDetector;//手势识别器
    private boolean mBlockParentIntercept = false;//阻塞父控件拦截
    private boolean mAllowParentInterceptOnEdge = true;
    private int mZoomDuration = DEFAULT_ZOOM_DURATION;//缩放动画的时间
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();//缩放动画使用的加速器
    private float mOverScaleCoefficient = 0.5f;//缩放系数

    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;

    private final static float DEFAULT_MAX_SCALE = 3.0f;
    private final static float DEFAULT_MID_SCALE = 1.75f;
    private final static float DEFAULT_MIN_SCALE = 1.0f;
    private final static int DEFAULT_ZOOM_DURATION = 200;

    private int mScrollEdge = EDGE_BOTH;
    private static final int EDGE_NONE = -1;
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_BOTH = 2;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        init(context);
    }

    private void init(Context context) {
        setOnTouchListener(this);
        addOnLayoutChangeListener(this);
        setScaleType(ScaleType.MATRIX);//设置ImageView的伸缩方式为矩阵

        mScaleDragDetector = new CustomGestureDetector(getContext(), mOnCustomGestureListener);//手势缩放识别器

        mGestureDetector = new GestureDetector(getContext(), mOnGestureListener);//双击手势识别器
        mGestureDetector.setOnDoubleTapListener(mOnGestureListener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean handled = false;
        if (getDrawable() != null) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);//禁止父视图拦截接下来的事件，默认 DOWN 事件不能被拦截
                    }
                    cancelFling();//每次点击时禁止fling事件
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    if (getScale() < mMinScale) {//缩放系数过小
                        RectF rectF = getDisplayRect();
                        if (rectF != null) {//执行缩放动画将其放大
                            v.postOnAnimation(new AnimatedZoomRunnable(getScale(), mMinScale, rectF.centerX(), rectF.centerY()));
                            handled = true;
                        }
                    } else if (getScale() > mMaxScale) {//缩放系数过大
                        RectF rectF = getDisplayRect();
                        if (rectF != null) {//执行缩放动画将其缩小
                            v.post(new AnimatedZoomRunnable(getScale(), mMaxScale, rectF.centerX(), rectF.centerY()));
                            handled = true;
                        }
                    }
                    break;
                }
            }
            //分发给 ScaleGestureDetector 识别缩放事件
            if (mScaleDragDetector != null) {
                boolean wasScaling = mScaleDragDetector.isScaling();
                boolean wasDragging = mScaleDragDetector.isDragging();

                handled = mScaleDragDetector.onTouchEvent(event);//传递识别缩放事件

                boolean didntScale = !wasScaling && !mScaleDragDetector.isScaling();
                boolean didntDrag = !wasDragging && !mScaleDragDetector.isDragging();

                //双重确认 ScaleGestureDetector 不需要消费事件
                mBlockParentIntercept = didntScale && didntDrag;
            }

            //分发给 GestureDetector 识别双击事件
            if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
                handled = true;
            }
        }
        return handled;
    }

    RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    private float getScale() {
        float currentScale = (float) Math.sqrt(
                (float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) +
                (float) Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y), 2)
        );
        return currentScale;
    }

    void setScale(float scale, float focalX, float focalY, boolean animate) {
        if (scale < mMinScale || scale > mMaxScale) {
            throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
        }

        if (animate) {
            post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //判断布局是否发生变化
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            updateBaseMatrix(getDrawable());//更新ImageView设置的drawable的矩阵
        }
    }

    //更新drawable对应的矩阵
    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        //获取ImageView和设置的drawable的宽高
        float viewWidth = getImageViewWidth();
        float viewHeight = getImageViewHeight();
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        mBaseMatrix.reset();

        //获取将drawable缩放的宽高
        float widthScale = viewWidth / drawableWidth;
        float heightScale = viewHeight / drawableHeight;

        //根据ImageView设置的ScalyType设置drawable的缩放类型
        switch (mScaleType) {
            case CENTER: {
                //将drawable的中心移动到ImageView的中心
                mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2.0f, (viewHeight - drawableHeight) / 2.0f);
                break;
            }
            case CENTER_CROP: {
                //将缩放后的drawable放大至填充满整个ImageView，然后将缩放后的drawable的中心移动到ImageView的中心
                float scale = Math.max(widthScale, heightScale);
                mBaseMatrix.postScale(scale, scale);
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2.0f, (viewHeight - drawableHeight * scale) / 2.0f);
                break;
            }
            case CENTER_INSIDE: {
                //将原本大小的drawable设置给ImageView，如果drawable大于ImageView，则缩小，然后将缩放后的drawable的中心移动到ImageView的中心
                float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
                mBaseMatrix.postScale(scale, scale);
                mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2.0f, (viewHeight - drawableHeight * scale) / 2.0f);
                break;
            }
            default:
                break;
        }

        RectF tempDst = new RectF(0, 0, viewWidth, viewHeight);
        RectF tempSrc = new RectF(0, 0, drawableWidth, drawableHeight);

        switch (mScaleType) {
            case FIT_CENTER: {
                mBaseMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
                break;
            }
            case FIT_START: {
                mBaseMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.START);
                break;
            }
            case FIT_END: {
                mBaseMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.END);
                break;
            }
            case FIT_XY: {
                mBaseMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.FILL);
                break;
            }
        }

        resetMatrix();
    }

    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageMatrix(getDrawMatrix());
        }
    }

    private void resetMatrix() {
        mSuppMatrix.reset();
        setImageMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    //检测当前 Drawable 的矩阵是否在正确的显示范围
    private boolean checkMatrixBounds() {
        RectF rectF = getDisplayRect(getDrawMatrix());
        if (rectF == null) {
            return false;
        }
        float height = rectF.height();
        float width = rectF.width();
        float deltaX = 0;
        float deltaY = 0;

        //处理drawable的高
        int viewHeight = getImageViewHeight();
        if (height <= viewHeight) {//如果drawable的高度小于ImageView的高度
            switch (mScaleType) {
                case FIT_START: {
                    deltaY = -rectF.top;//需要向上偏移
                    break;
                }
                case FIT_END: {
                    deltaY = viewHeight - height - rectF.top;//需要向下偏移
                    break;
                }
                default: {
                    deltaY = (viewHeight - height) / 2 - rectF.top;//居中处理
                    break;
                }
            }
        } else if (rectF.top > 0) {//置顶
            deltaY = -rectF.top;
        } else if (rectF.bottom < viewHeight) {//置底
            deltaY = viewHeight - rectF.bottom;
        }

        //处理drawable的宽
        int viewWidth = getImageViewWidth();
        if (width <= viewWidth) {
            switch (mScaleType) {
                case FIT_START: {
                    deltaX = -rectF.left;//向左偏移
                    break;
                }
                case FIT_END: {
                    deltaY = viewWidth - width - rectF.left;//向右偏移
                    break;
                }
                default: {
                    deltaX = (viewWidth - width) / 2 - rectF.left;//居中处理
                    break;
                }
            }
            mScrollEdge = EDGE_BOTH;
        } else if (rectF.left > 0) {
            mScrollEdge = EDGE_LEFT;
            deltaX = -rectF.left;
        } else if (rectF.right < viewWidth) {
            mScrollEdge = EDGE_RIGHT;
            deltaX = viewWidth - rectF.right;
        } else {
            mScrollEdge = EDGE_NONE;
        }

        //将需要平移的x,y保存在矩阵中
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private RectF getDisplayRect(Matrix matrix) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            mDisplayRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    private int getImageViewHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int getImageViewWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private final CustomGestureDetector.OnGestureListener mOnCustomGestureListener = new CustomGestureDetector.OnGestureListener() {

        @Override
        public void onDrag(float dx, float dy) {
            if (mScaleDragDetector.isScaling()) {
                return;
            }

            mSuppMatrix.postTranslate(dx, dy);
            checkAndDisplayMatrix();

            //是否让父视图拦截事件
            ViewParent parent = getParent();
            if (parent == null) {
                return;
            }
            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept) {
                if (mScrollEdge == EDGE_BOTH
                        || (mScrollEdge == EDGE_LEFT && dx >= 1f)
                        || (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }

        @Override
        public void onFling(float startX, float startY, float velocityX, float velocityY) {
            mCurrentFlingRunnable = new FlingRunnable(getContext());
            mCurrentFlingRunnable.fling(getImageViewWidth(), getImageViewHeight()
                    , (int) velocityX, (int) velocityY);
            PhotoView.this.post(mCurrentFlingRunnable);
        }

        @Override
        public void onScale(float scaleFactor, float focusX, float focusY) {
            if ((getScale() < (mMaxScale + mOverScaleCoefficient) || scaleFactor < 1f)//当前缩放系数小于最大缩放值，或者当前想要缩小
                    && (getScale() > (mMinScale - mOverScaleCoefficient) || scaleFactor > 1f)) {//当前缩放系数大于最小缩放值，或者当前想要放大
                mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                checkAndDisplayMatrix();
            }
        }
    };

    private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float scale = getScale();
            float x = e.getX();
            float y = e.getY();

            if (scale < mMidScale) {
                setScale(mMidScale, x, y, true);
            } else if (scale < mMaxScale) {
                setScale(mMaxScale, x, y, true);
            } else {
                setScale(mMinScale, x, y, true);
            }
            return true;
        }
    };

    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                             final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {
            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            mOnCustomGestureListener.onScale(deltaScale, mFocalX, mFocalY);//进行缩放的矩阵设置

            if (t < 1f) {
                PhotoView.this.postOnAnimation(this);//动画还没结束，继续执行
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {
        private OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = new OverScroller(context);
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {//判断滑动是否结束，结束了直接返回
                return;
            }
            if (mScroller.computeScrollOffset()) {//持续计算滑动过程，没结束返回true
                int newX = mScroller.getCurrX();
                int newY = mScroller.getCurrY();

                //将需要滑动的操作设置给用于计算的矩阵
                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                checkAndDisplayMatrix();

                mCurrentX = newX;
                mCurrentY = newY;
                PhotoView.this.postOnAnimation(this);
            }
        }

        void fling(int viewWidth, int viewHeight, int velocityX,
                   int velocityY) {
            final RectF rectF = getDisplayRect();
            if (rectF == null) {
                return;
            }

            final int startX = Math.round(-rectF.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rectF.width()) {
                minX = 0;
                maxX = Math.round(rectF.width() - viewWidth);
            } else {
                //视图的宽更大，不可拖动
                minX = maxX = startX;
            }

            final int startY = Math.round(-rectF.top);
            if (viewHeight < rectF.height()) {
                minY = 0;
                maxY = Math.round(rectF.height() - viewHeight);
            } else {
                //视图的高更大，不可拖动
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            }
        }

        void cancelFling() {
            mScroller.forceFinished(true);
        }
    }
}
