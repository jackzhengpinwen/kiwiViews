package com.zpw.views.exercise23;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Created by zpw on 2018/7/14.
 */

public class WindowInsetsFrameLayout extends FrameLayout {
    public WindowInsetsFrameLayout(@NonNull Context context) {
        super(context);
    }

    public WindowInsetsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; ++index)
            getChildAt(index).dispatchApplyWindowInsets(insets);
        // let children know about WindowInsets

        return insets;
    }
}
