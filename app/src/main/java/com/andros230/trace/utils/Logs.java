package com.andros230.trace.utils;

import android.util.Log;

public class Logs {
    public static boolean OPENDEBUG = false;

    public static void d(String TAG, String str) {
        if (OPENDEBUG) {
            Log.d("-= " + TAG, str);
        }
    }

    public static void e(String TAG, String str) {
        if (OPENDEBUG) {
            Log.e("-= " + TAG, str);
        }
    }

}
