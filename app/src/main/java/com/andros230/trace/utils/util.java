package com.andros230.trace.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class util {

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

}
