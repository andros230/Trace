package com.andros230.trace.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.andros230.trace.R;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends Activity implements VolleyCallBack {
    private EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        content = (EditText) findViewById(R.id.feedback_content);

    }

    public void send(View view) {
        String str = content.getText().toString();
        Logs.d("---", str);
        if (str.length() < 2) {
            Toast.makeText(Feedback.this, "请输入内容", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("content", str);
            String uid = util.readUid(this);
            params.put("uid", uid);
            new VolleyPost(this, this, util.ServerUrl + "Feedback", params).post();
        }


    }

    @Override
    public void volleySolve(String result) {
        if (result != null) {
            Toast.makeText(Feedback.this, "发送成功，感谢您的意见", Toast.LENGTH_LONG).show();
            content.setText("");
        } else {
            Toast.makeText(Feedback.this, "发送失败，请重新发送", Toast.LENGTH_LONG).show();
        }

    }
}
