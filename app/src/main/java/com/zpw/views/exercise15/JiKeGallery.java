package com.zpw.views.exercise15;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpw on 2018/7/3.
 */

public class JiKeGallery extends SmoothViewGroup {
    private List<Integer> mImgList = new ArrayList<>();//图片的url
    private ImageView[] mImgs = new ImageView[2];//两个ImageView用于切换图片
    private View mShadowView;

    public JiKeGallery(Context context) {
        super(context);
    }

    public JiKeGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JiKeGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        if (mImgList.size() == 0) {//如果没有内容，则不进行初始化操作
            return;
        }

        removeAllViews();

        MarginLayoutParams params = new MarginLayoutParams(mWidth, mHeight);
        for (int i = 0; i < mImgs.length; i++) {//两个ImageView加载前两张图
            mImgs[i] = new ImageView(getContext());
            addViewInLayout(mImgs[i], -1, params, true);
            mImgs[i].setImageBitmap(BitmapFactory.decodeResource(getResources(), mImgList.get(i)));
        }

        mShadowView = new View(getContext());//创建阴影View
        mShadowView.setBackgroundColor(Color.parseColor("#60000000"));
        mShadowView.setAlpha(0);
        addViewInLayout(mShadowView, -1, params, true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        MarginLayoutParams cParams;

        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            int cl = 0, ct = 0, cr, cb;//子View左上右下

            if (isOddCircle()) {//根据两个View来实现图像切换
                if (i == 1) {
                    cl = cParams.leftMargin;
                    ct = mSmoothMarginTop + mHeight;
                } else if (i == 0) {
                    cl = cParams.leftMargin;
                    ct = mSmoothMarginTop;
                }
            } else {
                if (i == 0) {
                    cl = cParams.leftMargin;
                    ct = mSmoothMarginTop + mHeight;
                } else if (i == 1) {
                    cl = cParams.leftMargin;
                    ct = mSmoothMarginTop;
                }
            }

            if (i == 2) {//控制shadowView
                cl = cParams.leftMargin;
                ct = mSmoothMarginTop + mHeight;
            }

            cr = cl + mWidth;
            cb = ct + mHeight;

            childView.layout(cl, ct, cr, cb);
        }
    }

    @Override
    protected void doAnimFinish() {
        if (isOddCircle()) {//在动画结束时更新图片显示
            mImgs[0].setImageBitmap(BitmapFactory.decodeResource(getResources(), getImgPath(mRepeatTimes + 1)));
        } else {
            mImgs[1].setImageBitmap(BitmapFactory.decodeResource(getResources(), getImgPath(mRepeatTimes + 1)));
        }
        mShadowView.setAlpha(0);
    }

    @Override
    protected void doAnim() {
        mShadowView.setAlpha(((1 - (-mSmoothMarginTop) / (float) mHeight)));
        requestLayout();
    }

    public void setImgList(List<Integer> imgList) {
        mImgList = imgList;
        initView();
    }

    /**
     * 获取图片地址
     *
     * @param position 位置
     * @return 图片地址
     */
    private Integer getImgPath(int position) {
        position = position % mImgList.size();
        return mImgList.get(position);
    }
}
