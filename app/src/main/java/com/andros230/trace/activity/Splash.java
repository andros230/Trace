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

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

public class Splash extends Activity {
    private String TAG = "Splash";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Logs.OPENDEBUG = true;

        bmobUpdate();


    }


    public void bmobUpdate() {
        Bmob.initialize(this, "eeb802dacc8153d5f4679cbcff1a8daf");
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                if (i == UpdateStatus.No || i == UpdateStatus.IGNORED) {
                    openIDCheck();
                    Logs.d(TAG, "无需更新版本或被忽略更新");
                } else {
                    Logs.d(TAG, "有新版本可用");
                }

            }
        });
        BmobUpdateAgent.update(this);

        //监听对话框按键操作
        BmobUpdateAgent.setDialogListener(new BmobDialogButtonListener() {
            @Override
            public void onClick(int i) {
                if (i == UpdateStatus.NotNow) {
                    Logs.d(TAG, "点击了以后再说");
                    openIDCheck();
                }
            }
        });

    }


    public void openIDCheck() {
        String openID = util.readOpenID(this);
        if (openID != null) {

            Map<String, String> params = new HashMap<>();
            params.put("openID", openID);
            String md5 = util.readMD5(this);
            params.put("md5", md5);
            new VolleyPost(this, util.ServerUrl + "UserCheck", params, new VolleyCallBack() {
                @Override
                public void volleyResult(String result) {
                    if (result != null) {
                        if (result.equals("NO")) {
                            Logs.d(TAG, "帐号最后登录设备不是本设备，需重新登录");
                            util.Logout(getApplicationContext());
                        }
                        Handler x = new Handler();
                        x.postDelayed(new splashHandler(), 100);
                    } else {
                        Toast.makeText(getApplicationContext(), "网络异常,请检查网络", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Handler x = new Handler();
            x.postDelayed(new splashHandler(), 100);
        }
    }

    class splashHandler implements Runnable {
        public void run() {
            String openID = util.readOpenID(getApplicationContext());
            if (openID == null) {
                Logs.d(TAG, "openID is null");
                startActivity(new Intent(getApplication(), Login.class));
                finish();
            } else {
                Logs.d(TAG, "openID:" + openID);
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();
            }
        }
    }


}
