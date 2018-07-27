package com.zpw.views.exercise34;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpw.views.R;

import java.util.List;

public class ViewSwitcherBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<ViewSwitcherItemData> mItemDatas;

    public ViewSwitcherBaseAdapter(Context context, List<ViewSwitcherItemData> itemDatas) {
        mContext = context;
        mItemDatas = itemDatas;
    }

    @Override
    public int getCount() {
        if (ViewSwitcherActivity.screenNo == ViewSwitcherActivity.screenCount - 1
                && mItemDatas.size() % ViewSwitcherActivity.NUMBER_PER_SCREEN != 0) {
            return mItemDatas.size() % ViewSwitcherActivity.NUMBER_PER_SCREEN;
        }
        return ViewSwitcherActivity.NUMBER_PER_SCREEN;
    }

    @Override
    public ViewSwitcherItemData getItem(int position) {
        return mItemDatas.get(ViewSwitcherActivity.screenNo * ViewSwitcherActivity.NUMBER_PER_SCREEN + position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_slide_item, null);
            holder = new ViewHolder();
            holder.nameTv = convertView.findViewById(R.id.name_tv);
            holder.iconImg = convertView.findViewById(R.id.icon_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTv.setText(getItem(position).getName());
        holder.iconImg.setImageResource(getItem(position).getIcon());
        return convertView;
    }

    private class ViewHolder {
        ImageView iconImg;
        TextView nameTv;
    }
}
