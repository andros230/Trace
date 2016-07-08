package com.andros230.trace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;

import java.util.HashMap;
import java.util.Map;

public class Splash extends Activity implements VolleyCallBack {
    private String TAG = "Splash";
    private String md5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Logs.OPENDEBUG = true;

        registerUser();

    }


    public void registerUser() {
        String uid = util.readUid(this);
        if (uid == null) {
            Logs.d(TAG, "新用户");
            md5 = util.createMD5(this);
            Map<String, String> params = new HashMap<>();
            params.put("md5", md5);
            new VolleyPost(this, this, util.ServerUrl + "newUser", params).post();
        } else {
            Logs.d(TAG, "已注册用户");
            Handler x = new Handler();
            x.postDelayed(new splashHandler(), 1000);
        }
    }

    @Override
    public void volleySolve(String result) {
        if (result != null) {
            Logs.d(TAG, "uid: " + result);
            util.writeUser(this, result, md5);

            Handler x = new Handler();
            x.postDelayed(new splashHandler(), 1000);
        } else {
            Toast.makeText(this, "网络异常,请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    class splashHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            Splash.this.finish();
        }
    }

}
