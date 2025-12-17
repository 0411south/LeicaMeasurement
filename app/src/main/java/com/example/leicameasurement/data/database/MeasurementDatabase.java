package com.example.leicameasurement.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.leicameasurement.data.dao.DetailPointDao;
import com.example.leicameasurement.data.dao.TraverseStationDao;
import com.example.leicameasurement.data.dao.TraverseTaskDao;
import com.example.leicameasurement.data.entity.DetailPoint;
import com.example.leicameasurement.data.entity.TraverseStation;
import com.example.leicameasurement.data.entity.TraverseTask;

/**
 * 数据库实例（单例）
 */
@Database(entities = {TraverseTask.class, TraverseStation.class, DetailPoint.class},
        version = 1, exportSchema = false)
public abstract class MeasurementDatabase extends RoomDatabase {

    public abstract TraverseTaskDao traverseTaskDao();

    public abstract TraverseStationDao traverseStationDao();

    public abstract DetailPointDao detailPointDao();
}
