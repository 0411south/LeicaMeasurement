package com.example.leicameasurement.ui.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.app.Application;

import com.example.leicameasurement.controller.DetailPointController;
import com.example.leicameasurement.device.adapter.InstrumentAdapter;
import com.example.leicameasurement.device.protocol.InstrumentException;

public class DetailPointViewModel extends AndroidViewModel {

    private final DetailPointController controller;
    private final MutableLiveData<String> measurementResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public DetailPointViewModel(Application application) {
        super(application);
        // 这里需要初始化controller，根据您的项目结构来
        this.controller = null; // 需要实际初始化
    }

    public void measureDetailPoint(double prismHeight, InstrumentAdapter.MeasureMode mode, String pointNumber) {
        isLoading.setValue(true);

        new Thread(() -> {
            try {
                // 这里调用controller的测量方法
                // DetailPoint result = controller.measureDetailPoint(prismHeight, mode, pointNumber);
                // measurementResult.postValue("测量成功: " + result.toString());

                // 暂时模拟成功
                Thread.sleep(1000);
                measurementResult.postValue("测量成功 - 点号: " + pointNumber);

            } catch (Exception e) {
                errorMessage.postValue("测量失败: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        }).start();
    }

    public LiveData<String> getMeasurementResult() {
        return measurementResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}