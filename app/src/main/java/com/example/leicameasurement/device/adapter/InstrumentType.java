package com.example.leicameasurement.device;

/**
 * 定义支持的仪器类型
 */
public enum InstrumentType {
    TS30,
    TS60,
    MS60;

    /**
     * 从字符串转换为枚举类型
     * @param model 仪器型号字符串（如 "TS30", "TS60"）
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果型号不支持
     */
    public static InstrumentType fromString(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("仪器型号不能为空");
        }

        String upperModel = model.trim().toUpperCase();

        try {
            return InstrumentType.valueOf(upperModel);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("不支持的仪器型号: " + model);
        }
    }
}
