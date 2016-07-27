package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.andros230.trace.R;
import com.andros230.trace.bean.Groups;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupList extends Activity {
    private ListView list;
    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        list = (ListView) findViewById(R.id.listView);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("groupID", data.get(i));
                intent.setClass(GroupList.this, MainActivity.class);
                startActivity(intent);
            }
        });


        Map<String, String> params = new HashMap<>();
        params.put("uid", util.readUid(this));
        new VolleyPost(this, util.ServerUrl + "GroupList", params, new VolleyCallBack() {
            @Override
            public void volleyResult(String result) {
                if (result != null) {
                    Gson gson = new Gson();
                    List<Groups> groups = gson.fromJson(result, new TypeToken<List<Groups>>() {
                    }.getType());
                    data = new ArrayList<>();
                    for (int i = 0; i < groups.size(); i++) {
                        Groups group = groups.get(i);
                        data.add(group.getGroupID() + "");
                    }
                    list.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, data));


                } else {
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void joinGroup(View view) {
        Intent intent = new Intent();
        intent.setClass(this, JoinGroup.class);
        startActivity(intent);
    }

    public void createGroup(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CreateGroup.class);
        startActivity(intent);
    }

}
