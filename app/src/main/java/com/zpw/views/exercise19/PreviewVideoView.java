package com.zpw.views.exercise19;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.VideoView;

/**
 * Created by zpw on 2018/7/7.
 */

public class PreviewVideoView extends VideoView {
    public PreviewVideoView(Context context) {
        this(context, null);
    }

    public PreviewVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, (int) (width/0.56));//根据视频文件进行View的尺寸缩放
    }

    //=======================================================
    private int sp2px(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()));
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}
