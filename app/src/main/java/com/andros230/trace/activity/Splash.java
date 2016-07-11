package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.andros230.trace.R;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;

import java.util.HashMap;
import java.util.Map;

public class Splash extends Activity implements VolleyCallBack {
    private String TAG = "Splash";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Logs.OPENDEBUG = true;


        String openID = util.readOpenID(this);
        if (openID != null) {
            Map<String, String> params = new HashMap<>();
            params.put("openID", openID);
            String md5 = util.readMD5(this);
            params.put("md5", md5);
            new VolleyPost(Splash.this, Splash.this, util.ServerUrl + "UserCheck", params).post();
        }else{
            Handler x = new Handler();
            x.postDelayed(new splashHandler(), 100);
        }
    }

    @Override
    public void volleySolve(String result) {
        if (result != null) {
            if (result.equals("NO")) {
                Logs.d(TAG, "帐号最后登录设备不是本设备，需重新登录");
                util.Logout(this);
            }
            Handler x = new Handler();
            x.postDelayed(new splashHandler(), 100);
        } else {
            Toast.makeText(this, "网络异常,请检查网络", Toast.LENGTH_LONG).show();
        }
    }


    class splashHandler implements Runnable {
        public void run() {
            String openID = util.readOpenID(getApplicationContext());
            if (openID == null) {
                Logs.d(TAG, "openID is null");
                startActivity(new Intent(getApplication(), Login.class));
                Splash.this.finish();
            } else {
                Logs.d(TAG, "openID:" + openID);
                startActivity(new Intent(getApplication(), MainActivity.class));
                Splash.this.finish();
            }
        }
    }

}
