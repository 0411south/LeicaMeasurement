package com.example.leicameasurement.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具（生成任务编号/日志命名）
 */
public class DateUtils {

    /**
     * 获取当前时间戳字符串
     * @return yyyyMMdd_HHmmss
     */
    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
