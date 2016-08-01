package com.andros230.trace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.andros230.trace.R;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment1 extends Fragment {
    private ListView list;
    private List<Map<String, Object>> rs;
    private Button joinGroup, createGroup, exitGroup, exit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        list = (ListView) view.findViewById(R.id.listView);
        joinGroup = (Button) view.findViewById(R.id.joinGroup);
        createGroup = (Button) view.findViewById(R.id.createGroup);
        exitGroup = (Button) view.findViewById(R.id.exitGroup);
        exit = (Button) view.findViewById(R.id.exit);

        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), JoinGroup.class);
                startActivity(intent);
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), CreateGroup.class);
                startActivity(intent);
            }
        });

        exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> par = new HashMap<>();
                par.put("uid", "11");
                par.put("groupID", "4");
                new VolleyPost(getActivity(), util.ServerUrl + "ExitGroup", par, new VolleyCallBack() {
                    @Override
                    public void volleyResult(String result) {
                        if (result != null) {
                            if (result.equals("YES")) {
                                Toast.makeText(getActivity(), "成功退出群", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.Logout(getActivity());
                Intent intent = new Intent();
                intent.setClass(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("groupID", (String) rs.get(i).get("groupID"));
                intent.setClass(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });


        Map<String, String> params = new HashMap<>();
        params.put("uid", util.readUid(getActivity()));
        new VolleyPost(getActivity(), util.ServerUrl + "GroupList", params, new VolleyCallBack() {
            @Override
            public void volleyResult(String result) {
                if (result != null) {
                    Gson gson = new Gson();
                    rs = gson.fromJson(result, new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
                    GroupListAdapter adapter = new GroupListAdapter(getActivity(), rs);
                    list.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


}