package com.example.leicameasurement.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 导线任务实体（任务ID/状态/时间）
 */
@Entity(tableName = "traverse_tasks")
public class TraverseTask {

    @PrimaryKey(autoGenerate = true)
    public long taskId;

    public String name;

    public String status;

    public long creationTime;
}
