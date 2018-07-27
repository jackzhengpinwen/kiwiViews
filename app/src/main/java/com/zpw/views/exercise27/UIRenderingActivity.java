package com.zpw.views.exercise27;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zpw.views.R;

public class UIRenderingActivity extends AppCompatActivity {
    private final String TAG = "NestedScrollViewActivity";
    private StaticLayout mLongStringLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_rendering_layout);

        initLayout(this, getResources().getString(R.string.content));

        RecyclerView rvTest = (RecyclerView) findViewById(R.id.rv_test);
        rvTest.setLayoutManager(new LinearLayoutManager(this));
        rvTest.setAdapter(new RVAdapter());
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVHolder> {


        @Override
        public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_list_item, parent, false);
            return new RVHolder(view);
        }

        @Override
        public void onBindViewHolder(RVHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }

        public class RVHolder extends RecyclerView.ViewHolder {

            public RVHolder(View itemView) {
                super(itemView);
                StaticLayoutView staticLayoutView = itemView.findViewById(R.id.static_layout_view);
                staticLayoutView.setLayout(mLongStringLayout);
                staticLayoutView.invalidate();
            }
        }
    }

    public void initLayout(Context context, CharSequence longString) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.density = context.getResources().getDisplayMetrics().density;
        textPaint.setTextSize(Util.fromDPtoPix(context, Util.TEXT_SIZE_DP));

        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;

        int hardCodeWidth = Util.getScreenWidth(context);

        mLongStringLayout = new StaticLayout(longString, textPaint, hardCodeWidth/2, alignment, 1.0f, 0f, true);

        Canvas dummyCanvas = new Canvas();

        mLongStringLayout.draw(dummyCanvas);
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
