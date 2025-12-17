// Converters.java (最终增强版)
package com.example.leicameasurement.data.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Converters {

    // --- Date 转换器 (已存在) ---
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // --- ✅ 新增：List<String> 转换器 ---
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<String> stringToStringList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String stringListToString(List<String> someObjects) {
        return gson.toJson(someObjects);
    }

    // --- ✅ 新增：List<Integer> 转换器 ---
    @TypeConverter
    public static List<Integer> stringToIntegerList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String integerListToString(List<Integer> someObjects) {
        return gson.toJson(someObjects);
    }

    // --- ✅ 新增：List<Double> 转换器 ---
    @TypeConverter
    public static List<Double> stringToDoubleList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Double>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String doubleListToString(List<Double> someObjects) {
        return gson.toJson(someObjects);
    }

    // 如果你有枚举类型，也需要添加转换器，例如：
    /*
    @TypeConverter
    public static String fromTaskStatus(TaskStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static TaskStatus toTaskStatus(String statusName) {
        return statusName == null ? null : TaskStatus.valueOf(statusName);
    }
    */
}
