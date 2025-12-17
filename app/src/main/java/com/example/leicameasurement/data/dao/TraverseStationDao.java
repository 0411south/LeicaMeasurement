package com.example.leicameasurement.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.leicameasurement.data.entity.TraverseStation;
import java.util.List;

/**
 * 测站DAO
 */
@Dao
public interface TraverseStationDao {

    @Insert
    void insert(TraverseStation station);

    @Query("SELECT * FROM traverse_stations WHERE taskId = :taskId")
    List<TraverseStation> getStationsForTask(long taskId);
}
