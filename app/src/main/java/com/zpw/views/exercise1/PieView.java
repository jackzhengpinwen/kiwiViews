package com.zpw.views.exercise1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * 自定义饼状图
 *
 * Created by zpw on 2018/6/23.
 */

public class PieView extends View {
    // 颜色表 (注意: 此处定义颜色使用的是ARGB，带Alpha通道的)
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
    // 画笔
    private Paint mPaint = new Paint();
    // 宽高
    private int mWidth, mHeight;
    // 数据
    private List<PieData> mData;
    // 饼状图初始绘制角度
    private float mStartAngle = 0;

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
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
        if (mData == null) return;
        float currentStartAngle = mStartAngle; //当前起始角度
        canvas.translate(mWidth/2, mHeight/2); //将画布坐标原点移动到中心位置
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);// 饼状图半径
        RectF rect = new RectF(-r, -r, r, r); // 饼状图绘制区域
        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);
            mPaint.setColor(pie.getColor()); //不同的部分用不同颜色
            canvas.drawArc(rect, currentStartAngle, pie.getAngle(), true, mPaint); //绘制不同的饼状图
            currentStartAngle += pie.getAngle(); //每次更新绘制的起始角度
        }
        canvas.restore();
    }

    public void setData(List<PieData> data) {
        mData = data;
        initData(mData);
        invalidate();
    }

    private void initData(List<PieData> data) {
        if (data == null || data.size() == 0) return; // 数据有问题 直接返回
        float sumValue = 0;
        for (int i = 0; i < data.size(); i++) {
            PieData pie = data.get(i);
            sumValue += pie.getValue(); //计算数值和
            int j = i % mColors.length; //设置颜色
            pie.setColor(mColors[j]);
        }
        for (int i = 0; i < data.size(); i++) {
            PieData pie = data.get(i);
            float percentage = pie.getValue() / sumValue; // 百分比
            float angle = percentage * 360; // 对应的角度
            pie.setAngle(angle); // 记录角度大小
            pie.setPercentage(percentage); // 记录百分比
        }
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }
}
