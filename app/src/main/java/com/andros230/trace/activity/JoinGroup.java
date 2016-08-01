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

public class JoinGroup extends Activity {
    private EditText groupNum, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        groupNum = (EditText) findViewById(R.id.groupNum);
        password = (EditText) findViewById(R.id.join_group_password);

    }

    public void joinGroup(View view) {
        String str_groupNum = groupNum.getText().toString();
        String str_password = password.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("uid", util.readUid(this));
        params.put("groupID", str_groupNum);
        params.put("password", str_password);
        new VolleyPost(this, util.ServerUrl + "JoinGroup", params, new VolleyCallBack() {
            @Override
            public void volleyResult(String result) {
                if (result != null) {
                    if (result.equals("YES")) {
                        Toast.makeText(getApplicationContext(), "加入群成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(JoinGroup.this, FragmentMain.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
