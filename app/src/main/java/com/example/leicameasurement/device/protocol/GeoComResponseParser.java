package com.example.leicameasurement.device.protocol;

import com.example.leicameasurement.device.adapter.InstrumentInfo;
import com.example.leicameasurement.device.adapter.InstrumentStatus;
import com.example.leicameasurement.infrastructure.LogManager;

/**
 * GeoCOM响应解析器：负责将仪器返回的原始字节流转换为结构化的数据对象或状态
 * 核心职责：解析成功/失败响应、解析测量数据、解析仪器信息
 */
public class GeoComResponseParser {

    private static final String TAG = "GeoComResponseParser";

    /**
     * 解析设置测站响应
     * @param response 仪器响应字节数组
     * @return true=成功
     */
    public boolean parseSetStationResponse(byte[] response) {
        String responseStr = bytesToString(response);
        return isCommandSuccess(responseStr);
    }

    /**
     * 解析简单操作响应
     * @param response 仪器响应字节数组
     * @return true=成功
     */
    public boolean parseSimpleResponse(byte[] response) {
        String responseStr = bytesToString(response);
        return isCommandSuccess(responseStr);
    }

    /**
     * 解析仪器型号响应
     * @param response 仪器响应字节数组
     * @return 仪器型号字符串
     */
    public String parseModelResponse(byte[] response) {
        String responseStr = bytesToString(response);
        if (isCommandSuccess(responseStr)) {
            try {
                // 格式：%R1P,0,0:TS60
                String model = responseStr.substring(responseStr.lastIndexOf(":") + 1).trim();
                LogManager.i(TAG, "解析仪器型号成功：" + model);
                return model;
            } catch (Exception e) {
                LogManager.e(TAG, "解析仪器型号失败：" + e.getMessage());
            }
        }
        return "UNKNOWN";
    }

    /**
     * 解析序列号响应
     * @param response 仪器响应字节数组
     * @return 序列号字符串
     */
    public String parseSerialNumberResponse(byte[] response) {
        String responseStr = bytesToString(response);
        if (isCommandSuccess(responseStr)) {
            try {
                // 格式：%R1P,0,0:123456789
                String serial = responseStr.substring(responseStr.lastIndexOf(":") + 1).trim();
                LogManager.i(TAG, "解析序列号成功：" + serial);
                return serial;
            } catch (Exception e) {
                LogManager.e(TAG, "解析序列号失败：" + e.getMessage());
            }
        }
        return "UNKNOWN";
    }

    /**
     * 解析仪器状态响应
     * @param response 仪器响应字节数组
     * @return 仪器状态对象
     */
    public InstrumentStatus parseStatusResponse(byte[] response) {
        String responseStr = bytesToString(response);

        // 简化实现，实际应根据仪器返回的状态码进行解析
        boolean isBatteryOk = true;
        boolean isLocked = false;
        boolean isErrorState = false;
        String errorMessage = "";

        if (!isCommandSuccess(responseStr)) {
            isErrorState = true;
            errorMessage = "仪器返回错误状态";
        }

        // 这里可以添加更详细的状态解析逻辑
        // 例如解析电池状态、锁定状态等

        return new InstrumentStatus(isBatteryOk, isLocked, isErrorState, errorMessage);
    }

    /**
     * 解析测量响应数据
     * @param response 测量响应字节数组
     * @return double数组：[水平角(rad), 垂直角(rad), 斜距(m), X坐标, Y坐标, Z坐标]
     */
    public double[] parseMeasurementResponse(byte[] response) {
        String responseStr = bytesToString(response);
        if (responseStr == null || !responseStr.contains(",")) {
            LogManager.e(TAG, "无效的测量数据响应：" + responseStr);
            return null;
        }

        try {
            // 示例响应格式：%R1P,0,0:水平角,垂直角,斜距,X,Y,Z
            String dataPart = responseStr.substring(responseStr.indexOf(":") + 1);
            String[] parts = dataPart.split(",");

            if (parts.length >= 6) {
                double horizontalAngle = Double.parseDouble(parts[0]);  // 水平角 (rad)
                double verticalAngle = Double.parseDouble(parts[1]);    // 垂直角 (rad)
                double slopeDistance = Double.parseDouble(parts[2]);    // 斜距 (m)
                double x = Double.parseDouble(parts[3]);                // X坐标
                double y = Double.parseDouble(parts[4]);                // Y坐标
                double z = Double.parseDouble(parts[5]);                // Z坐标

                LogManager.d(TAG, String.format("解析测量数据成功：Hz=%.6frad, V=%.6frad, SD=%.4fm, X=%.3f, Y=%.3f, Z=%.3f",
                        horizontalAngle, verticalAngle, slopeDistance, x, y, z));

                return new double[]{horizontalAngle, verticalAngle, slopeDistance, x, y, z};
            } else if (parts.length >= 3) {
                // 兼容只有角度和距离的响应
                double horizontalAngle = Double.parseDouble(parts[0]);
                double verticalAngle = Double.parseDouble(parts[1]);
                double slopeDistance = Double.parseDouble(parts[2]);

                LogManager.d(TAG, String.format("解析基础测量数据成功：Hz=%.6frad, V=%.6frad, SD=%.4fm",
                        horizontalAngle, verticalAngle, slopeDistance));

                return new double[]{horizontalAngle, verticalAngle, slopeDistance, 0, 0, 0};
            }
        } catch (Exception e) {
            LogManager.e(TAG, "解析测量数据失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * 解析电池状态响应
     * @param response 电池状态响应字节数组
     * @return 电池电量百分比 (0-100)，-1表示解析失败
     */
    public int parseBatteryStatusResponse(byte[] response) {
        String responseStr = bytesToString(response);
        if (isCommandSuccess(responseStr)) {
            try {
                // 格式：%R1P,0,0:85 (电量百分比)
                String batteryStr = responseStr.substring(responseStr.lastIndexOf(":") + 1).trim();
                int batteryLevel = Integer.parseInt(batteryStr);
                LogManager.i(TAG, "解析电池状态成功：" + batteryLevel + "%");
                return Math.max(0, Math.min(100, batteryLevel)); // 确保在0-100范围内
            } catch (Exception e) {
                LogManager.e(TAG, "解析电池状态失败：" + e.getMessage());
            }
        }
        return -1;
    }

    /**
     * 判断指令是否执行成功
     * @param response 仪器响应字符串
     * @return true=成功
     */
    public boolean isCommandSuccess(String response) {
        if (response == null || response.isEmpty()) {
            LogManager.w(TAG, "响应为空，判断为失败");
            return false;
        }

        // 成功响应格式：%R1P,0,0: 或类似的成功码
        if (response.startsWith("%R1P,0,0")) {
            LogManager.i(TAG, "指令执行成功");
            return true;
        }

        // 检查是否为错误码
        try {
            // 提取错误码，格式可能是：%R1P,1,错误码: 或其他格式
            if (response.startsWith("%R1P,1,")) {
                String errorPart = response.substring(7); // 跳过"%R1P,1,"
                int colonIndex = errorPart.indexOf(":");
                if (colonIndex > 0) {
                    String errorCodeStr = errorPart.substring(0, colonIndex);
                    int errorCode = Integer.parseInt(errorCodeStr.trim());
                    String errorMsg = ErrorCodes.getMessageByCode(errorCode);
                    LogManager.e(TAG, "指令执行失败，错误码：" + errorCode + "，信息：" + errorMsg);
                }
            } else {
                // 尝试直接解析数字错误码
                int errorCode = Integer.parseInt(response.trim());
                String errorMsg = ErrorCodes.getMessageByCode(errorCode);
                LogManager.e(TAG, "指令执行失败，错误码：" + errorCode + "，信息：" + errorMsg);
            }
        } catch (NumberFormatException e) {
            LogManager.e(TAG, "无法解析的失败响应：" + response);
        }
        return false;
    }

    /**
     * 解析测量数据（角度和距离）- 保持向后兼容
     * @param response 测量响应字符串
     * @return double数组：[水平角(rad), 垂直角(rad), 斜距(m)]，null=解析失败
     */
    public double[] parseMeasureData(String response) {
        if (response == null || !response.contains(",")) {
            LogManager.e(TAG, "无效的测量数据响应：" + response);
            return null;
        }
        try {
            // 示例响应格式：%R1P,0,0:水平角,垂直角,斜距
            String[] parts = response.substring(response.indexOf(":") + 1).split(",");
            if (parts.length >= 3) {
                double horizontalAngle = Double.parseDouble(parts[0]);
                double verticalAngle = Double.parseDouble(parts[1]);
                double slopeDistance = Double.parseDouble(parts[2]);
                LogManager.d(TAG, String.format("解析测量数据成功：Hz=%.6f, V=%.6f, SD=%.4f",
                        horizontalAngle, verticalAngle, slopeDistance));
                return new double[]{horizontalAngle, verticalAngle, slopeDistance};
            }
        } catch (Exception e) {
            LogManager.e(TAG, "解析测量数据失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * 字节数组转字符串
     * @param bytes 字节数组
     * @return 字符串，如果转换失败返回null
     */
    private String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return new String(bytes).trim();
        } catch (Exception e) {
            LogManager.e(TAG, "字节数组转字符串失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 检查响应是否包含有效数据
     * @param response 仪器响应字节数组
     * @return true=包含有效数据
     */
    public boolean hasValidData(byte[] response) {
        if (response == null || response.length == 0) {
            return false;
        }
        String responseStr = bytesToString(response);
        return responseStr != null && !responseStr.isEmpty() &&
                (responseStr.startsWith("%R1P") || responseStr.startsWith("*IDN"));
    }
}