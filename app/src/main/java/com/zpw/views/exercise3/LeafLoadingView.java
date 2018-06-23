package com.zpw.views.exercise3;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.zpw.views.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by zpw on 2018/6/23.
 */

public class LeafLoadingView extends View {

    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;

    private Resources mResources;

    // 用于控制绘制的进度条距离左／上／下的距离
    private static final int LEFT_MARGIN = 9;
    // 用于控制绘制的进度条距离右的距离
    private static final int RIGHT_MARGIN = 25;
    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;
    // 总进度
    private static final int TOTAL_PROGRESS = 100;
    
    private int mLeftMargin;
    private int mRightMargin;
    private long mLeafFloatTime;
    private long mLeafRotateTime;
    private Bitmap mLeafBitmap;
    private Bitmap mOuterBitmap;
    private int mLeafWidth;
    private int mLeafHeight;
    private int mOuterWidth;
    private int mOuterHeight;
    private Paint mBitmapPaint;
    private Paint mWhitePaint;
    private Paint mOrangePaint;
    private int mAddTime;
    private LeafFactory mLeafFactory;
    private List<Leaf> mLeafInfos;
    private int mTotalWidth;
    private int mTotalHeight;
    private int mProgressWidth;
    private int mArcRadius;
    private Rect mOuterSrcRect;
    private Rect mOuterDestRect;
    private RectF mWhiteRectF;
    private float mCurrentProgressPosition;
    private RectF mOrangeRectF;
    private RectF mArcRectF;
    private int mArcRightLocation;
    private float mProgress;
    private int mMiddleAmplitude;
    private int mAmplitudeDisparity;

    public LeafLoadingView(Context context) {
        this(context, null);
    }

    public LeafLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mResources = getResources();
        
        mLeftMargin = dipToPx(context, LEFT_MARGIN);
        mRightMargin = dipToPx(context, RIGHT_MARGIN);

        mLeafFloatTime = LEAF_FLOAT_TIME;
        mLeafRotateTime = LEAF_ROTATE_TIME;

        mLeafBitmap = BitmapFactory.decodeResource(mResources, R.mipmap.leaf);
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();

        mOuterBitmap = BitmapFactory.decodeResource(mResources, R.mipmap.leaf_kuang);
        mOuterWidth = mOuterBitmap.getWidth();
        mOuterHeight = mOuterBitmap.getHeight();

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);

        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mProgressWidth = mTotalWidth - mLeftMargin - mRightMargin;
        mArcRadius = (mTotalHeight - 2 * mLeftMargin) / 2;

        mOuterSrcRect = new Rect(0, 0, mOuterWidth, mOuterHeight);
        mOuterDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

        mWhiteRectF = new RectF(mLeftMargin + mCurrentProgressPosition, mLeftMargin, mTotalWidth
                - mRightMargin,
                mTotalHeight - mLeftMargin);
        mOrangeRectF = new RectF(mLeftMargin + mArcRadius, mLeftMargin,
                mCurrentProgressPosition
                , mTotalHeight - mLeftMargin);

        mArcRectF = new RectF(mLeftMargin, mLeftMargin, mLeftMargin + 2 * mArcRadius,
                mTotalHeight - mLeftMargin);
        mArcRightLocation = mLeftMargin + mArcRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制进度条和叶子
        // 之所以把叶子放在进度条里绘制，主要是层级原因
        drawProgressAndLeafs(canvas);

        canvas.drawBitmap(mOuterBitmap, mOuterSrcRect, mOuterDestRect, mBitmapPaint);

        postInvalidate();
    }

    private void drawProgressAndLeafs(Canvas canvas) {
        if (mProgress >= TOTAL_PROGRESS) {
            mProgress = 0;
        }
        // mProgressWidth为进度条的宽度，根据当前进度算出进度条的位置
        mCurrentProgressPosition = mProgressWidth * mProgress / TOTAL_PROGRESS;
        // 即当前位置在图中所示1范围内
        if (mCurrentProgressPosition < mArcRadius) {
            // 1.绘制白色ARC
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);

            // 2.绘制白色矩形
            mWhiteRectF.left = mArcRightLocation;
            canvas.drawRect(mWhiteRectF, mWhitePaint);

            // 绘制叶子
            drawLeafs(canvas);

            // 3.绘制棕色 ARC
            // 单边角度
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressPosition)
                    / (float) mArcRadius));
            // 起始的位置
            int startAngle = 180 - angle;
            // 扫过的角度
            int sweepAngle = 2 * angle;

            canvas.drawArc(mArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            // 1.绘制white RECT
            mWhiteRectF.left = mCurrentProgressPosition;
            canvas.drawRect(mWhiteRectF, mWhitePaint);
            // 绘制叶子
            drawLeafs(canvas);
            // 2.绘制Orange ARC
            canvas.drawArc(mArcRectF, 90, 180, false, mOrangePaint);
            // 3.绘制orange RECT
            mOrangeRectF.left = mArcRightLocation;
            mOrangeRectF.right = mCurrentProgressPosition;
            canvas.drawRect(mOrangeRectF, mOrangePaint);
        }
    }

    private void drawLeafs(Canvas canvas) {
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的（x，y）
                getLeafLocation(leaf, currentTime);
                // 根据时间计算旋转角度
                canvas.save();
                // 通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                matrix.postTranslate(transX, transY);
                // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        / (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                        + leaf.rotateAngle;
                matrix.postRotate(rotate, transX
                        + mLeafWidth / 2, transY + mLeafHeight / 2);
                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
                canvas.restore();
            } else {
                continue;
            }
        }
    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis()
                    + new Random().nextInt((int) mLeafFloatTime);
        }

        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (int) (mProgressWidth - mProgressWidth * fraction);
        leaf.y = getLocationY(leaf);
    }

    private float getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h
        float w = (float) ((float) 2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                a = mMiddleAmplitude;
                break;
            case BIG:
                // 小振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        return (int) (a * Math.sin(w * leaf.x)) + mArcRadius * 2 / 3;
    }

    private enum StartType {
        LITTLE, MIDDLE, BIG
    }

    private class Leaf {
        // 在绘制部分的位置
        float x, y;
        // 控制叶子飘动的幅度
        StartType type;
        // 旋转角度
        int rotateAngle;
        // 旋转方向--0代表顺时针，1代表逆时针
        int rotateDirection;
        // 起始时间(ms)
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        // 生成一个叶子信息
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(3);
            StartType type = StartType.MIDDLE;
            switch (randomType) {
                case 0:
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;

                default:
                    break;
            }
            leaf.type = type;
            leaf.rotateAngle = random.nextInt(360);
            leaf.rotateDirection = random.nextInt(2);
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }

        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<Leaf>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
    }

    /**
     * 设置中等振幅
     *
     * @param amplitude
     */
    public void setMiddleAmplitude(int amplitude) {
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * 设置振幅差
     *
     * @param disparity
     */
    public void setMplitudeDisparity(int disparity) {
        this.mAmplitudeDisparity = disparity;
    }

    /**
     * 获取中等振幅
     */
    public int getMiddleAmplitude() {
        return mMiddleAmplitude;
    }

    /**
     * 获取振幅差
     *
     */
    public int getMplitudeDisparity() {
        return mAmplitudeDisparity;
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    /**
     * 设置叶子飘完一个周期所花的时间
     *
     * @param time
     */
    public void setLeafFloatTime(long time) {
        this.mLeafFloatTime = time;
    }

    /**
     * 设置叶子旋转一周所花的时间
     *
     * @param time
     */
    public void setLeafRotateTime(long time) {
        this.mLeafRotateTime = time;
    }

    /**
     * 获取叶子飘完一个周期所花的时间
     */
    public long getLeafFloatTime() {
        mLeafFloatTime = mLeafFloatTime == 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        return mLeafFloatTime;
    }

    /**
     * 获取叶子旋转一周所花的时间
     */
    public long getLeafRotateTime() {
        mLeafRotateTime = mLeafRotateTime == 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        return mLeafRotateTime;
    }

    public int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    public float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }
}
