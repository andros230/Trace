package com.andros230.trace.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class util {
    private static final String PREFERENCES_NAME = "com_andros230_tableName";
    public static String macClassName;

    public static String getNowTime(boolean bool) {
        try {
            SimpleDateFormat sdf;
            if (bool) {
                sdf = new SimpleDateFormat("HH:mm:ss");    //设置时间格式
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd");    //设置时间格式
            }
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = sdf.format(curDate);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean compareTime(String time, String time2) {
        if (time == null) {
            return true;
        }
        int min = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    //设置时间格式
            Date d1 = sdf.parse(time2);
            Date d2 = sdf.parse(time);
            long l = d1.getTime() - d2.getTime();
            min = (int) ((l / (60 * 1000)));//分
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (min > 10) {
            return false;
        } else {
            return true;

        }
    }


    //获取Mac
    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * Gps是否打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = locationManager.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }


    public static void writeTableName(Context context, String tableName) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("tableName", tableName);
        editor.commit();
    }


    //读取数据库表名
    public static String readTableName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String name = pref.getString("tableName", "");
        return name;
    }
}
