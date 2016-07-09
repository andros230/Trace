package com.andros230.trace.utils;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.List;

public class util {
    public static final String ServerUrl = "http://192.168.18.105:8080/Trace/";
    private static final String NAME_UID = "com_andros230_UID";
    private static final String NAME_OPENID = "com_andros230_OPENID";
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

    public static boolean compareTime(String time, String time2) {
        if (time == null) {
            return true;
        }
        int min = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");    //设置时间格式
            Date d1 = sdf.parse(time2);
            Date d2 = sdf.parse(time);
            long l = d1.getTime() - d2.getTime();
            min = (int) ((l / (60 * 1000)));//分
            Logs.d(TAG, "compareTime:" + min + " time:" + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (min > 10) {
            return false;
        } else {
            return true;
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
        return aa;
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

    //获取openid
    public static void writeOpenID(Context context, String openID) {
        SharedPreferences pref = context.getSharedPreferences(NAME_OPENID, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("openID", openID);
        editor.commit();
    }

    //读取openID
    public static String readOpenID(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_OPENID, Context.MODE_APPEND);
        String name = pref.getString("openID", "");
        if (name.equals("")) {
            return null;
        } else {
            return name;
        }
    }
}
