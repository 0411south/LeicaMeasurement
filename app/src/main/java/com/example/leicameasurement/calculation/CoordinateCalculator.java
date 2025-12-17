package com.example.leicameasurement.calculation;

/**
 * 坐标计算（正算/反算）
 */
public class CoordinateCalculator {

    /**
     * 坐标正算
     * @param startX 起点X
     * @param startY 起点Y
     * @param distance 距离
     * @param azimuth 方位角
     * @return 终点坐标
     */
    public static double[] directCalculate(double startX, double startY, double distance, double azimuth) {
        double endX = startX + distance * Math.cos(azimuth);
        double endY = startY + distance * Math.sin(azimuth);
        return new double[]{endX, endY};
    }

    /**
     * 坐标反算
     * @param startX 起点X
     * @param startY 起点Y
     * @param endX 终点X
     * @param endY 终点Y
     * @return 距离和方位角
     */
    public static double[] inverseCalculate(double startX, double startY, double endX, double endY) {
        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double azimuth = Math.atan2(deltaY, deltaX);
        return new double[]{distance, azimuth};
    }
}
