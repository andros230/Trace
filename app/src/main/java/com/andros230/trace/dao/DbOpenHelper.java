package com.andros230.trace.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.network.VolleyCallBack;
import com.andros230.trace.network.VolleyPost;
import com.andros230.trace.utils.Logs;
import com.andros230.trace.utils.util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;

public class DbOpenHelper extends SQLiteOpenHelper implements VolleyCallBack {
    private String TAG = "DbOpenHelper";
    private static final String TABLE_NAME = "trace";
    private final String table_sql = "CREATE TABLE " + TABLE_NAME + " (id INTEGER primary key autoincrement, lat text, lng text, date text,time text, status text);";
    private LatLngKit kit;
    private Context context;


    public DbOpenHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(table_sql);
        Logs.d(TAG, "创建数据库");
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i(TAG, "删除数据库");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        db.execSQL(table_sql);
        Log.i(TAG, "删除数据库后再新建");
    }

    //查询某天数据
    public Cursor query() {
        String date = util.getNowTime(false);
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from " + TABLE_NAME + " where date = '" + date + "' order by time";
        Cursor cur = db.rawQuery(sql, null);
        return cur;
    }


    public void saveLatLng(LatLngKit kit) {
        this.kit = kit;
        String uid = util.readUid(context);
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("lat", kit.getLat());
        params.put("lng", kit.getLng());
        params.put("date", kit.getDate());
        params.put("time", kit.getTime());
        new VolleyPost(context, this, util.ServerUrl + "RealTimeSaveLatLng", params).post();
    }

    @Override
    public void volleySolve(String result) {
        if (result != null) {
            if (result.equals("YES")) {
                Logs.d(TAG, "实时坐标数据上传成功");
            } else {
                Logs.d(TAG, "实时坐标数据上传失败");
                insert(kit);
            }
        } else {
            Logs.e(TAG, "网络异常,实时坐标数据上传失败");
            insert(kit);
        }
    }

    private void insert(LatLngKit kit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("lat", kit.getLat());
        cv.put("lng", kit.getLng());
        cv.put("date", kit.getDate());
        cv.put("time", kit.getTime());
        cv.put("status", "N");
        long row = db.insert(TABLE_NAME, null, cv);
        Logs.d(TAG, "数据保存到本地" + row);
    }


    //查询未上传的数据
    public Cursor updateDataToServer() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from " + TABLE_NAME + " where status = 'N'";
        Cursor cur = db.rawQuery(sql, null);
        return cur;
    }

    //更改状态
    public void changeStatus() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", "Y");
        int rs = db.update(TABLE_NAME, cv, "status = ?", new String[]{"N"});
        Logs.d("changeStatus", "已上传" + rs + "条数据");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}
