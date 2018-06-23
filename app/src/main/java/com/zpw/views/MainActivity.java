package com.zpw.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zpw.views.exercise1.PieData;
import com.zpw.views.exercise1.PieView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PieView pieView = new PieView(this);
        ArrayList<PieData> data = new ArrayList<>();
        data.add(new PieData("红", 30));
        data.add(new PieData("绿", 20));
        data.add(new PieData("蓝", 50));
        pieView.setData(data);
        setContentView(pieView);
    }
}
