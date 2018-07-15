package com.zpw.views.exercise22;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zpw on 2018/7/12.
 */

public class FindViewUtil {
    public static View findView(ViewGroup vg, int id) {
        if (vg == null) {
            return null;
        }
        int size = vg.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = vg.getChildAt(i);
            if (view.getId() == id) {
                return view;
            }
            if (view instanceof ViewGroup) {
                View temp = findView((ViewGroup) view, id);
                if (temp != null) {
                    return temp;
                }
            }
        }
        return null;
    }
}
