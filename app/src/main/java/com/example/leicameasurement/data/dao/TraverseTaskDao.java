package com.example.leicameasurement.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.leicameasurement.data.entity.TraverseTask;
import java.util.List;

/**
 * 导线任务DAO（增删改查）
 */
@Dao
public interface TraverseTaskDao {

    @Insert
    void insert(TraverseTask task);

    @Query("SELECT * FROM traverse_tasks")
    List<TraverseTask> getAllTasks();
}
