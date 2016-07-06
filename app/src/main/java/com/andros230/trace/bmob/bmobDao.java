package com.andros230.trace.bmob;

import android.util.Log;

import com.andros230.trace.bean.LatLngKit;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class bmobDao {
    private String TAG = "bmobDao";

    //保存数据
    public void save(LatLngKit kit) {
        kit.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "添加数据成功");
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }

}
