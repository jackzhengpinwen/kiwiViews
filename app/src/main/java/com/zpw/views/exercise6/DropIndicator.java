package com.zpw.views.exercise6;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zpw on 2018/6/24.
 */

public class DropIndicator extends ViewGroup {
    private String TAG = "DropIndicator";

    private int circleColor;
    private int clickColor;
    private int duration;
    private Paint mClickPaint;//绘制下标的实心圆
    private Paint mPaintCircle;//绘制下标的圆框
    private Paint mPaint;
    private Path mPath = new Path();//用贝塞尔曲线绘制圆时用到
    private float ratio = 50;//比例
    private final double c = 0.552284749831;//用贝塞尔曲线绘制圆的魔数
    private final int r = 1;//
    private int mWidth, mHeight;//父控件宽，高
    private float startX, startY;//第一个子控件的宽，高
    private float totalOff;//最后一个子控件与第一个子控件的距离
    private float distance;
    private int currOff;
    private float mCurrentTime;
    private int tabNum = 0;//子View个数
    //确定绘制圆的四个数据点
    private XPoint p2, p4;
    private YPoint p1, p3;
    private float mc;//绘制圆时，控制点到数据点的距离
    private float radius;//子控件的半径
    private int[] roundColors = new int[4];
    private float div;//每个下标的间距
    private float scale = 0.8f;

    private int currentPos = 0;//当前位置
    private int toPos = -1;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private int viewPagerState;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    private boolean animating;

    float[][] f;
    float[] result;//保存贝塞尔圆滑动过程中的颜色r,g,b
    int[] colors = new int[4];

    float lastCurrentTime = 0;

    private double g2 = 1.41421;//2的段数平方根

    //每个圆开始，结束的颜色
    private int startColor, endColor;
    boolean direction = true;

    ValueAnimator animator;

    public DropIndicator(Context context) {
        this(context, null);
    }

    public DropIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        roundColors[0] = Color.parseColor("#B04285F4");
        roundColors[1] = Color.parseColor("#B0EA4335");
        roundColors[2] = Color.parseColor("#B0FBBC05");
        roundColors[3] = Color.parseColor("#B034A853");

        clickColor = Color.WHITE;
        circleColor = Color.GRAY;
        radius = 50;
        duration = 1000;
        scale = 0.8f;

        f = new float[roundColors.length][3];
        result = new float[3];
        ratio = radius;
        mc = (float) (c * ratio);

        mPaintCircle = new Paint();
        mPaintCircle.setColor(circleColor);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStrokeWidth(3);

        mClickPaint = new Paint();
        mClickPaint.setColor(clickColor);
        mClickPaint.setStyle(Paint.Style.STROKE);
        mClickPaint.setAntiAlias(true);
        mClickPaint.setStrokeWidth(radius / 2);

        mPaint = new Paint();
        startColor = roundColors[0];
        mPaint.setColor(startColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        tabNum = getChildCount();
        for (int i = 0; i < tabNum; i++) {//依次测量每个子View
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取父控件宽，高
        mWidth = w;
        mHeight = h;
        //获取每个下标的间距
        div = (mWidth - 2 * tabNum * radius) / (tabNum + 1);
        //第一个子控件的横坐标，纵坐标
        startX = div + radius;
        startY = mHeight / 2;
        //最后一个子控件与第一个子控件的距离
        totalOff = (tabNum - 1) * (2 * radius + div) - radius;

        if (currentPos == 0) {//当前位置为0
            //圆的半径
            radius = r * ratio;
            //数据点到控制点的距离
            mc = (float) (c * ratio);
            //初始化绘制圆的四个数据点
            p1 = new YPoint(0, radius, mc);
            p3 = new YPoint(0, -radius, mc);
            p2 = new XPoint(radius, 0, mc);
            p4 = new XPoint(-radius, 0, mc);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < tabNum; i++) {//安置所有子 View 的位置
            View child = getChildAt(i);
            float c2cDistance = div + 2 * radius;//两个圆心的距离
            int left = (int) (div + (1 - scale * 1 / g2) * radius + i * c2cDistance);
            int top = (int) (startY - scale * radius / g2);
            int right = (int) (div + (1 + scale * 1 / g2) * radius + i * c2cDistance);
            int bottom = (int) (startY + scale * radius / g2);
            Log.d(TAG, "onLayout: left->" + left + ",top->" + top + ",right->" + right + ",bottom->" + bottom);
            child.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        mPath.reset();
        float c2cDiastance = div + 2 * radius;//愿心之间的距离
        for (int i = 0; i < tabNum; i++) {//切图只有内容，没有边框，这边需要自己绘制
            canvas.drawCircle(div + radius + i * c2cDiastance, startY, radius, mPaintCircle);
        }
        if (mCurrentTime == 0) {//还没开始绘制子控件时
            resetP();//重新绘制圆
            mPaint.setColor(startColor);//绘制每个圆对应的颜色
            canvas.translate(startX, startY);//移动画布到第一个子控件的圆心
            if (toPos > currentPos) {
                p2.setX(radius);
            } else {
                p4.setX(-radius);
            }
        }
        if (mCurrentTime > 0 && mCurrentTime <= 0.2) {//子控件开始运动的前0.2s
            direction = toPos > currentPos ? true : false;
            if (animating)
                canvas.drawCircle(div + radius + (toPos) * c2cDiastance, startY, radius * 1.0f * 5 * mCurrentTime, mClickPaint);
            canvas.translate(startX, startY);
            if (toPos > currentPos) {
                p2.setX(radius + 2 * 5 * mCurrentTime * radius / 2);
            } else {
                p4.setX(-radius - 2 * 5 * mCurrentTime * radius / 2);
            }
        } else if (mCurrentTime > 0.2 && mCurrentTime <= 0.5) {//子控件开始运动的0.2s-0.5s
            canvas.translate(startX + (mCurrentTime - 0.2f) * distance / 0.7f, startY);
            if (toPos > currentPos) {
                p2.setX(2 * radius);
                p1.setX(0.5f * radius * (mCurrentTime - 0.2f) / 0.3f);
                p3.setX(0.5f * radius * (mCurrentTime - 0.2f) / 0.3f);
                p2.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
                p4.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
            } else {
                p4.setX(-2 * radius);
                p1.setX(-0.5f * radius * (mCurrentTime - 0.2f) / 0.3f);
                p3.setX(-0.5f * radius * (mCurrentTime - 0.2f) / 0.3f);
                p2.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
                p4.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
            }
        } else if (mCurrentTime > 0.5 && mCurrentTime <= 0.8) {//子控件开始运动的0.5s-0.8s
            canvas.translate(startX + (mCurrentTime - 0.2f) * distance / 0.7f, startY);
            if (toPos > currentPos) {
                p1.setX(0.5f * radius + 0.5f * radius * (mCurrentTime - 0.5f) / 0.3f);
                p3.setX(0.5f * radius + 0.5f * radius * (mCurrentTime - 0.5f) / 0.3f);
                p2.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
                p4.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
            } else {
                p1.setX(-0.5f * radius - 0.5f * radius * (mCurrentTime - 0.5f) / 0.3f);
                p3.setX(-0.5f * radius - 0.5f * radius * (mCurrentTime - 0.5f) / 0.3f);
                p2.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
                p4.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
            }
        } else if (mCurrentTime > 0.8 && mCurrentTime <= 0.9) {//子控件开始运动的0.8s-0.9s
            p2.setMc(mc);
            p4.setMc(mc);
            canvas.translate(startX + (mCurrentTime - 0.2f) * distance / 0.7f, startY);
            if (toPos > currentPos) {
                p4.setX(-radius + 1.6f * radius * (mCurrentTime - 0.8f) / 0.1f);
            } else {
                p2.setX(radius - 1.6f * radius * (mCurrentTime - 0.8f) / 0.1f);
            }
        } else if (mCurrentTime > 0.9 && mCurrentTime < 1) {//子控件开始运动的0.9s-1.0s
            if (toPos > currentPos) {
                p1.setX(radius);
                p3.setX(radius);
                canvas.translate(startX + distance, startY);
                p4.setX(0.6f * radius - 0.6f * radius * (mCurrentTime - 0.9f) / 0.1f);
            } else {
                p1.setX(-radius);
                p3.setX(-radius);
                canvas.translate(startX + distance, startY);
                p2.setX(-0.6f * radius + 0.6f * radius * (mCurrentTime - 0.9f) / 0.1f);
            }
        }
        if (mCurrentTime == 1) {//移动结束时
            lastCurrentTime = 0;
            mPaint.setColor(endColor);//设置最终颜色
            if (direction) {
                p1.setX(radius);
                p3.setX(radius);
                canvas.translate(startX + distance, startY);
                p4.setX(0);
            } else {
                p1.setX(-radius);
                p3.setX(-radius);
                canvas.translate(startX + distance, startY);
                p2.setX(0);
            }
            currentPos = toPos;
            resetP();
            if (direction)
                canvas.translate(radius, 0);
            else
                canvas.translate(-radius, 0);
        }

        //构造贝塞尔曲线path
        mPath.moveTo(p1.x, p1.y);
        mPath.cubicTo(p1.right.x, p1.right.y, p2.bottom.x, p2.bottom.y, p2.x, p2.y);
        mPath.cubicTo(p2.top.x, p2.top.y, p3.right.x, p3.right.y, p3.x, p3.y);
        mPath.cubicTo(p3.left.x, p3.left.y, p4.top.x, p4.top.y, p4.x, p4.y);
        mPath.cubicTo(p4.bottom.x, p4.bottom.y, p1.left.x, p1.left.y, p1.x, p1.y);

        if (mCurrentTime > 0 && mCurrentTime < 1)//变换过程中有自己的颜色
            mPaint.setColor(getCurrentColor(mCurrentTime, startColor, endColor));

        canvas.drawPath(mPath, mPaint);//绘制贝塞尔曲线

        canvas.restore();//与canvas.save();对应

        super.dispatchDraw(canvas);
    }

    //重新绘制贝塞尔曲线的圆
    private void resetP() {
        p1.setY(radius);
        p1.setX(0);
        p1.setMc(mc);

        p3.setY(-radius);
        p3.setX(0);
        p3.setMc(mc);

        p2.setY(0);
        p2.setX(radius);
        p2.setMc(mc);

        p4.setY(0);
        p4.setX(-radius);
        p4.setMc(mc);
    }

    //设置贝塞尔圆移动中的渐变颜色
    public int getCurrentColor(float percent, int startColor, int endColor) {
        colors[0] = startColor;
        colors[1] = Color.GRAY;
        colors[2] = Color.GRAY;
        colors[3] = endColor;
        for (int i = 0; i < colors.length; i++) {//获取贝塞尔圆移动中四个阶段的颜色
            f[i][0] = (colors[i] & 0xff0000) >> 16;//r
            f[i][1] = (colors[i] & 0x00ff00) >> 8;//g
            f[i][2] = (colors[i] & 0x0000ff);//b
        }
        for (int i = 0; i < 3; i++) {//设置贝塞尔圆移动中的颜色
            for (int j = 0; j < f.length; j++) {
                if (f.length == 1 || percent == j / (f.length - 1f)) {
                    result = f[j];
                } else {
                    if (percent > j / (f.length - 1f) && percent < (j + 1f) / (f.length - 1)) {
                        result[i] = f[j][i] - (f[j][i] - f[j + 1][i]) * (percent - j / (f.length - 1f)) * (f.length - 1f);
                    }
                }
            }
        }
        return Color.rgb((int) result[0], (int) result[1], (int) result[2]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        if (x > div + 2 * radius && x < (div + 2 * radius) * tabNum) {
            if (animator != null)
                animator.cancel();
            int toPos = (int) (x / (div + 2 * radius));
            if (toPos != currentPos && toPos <= tabNum)
                startAniTo(currentPos, toPos);
        } else if (x > div && x < div + 2 * radius) {
            if (animator != null)
                animator.cancel();
            if (currentPos != 0)
                startAniTo(currentPos, 0);
        }
        return super.onTouchEvent(event);
    }

    private boolean startAniTo(int currentPos, int toPos) {
        this.currentPos = currentPos;
        this.toPos = toPos;
        if (currentPos == toPos)
            return true;
        startColor = roundColors[(this.currentPos) % 4];
        endColor = roundColors[(toPos) % 4];
        resetP();


        startX = div + radius + (this.currentPos) * (div + 2 * radius);
        distance = (toPos - this.currentPos) * (2 * radius + div) + (toPos > currentPos ? -radius : radius);

        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, 1.0f);
            animator.setDuration(duration);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentTime = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animating = true;
                    setTouchAble(!animating);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    goo();
                    animating = false;
                    setTouchAble(!animating);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    goo();
                    animating = false;
                    setTouchAble(!animating);
                    mCurrentTime=1;
                    invalidate();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        animator.start();
        if (mViewPager != null) {
            mViewPager.setCurrentItem(toPos);
        }
        return true;
    }

    private void setTouchAble(boolean touchAble) {
        if (mViewPager instanceof Touchable)
            ((Touchable) mViewPager).setTouchable(touchAble);
    }

    private void goo() {
        currentPos = toPos;
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                try {
                    if (!animating) {
                        updateDrop(position, positionOffset, positionOffsetPixels);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                viewPagerState = state;
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    private void updateDrop(int position, float positionOffset, int positionOffsetPixels) {
        if (animator != null)
            animator.cancel();
        if ((position + positionOffset) - currentPos > 0)
            direction = true;
        else if ((position + positionOffset) - currentPos < 0)
            direction = false;
        if (direction)
            toPos = currentPos + 1;
        else
            toPos = currentPos - 1;
        startColor = roundColors[(currentPos) % 4];
        endColor = roundColors[(currentPos + (direction ? 1 : -1)) % 4];
        startX = div + radius + (currentPos) * (div + 2 * radius);
        distance = direction ? ((2 * radius + div) + (direction ? -radius : radius)) : (-(2 * radius + div) + (direction ? -radius : radius));
        mCurrentTime = position + positionOffset - (int) (position + positionOffset);
        if (!direction)
            mCurrentTime = 1 - mCurrentTime;
        if (Math.abs(lastCurrentTime - mCurrentTime) > 0.2) {
            if (lastCurrentTime < 0.1)
                mCurrentTime = 0;
            else if (lastCurrentTime > 0.9)
                mCurrentTime = 1;
        }

        lastCurrentTime = mCurrentTime;
        invalidate();
    }

    class XPoint {
        public float x;
        public float y;
        public float mc;
        public PointF bottom;
        public PointF top;

        public XPoint(float x, float y, float mc) {
            this.x = x;
            this.y = y;
            this.mc = mc;
            if (bottom == null)
                bottom = new PointF();
            if (top == null)
                top = new PointF();
            bottom.y = y + mc;
            top.y = y - mc;
            bottom.x = x;
            top.x = x;
        }

        public void setMc(float mc) {
            this.mc = mc;
            bottom.y = y + mc;
            top.y = y - mc;
        }

        public void setY(float y) {
            this.y = y;
            bottom.y = y + mc;
            top.y = y - mc;
        }

        public void setX(float x) {
            this.x = x;
            bottom.x = x;
            top.x = x;
        }

        @Override
        public String toString() {
            return "XPoint{" +
                    "x=" + x +
                    ", y=" + y +
                    ", mc=" + mc +
                    ", bottom=" + bottom +
                    ", top=" + top +
                    '}';
        }
    }

    class YPoint {
        public float x;
        public float y;
        public float mc;
        public PointF left;
        public PointF right;

        public YPoint(float x, float y, float mc) {
            this.x = x;
            this.y = y;
            this.mc = mc;
            if (left == null)
                left = new PointF();
            if (right == null)
                right = new PointF();
            right.x = x + mc;
            left.x = x - mc;
            left.y = y;
            right.y = y;
        }

        public void setMc(float mc) {
            this.mc = mc;
            right.x = x + mc;
            left.x = x - mc;
        }

        public void setX(float x) {
            this.x = x;
            right.x = x + mc;
            left.x = x - mc;
        }

        public void setY(float y) {
            this.y = y;
            left.y = y;
            right.y = y;
        }

        public void setLeftX(float leftX) {
            left.x = leftX;
            x = (left.x + right.x) / 2;
        }

        @Override
        public String toString() {
            return "YPoint{" +
                    "x=" + x +
                    ", y=" + y +
                    ", mc=" + mc +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }

    public int dip2px(Context mContext, float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
