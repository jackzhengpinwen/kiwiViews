package com.zpw.views.exercise26;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zpw.views.R;

/**
 * Created by zpw on 2018/7/15.
 */

public class ExampleFragment extends Fragment {
    private final static String BACKGROUND_COLOR = "background_color";
    private final static String BACKGROUND_DRAWABLE = "background_drawable";

    public static ExampleFragment newInstance(int IdRes) {
        ExampleFragment frag = new ExampleFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, IdRes);
        b.putInt(BACKGROUND_DRAWABLE, IdRes);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        ImageView bg = (ImageView) view.findViewById(R.id.bg_img);
        bg.setImageDrawable(getActivity().getResources().getDrawable(getArguments().getInt(BACKGROUND_DRAWABLE)));

        return view;
    }
}

