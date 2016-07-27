package com.andros230.trace.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
        if (util.isNetworkConnected(this)) {
            bmobUpdate();
        } else {
            networkDialog();
        }
    }


    protected void networkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("当前网络不可用,请检查网络");
        builder.setTitle("网络异常");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Splash.this.finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    public void bmobUpdate() {
        Bmob.initialize(this, "eeb802dacc8153d5f4679cbcff1a8daf");
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                if (i == UpdateStatus.No || i == UpdateStatus.IGNORED) {
                    md5Check();
                    Logs.d(TAG, "无需更新版本或被忽略更新");
                } else {
                    Logs.d(TAG, "有新版本可用");
                }

            }
        });


        //监听对话框按键操作
        BmobUpdateAgent.setDialogListener(new BmobDialogButtonListener() {
            @Override
            public void onClick(int i) {
                if (i == UpdateStatus.NotNow) {
                    Logs.d(TAG, "点击了以后再说");
                    md5Check();
                }
            }
        });

    }


    public void md5Check() {
        String uid = util.readUid(this);
        if (uid != null) {
            Map<String, String> params = new HashMap<>();
            params.put("uid", uid);
            params.put("md5", util.readMD5(this));
            Logs.d(TAG, util.readMD5(this));
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
            String uid = util.readUid(getApplicationContext());
            Logs.d(TAG, uid);
            if (uid == null) {
                Logs.d(TAG, "uid is null");
                startActivity(new Intent(getApplication(), Login.class));
                finish();
            } else {
                Logs.d(TAG, "uid:" + uid);
                startActivity(new Intent(getApplication(), GroupList.class));
                finish();
            }
        }
    }


}
