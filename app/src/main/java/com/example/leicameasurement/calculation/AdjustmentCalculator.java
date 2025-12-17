package com.example.leicameasurement.calculation;

/**
 * 平差计算（等权/加权平差）
 */
public class AdjustmentCalculator {

    /**
     * 角度平差
     * @param angles 观测角度
     * @param angleClosure 角度闭合差
     * @return 平差后的角度
     */
    public static double[] adjustAngles(double[] angles, double angleClosure) {
        double correction = -angleClosure / angles.length;
        double[] adjustedAngles = new double[angles.length];
        for (int i = 0; i < angles.length; i++) {
            adjustedAngles[i] = angles[i] + correction;
        }
        return adjustedAngles;
    }

    /**
     * 坐标平差
     * @param coordinates 观测坐标
     * @param coordinateClosure 坐标闭合差
     * @return 平差后的坐标
     */
    public static double[][] adjustCoordinates(double[][] coordinates, double[] coordinateClosure) {
        // This is a simplified example. A real implementation would be more complex.
        double[][] adjustedCoordinates = new double[coordinates.length][2];
        double totalLength = 0;
        for (int i = 0; i < coordinates.length - 1; i++) {
            totalLength += Math.sqrt(Math.pow(coordinates[i+1][0] - coordinates[i][0], 2) + Math.pow(coordinates[i+1][1] - coordinates[i][1], 2));
        }

        double currentLength = 0;
        for (int i = 1; i < coordinates.length; i++) {
            currentLength += Math.sqrt(Math.pow(coordinates[i][0] - coordinates[i-1][0], 2) + Math.pow(coordinates[i][1] - coordinates[i-1][1], 2));
            adjustedCoordinates[i][0] = coordinates[i][0] - coordinateClosure[0] * currentLength / totalLength;
            adjustedCoordinates[i][1] = coordinates[i][1] - coordinateClosure[1] * currentLength / totalLength;
        }

        return adjustedCoordinates;
    }
}
