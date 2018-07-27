package com.zpw.views.exercise34;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ViewSwitcher;

import com.zpw.views.R;

import java.util.ArrayList;
import java.util.List;

public class ViewSwitcherActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int NUMBER_PER_SCREEN = 12;

    public static int screenNo = 0;

    public static int screenCount;

    private Button mPrevBtn, mNextBtn;
    private ViewSwitcher mViewSwitcher;

    private List<ViewSwitcherItemData> mItemDatas = new ArrayList<>();
    private ViewSwitcherBaseAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewswitcher_layout);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        mPrevBtn = (Button) findViewById(R.id.prev_btn);
        mNextBtn = (Button) findViewById(R.id.next_btn);

        mViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return getLayoutInflater().inflate(R.layout.gridview_slide, null);
            }
        });

        for (int i = 0; i < 40; i++) {
            ViewSwitcherItemData item = new ViewSwitcherItemData("item" + i, R.mipmap.ic_launcher);
            mItemDatas.add(item);
        }

        screenCount = mItemDatas.size() % NUMBER_PER_SCREEN == 0 ?
                mItemDatas.size() / NUMBER_PER_SCREEN :
                mItemDatas.size() / NUMBER_PER_SCREEN + 1;

        mAdapter = new ViewSwitcherBaseAdapter(this, mItemDatas);

        mPrevBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        ((GridView) mViewSwitcher.getChildAt(0)).setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_btn: {
                prev();
                break;
            }
            case R.id.next_btn: {
                next();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void next() {
        if (screenNo < screenCount - 1) {
            screenNo++;
            mViewSwitcher.setInAnimation(this, R.anim.slide_in_right);
            mViewSwitcher.setOutAnimation(this, R.anim.slide_out_left);
            ((GridView) mViewSwitcher.getNextView()).setAdapter(mAdapter);
            mViewSwitcher.showNext();
        }
    }

    private void prev() {
        if (screenNo > 0) {
            screenNo--;
            mViewSwitcher.setInAnimation(this, android.R.anim.slide_in_left);
            mViewSwitcher.setOutAnimation(this, android.R.anim.slide_out_right);
            ((GridView) mViewSwitcher.getNextView()).setAdapter(mAdapter);
            mViewSwitcher.showPrevious();
        }
    }
}
