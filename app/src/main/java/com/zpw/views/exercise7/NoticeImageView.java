package com.zpw.views.exercise7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zpw.views.R;

/**
 * Created by zpw on 2018/6/24.
 */

public class NoticeImageView extends AppCompatImageView {
    private final String TAG = "NoticeImageView";
    private Context mContext;

    public NoticeImageView(Context context) {
        this(context, null);
    }

    public NoticeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
                    int pixel = bitmap.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                    if (pixel == 0) {
                        Toast.makeText(mContext, TAG + "->透明区域", Toast.LENGTH_SHORT).show();
                        ViewGroup parent = (ViewGroup) getParent();
                        int childCount = parent.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View child = parent.getChildAt(i);
                            if (child.getId() == R.id.img_camera && child instanceof CameraImageView) {
                                ((CameraImageView)child).click(child);
                                break;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, TAG + "->点击事件", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
