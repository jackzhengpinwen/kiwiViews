package com.zpw.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview_listview);
        ListView lv = (ListView) findViewById(R.id.lv_test);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                new String[] {"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"
                        , "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"
                        , "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"
                        , "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"}));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
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
