package com.example.leicameasurement.calculation;

/**
 * 误差校验（测量值超限检测）
 */
public class ErrorChecker {

    // 角度偏差阈值（单位：弧度）
    private static final double ANGLE_TOLERANCE = Math.toRadians(5.0 / 3600.0); // 5秒
    // 距离往返差阈值（比例）
    private static final double DISTANCE_TOLERANCE_RATIO = 1.0 / 2000.0;

    /**
     * 校验角度偏差是否超限
     * @param angle1 角度1
     * @param angle2 角度2
     * @return true=超限
     */
    public static boolean isAngleDifferenceExceeded(double angle1, double angle2) {
        return Math.abs(angle1 - angle2) > ANGLE_TOLERANCE;
    }

    /**
     * 校验距离往返差是否超限
     * @param distance1 距离1
     * @param distance2 距离2
     * @return true=超限
     */
    public static boolean isDistanceDifferenceExceeded(double distance1, double distance2) {
        double averageDistance = (distance1 + distance2) / 2.0;
        return Math.abs(distance1 - distance2) / averageDistance > DISTANCE_TOLERANCE_RATIO;
    }
}
