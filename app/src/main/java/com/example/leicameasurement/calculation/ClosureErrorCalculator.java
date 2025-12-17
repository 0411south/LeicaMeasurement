package com.example.leicameasurement.calculation;

/**
 * 闭合差计算（角度/坐标闭合差）
 */
public class ClosureErrorCalculator {

    /**
     * 计算角度闭合差
     * @param theoreticalSum 理论角度和
     * @param measuredSum 实际测量角度和
     * @return 角度闭合差
     */
    public static double calculateAngleClosure(double theoreticalSum, double measuredSum) {
        return measuredSum - theoreticalSum;
    }

    /**
     * 计算坐标闭合差
     * @param startX 起点X
     * @param startY 起点Y
     * @param endX 终点X
     * @param endY 终点Y
     * @return 坐标闭合差
     */
    public static double[] calculateCoordinateClosure(double startX, double startY, double endX, double endY) {
        double deltaX = endX - startX;
        double deltaY = endY - startY;
        return new double[]{deltaX, deltaY};
    }
}
