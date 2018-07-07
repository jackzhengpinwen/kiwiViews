package com.zpw.views.exercise19;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zpw.views.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpw on 2018/7/7.
 */

public class PreviewIndicator extends LinearLayout {
    private final int INDICATOR_COUNT = 3;//指示器个数
    private List<ImageView> mImageList = new ArrayList<>();

    public PreviewIndicator(Context context) {
        this(context, null);
    }

    public PreviewIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        for (int i = 0; i < INDICATOR_COUNT; i++) {
            ImageView imageView = new ImageView(context);
            if (i == 0) {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_circle_selected));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_circle_unselected));
            }
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dp2px(10), 0, dp2px(10), 0);
            addView(imageView, lp);
            mImageList.add(imageView);
        }
    }

    public void setSelected(int position) {
        for (int i = 0; i < mImageList.size(); i++) {
            ImageView imageView = mImageList.get(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_circle_selected));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_circle_unselected));
            }
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
