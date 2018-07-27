package com.zpw.views.exercise26;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.zpw.views.exercise28.dslv.DragSortListView;

/**
 * Created by zpw on 2018/7/18.
 */

public class CanDragFrameLayout extends FrameLayout {
    public CanDragFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CanDragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        DragSortListView listview = (DragSortListView) findViewById(android.R.id.list);
        if (listview instanceof DragSortListView) {
            if (((DragSortListView) listview).getDragState() == DragSortListView.DRAGGING
                    || ((DragSortListView) listview).getDragState() == DragSortListView.REMOVING
                    || ((DragSortListView) listview).getDragState() == DragSortListView.DROPPING
                    || ((DragSortListView) listview).getDragState() == DragSortListView.STOPPED) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DragSortListView listview = (DragSortListView) findViewById(android.R.id.list);
        if (((DragSortListView) listview).getDragState() == DragSortListView.DRAGGING
                || ((DragSortListView) listview).getDragState() == DragSortListView.REMOVING
                || ((DragSortListView) listview).getDragState() == DragSortListView.DROPPING
                || ((DragSortListView) listview).getDragState() == DragSortListView.STOPPED) {
            final float x = event.getX() - getPaddingLeft();
            final float y = event.getY() - getPaddingTop();
//            event.setLocation(x, y);
            if (listview.onTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
