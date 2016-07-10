package com.andros230.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.andros230.trace.R;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;

public class Splash extends Activity {
    private String TAG = "Splash";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Logs.OPENDEBUG = true;

        Handler x = new Handler();
        x.postDelayed(new splashHandler(), 1000);
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
