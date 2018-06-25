package com.zpw.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zpw.views.exercise8.SearchView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SearchView(this));
    }
}
