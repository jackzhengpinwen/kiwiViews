package com.zpw.views.exercise23;

import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpw.views.R;

public class CoordinatorLayoutActivity extends AppCompatActivity {
    private final String TAG = "NestedScrollViewActivity";
    private CoordinatorLayout mViewgroup;
    private ImageView mMsg;
    private Toolbar mToolbar;
    private FrameLayout mBottom;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_layout);
        mViewgroup = (CoordinatorLayout) findViewById(R.id.contentView);
        mViewgroup.setStatusBarBackground(getResources().getDrawable(R.mipmap.intro_text_1));
        mMsg = (ImageView) findViewById(R.id.img_msg);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setElevation(15);
        mToolbar.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        });
        mToolbar.setClipToOutline(true);
        mBottom = (FrameLayout) findViewById(R.id.fl_bottom);

//        ViewCompat.setOnApplyWindowInsetsListener(mToolbar, new OnApplyWindowInsetsListener() {
//            @Override
//            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
//                v.setPadding(v.getLeft(), v.getTop() + insets.getSystemWindowInsetTop(), v.getRight(), v.getBottom());
//                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//                params.topMargin = insets.getSystemWindowInsetTop();
//                return insets.consumeSystemWindowInsets();
//            }
//        });
//
//        ViewCompat.setOnApplyWindowInsetsListener(mBottom, new OnApplyWindowInsetsListener() {
//            @Override
//            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
//                v.setPadding(v.getLeft(), v.getTop(), v.getRight(), v.getBottom() + insets.getSystemWindowInsetBottom());
//                return insets.consumeSystemWindowInsets();
//            }
//        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_test);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RVAdapter());
    }

    public static class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVHolder> {


        @Override
        public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new RVHolder(view);
        }

        @Override
        public void onBindViewHolder(RVHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }

        public static class RVHolder extends RecyclerView.ViewHolder {

            public RVHolder(View itemView) {
                super(itemView);
                TextView text = itemView.findViewById(android.R.id.text1);
                text.setText("1");
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(mViewgroup, "Snackbar", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }
}
