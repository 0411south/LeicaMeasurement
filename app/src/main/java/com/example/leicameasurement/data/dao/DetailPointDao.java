package com.example.leicameasurement.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.leicameasurement.data.entity.DetailPoint;
import java.util.List;

/**
 * 碎步点DAO
 */
@Dao
public interface DetailPointDao {

    @Insert
    void insert(DetailPoint point);

    @Query("SELECT * FROM detail_points WHERE taskId = :taskId")
    List<DetailPoint> getPointsForTask(long taskId);
}
