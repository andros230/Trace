package com.andros230.trace.activity;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andros230.trace.R;

import java.util.List;
import java.util.Map;


public class GroupListAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater mInflater;


    public GroupListAdapter(Context context, List<Map<String, Object>> data) {
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
            view = mInflater.inflate(R.layout.group_list_item, null);
            holder.name = (TextView) view.findViewById(R.id.groupList_item_groupName);
            holder.groupID = (TextView) view.findViewById(R.id.groupList_item_groupID);
            holder.num = (TextView) view.findViewById(R.id.groupList_item_num);
            holder.introdu = (TextView) view.findViewById(R.id.groupList_item_introdu);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText((String) data.get(i).get("groupName"));
        holder.groupID.setText((String) data.get(i).get("groupID"));
        holder.num.setText((String) data.get(i).get("num"));
        holder.introdu.setText((String) data.get(i).get("introdu"));
        return view;
    }

    public class ViewHolder {
        public TextView name;
        private TextView groupID;
        private TextView num;
        private TextView introdu;
    }

}