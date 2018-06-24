package com.zpw.views.exercise7;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 * Created by zpw on 2018/6/24.
 */

public class CameraImageView extends AppCompatImageView {
    private final String TAG = "CameraImageView";
    private Context mContext;

    public CameraImageView(Context context) {
        this(context, null);
    }

    public CameraImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void click(View view) {
        Toast.makeText(mContext, TAG + "->点击事件", Toast.LENGTH_SHORT).show();
    }
}
