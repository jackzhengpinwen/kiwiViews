package com.zpw.views.exercise28.demodslv;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zpw.views.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Launcher extends ListActivity {

    //private ArrayAdapter<ActivityInfo> adapter;
    private MyAdapter adapter;

    private ArrayList<ActivityInfo> mActivities = null;

    private String[] mActTitles;
    private String[] mActDescs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo("com.zpw.views", PackageManager.GET_ACTIVITIES);
            mActivities = new ArrayList<>(Arrays.asList(pi.activities));
            String ourName = getClass().getName();
            for (int i = 0; i < mActivities.size(); ++i) {
                if (ourName.equals(mActivities.get(i).name)) {
                    mActivities.remove(i);
                    break;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing. Adapter will be empty.
        }

        mActTitles = getResources().getStringArray(R.array.activity_titles);
        mActDescs = getResources().getStringArray(R.array.activity_descs);

        adapter = new MyAdapter();
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent();
        intent.setClassName(this, mActivities.get(position).name);
        startActivity(intent);
    }

    private class MyAdapter extends ArrayAdapter<ActivityInfo> {
        MyAdapter() {
            super(Launcher.this, R.layout.launcher_item, R.id.activity_title, mActivities);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            TextView title = (TextView) v.findViewById(R.id.activity_title);
            TextView desc = (TextView) v.findViewById(R.id.activity_desc);

            title.setText(mActTitles[position]);
            desc.setText(mActDescs[position]);
            return v;
        }

    }

}
