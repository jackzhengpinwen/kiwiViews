package com.zpw.views.exercise35;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.zpw.views.R;

public class TextSwitcherActivity extends AppCompatActivity {
    private TextSwitcher mTextSwitcher = null;
    private String[] mContents = {
            "你好", "HelloWorld", "Good!!!", "TextSwitcher", "你会了吗？"
    };
    private int mIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textwitcher_layout);

        mTextSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(TextSwitcherActivity.this);
                tv.setTextSize(40);
                tv.setTextColor(Color.MAGENTA);
                return tv;
            }
        });

        mTextSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextSwitcher.setText(mContents[++mIndex % mContents.length]);
            }
        });

        mTextSwitcher.setText(mContents[0]);
    }
}
