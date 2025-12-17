package com.example.leicameasurement.infrastructure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池（区分通信/计算/存储线程）
 */
public class ThreadExecutor {

    private static final ExecutorService communicationExecutor = Executors.newSingleThreadExecutor();
    private static final ExecutorService calculationExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ExecutorService storageExecutor = Executors.newSingleThreadExecutor();
    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    public static ExecutorService getCommunicationExecutor() {
        return communicationExecutor;
    }

    public static ExecutorService getCalculationExecutor() {
        return calculationExecutor;
    }

    public static ExecutorService getStorageExecutor() {
        return storageExecutor;
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }
}
