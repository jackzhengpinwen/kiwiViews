package com.zpw.views.exercise15;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.zpw.views.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpw on 2018/7/3.
 */

public class JiKeGalleryView extends FrameLayout {
    private JiKeGallery mGallery;
    private JiKeTitleView mTitleView;

    public JiKeGalleryView(@NonNull Context context) {
        this(context, null);
    }

    public JiKeGalleryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiKeGalleryView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.layout_jike_gallery_view, this);
        mGallery = (JiKeGallery) findViewById(R.id.gallery);
        mTitleView = (JiKeTitleView) findViewById(R.id.title_view);
    }

    public void startSmooth() {
        mGallery.startSmooth();
        mTitleView.startSmooth();
    }

    public void addGalleryData(List<JiKeGalleryEntity> listEntities) {
        List<Integer> imgList = new ArrayList<>();
        for (JiKeGalleryEntity entity : listEntities) {
            imgList.add(entity.imgUrl);
        }
        mGallery.setImgList(imgList);

        List<String> titleList = new ArrayList<>();
        for (JiKeGalleryEntity entity : listEntities) {
            titleList.add(entity.title);
        }
        mTitleView.setTitleList(titleList);
    }
}
