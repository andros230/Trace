package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.andros230.trace.R;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.util;

import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends Activity {
    private EditText groupName, password, introdu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupName = (EditText) findViewById(R.id.groupName);
        password = (EditText) findViewById(R.id.password);
        introdu = (EditText) findViewById(R.id.introdu);


    }

    public void createGroup(View view) {
        String str_groupName = groupName.getText().toString();
        String str_password = password.getText().toString();
        String str_introdu = introdu.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("groupName", str_groupName);
        params.put("uid", util.readUid(this));
        params.put("password", str_password);
        params.put("introdu", str_introdu);
        new VolleyPost(this, util.ServerUrl + "CreateGroup", params, new VolleyCallBack() {
            @Override
            public void volleyResult(String result) {
                if (result != null) {
                    if (result.equals("YES")) {
                        Toast.makeText(getApplicationContext(), "创建群成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(CreateGroup.this,FragmentMain.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
