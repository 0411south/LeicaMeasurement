package com.example.leicameasurement.calculation;

/**
 * 碎步点计算（相对坐标→绝对坐标）
 */
public class DetailPointCalculator {

    /**
     * 计算碎步点绝对坐标
     * @param stationX 测站X
     * @param stationY 测站Y
     * @param stationH 测站H
     * @param horizontalAngle 水平角
     * @param verticalAngle 垂直角
     * @param slopeDistance 斜距
     * @return 绝对坐标
     */
    public static double[] calculateAbsoluteCoordinates(double stationX, double stationY, double stationH, double horizontalAngle, double verticalAngle, double slopeDistance) {
        double horizontalDistance = slopeDistance * Math.sin(verticalAngle);
        double heightDifference = slopeDistance * Math.cos(verticalAngle);

        double pointX = stationX + horizontalDistance * Math.cos(horizontalAngle);
        double pointY = stationY + horizontalDistance * Math.sin(horizontalAngle);
        double pointH = stationH + heightDifference;

        return new double[]{pointX, pointY, pointH};
    }
}
