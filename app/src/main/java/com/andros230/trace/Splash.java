package com.andros230.trace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.andros230.trace.bmob.BmobDao;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;

import cn.bmob.v3.Bmob;


public class Splash extends Activity {
    private DbOpenHelper db;
    private String TAG = "Splash";
    private BmobDao bmobDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Logs.OPENDEBUG = true;
        createTableName();
        Bmob.initialize(this, "eeb802dacc8153d5f4679cbcff1a8daf");

        db = new DbOpenHelper(this);
        bmobDao = new BmobDao(db);
        //同步数据到服务器
        bmobDao.updateData();

        bmobDao.downData(1);

        Handler x = new Handler();
        x.postDelayed(new splashHandler(), 2000);

    }


    class splashHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            Splash.this.finish();
        }
    }


    public void createTableName() {
        String table = util.readTableName(this);
        if (table == null || table.equals("")) {
            String mac = util.getMac(this);
            String tableName;
            if (mac != null) {
                if (mac.replaceAll(":", "").length() == 12) {
                    String time = util.getNowTime(true).replaceAll(":", "");
                    tableName = mac.replaceAll(":", "") + time + (int) (Math.random() * 9);
                } else {
                    tableName = timeTableName();
                }
            } else {
                tableName = timeTableName();
            }
            util.writeTableName(this, "z" + tableName);
            Logs.d(TAG, "创建服务器数据库的表名: " + "z" + tableName);
        } else {
            Logs.d(TAG, "数据库表已存在: " + table);
        }
        util.macClassName = util.readTableName(this);
    }


    public String timeTableName() {
        String date = util.getNowTime(false).replaceAll("-", "");
        String time = util.getNowTime(true).replaceAll(":", "");
        return date + time + (int) (Math.random() * 9999);
    }

}
