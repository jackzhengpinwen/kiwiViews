package com.zpw.views.exercise15;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpw on 2018/7/3.
 */

public class JiKeTitleView extends SmoothViewGroup {
    private List<String> mTitleList = new ArrayList<>();//文字
    private TextView[] mTexts = new TextView[2];//两个TextView用于切换文字

    private int mTextLineHeight = 0;

    public JiKeTitleView(@NonNull Context context) {
        this(context, null);
    }

    public JiKeTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiKeTitleView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void initView() {
        //如果没有内容，则不进行初始化操作
        if (mTitleList.size() == 0) {
            return;
        }

        removeAllViews();

        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < mTexts.length; i++) {//两个TextView加载前两个文字
            mTexts[i] = new TextView(getContext());
            mTexts[i].setText(getTitle(i));
            mTexts[i].setLines(2);
            mTexts[i].setEllipsize(TextUtils.TruncateAt.END);
            mTexts[i].setTextSize(14);
            if (mTextLineHeight < mTexts[i].getLineHeight()) {
                mTextLineHeight = mTexts[i].getLineHeight();
            }
            addViewInLayout(mTexts[i], -1, params, true);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        MarginLayoutParams cParams;

        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            int cl = 0, ct = 0, cr, cb;

            if (isOddCircle()) {
                if (i == 1) {
                    cl = cParams.leftMargin;
                    ct = mHeight / 2 - mTextLineHeight + mSmoothMarginTop + mHeight;
                } else if (i == 0) {
                    cl = cParams.leftMargin;
                    ct = mHeight / 2 - mTextLineHeight + mSmoothMarginTop;
                }
            } else {
                if (i == 0) {
                    cl = cParams.leftMargin;
                    ct = mHeight / 2 - mTextLineHeight + mSmoothMarginTop + mHeight;
                } else if (i == 1) {
                    cl = cParams.leftMargin;
                    ct = mHeight / 2 - mTextLineHeight + mSmoothMarginTop;
                }
            }

            cr = cl + mWidth;
            cb = ct + mHeight;

            childView.layout(cl, ct, cr, cb);
        }
    }

    @Override
    protected void doAnimFinish() {
        if (isOddCircle()) {
            mTexts[0].setText(getTitle(mRepeatTimes + 1));
        } else {
            mTexts[1].setText(getTitle(mRepeatTimes + 1));
        }

        for (int i = 0; i < mTexts.length; i++) {
            mTexts[i].setAlpha(1);
        }
    }

    @Override
    protected void doAnim() {
        if (isOddCircle()) {
            mTexts[1].setAlpha(-mSmoothMarginTop / (float) mHeight);
        } else {
            mTexts[0].setAlpha(-mSmoothMarginTop / (float) mHeight);
        }
        requestLayout();
    }

    public void setTitleList(List<String> titleList) {
        mTitleList = titleList;
        initView();
    }

    private String getTitle(int position) {
        position = position % mTitleList.size();
        return mTitleList.get(position);
    }
}
