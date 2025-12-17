// 文件路径: com/example/leicameasurement/data/dao/MeasurementDao.java
package com.example.leicameasurement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.leicameasurement.data.entity.MeasurementPoint;

import java.util.List;

/**
 * MeasurementPoint的数据访问对象 (DAO)
 */
@Dao
public interface MeasurementDao {

    /**
     * 插入一个新的测量点
     * @param point 要插入的点
     */
    @Insert
    void insert(MeasurementPoint point);

    /**
     * 根据测站ID查询该测站下的所有测量点
     * @param stationId 测站的ID
     * @return 返回一个包含测量点列表的LiveData，可以被UI观察
     *
     * ✅✅✅ [已修复] 将查询条件中的 "station_id" 修改为 "station_id_fk"，
     * 以匹配 MeasurementPoint 实体类中 @ColumnInfo(name = "station_id_fk") 定义的列名。
     */
    @Query("SELECT * FROM measurement_points WHERE station_id_fk = :stationId")
    LiveData<List<MeasurementPoint>> getPointsForStation(long stationId);

    // 你可以在这里添加更多查询，例如：
    // @Query("SELECT * FROM measurement_points WHERE point_id = :pointId")
    // MeasurementPoint getPointById(long pointId);

    // @Query("DELETE FROM measurement_points")
    // void deleteAllPoints();
}
