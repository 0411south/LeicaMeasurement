package com.example.leicameasurement.infrastructure;

import android.util.Log;

/**
 * 日志管理（统一日志输出）
 */
public class LogManager {

    private static final String TAG = "LeicaMeasurement";

    public static void i(String tag, String msg) {
        Log.i(TAG, "[" + tag + "] " + msg);
    }

    public static void d(String tag, String msg) {
        Log.d(TAG, "[" + tag + "] " + msg);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG, "[" + tag + "] " + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, "[" + tag + "] " + msg);
    }
}
