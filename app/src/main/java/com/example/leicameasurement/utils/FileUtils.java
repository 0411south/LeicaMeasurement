package com.example.leicameasurement.utils;

import java.io.File;

/**
 * 文件工具（创建目录/删除文件）
 */
public class FileUtils {

    /**
     * 创建目录
     * @param dirPath 目录路径
     * @return true=创建成功
     */
    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return true=删除成功
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}
