package com.andros230.trace.bmob;

import android.database.Cursor;

import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.dao.DbOpenHelper;
import com.andros230.trace.utils.Logs;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;

public class BmobDao {
    private String TAG = "bmobDao";
    private DbOpenHelper db;

    public BmobDao(DbOpenHelper db) {
        this.db = db;
    }


    //保存数据
    public void save(final LatLngKit kit) {
        kit.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Logs.d(TAG, "坐标成功保存到服务器");
                    db.insert(kit, true);
                } else {
                    db.insert(kit, false);
                    Logs.e(TAG, "坐标保存到服务器失败 " + e.getMessage());
                }
            }
        });
    }


    //同步数据到服务器
    public void insertBatch() {
        Cursor cur = db.updateDataToServer();
        List<BmobObject> kits = new ArrayList<>();
        while (cur.moveToNext()) {

            String lat = cur.getString(1);
            String lng = cur.getString(2);
            String date = cur.getString(3);
            String time = cur.getString(4);

            LatLngKit kit = new LatLngKit();
            kit.setLat(lat);
            kit.setLng(lng);
            kit.setDate(date);
            kit.setTime(time);
            kits.add(kit);
        }
        if (kits.size() != 0) {
            new BmobBatch().insertBatch(kits).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        Logs.d(TAG, "批量保存成功");
                        db.changeStatus();
                    } else {
                        Logs.e(TAG, "批量保存失败");
                    }
                }
            });
        }else{
            Logs.d(TAG, "无需同步数据");
        }
    }
}
