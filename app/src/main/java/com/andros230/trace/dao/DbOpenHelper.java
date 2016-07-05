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
        String sql = "CREATE TABLE " + TABLE_NAME + " (id INTEGER primary key autoincrement, lat text, lng text, day text,time text);";
        sqLiteDatabase.execSQL(sql);
        Log.d(TAG, "创建数据库");
    }


    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i(TAG, "删除数据库");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        String sql = "CREATE TABLE " + TABLE_NAME + " (id INTEGER primary key autoincrement, lat text, lng text, day text,time text);";
        db.execSQL(sql);
        Log.i(TAG, "删除数据库后再新建");
    }


    public void insert(LatLngKit latLng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("lat", latLng.getLat());
        cv.put("lng", latLng.getLng());
        cv.put("day", util.getNowTime(false));
        cv.put("time", util.getNowTime(true));
        long row = db.insert(TABLE_NAME, null, cv);
        Log.d(TAG, "增加数据" + row);
    }

    //查询某天数据
    public Cursor query(String day) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from " + TABLE_NAME + " where day = '" + day + "' order by time";
        Cursor cur = db.rawQuery(sql, null);
        return cur;
    }


    //查询历史数据
    public Cursor queryHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select distinct day from " + TABLE_NAME;
        Cursor cur = db.rawQuery(sql, null);
        return cur;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
