package com.andros230.trace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.andros230.trace.bmob.BmobDao;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.utils.Logs;

import cn.bmob.v3.Bmob;


public class Splash extends Activity {
    private DbOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Bmob.initialize(this, "eeb802dacc8153d5f4679cbcff1a8daf");
        Logs.OPENDEBUG = true;
        db = new DbOpenHelper(this);
        //同步数据到服务器
        new BmobDao(db).insertBatch();
        Handler x = new Handler();
        x.postDelayed(new splashHandler(), 2000);
    }




    class splashHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            Splash.this.finish();
        }
    }
}
