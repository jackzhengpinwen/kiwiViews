package com.zpw.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.zpw.views.exercise12.SlideTapeView;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";// 1464 976 732 488 366

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SlideTapeView tapeView = new SlideTapeView(this);
        tapeView.setShortPointCount(10);
        tapeView.setValue(30, 31);
        tapeView.setLongUnix(1);
        setContentView(tapeView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "dispatchTouchEvent: ");
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }
}
