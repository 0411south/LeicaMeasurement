package com.example.leicameasurement.data.wal;

import android.os.Environment;
import com.example.leicameasurement.infrastructure.LogManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
* WAL日志写入器（极简版）：实时保存仪器原始数据，确保不丢失
* 核心职责：追加写入原始数据（时间戳+指令+响应）
*/
public class WalFileWriter {

    private static final String TAG = "WalFileWriter";
    private static final String WAL_DIR = "totalstation_wal"; // 日志目录
    private static final String WAL_FILE_NAME = "raw_data.log"; // 日志文件名

    private FileWriter mFileWriter;
    private File mWalFile;

    public WalFileWriter() {
        initWalFile();
    }

    /**
    * 初始化WAL文件（内部存储/外部存储）
    */
    private void initWalFile() {
        try {
            // 1. 创建日志目录（外部存储，便于调试）
            File walDir = new File(Environment.getExternalStorageDirectory(), WAL_DIR);
            if (!walDir.exists()) {
                if (walDir.mkdirs()) {
                    LogManager.i(TAG, "WAL目录创建成功：" + walDir.getAbsolutePath());
                } else {
                    LogManager.e(TAG, "WAL目录创建失败");
                    return;
                }
            }
            // 2. 创建日志文件（追加模式）
            mWalFile = new File(walDir, WAL_FILE_NAME);
            mFileWriter = new FileWriter(mWalFile, true);
            LogManager.i(TAG, "WAL文件初始化成功：" + mWalFile.getAbsolutePath());
        } catch (IOException e) {
            LogManager.e(TAG, "WAL文件初始化失败：" + e.getMessage());
        }
    }

    /**
    * 写入原始数据到WAL日志
    * @param command 发送的指令
    * @param response 接收的响应
    */
    public void writeRawData(String command, String response) {
        if (mFileWriter == null) {
            LogManager.w(TAG, "WAL写入失败：文件写入器为空");
            return;
        }
        try {
            // 日志格式：时间戳|指令|响应（便于后期解析）
            String logLine = System.currentTimeMillis() + "|" +
                    command.trim() + "|" +
                    (response == null ? "NULL" : response.trim()) + "\n";
            mFileWriter.write(logLine);
            mFileWriter.flush(); // 强制写入磁盘（避免内存缓存丢失）
            LogManager.d(TAG, "WAL写入成功：" + logLine.trim());
        } catch (IOException e) {
            LogManager.e(TAG, "WAL写入失败：" + e.getMessage());
        }
    }

    /**
    * 关闭写入器
    */
    public void close() {
        if (mFileWriter != null) {
            try {
                mFileWriter.close();
                LogManager.i(TAG, "WAL写入器已关闭");
            } catch (IOException e) {
                LogManager.e(TAG, "WAL写入器关闭失败：" + e.getMessage());
            }
        }
    }

    /**
    * 获取WAL文件路径（用于调试/导出）
    * @return 文件路径
    */
    public String getWalFilePath() {
        return mWalFile != null ? mWalFile.getAbsolutePath() : null;
    }
}