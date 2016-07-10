package com.andros230.trace.activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andros230.trace.R;

import java.util.List;


public class popup_adapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private LayoutInflater mInflater;
    public popup_adapter( Context context,List<String> list) {
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (holder == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.popup_item, null);
            holder.popup_tv = (TextView) view.findViewById(R.id.popup_item_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.popup_tv.setText(list.get(i));
        return view;
    }

    public class ViewHolder {
        private TextView popup_tv;
    }
}