package com.andros230.trace.activity;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andros230.trace.R;
import com.andros230.trace.bean.LatLngKit;

import java.util.List;
import java.util.Map;

public class MainAdapter extends BaseAdapter {
    private List<LatLngKit> data;
    private LayoutInflater mInflater;


    public MainAdapter(Context context, List<LatLngKit> data) {
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.main_listview_item, null);
            holder.uid = (TextView) view.findViewById(R.id.main_uid);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.uid.setText(data.get(i).getUid());
        return view;
    }

    public class ViewHolder {
        public TextView uid;
    }

}