package com.zpw.views.exercise32;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zpw.views.R;

import java.util.ArrayList;


public class GestureOverlayViewActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener, GestureOverlayView.OnGesturingListener {
    private final String TAG = "NestedScrollViewActivity";

    private GestureOverlayView mDrawGestureView;
    private LinearLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_overlay_view_layout);

        mDrawGestureView = (GestureOverlayView) findViewById(R.id.gesture);
        mRoot = (LinearLayout) findViewById(R.id.root);

        //设置手势可多笔画绘制，默认情况为单笔画绘制
        mDrawGestureView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);

        mDrawGestureView.setGestureColor(gestureColor(R.color.black));

        mDrawGestureView.setUncertainGestureColor(gestureColor(R.color.blue));

        mDrawGestureView.setGestureStrokeWidth(4);

        mDrawGestureView.setFadeOffset(5 * 1000);

        mDrawGestureView.addOnGesturePerformedListener(this);
        mDrawGestureView.addOnGesturingListener(this);

        final GestureLibrary library = GestureLibraries.fromRawResource(GestureOverlayViewActivity.this, R.raw.gestures);//获取手势文件
        library.load();

        mDrawGestureView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {
                showMessage("手势绘制完成");//图像消失时回调
                //读出手势库中内容 识别手势
                ArrayList<Prediction> mygesture = library.recognize(gesture);
                Prediction predction = mygesture.get(0);
                if (predction.score >= 3.5) {//阈值匹配
                    if (predction.name.equals("test_down")) {
                        Toast.makeText(GestureOverlayViewActivity.this, "向下手势", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GestureOverlayViewActivity.this, "没有该手势", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        showMessage("手势绘制完成");//图像消失时回调
    }

    @Override
    public void onGesturingStarted(GestureOverlayView overlay) {
        showMessage("正在绘制手势");
    }

    @Override
    public void onGesturingEnded(GestureOverlayView overlay) {
        showMessage("结束正在绘制手势");
        overlay.setDrawingCacheEnabled(true);
        final ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(overlay.getWidth(), overlay.getHeight()));
        imageView.setImageBitmap(Bitmap.createBitmap(overlay.getDrawingCache()));
        overlay.setDrawingCacheEnabled(false);
        overlay.setBackgroundColor(getResources().getColor(R.color.blue));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDrawGestureView.setVisibility(View.GONE);
                mRoot.addView(imageView);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawGestureView.removeOnGesturePerformedListener(this);
        mDrawGestureView.removeOnGesturingListener(this);
    }

    private int gestureColor(int resId) {
        return getResources().getColor(resId);
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
