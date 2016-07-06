package com.andros230.trace.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.andros230.trace.bean.LatLngKit;
import com.andros230.trace.utils.util;

public class DbOpenHelper extends SQLiteOpenHelper {
    private String TAG = "DbOpenHelper";
    private static final String TABLE_NAME = "trace";


    public DbOpenHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (id INTEGER primary key autoincrement, lat text, lng text, date text,time text, status text);";
        sqLiteDatabase.execSQL(sql);
        Log.d(TAG, "创建数据库");
    }


    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i(TAG, "删除数据库");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        String sql = "CREATE TABLE " + TABLE_NAME + " (id INTEGER primary key autoincrement, lat text, lng text, date text,time text, status text);";
        db.execSQL(sql);
        Log.i(TAG, "删除数据库后再新建");
    }


    public void insert(LatLngKit kit, boolean bool) {
        if (bool) {
            kit.setStatus("Y");
        } else {
            kit.setStatus("N");
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("lat", kit.getLat());
        cv.put("lng", kit.getLng());
        cv.put("date", kit.getDate());
        cv.put("time", kit.getTime());
        cv.put("status", kit.getStatus());
        long row = db.insert(TABLE_NAME, null, cv);
        Log.d(TAG, "增加数据" + row);
    }

    //查询某天数据
    public Cursor query(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from " + TABLE_NAME + " where date = '" + date + "' order by time";
        Cursor cur = db.rawQuery(sql, null);
        return cur;
    }


    //查询历史数据
    public Cursor queryHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select distinct date from " + TABLE_NAME + " order by time";
        Cursor cur = db.rawQuery(sql, null);
        return cur;
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
        int a = db.update(TABLE_NAME, cv, "status = ?", new String[]{"N"});
        Log.e("changeStatus", a + "");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
