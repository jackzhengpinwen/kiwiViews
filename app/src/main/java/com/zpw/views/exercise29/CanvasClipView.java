package com.zpw.views.exercise29;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by zpw on 2018/7/18.
 */

public class CanvasClipView extends View {
    private int mColumns = 3;
    private int mVerCount;
    private int mItemSize;
    private int countOffset = 2;
    private int mDividerSize = dp2px(10);
    private int mItemCount = Region.Op.values().length + countOffset;
    //
    private RectF mTempRect = new RectF();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //
    private RectF mClipRect0 = new RectF();
    private RectF mClipRect1 = new RectF();

    public CanvasClipView(Context context) {
        super(context);
    }

    public CanvasClipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPaint.setFakeBoldText(true);
        mPaint.setTextSize(sp2px(10));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setShadowLayer(1, 1, 1, 0xff000000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        mItemSize = (getMeasuredWidth() - mDividerSize * (mColumns + 1)) / mColumns;
        mVerCount = (mItemCount / mColumns) + (mItemCount % mColumns == 0 ? 0 : 1);
        setMeasuredDimension(getMeasuredWidth(), mItemSize * mVerCount + mDividerSize * (mVerCount + 1));
        //
        mClipRect0.set(0, 0, mItemSize / 2, mItemSize / 2);
        mClipRect1.set(mItemSize / 4, mItemSize / 4, mItemSize, mItemSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw divider line
        mPaint.setColor(0xFF000000);
        mPaint.setStrokeWidth(dp2px(1));
        final int offset = mDividerSize / 2;
        for (int i = 0; i <= mColumns; i++) {
            final int x = i * (mDividerSize + mItemSize) + offset;
            canvas.drawLine(x, offset, x, getMeasuredHeight() - offset, mPaint);
        }
        for (int i = 0; i <= mVerCount; i++) {
            final int y = i * (mDividerSize + mItemSize) + offset;
            canvas.drawLine(offset, y, getMeasuredWidth() - offset, y, mPaint);
        }
        //
        for (int i = 0; i < mItemCount; i++) {
            final int tranX = (i % mColumns) * (mDividerSize + mItemSize) + mDividerSize;
            final int tranY = (i / mColumns) * (mDividerSize + mItemSize) + mDividerSize;

            // save matrix
            final int matrixCount = canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.translate(tranX, tranY);

            // draw clip
            final int clipCount = canvas.save(Canvas.CLIP_SAVE_FLAG);
            final String name = drawTheRects(canvas, i);
            canvas.restoreToCount(clipCount);

            // draw name
            mPaint.setColor(0xFFFFFFFF);
            canvas.drawText(name, mItemSize / 2, mItemSize, mPaint);

            // restore matrix
            canvas.restoreToCount(matrixCount);
        }
    }

    private String drawTheRects(Canvas canvas, int index) {
        final String name;
        if (index == 0) {
            name = "CLIP-RECT";
            mPaint.setColor(0xFFFF0000);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(mClipRect0, mPaint);
            canvas.drawRect(mClipRect1, mPaint);
        } else {
            if (index < countOffset) {
                name = "ORIGINAL";
            } else {
                canvas.clipRect(mClipRect0);
                canvas.clipRect(mClipRect1, Region.Op.values()[index - countOffset]);
                name = Region.Op.values()[index - countOffset].toString();
            }
            drawBasic(canvas);
        }
        return name;
    }

    private void drawBasic(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        canvas.clipRect(0, 0, mItemSize, mItemSize);
        //
        repeatlyDraw(canvas, 0, mItemSize / 4);
        //
//        mPaint.setColor(0xFF000000);
//        mPaint.setStrokeWidth(ViewUtil.getDP(8));
//        canvas.drawLine(0, 0, mItemSize, mItemSize, mPaint);
    }

    private void repeatlyDraw(Canvas canvas, int... offsets) {
        for (int off : offsets) {
            mTempRect.set(off, off, mItemSize - off, mItemSize - off);
            drawRectWithCircle(canvas, mTempRect);
        }
    }

    private void drawRectWithCircle(Canvas canvas, RectF rectF) {
        mPaint.setColor(0xFF00FF00);
        canvas.drawRect(rectF, mPaint);
        mPaint.setColor(0xFF0000FF);
        canvas.drawRoundRect(rectF, rectF.width() / 2, rectF.height() / 2, mPaint);
    }

    //=======================================================
    private int sp2px(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()));
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}
