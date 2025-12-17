package com.example.leicameasurement.utils;

/**
 * 字符串工具（指令格式化/校验）
 */
public class StringUtils {

    /**
     * 检查字符串是否为空
     * @param str 字符串
     * @return true=为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
