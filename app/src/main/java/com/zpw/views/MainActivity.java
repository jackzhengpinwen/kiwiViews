package com.zpw.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zpw.views.exercise4.RadarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new RadarView(this));
    }
}
