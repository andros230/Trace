package com.andros230.trace.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class util {

    public static final String ServerUrl = "http://192.168.18.105:8080/Trace/";
    private static final String NAME_UID = "com_andros230_UID";
    private static final String NAME_MD5 = "com_andros230_MD5";
    private static String TAG = "util";

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


    public static String createMD5(Context context) {
        String aa;
        String time = util.getNowTime(false) + " " + util.getNowTime(true);
        String mac = util.getMac(context);
        if (mac != null) {
            aa = MD5(mac + time);
        } else {
            aa = MD5((int) (Math.random() * 999999) + time);
        }
        writeMD5(context, aa);
        return aa;
    }

    //保存MD5
    private static void writeMD5(Context context, String uid) {
        SharedPreferences pref = context.getSharedPreferences(NAME_MD5, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("md5", uid);
        editor.commit();
    }

    // MD5加密，16位
    private static String MD5(String str) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }


    //保存UID
    public static void writeUid(Context context, String uid) {
        SharedPreferences pref = context.getSharedPreferences(NAME_UID, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uid", uid);
        editor.commit();
    }

    //读取UID
    public static String readUid(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_UID, Context.MODE_APPEND);
        String name = pref.getString("uid", "");
        if (name.equals("")) {
            return null;
        } else {
            return name;
        }
    }

    //清除UID
    public static void clearUid(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_UID, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }




    //读取MD5
    public static String readMD5(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_MD5, Context.MODE_APPEND);
        String name = pref.getString("md5", "");
        if (name.equals("")) {
            return null;
        } else {
            return name;
        }
    }

    //清除MD5
    public static void clearMD5(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_MD5, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public static void Logout(Context context) {
        util.clearUid(context);
        util.clearMD5(context);
    }

    //获取Mac
    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    //检测网络是否可用
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    //Gps是否打开
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //检查是否有GPS权限
    public static boolean gpsPermission(Context context) {
        boolean permission = (PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION"));
        return permission;
    }


}
