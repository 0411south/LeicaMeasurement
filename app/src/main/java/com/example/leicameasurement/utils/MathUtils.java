package com.example.leicameasurement.utils;

/**
 * 数学工具（角度转换/距离计算）
 */
public class MathUtils {

    /**
     * 角度转弧度
     * @param degree 角度
     * @return 弧度
     */
    public static double toRadians(double degree) {
        return Math.toRadians(degree);
    }

    /**
     * 弧度转角度
     * @param radians 弧度
     * @return 角度
     */
    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }
}
