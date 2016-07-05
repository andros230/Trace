package com.andros230.trace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class History_adapter extends BaseAdapter {
    private List<String> list;
    private LayoutInflater mInflater;

    public History_adapter(Context context, List<String> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
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
            view = mInflater.inflate(R.layout.history_listview_item, null);
            holder.day = (TextView) view.findViewById(R.id.history_item_day);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.day.setText(list.get(i));
        return view;
    }

    public class ViewHolder {
        private TextView day;
    }
}
