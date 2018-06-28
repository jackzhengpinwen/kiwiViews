package com.zpw.views.exercise9;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

/**
 * Created by zpw on 2018/6/26.
 */

public class View2 extends View {
    private final String TAG = "View2";

    public View2(Context context) {
        super(context);
    }

    public View2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        enlargeBtnTouchScope((View) getParent(), this, new Rect(), (int)(right - left), (int)(bottom - top));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: action=" + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: action=" + event.getAction());
        return super.onTouchEvent(event);
//        return true;
    }


    public static void enlargeBtnTouchScope(View parent, View view, Rect rect, float width, float height){
        rect.top = view.getTop();
        rect.bottom = view.getBottom();
        rect.left = view.getLeft();
        rect.right = view.getRight();

        rect.top -= height;
        rect.bottom += height;
        rect.left -= width;
        rect.right += width;

        parent.setTouchDelegate(new TouchDelegate(rect, view));
    }

}
